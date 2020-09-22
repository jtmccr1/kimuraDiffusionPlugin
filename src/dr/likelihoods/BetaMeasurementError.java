package dr.likelihoods;

import dr.inference.model.Parameter;
import dr.math.distributions.BetaDistribution;

public class BetaMeasurementError implements MeasurementErrorProvider{
    private final Parameter v;
    public BetaMeasurementError(Parameter v){
        this.v = v;
    }
    @Override
    public double getLogLikelihood(double mean, double observed, double titer) {
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
    public double getLogLikelihood(double observed, double mean){
        return getLogLikelihood(observed, mean, -1);
    }

}
