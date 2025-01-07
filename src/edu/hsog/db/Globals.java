package edu.hsog.db;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author student
 */
public class Globals {

    static String url = "jdbc:oracle:thin:@//localhost:1521/FREE";
    // static String url = "jdbc:mariadb://localhost:1521/chris";
    static String username = "chris";
    static String passwd = "xyz";

    static private BasicDataSource conPool = null;

    public static void initConnectionPool() {
        if(conPool==null){
            System.out.println("Account: " + username + ":" + passwd);
            conPool = new BasicDataSource();
            conPool.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            // conPool.setDriverClassName("org.mariadb.jdbc.Driver");
            conPool.setUrl(url);
            conPool.setUsername(username);
            conPool.setPassword(passwd);
            conPool.setMaxTotal(5);
            conPool.setInitialSize(5);
        }
    }

    public static Connection getPoolConnection() {
        Connection v_connection = null;
        try {
            v_connection = conPool.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
        }
        return v_connection;
    }
}
