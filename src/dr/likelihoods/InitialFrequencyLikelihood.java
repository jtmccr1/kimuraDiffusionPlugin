package dr.likelihoods;

import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.BetaDistribution;
import dr.utils.ISNV;

import java.util.Arrays;
import java.util.List;

public class InitialFrequencyLikelihood extends AbstractModelLikelihood {

    private final Parameter p;
    private final Parameter Ne;
    private final Parameter generationTime;
    private final Parameter mu;
    private final Parameter lag;
    private final List<ISNV> data;
    private boolean[] updated;
    private boolean[] storedUpdated;
    private double[] traceLogLikelihoods;
    private double[] storedTraceLogLikelihood;
    private boolean likelihoodKnown;
    private boolean storedLikelihoodKnown;
    private int storedLogLikelihood;
    private int logLikelihood;

    public InitialFrequencyLikelihood(String s, List<ISNV> data, Parameter p,
                                      Parameter Ne, Parameter generationTime, Parameter mu, Parameter lag) {
        super(s);
        this.p = p;

        this.Ne = Ne;
        this.generationTime = generationTime;
        this.mu = mu;
        this.lag = lag;

        addVariable(p);
        addVariable(Ne);
        addVariable(generationTime);
        addVariable(mu);
        addVariable(lag);

        this.data = data;


        this.updated = new boolean[data.size()];
        this.storedUpdated = new boolean[data.size()];

        this.traceLogLikelihoods = new double[data.size()];
        this.storedTraceLogLikelihood = new double[data.size()];

        this.likelihoodKnown = false;
        this.storedLikelihoodKnown = false;

        this.logLikelihood = 0;
        this.storedLogLikelihood = 0;

    }


    @Override
    protected void handleModelChangedEvent(Model model, Object o, int i) {

    }

    @Override
    protected void handleVariableChangedEvent(Variable variable, int i, Variable.ChangeType changeType) {
        if (variable == p && i != -1) {
            updated[i] = true;
        } else {
            Arrays.fill(updated, true);

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
                double freq = data.get(i).freq;
                double dpi = data.get(i).sample.DPI;
                double generations = (dpi + lag.getParameterValue(0)) * 24 / generationTime.getParameterValue(0);
                double LL = Math.log(2 * mu.getParameterValue(0) * Ne.getParameterValue(0) / p.getParameterValue(i)) - 2 * Ne.getParameterValue(0) * p.getParameterValue(i) / generations;
                LL += measurementError(freq, p.getParameterValue(i), data.get(i).sample.floorLogGcUL);
                traceLogLikelihoods[i] = LL;
                updated[i] = false;
            }
            logLikelihood += traceLogLikelihoods[i];
        }
        likelihoodKnown = true;
    }

    private double measurementError(double observed, double mean, double titer) {
        double a = mean * 503.0; // TODO make this estimable
        double b = (1 - mean) * 503.0;
//     No false positives
        if (mean == 0 && observed != 0) {
            return Double.NEGATIVE_INFINITY;
        }
        if (observed == 0) {
            if (mean == 0) {
                return 0;
            }
            if (mean > 0) {                // prob measured below cut off
                double probMissed = 0;
                if (titer != -1) {
                    if (mean > 0.02 && mean < 0.05) {
                        probMissed = 1 - 0.15;  // 0.85 sensitivity at 2% in all titers
                    } else if (mean > 0.05 && mean < 0.1) {
                        if (titer < 5) {
                            probMissed = 1 - 0.7;
                        } else {
                            probMissed = 1 - 0.85;
                        }
                    }
                }
                double probBelowCut = new BetaDistribution(a, b).cdf(0.02);
                return Math.log(probBelowCut + probMissed);
            }
        }
        return  new BetaDistribution(a, b).logPdf(observed);
    }
}
