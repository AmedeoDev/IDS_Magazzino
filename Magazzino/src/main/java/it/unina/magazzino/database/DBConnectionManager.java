package it.unina.magazzino.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {

    private static Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/magazzino";
    private static final String USER = "root";
    private static final String PSW = "m1aSequenza!";

    private DBConnectionManager(){}

    public static Connection getConnection() throws SQLException{
        if(connection == null || connection.isClosed()){
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PSW);
                System.out.println("Connesione al db effettuata!");
            } catch (ClassNotFoundException e){
                throw new SQLException("Driver non trovato: " + e.getMessage());
            }
        }
        return connection;
    }
}
