package dr.likelihoods;

import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.utils.ISNVtrace;
import dr.utils.SpecialFunctions;

import java.util.Arrays;
import java.util.List;

public class KimuraDiffusionLikelihood extends AbstractModelLikelihood {

    public KimuraDiffusionLikelihood(String s, List<ISNVtrace> data, Parameter p0, Parameter pt,
                                     Parameter Ne, Parameter generationTime,
                                     MeasurementErrorProvider measurementErrorProvider,
                                     boolean conditionOnPolymorphic) {
        super(s);
        this.p0 = p0;
        this.pt = pt;
        this.Ne = Ne;
        this.generationTime = generationTime;
        addVariable(p0);
        addVariable(pt);
        addVariable(Ne);
        addVariable(generationTime);

        this.data = data;


        this.updated = new boolean[data.size()];
        this.storedUpdated = new boolean[data.size()];

        this.traceLogLikelihoods = new double[data.size()];
        this.storedTraceLogLikelihood = new double[data.size()];

        this.likelihoodKnown = false;
        this.storedLikelihoodKnown = false;

        logLikelihood = 0;
        storedLogLikelihood = 0;

        this.conditionOnPolymorphic = conditionOnPolymorphic;
        this.measurementErrorProvider = measurementErrorProvider;
    }
    public KimuraDiffusionLikelihood(String s, List<ISNVtrace> data, Parameter p0, Parameter pt,
                                     Parameter Ne, Parameter generationTime,
                                     boolean conditionOnPolymorphic){
        this(s, data, p0, pt, Ne, generationTime, new NoMeasurementError(), conditionOnPolymorphic);
    }
    @Override
    protected void handleModelChangedEvent(Model model, Object o, int i) {

    }

    @Override
    protected void handleVariableChangedEvent(Variable variable, int i, Variable.ChangeType changeType) {
        if (variable == Ne || variable == generationTime || i == -1) {
            Arrays.fill(updated, true);

        } else {
            updated[i] = true;
        }
        likelihoodKnown = false;
    }

    @Override
    protected void storeState() {

        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;

        System.arraycopy(this.traceLogLikelihoods, 0, this.storedTraceLogLikelihood, 0, this.traceLogLikelihoods.length);
        System.arraycopy(this.updated, 0, this.storedUpdated, 0, this.updated.length);

    }

    @Override
    protected void restoreState() {
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;

        double[] tmp = storedTraceLogLikelihood;
        storedTraceLogLikelihood = traceLogLikelihoods;
        traceLogLikelihoods = tmp;

        boolean[] tmp2 = storedUpdated;
        storedUpdated = updated;
        updated = tmp2;

    }

    @Override
    protected void acceptState() {

    }

    @Override
    public Model getModel() {
        return this;
    }

    @Override
    public double getLogLikelihood() {
        if (!likelihoodKnown) {
            calculateLogLikelihood();
        }
        return logLikelihood;
    }

    @Override
    public void makeDirty() {
        Arrays.fill(updated, true);
        likelihoodKnown = false;
    }

