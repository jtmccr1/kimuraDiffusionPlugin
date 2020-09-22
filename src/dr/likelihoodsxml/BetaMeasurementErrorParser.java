package dr.likelihoodsxml;

import dr.inference.model.Parameter;
import dr.likelihoods.BetaMeasurementError;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

public class BetaMeasurementErrorParser extends AbstractXMLObjectParser {

    public static final String BETA_ERROR = "betaError";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Parameter v = (Parameter) xo.getChild(Parameter.class);
        return new BetaMeasurementError(v);
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
        return BetaMeasurementError.class;
    }

    @Override
    public String getParserName() {
        return BETA_ERROR;
    }
}
