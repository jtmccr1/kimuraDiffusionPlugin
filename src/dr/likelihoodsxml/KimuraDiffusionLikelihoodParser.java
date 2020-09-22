package dr.likelihoodsxml;

import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import dr.likelihoods.KimuraDiffusionLikelihood;
import dr.likelihoods.MeasurementErrorProvider;
import dr.utils.ISNVtrace;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

import java.util.ArrayList;
import java.util.List;

public class KimuraDiffusionLikelihoodParser extends AbstractXMLObjectParser {
    public static final String KIMURA_LIKELIHOOD = "kimuraDiffusionLikelihood";

    @Override
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        List<ISNVtrace> traces =  (ArrayList<ISNVtrace>) xo.getChild(List.class);
        Parameter p0 = (Parameter) xo.getElementFirstChild("p0");
        Parameter pt = (Parameter) xo.getElementFirstChild("pt");
        Parameter Ne = (Parameter) xo.getElementFirstChild("Ne");
        Parameter generationTime = (Parameter) xo.getElementFirstChild("generationTime");
        boolean conditionOnPolymorphic = xo.getBooleanAttribute("conditionOnPolymorphic");

        MeasurementErrorProvider measurementErrorProvider = (MeasurementErrorProvider) xo.getChild(MeasurementErrorProvider.class);

        if(measurementErrorProvider!=null){
            return new KimuraDiffusionLikelihood(KIMURA_LIKELIHOOD,traces,p0,pt,Ne,generationTime,measurementErrorProvider,conditionOnPolymorphic);
        }
        return new KimuraDiffusionLikelihood(KIMURA_LIKELIHOOD,traces,p0,pt,Ne,generationTime,conditionOnPolymorphic);


    }

    @Override
    public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[0];
    }

    @Override
    public String getParserDescription() {
        return "This element represents the likelihood of allele trajectories.";
    }

    @Override
    public Class getReturnType() {
        return Likelihood.class;
    }

    @Override
    public String getParserName() {
        return KIMURA_LIKELIHOOD;
    }
}
