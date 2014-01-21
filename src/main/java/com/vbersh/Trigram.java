package com.vbersh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * coding exercise http://codekata.pragprog.com/2007/01/kata_fourteen_t.html
 *
 * note: in a distributed environment can be solved with hadoop
 */
public class Trigram {

    private String book = null;

    private Map<String, List<Node>> trigram = new HashMap<String, List<Node>>();

    private String newText = null;

    public static void main(String... args) {

        Trigram t = new Trigram();

        t.loadBook("book.txt");

        t.doTrigramAnalysis();

        String start = "I think";

        String text = t.generateNewText(start);

        System.out.println(text);
    }

    public String generateNewText(String key) {
        List<Node> nodes = trigram.get(key);

        if(nodes == null) { // reached the end
            return newText;
        }

        String [] pair = key.split(" ");

        if(pair.length != 2) {
            return newText;
        }

        String newKey = null;

        for(Node node : nodes) {
            if(!node.isVisited()){
                node.setVisited(true);
                if(newText == null) newText = key;
                newText += " " + node.getWord();
                newKey = pair[1] + " " + node.getWord();
                break;
            }
        }

        if(newKey == null) { // reached the end
            return newText;
        }

        return generateNewText(newKey);
    }

    public void loadBook(String fileName) {
        FileReader fr = null;
        try {
            fr = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);

        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.book = sb.toString();
    }

    public void doTrigramAnalysis() {
        String [] words = book.split("\\s");

        for(int i = 0; i<words.length-2;i++) {
            String key = words[i] + " " + words[i+1];
            String value = words[i+2];

            // hash and group
            List<Node> list = trigram.get(key);
            if(list == null) {
                list = new LinkedList<Node>();
            }
            list.add(new Node(value));
            trigram.put(key, list);
        }
    }

    private class Node {
        String word;
        boolean visited = false;

        private Node(String word) {
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }
    }

}
