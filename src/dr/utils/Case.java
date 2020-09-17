
package dr.utils;



import java.util.ArrayList;
import java.util.List;

/**
 * A data structure to hold case data for each period of illness for each enrolled individual.
 */
public class Case {
    private static int currentNumber = 0;
    final public int vaccinationStatus;
    final public String pcrResult;// enum
    final public String season;
    final public int number;
    final public String onset; //date
    final public Person person;
    final private List<Sample> samples  = new ArrayList<>();

    public Case(Person person, int vaccinationStatus, String pcr_result, String season, String onset) {
        this.vaccinationStatus = vaccinationStatus;
        this.pcrResult = pcr_result;
        this.season = season;
        this.onset = onset;
        this.person = person;
        this.number = currentNumber;
        currentNumber++;
    }

    public void addSample(Sample sample){
        samples.add(sample);
    }
    public int getSampleCount(){
        return samples.size();
    }
    public Sample getSample(int i) {
        return samples.get(i);
    }

    public int getNumber() {
        return number;
    }
    public String getEnrollID() {
        return this.person.enrollID;
    }
}
