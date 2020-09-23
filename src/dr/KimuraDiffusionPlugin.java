package dr;

import dr.app.plugin.Plugin;
import dr.likelihoodsxml.BetaMeasurementErrorParser;
import dr.likelihoodsxml.InitialFrequencyLikelihoodParser;
import dr.likelihoodsxml.KimuraDiffusionLikelihoodParser;
import dr.utilsxml.*;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.XMLObjectParser;

import java.util.HashSet;
import java.util.Set;

public class KimuraDiffusionPlugin implements Plugin {
    @Override
    public Set<XMLObjectParser> getParsers() {
        Set<XMLObjectParser> parsers = new HashSet<XMLObjectParser>();
        AbstractXMLObjectParser ISNVtracesParser = new ISNVtracesParser();
        parsers.add(ISNVtracesParser);

        AbstractXMLObjectParser ISNVtraceParser = new ISNVtraceParser();
        parsers.add(ISNVtraceParser);

        AbstractXMLObjectParser sampleParser = new SampleParser();
        parsers.add(sampleParser);

        AbstractXMLObjectParser isnvParser = new ISNVParser();
        parsers.add(isnvParser);

        AbstractXMLObjectParser isnvList = new ISNVListParser();
        parsers.add(isnvList);

        AbstractXMLObjectParser initialLikelihoodParser =new InitialFrequencyLikelihoodParser();
        parsers.add(initialLikelihoodParser);

        AbstractXMLObjectParser kimuraDiffusionLikelihoodParser = new KimuraDiffusionLikelihoodParser();
        parsers.add(kimuraDiffusionLikelihoodParser);

        AbstractXMLObjectParser betaMeasurementErrorParser  = new BetaMeasurementErrorParser();
        parsers.add(betaMeasurementErrorParser);


        return parsers;
    }
}
