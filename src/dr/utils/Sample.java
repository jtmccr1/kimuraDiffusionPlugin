package dr.utils;


import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for each sample taken in the study. A sample contains iSNV. Each case can have multiple samples.
 */
public class Sample {
    final public String specID;
    final public String collect; //date
    final public String LAURING_ID;
    final public double DPI;
    final public double floorLogGcUL;
    final private List<ISNV> iSNV = new ArrayList<>();
    final public Case kase;

    public Sample(String specID, String collect, String lauring_id, double dpi, double floorLogGcUL, Case kase) {
        this.specID = specID;
        this.collect = collect;
        LAURING_ID = lauring_id;
        DPI = dpi;
        this.floorLogGcUL = floorLogGcUL;
        this.kase = kase;
    }

    public void addISNV(ISNV iSNV){
        this.iSNV.add(iSNV);
    }
    public int getISNVCount(){
        return iSNV.size();
    }
    public ISNV getISNV(int i) {
        return iSNV.get(i);
    }


}
