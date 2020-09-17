package dr.utilsxml;

import dr.utils.ISNV;
import dr.utils.Sample;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

public class ISNVParser extends AbstractXMLObjectParser {
    public static final String ISNV_NAME = "isnv";
    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        double freq = xo.getDoubleAttribute("freq");
        Sample sample = (Sample) xo.getChild(Sample.class);
        return new ISNV(sample,freq);
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "This element represents an isnv";
    }

    @Override
    public Class getReturnType() {
        return ISNV.class;
    }

    @Override
    public String getParserName() {
        return ISNV_NAME;
    }
}