package dr.utils;

/**
 * Data structure for each variant identified in a sample.
 */
public class ISNV {

    public ISNV(Sample sample, double freq) {
        this.sample = sample;
        this.freq = freq;
    }

    public Sample sample;
    final public double freq;

}
