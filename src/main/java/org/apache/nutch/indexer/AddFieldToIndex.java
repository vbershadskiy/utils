package org.apache.nutch.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

public class AddFieldToIndex implements IndexingFilter {

    private static final Log LOG = LogFactory.getLog(AddFieldToIndex.class);
    private Configuration conf;

    //implements the filter-method which gives you access to important Objects like NutchDocument
    public NutchDocument filter(NutchDocument doc, Parse parse, Text url,
                                CrawlDatum datum, Inlinks inlinks) {
        String content = parse.getText();
        //adds the new field to the document

        String can = null;

        String[] links = content.split("<link");

        for(String link : links) {
            if(link.contains("canonical")) {
                link = link.substring(0, link.indexOf(">"));
                int ix_begin = link.indexOf("href=\"") + 6;
                int ix_end = link.lastIndexOf("\"");
                can = link.substring(ix_begin, ix_end);
                break;
            }
        }

        doc.add("canonicalUrl", can);

        return doc;
    }

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }
}