package org.apache.nutch.parse;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.HtmlParseFilter;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;
import org.w3c.dom.DocumentFragment;

public class ParseFieldFromHtml implements HtmlParseFilter {

    private Configuration conf;

    @Override
    public ParseResult filter(Content content, ParseResult parseResult,
                              HTMLMetaTags metaTags, DocumentFragment docFragment) {

        String canUrl = null;

        String[] links = content.toString().split("<link");

        for(String link : links) {
            if(link.contains("canonical")) {
                link = link.substring(0, link.indexOf(">"));
                int ix_begin = link.indexOf("href=\"") + 6;
                int ix_end = link.lastIndexOf("\"");
                canUrl = link.substring(ix_begin, ix_end);
                break;
            }
        }

        Parse parse = parseResult.get(content.getUrl());

        parse.getData().getContentMeta().set("canUrl", canUrl);

        return parseResult;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }
}
