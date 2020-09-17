package dr.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure for each person enrolled in the study.
 */
public class Person {
    public final String enrollID;
    private final List<Case> cases = new ArrayList<>();

    public Person(String enrollID) {
        this.enrollID = enrollID;
    }

    public void addCase(Case kase){
        cases.add(kase);
    }
    public int getCaseCount(){
        return cases.size();
    }
    public Case getCase(int i) {
        return cases.get(i);
    }

}
