package com.vbersh;

import com.vbersh.model.Person;

import java.util.List;

public class Lambdas {

    public static void main(String[] args) {

        Runnable r1 = () -> {
            System.out.println("r1 equals r2");
        };

        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("r1 equals r2");
            }
        };

        Thread t1 = new Thread(r1);
        t1.start();

        Thread t2 = new Thread(r1);
        t2.start();


        Person p1 = new Person();
        p1.setName("Person One");
        p1.setId(1);


        Person p2 = new Person();
        p2.setName("Person two");
        p2.setId(2);

        List<Person> peeps = java.util.Arrays.asList(p1, p2);

        for(Person peep : peeps) {
            if(peep.getId() % 2 == 0) {
                System.out.println("The peep is: " + peep.getName());
            }
        }

        peeps.stream().
                filter(peep -> peep.getId() % 2 == 0).
                forEach(peep -> System.out.println("The peep is: " + peep.getName()));



    }

}
