package dr.likelihoods;

//TODO make a likelihood model so can estimated
public interface MeasurementErrorProvider{
    double getLogLikelihood(double mean, double observed, double titer);
    double getLogLikelihood(double mean, double observed);
}
