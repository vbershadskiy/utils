package com.vbersh.dbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * http://www.h2database.com/html/grammar.html#create_trigger
 * http://code.google.com/p/h2database/source/browse/trunk/h2/src/test/org/h2/samples/TriggerSample.java?r=759
 *
 * /home/soft/jdk1.7.0/bin/java -jar ~/.m2/repository/com/h2database/h2/1.3.175/h2-1.3.175.jar -web -webAllowOthers -tcp -tcpAllowOthers
 * http://192.168.1.9:8082/
 * jdbc:h2:tcp://192.168.1.9:9092/~/gitrepos/utils/db/sample.db
 */
public class H2Sample {

    public static void main(String[] args) throws ClassNotFoundException {
        // load the h2-JDBC driver using the current class loader
        Class.forName("org.h2.Driver");

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:h2:db/sample.db", "sa", "");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id int, name varchar)");
            statement.executeUpdate("insert into person values(1, 'leo4')");
            statement.executeUpdate("insert into person values(2, 'yui4')");

            for(int i=3;i<10000;i++) {
                statement.executeUpdate("insert into person values("+i+", 'yui4')");
            }

            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch(SQLException e) {
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

}
