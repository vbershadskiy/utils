package org.apache.nutch.indexer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

import java.util.Map;

public class AddFieldToIndex implements IndexingFilter {

    private static final Log LOG = LogFactory.getLog(AddFieldToIndex.class);
    private Configuration conf;

    //implements the filter-method which gives you access to important Objects like NutchDocument
    public NutchDocument filter(NutchDocument doc, Parse parse, Text url,
                                CrawlDatum datum, Inlinks inlinks) {

        String canUrl = parse.getData().getMeta("canUrl");

        doc.add("datum", canUrl);

        MapWritable metaData = datum.getMetaData();
        if (metaData != null) {
            for (Map.Entry<Writable, Writable> e : metaData.entrySet()) {
                String key = e.getKey().toString();
                if(key.startsWith("nutch.col")) {
                    doc.add("datum", e.getValue());
                }
            }
        }

        return doc;
    }

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }
}