package com.foo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

//
// Here are the dbcp-specific classes.
// Note that they are only used in the setupDataSource
// method. In normal use, your classes interact
// only with the standard JDBC API
//
import org.apache.commons.dbcp2.BasicDataSource;

//
// Here's a simple example of how to use the BasicDataSource.
//

//
// Note that this example is very similar to the PoolingDriver
// example.

//
// To compile this example, you'll want:
//  * commons-pool-2.3.jar
//  * commons-dbcp-2.1.jar 
// in your classpath.
//
// To run this example, you'll want:
//  * commons-pool-2.3.jar
//  * commons-dbcp-2.1.jar 
//  * commons-logging-1.2.jar
// in your classpath.
//
//
// Invoke the class using two arguments:
//  * the connect string for your underlying JDBC driver
//  * the query you'd like to execute
// You'll also want to ensure your underlying JDBC driver
// is registered.  You can use the "jdbc.drivers"
// property to do this.
//
// For example:
//  java -Djdbc.drivers=org.h2.Driver \
//       -classpath commons-pool2-2.3.jar:commons-dbcp2-2.1.jar:commons-logging-1.2.jar:h2-1.3.152.jar:. \
//       BasicDataSourceExample \
//       "jdbc:h2:~/test" \
//       "SELECT 1"
//
public class BasicDataSourceExample {

    public static void main(String[] args) {
    	
        // First we set up the BasicDataSource.
        // Normally this would be handled auto-magically by
        // an external configuration, but in this example we'll
        // do it manually.
        //
        System.out.println("Setting up data source.");
        DataSource dataSource = setupDataSource();
        System.out.println("Done.");

        //
        // Now, we can use JDBC DataSource as we normally would.
        //
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
        	
            System.out.println("Creating connection.");
            conn = dataSource.getConnection();
            System.out.println("Creating statement.");
            stmt = conn.createStatement();
            System.out.println("Executing statement.");
            rset = stmt.executeQuery("select * from DAT_tea");
            System.out.println("Results:");
            int numcols = rset.getMetaData().getColumnCount();
            while(rset.next()) {
                for(int i=1;i<=numcols;i++) {
                    System.out.print("\t" + rset.getString(i));
                }
                System.out.println("");
            }
        } catch(SQLException e) {
        	
            e.printStackTrace();
            
        } finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
    }

    public static void printDataSourceStats(DataSource ds) {
    	
        BasicDataSource bds = (BasicDataSource) ds;
        System.out.println("NumActive: " + bds.getNumActive());
        System.out.println("NumIdle: " + bds.getNumIdle());
    }

    public static void shutdownDataSource(DataSource ds) throws SQLException {
    	
        BasicDataSource bds = (BasicDataSource) ds;
        bds.close();
    }
    
    public static DataSource setupDataSource() {
    	
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost/matcha");
        ds.setUsername("root");
        ds.setPassword("secret3!");
        return ds;
    }
}