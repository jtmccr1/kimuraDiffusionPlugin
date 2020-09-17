package dr.utils;
import java.util.List;
import java.util.Map;

/**
 * A data class for all the meta data for samples used in an analysis.
 */
public class MetaData {
    public final Map<String, Person> people;
    public final List<Case> cases;
    public final Map<String, Sample> samples;

    public MetaData(Map<String, Person> people, List<Case> cases, Map<String, Sample> samples) {
        this.people = people;
        this.cases = cases;
        this.samples = samples;
    }
}