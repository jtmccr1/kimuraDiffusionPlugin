package test.dr;

import dr.utils.SpecialFunctions;
import junit.framework.TestCase;

public class SpecialFunctionsTest extends TestCase {

    public void testGamma(){
        double[] answers40 = {0.4,-0.0666667, -0.16, -0.01888, 0.0808533, 0.0380251,-0.03968,-0.0396558, 0.0146106, 0.0344758};
        for (int i = 1; i < 11; i++) {
            assertEquals(answers40[i-1], SpecialFunctions.hyp2f1(-i,i+2,2,0.4),1e-7);
        }

    double[] answers02 = {0.97,0.921333,0.85593,0.776356,0.685687,0.587356,0.48499,0.382236,0.282592,0.189249};
        for (int i = 1; i < 11; i++) {
        assertEquals(answers02[i-1],SpecialFunctions.hyp2f1(-i,i+2,2,0.02),1e-5);
    }
        int i=15;
        assertEquals(-0.0010582,SpecialFunctions.hyp2f1(-i,i+2,2,0.97),1e-5);

    }


}
