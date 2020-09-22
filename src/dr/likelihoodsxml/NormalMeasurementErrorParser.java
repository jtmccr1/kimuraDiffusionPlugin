package dr.likelihoodsxml;

import dr.inference.model.Parameter;
import dr.likelihoods.NormalMeasurementError;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

public class NormalMeasurementErrorParser extends AbstractXMLObjectParser {

    public static final String NORMAL_ERROR = "normalError";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Parameter n = (Parameter) xo.getChild(Parameter.class);
        return new NormalMeasurementError(n);
    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return null;
    }

    @Override
    public Class getReturnType() {
        return NormalMeasurementError.class;
    }

    @Override
    public String getParserName() {
        return NORMAL_ERROR;
    }
}

