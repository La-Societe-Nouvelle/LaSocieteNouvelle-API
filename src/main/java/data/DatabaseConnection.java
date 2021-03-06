/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author SylvainPro
 */
public class DatabaseConnection {
    
    private final Connection connection;
    
    private Statement statement;
    private ResultSet resultSet;
    
    /* ---------- Constructor ---------- */
    
    public DatabaseConnection() 
    {
        connection = getConnection();
        sendQueryPoint();
    }
    
    // Initialize the connection to the database
    private static Connection getConnection() 
    {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/postgres");
            Connection connection = ds.getConnection();
            return connection;
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void sendQueryPoint() 
    {
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");  
        Date date = new Date();  
        
        String query = "INSERT INTO stats.queryPoints (queryDate,queryTime) VALUES "
                + "('"+formatterDate.format(date)+"','"+formatterTime.format(date)+"');";
        
        try {
            statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    /* ---------- execution ---------- */
    
    // Execute the query and return the resultset - SELECT
    public ResultSet executeQuery(String query) throws SQLException 
    {
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);
        return resultSet;
    }
    
    /* ---------- close ---------- */
    
    // Close the connection to the database
    public void close() 
    {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
