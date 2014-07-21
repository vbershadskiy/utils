package com.vbersh.async;

import com.vbersh.model.BrokerObserver;
import com.vbersh.model.Person;
import com.vbersh.model.StockObservable;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObserveTest {

    public static void main(String[] args) {
        // initialize observable
        StockObservable stock = new StockObservable();
        stock.setSymbol("IBM");
        stock.setPrice(55.42);

        // register observers
        BrokerObserver broker1 = new BrokerObserver();
        BrokerObserver broker2 = new BrokerObserver();
        stock.addObserver(broker1);
        stock.addObserver(broker2);

        // update observable and notify observers
        stock.setPrice(55.45); // setChanged() called
        stock.notifyObservers("price change");

        new ObserveTest().rxJavaAsyncObserve();
    }

    public void rxJavaAsyncObserve() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Person is retrieved async and subscriber's callbacks are activated
        Observable<Person> o = Observable.create((Observable.OnSubscribe<Person>) subscriber -> {

            Runnable r = () -> {
                try {
                    // blocking call to get person object
                    Person p = new Person();
                    p.setName("al bundy");

                    // push person object to all subscribers
                    subscriber.onNext(p);

                } catch (Throwable t) {
                    subscriber.onError(t);
                } finally {
                    subscriber.onCompleted();
                }
            };

            executor.execute(r);
        });

        // by subscribing to the observable the async function is executed
        o.subscribe(new Subscriber<Person>() {
            @Override
            public void onCompleted() {
                System.out.println("finished");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Person person) {
                System.out.println(person);
            }
        });

        executor.shutdown();

    }
}
