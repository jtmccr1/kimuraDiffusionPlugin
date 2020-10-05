# kimuraDiffusionPlugin
A plugin for BEAST that estimates Ne from allele trajectories.  The plugin has been compiled against BEAST v1.10.5 Prerelease #23570d1. 

The plugin implements the likelihoods in equation 15' of Kimura, 1955.  Measurement error and limited sensitity are included as discussed in McCrone et al., 2020. 
The current implementation is specific to the analysis pipeline above, but may be made more generalizable in the future.


To use: place the jar file from dist/dr.KimuraDiffusionPlugin.jar into a 'plugins' directory next to your xml file. An example xml is at example/example.xml.
