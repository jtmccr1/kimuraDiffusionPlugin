# Title     : TODO
# Objective : TODO
# Created by: jtmccrone
# Created on: 2020-09-14

require(tidyverse)
require(glue)
snv<-read_csv("./data/Intrahost_initially_present.csv") %>% filter(freq1<0.5,within_host_time>0)
text<-c()
for(i in seq_len(nrow(snv))){
  text[i]<-glue("<isnvTrace freq1=\"{snv$freq1[i]}\" freq2=\"{snv$freq2[i]}\" interval=\"{snv$within_host_time[i]}\" titer=\"{log10(snv$gc_ul2[i])}\"/>")
}

cat(text,sep = "\n")

cat(snv$freq1)
cat(snv$freq2)