package com.vbersh;

public class Arrays {


    public char[] reverse(char[] array) {
        for(int i=0;i<array.length/2;i++) {
            char temp = array[i];
            array[i] = array[array.length-i-1];
            array[array.length-i-1] = temp;
        }
        return array;
    }

    public static void main(String... args) {
        Arrays r = new Arrays();
        char[] ar = new char[4];
        ar[0]='a';
        ar[1]='b';
        ar[2]='c';
        ar[3]='d';
        System.out.println(r.reverse(ar));
    }

}
