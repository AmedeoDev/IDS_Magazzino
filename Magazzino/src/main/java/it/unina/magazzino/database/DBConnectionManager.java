package it.unina.magazzino.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionManager {

    private static Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/magazzino";
    private static final String USER = "root";
    private static final String PSW ;


    static {
        Properties props = new Properties();
        try (InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("config.properties")){
            if(in == null){
                throw new RuntimeException("File config.properties non caricato correttamente" +
                        "\nApri il file e carica le tue credenziali");
            }

            props.load(in);
        } catch (IOException e){
            throw new RuntimeException("Errore nel caricamento di config.properties" + e.getMessage());
        }

        // URL = props.getProperty("db.url");
        // USER = props.getProperty("db.user");
        PSW = props.getProperty("db.password");
    }




    private DBConnectionManager(){}

    public static Connection getConnection() throws SQLException{
        if(connection == null || connection.isClosed() || !connection.isValid(2)){
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
