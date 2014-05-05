package com.vbersh.async;

import com.vbersh.model.Person;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.h2.jdbcx.JdbcConnectionPool;

import java.util.List;
import javax.sql.DataSource;

import java.util.Arrays;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * http://commons.apache.org/proper/commons-dbutils/examples.html
 */
public class AsyncQuery<T> {

    private DataSource ds;
    private ExecutorService es;
    private String query;
    private Object[] params;

    public AsyncQuery(DataSource ds, ExecutorService es, String query, Object... params) {
        this.ds = ds;
        this.es = es;
        this.query = query;
        this.params = params;
    }

    private Future<List<T>> getFuture(Class<T> type) throws SQLException {
        AsyncQueryRunner run = new AsyncQueryRunner(es, new QueryRunner(ds));
        ResultSetHandler<List<T>> h = new BeanListHandler<>(type);
        return run.query(query, h, params);
    }

    public static void main(String... args) throws SQLException {
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:db/sample.db;MULTI_THREADED=1", "sa", "");

        ExecutorService es = Executors.newFixedThreadPool(2);

        AsyncQuery<Person> query1 = new AsyncQuery<>(ds, es, "SELECT a.id from person a left join (select id from person) b on a.id=b.id");
        AsyncQuery<Person> query2 = new AsyncQuery<>(ds, es, "SELECT a.name from person a left join (select id from person) b on a.id=b.id");

        long start = System.currentTimeMillis();

        Future<List<Person>> future1 = query1.getFuture(Person.class);
        Future<List<Person>> future2 = query2.getFuture(Person.class);

        List<Person> persons1 = Futures.get(future1);
        List<Person> persons2 = Futures.get(future2);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("elapsed: " + elapsed);

        System.out.println("persons1: "+Arrays.toString(persons1.toArray()));
        System.out.println("persons2: "+Arrays.toString(persons2.toArray()));

        es.shutdown();
    }
}
