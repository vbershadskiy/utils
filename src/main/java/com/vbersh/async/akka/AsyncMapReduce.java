package com.vbersh.async.akka;

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;
import akka.dispatch.OnSuccess;
import akka.japi.Function2;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static akka.dispatch.Futures.reduce;
import static akka.dispatch.Futures.future;
import static akka.dispatch.Futures.sequence;


/**
 * http://doc.akka.io/docs/akka/snapshot/java/futures.html
 * http://code.google.com/p/guava-libraries/wiki/ListenableFutureExplained
 */
public class AsyncMapReduce {

    public static void main(String... args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        ExecutionContext ec = ExecutionContexts.fromExecutorService(executor);

        List<Future<Long>> listOfFutureLongs = new ArrayList<>();
        listOfFutureLongs.add(future(new NonBlockingGetLongCall(), ec));
        listOfFutureLongs.add(future(new NonBlockingGetLongCall(), ec));

        _map(ec, listOfFutureLongs);

        _reduce(ec, listOfFutureLongs);

        executor.shutdown();
    }

    public static void _map(ExecutionContext ec, List<Future<Long>> listOfFutureLongs) throws  Exception {
        // create one future from a list of futures
        Future<Iterable<Long>> futureListOfLongs = sequence(listOfFutureLongs, ec);

        // apply some function to the results of the futures and return a new list with those results
        Future<Iterable<Long>> resultFuture = futureListOfLongs.map(
                new Mapper<Iterable<Long>, Iterable<Long>>() {
                    @Override
                    public Iterable<Long> apply(Iterable<Long> longs) {
                        List<Long> newList = new ArrayList<>();
                        for (Long l : longs) {
                            newList.add(l + 1);
                        }
                        return newList;
                    }
                }, ec
        );

        resultFuture.onSuccess(new PrintResult<Iterable<Long>>("map"), ec);
        Duration timeoutDuration = Duration.apply(5, TimeUnit.SECONDS);
        Iterable<Long> result = Await.result(resultFuture, timeoutDuration);
    }

    public static void _reduce(ExecutionContext ec, List<Future<Long>> listOfFutureLongs) throws Exception {
        Future<Long> resultFuture = reduce(listOfFutureLongs,
                new Function2<Long, Long, Long>() {
                    @Override
                    public Long apply(Long r, Long t) {
                        return r + t;
                    }
                }, ec);

        resultFuture.onSuccess(new PrintResult<Long>("reduce"), ec);
        Duration timeoutDuration = Duration.apply(5, TimeUnit.SECONDS);
        Long result = Await.result(resultFuture, timeoutDuration);
    }

    public static class PrintResult<T> extends OnSuccess<T> {
        private String name;

        public PrintResult(String name) {
            this.name = name;
        }

        @Override
        public final void onSuccess(T t) {
            System.out.println(name+" onSuccess: " + t);
        }
    }

    public static class NonBlockingGetLongCall implements Callable<Long> {
        public Long call() throws InterruptedException {
            Thread.sleep(1000l);
            return 1000l;
        }
    }

}
