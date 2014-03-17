package com.vbersh.dbs;

import org.sqlite.SQLiteJDBCLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * http://code.google.com/p/sqlite-jdbc/wiki/Introduction
 * http://www.javaworkspace.com/connectdatabase/connectSQLite.do
 * http://www.sqlite.org/lang_createtrigger.html
 */
public class SQLiteSample {
    public static void main(String[] args) throws ClassNotFoundException {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:db/sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo2')");
            statement.executeUpdate("insert into person values(2, 'yui2')");
            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally  {
            try  {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    void test() {
        System.out.println(String.format("running in %s mode", SQLiteJDBCLoader.isNativeMode() ? "native" : "pure-java"));
    }

}