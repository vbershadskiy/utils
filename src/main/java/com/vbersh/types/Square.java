package com.vbersh.types;

/**
 * Type parameters (ie. T) can be declared on the class name or individually on the method.
 *
 * @param <T>
 */
public class Square<T> extends Shape {

    public void set(T t) {
        
    }
    
    public T get(T o) {
        return o;
    }

    public <G> void someMethodWithAnInLineTypeParameter(G param) {

    }

}
