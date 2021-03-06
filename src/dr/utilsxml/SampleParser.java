package dr.utilsxml;

import dr.utils.ISNV;
import dr.utils.Sample;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

public class SampleParser extends AbstractXMLObjectParser {
    public static final String SAMPLE = "sample";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        String specID = xo.getStringAttribute("specID");
        double DPI = xo.getDoubleAttribute("DPI");
        double floorLogGcUL= xo.getDoubleAttribute("floorLogGcUL");
//        Case kase = (Case) xo.getChild(Case.class);
        Sample sample  = new Sample(specID,null,null,DPI,floorLogGcUL,null);


        for (ISNV isnv:xo.getAllChildren(ISNV.class)) {
            sample.addISNV(isnv);
            isnv.sample = sample;
        }

        return sample;
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "This element represents an Sample trace.";
    }

    @Override
    public Class getReturnType() {
        return Sample.class;
    }

    @Override
    public String getParserName() {
        return SAMPLE;
    }
}
