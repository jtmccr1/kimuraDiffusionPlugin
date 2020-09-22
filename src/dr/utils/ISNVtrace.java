package dr.utils;

import java.util.List;

/**
 * A bespoke data class for iSNV identified in longitudinal samples.
 */
public class ISNVtrace {


    final public List<ISNV> observations;
    public ISNVtrace(List<ISNV> observations){
        this.observations = observations;
    }

    public double getFreq(int i) {
        return observations.get(i).freq ;
    }

    public double getInterval(int i) {
        return observations.get(i+1).sample.DPI- observations.get(i).sample.DPI;
    }

    public double getLogTiter(int i){
        return observations.get(i).sample.floorLogGcUL;
    }
}
