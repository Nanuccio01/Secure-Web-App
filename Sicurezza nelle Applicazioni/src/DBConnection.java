import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String dbDriver;

    // Caricamento delle configurazioni dal file config
    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Unable to find db.properties");
            }
            
            // Carica il file di configurazione
            Properties properties = new Properties();
            properties.load(input);

            // Ottieni i valori dal file
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
            dbDriver = properties.getProperty("db.driver");

            // Carica il driver del database
            Class.forName(dbDriver);
            System.out.println("Driver JDBC caricato con successo.");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file db.properties: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC non trovato: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Metodo per ottenere una connessione al database
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connessione al database riuscita.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Errore durante la connessione al database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Metodo per chiudere la connessione
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connessione al database chiusa correttamente.");
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
            }
        }
    }
}