    private void calculateLogLikelihood() {
        logLikelihood = 0;
        for (int i = 0; i < data.size(); i++) {
            if (updated[i]) {
                double freq1 = p0.getValue(i);
                double freq2 = pt.getValue(i);
                ISNVtrace isnv = data.get(i);
                //TODO generalize to take more than 2 observations
                double generations = isnv.getInterval(0) * (24.0 / generationTime.getValue(0));
                double traceLL;
                if (freq2 > 0) {
                    traceLL = polymorphicLL(freq1, freq2, generations, Ne.getValue(0));
                } else {
                    traceLL = fixedLL(freq1,freq2, generations, Ne.getValue(0));
                }
                traceLL += measurementErrorProvider.getLogLikelihood( freq1,isnv.getFreq(0));
                traceLL += measurementErrorProvider.getLogLikelihood( freq2,isnv.getFreq(1), isnv.getLogTiter(1));

                if (conditionOnPolymorphic) {  //account for fact that it could not be 0
                    traceLL -= Math.log(1 - (Math.exp(
                            fixedLL(freq1, 0, generations, Ne.getValue(0))) + Math.exp(
                            fixedLL(freq1, 1, generations, Ne.getValue(0)))));
                }
                traceLogLikelihoods[i] = traceLL;
                updated[i] = false;
            }
            logLikelihood += traceLogLikelihoods[i];

        }
        likelihoodKnown = true;
    }
    private double ith_term(int i, double p0,double pt, double t, double N){
       double q = 1.0 - p0;
        double first = p0 * q * i * (i + 1) * ((2 * i) + 1);
        double geometric_1 = SpecialFunctions.hyp2f1(1 - i, i + 2, 2, p0);
        double geometric_2 = SpecialFunctions.hyp2f1(1 - i, i + 2, 2, pt);
        double exponent = i * (i + 1) * t / (2 * N); // 4N in the book
        return first * geometric_1 * geometric_2 * Math.exp(-1 * exponent);
    }

    private double polymorphicLL(double p0,double pt, double generations, double ne){
        double prob = 0.0;
        int i = 1;
        double term = ith_term(i , p0, pt, generations, ne);
        prob += term;
        while ((Math.abs(term) > 1e-10 || prob < 0) && i < 1000){
            i += 1;
            term = ith_term(i , p0, pt, generations, ne);
            prob += term;
        }

        if(i == 1000){
            System.out.println("hit max depth");
            System.out.println(term);
        }

        if(prob < 0){ // # underflow at very small probability
            System.out.println("WARNING: Underflow with Ne of " + ne + "(" + p0 + "->" + pt + ")");
            return Double.NEGATIVE_INFINITY;
        }

        return Math.log(prob);
    }

    private double ith_term_fixed(double i, double p, double t, double N) {  //#proofed JT 5 / 22 / 17
        double first = (2 * i + 1) * p * (1 - p) * Math.pow(-1,i);
        double geometric = SpecialFunctions.hyp2f1(1 - i, i + 2, 2, p);
        double exponent = i * (i + 1) * t / (2 * N);  //#4 N in the book
        return first * geometric * Math.exp(-1 * exponent);
    }
    private double fixedLL(double p0, double pt, double generations, double ne){
        double fixed_freq;
        if(pt == 0) {
            fixed_freq = 1.0 - p0;  //# set for loss. The probability the other allele is fixed
        }
        else if(pt == 1){
            fixed_freq = p0;  //# In this case this is the frequency of the allele we want to fix
        }else{
            throw new RuntimeException("pt must be 0 or 1");
        }

        double prob = fixed_freq;

        int i = 1;
        double term = ith_term_fixed(i, fixed_freq, generations, ne);
        prob += term;
        while ((Math.abs(term) > 1e-10 || prob < 0) && i < 1000){
            i += 1;
            term = ith_term_fixed(i, fixed_freq, generations, ne);
            prob += term;
        }

        if(i == 1000){
            System.out.println(term);
            System.out.println(prob);
            System.out.println("fixed_freq = " + fixed_freq);
            System.out.println("generations = " + generations);
            System.out.println("ne = " + ne);
            throw new RuntimeException("hit max depth");
        }

        return Math.log(prob);
    }



    private final Parameter p0;
    private final Parameter pt;
    private final Parameter Ne;
    private final List<ISNVtrace> data;
    private final Parameter generationTime;

    private final boolean conditionOnPolymorphic;
    private boolean[] updated;
    private boolean[] storedUpdated;

    private boolean likelihoodKnown;
    private boolean storedLikelihoodKnown;

    private double logLikelihood;
    private double storedLogLikelihood;

    private double[] traceLogLikelihoods;
    private double[] storedTraceLogLikelihood;

    private MeasurementErrorProvider measurementErrorProvider;
}

