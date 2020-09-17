package dr.utilsxml;

import dr.utils.ISNV;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

import java.util.ArrayList;
import java.util.List;


public class ISNVListParser extends AbstractXMLObjectParser {
    public static final String ISNV_LIST = "isnvList";
    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        return new ArrayList<>(xo.getAllChildren(ISNV.class));
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "parse a list of isnvs";
    }

    @Override
    public Class getReturnType() {
        return List.class;
    }

    @Override
    public String getParserName() {
        return ISNV_LIST;
    }
}