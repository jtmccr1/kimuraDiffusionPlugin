package dr.likelihoodsxml;

import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.likelihoods.InitialFrequencyLikelihood;
import dr.utils.ISNV;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

import java.util.ArrayList;
import java.util.List;

public class InitialFrequencyLikelihoodParser extends AbstractXMLObjectParser {
    public static final String INITIAL_FREQUENCY_LIKELIHOOD = "initialFrequencyLikelihood";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        List<ISNV> variants =  (ArrayList<ISNV>) xo.getChild(List.class);
        Parameter p = (Parameter) xo.getElementFirstChild("p");
        Parameter Ne = (Parameter) xo.getElementFirstChild("Ne");
        Parameter mu = (Parameter) xo.getElementFirstChild("mu");
        Parameter lag = (Parameter) xo.getElementFirstChild("lag");
        Parameter generationTime = (Parameter) xo.getElementFirstChild("generationTime");
        return new InitialFrequencyLikelihood(INITIAL_FREQUENCY_LIKELIHOOD,variants,p,Ne,generationTime,mu,lag);

    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "This element represents the likelihood of initial frequencies.";
    }

    @Override
    public Class getReturnType() {
        return Likelihood.class;
    }

    @Override
    public String getParserName() {
        return INITIAL_FREQUENCY_LIKELIHOOD;
    }
}
