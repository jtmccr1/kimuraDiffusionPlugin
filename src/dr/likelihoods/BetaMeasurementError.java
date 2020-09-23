package dr.likelihoods;

import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.BetaDistribution;
import dr.utils.ISNV;

import java.util.Arrays;
import java.util.List;

public class BetaMeasurementError extends AbstractModelLikelihood {
    private final Parameter v;
    private final Parameter p;
    private final List<ISNV> data;
    private boolean[] updated;
    private boolean[] storedUpdated;

    private boolean likelihoodKnown;
    private boolean storedLikelihoodKnown;

    private double logLikelihood;
    private double storedLogLikelihood;

    private double[] iSNVLogLikelihoods;
    private double[] storedISNVLogLikelihood;
    private final boolean perfectSensitivity;
    public BetaMeasurementError(Parameter v,Parameter p, List<ISNV> data,boolean perfectSensitivity){
        super("betaMeasurementError");
        this.v = v;
        this.p = p;
        this.data = data;

        addVariable(v);
        addVariable(p);

        this.updated = new boolean[data.size()];
        this.storedUpdated = new boolean[data.size()];

        this.iSNVLogLikelihoods = new double[data.size()];
        this.storedISNVLogLikelihood = new double[data.size()];

        this.likelihoodKnown = false;
        this.storedLikelihoodKnown = false;

        logLikelihood = 0;
        storedLogLikelihood = 0;

        this.perfectSensitivity = perfectSensitivity;

    }


    @Override
    protected void handleModelChangedEvent(Model model, Object o, int i) {

    }

    @Override
    protected void handleVariableChangedEvent(Variable variable, int i, Variable.ChangeType changeType) {
        if (variable == v|| i==-1) {
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

        System.arraycopy(this.iSNVLogLikelihoods, 0, this.storedISNVLogLikelihood, 0, this.iSNVLogLikelihoods.length);
        System.arraycopy(this.updated, 0, this.storedUpdated, 0, this.updated.length);

    }

    @Override
    protected void restoreState() {
        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;

        double[] tmp = storedISNVLogLikelihood;
        storedISNVLogLikelihood = iSNVLogLikelihoods;
        iSNVLogLikelihoods = tmp;

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

    private void calculateLogLikelihood(){
        logLikelihood = 0;
        for (int i = 0; i < data.size(); i++) {
            if (updated[i]) {
                double LL;
                ISNV iSNV = data.get(i);
                if(perfectSensitivity){
                    LL = calculateLogLikelihood(p.getParameterValue(i),iSNV.freq);
                }else{
                    LL = calculateLogLikelihood(p.getParameterValue(i),iSNV.freq,iSNV.sample.floorLogGcUL);
                }
                iSNVLogLikelihoods[i] = LL;
                updated[i] = false;
            }
            logLikelihood += iSNVLogLikelihoods[i];
        }
        likelihoodKnown = true;
    }
    private double calculateLogLikelihood(double mean, double observed, double titer) {

        double a = mean * v.getParameterValue(0);
        double b = (1 - mean) *v.getParameterValue(0);
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
        return new BetaDistribution(a, b).logPdf(observed);
    }

    private double calculateLogLikelihood(double mean, double observed){
        return calculateLogLikelihood(mean,observed,-1);
    }

}
