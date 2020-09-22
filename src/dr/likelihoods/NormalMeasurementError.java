package dr.likelihoods;

import dr.inference.model.Parameter;
import dr.math.distributions.NormalDistribution;

public class NormalMeasurementError implements MeasurementErrorProvider{
    private final Parameter n;

    public NormalMeasurementError(Parameter n){
        this.n = n;
    }
    @Override
    public double getLogLikelihood(double mean, double observed, double titer) {
        double mu = mean;
        double sigma = Math.sqrt(mean*(1-mean)/(n.getParameterValue(0)));
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
                double probBelowCut = new NormalDistribution(mu, sigma).cdf(0.02);
                return Math.log(probBelowCut + probMissed);
            }
        }
        return  new NormalDistribution(mu, sigma).logPdf(observed);
    }


    @Override
    public double getLogLikelihood(double mean, double observed) {
        return getLogLikelihood(mean,observed,-1);
    }
}
