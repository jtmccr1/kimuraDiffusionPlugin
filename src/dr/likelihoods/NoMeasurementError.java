package dr.likelihoods;

public class NoMeasurementError implements MeasurementErrorProvider{
    @Override
    public double getLogLikelihood(double mean, double observed, double titer) {
        return 0;
    }

    @Override
    public double getLogLikelihood(double mean, double observed) {
        return 0;
    }
}
