package com.vbersh.model;

import java.util.Observable;
import java.util.Observer;

public class BrokerObserver implements Observer {


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("The observable object changed: "+o +" more info: "+arg);
    }
}
