package com.vbersh;

import com.vbersh.types.Circle;
import com.vbersh.types.Shape;
import com.vbersh.types.Square;

import java.util.ArrayList;
import java.util.List;

public class Generics {


    public void nono() {
        List<String> ls = new ArrayList<String>();

        // below is a compile time error because you cannot assign a List<String> to a list of something else
        //List<Object> lo = ls;
    }
    
    public void draw(List<Shape> shapes) {
        for(Shape shape : shapes) {
            System.out.println(shape);
        }
    }

    public void draw2(List<? extends Shape> shapes) {
        for(Shape shape : shapes) {
            System.out.println(shape);
        }
    }

    public static void main(String... args) {
        Generics g = new Generics();

        List<Shape> shapes = new ArrayList<>();
        Shape s1 = new Shape();
        shapes.add(s1);
        g.draw(shapes);

        List<Square> squares = new ArrayList<>();
        Square sq1 = new Square();
        squares.add(sq1);
        g.draw2(squares); // cant use g.draw because it strictly takes a list of Shape.
                          // to avoid writing a draw method for every subtype of Shape
                          // we declare a list of 'unknown(?) extends Shape'
                          // List<? extends Shape> is an example of a bounded wildcard
    }
    
    public void addRectangle(List<? extends Shape> shapes) {
        //shapes.add(0, new Circle());
    }
    

}
