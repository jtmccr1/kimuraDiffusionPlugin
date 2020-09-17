package dr.utilsxml;

import dr.utils.ISNV;
import dr.utils.ISNVtrace;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

import java.util.ArrayList;
import java.util.List;


public class ISNVtraceParser extends AbstractXMLObjectParser {
    public static final String ISNV_TRACE = "isnvTrace";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        List<ISNV> observations = new ArrayList<>(xo.getAllChildren(ISNV.class));
        return new ISNVtrace(observations);
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "This element represents an isnv trace.";
    }

    @Override
    public Class getReturnType() {
        return ISNVtrace.class;
    }

    @Override
    public String getParserName() {
        return ISNV_TRACE;
    }
}
