package com.vbersh;

import org.jsoup.Jsoup;

public class HtmlParser {

    public static void main(String[] args) {
        String content = "<!DOCTYPE html>\n" +
                "\n" +
                "<html>\n" +
                "\t<head>\n" +
                "<link rel=\"canonical\" href=\"blabla.com/path\"/>\n" +
                "\t<body>\n" +
                "\t</body>\n" +
                "</html>";

        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        String url = doc.select("head link[rel=canonical]").attr("href");
        System.out.println(url);
    }

}
