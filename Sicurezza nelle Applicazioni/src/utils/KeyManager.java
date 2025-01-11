package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import javax.crypto.SecretKey;
import java.util.Properties;

public class KeyManager {

    private static String keystorePath;
    private static String keystorePassword;
    private static String keyAlias;

    // Caricamento delle proprietà al caricamento della classe
    static {
        loadProperties();
    }

    private static void loadProperties() {
    	try (InputStream input = KeyManager.class.getClassLoader().getResourceAsStream("cookie.properties")) {
            if (input == null) {
                throw new IOException("Unable to find cookie.properties");
            }
            Properties properties = new Properties();
            properties.load(input);
            keystorePath = properties.getProperty("keystore.path");
            keystorePassword = properties.getProperty("keystore.password");
            keyAlias = properties.getProperty("key.alias");

            if (keystorePath == null || keystorePassword == null || keyAlias == null) {
                throw new IllegalArgumentException("Properties file missing required fields");
            }
        } catch (IOException e) {
            System.err.println("Errore durante il caricamento del file di configurazione: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties file", e);
        }
    }


    // Metodo per caricare la chiave segreta dal keystore
    public static SecretKey loadSecretKey() throws Exception {
        if (keystorePath == null || keystorePassword == null || keyAlias == null) {
            throw new IllegalStateException("Keystore properties not loaded correctly");
        }

        // Carica il keystore
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(fis, keystorePassword.toCharArray());

            // Recupera la chiave segreta
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(
                keyAlias,
                new KeyStore.PasswordProtection(keystorePassword.toCharArray())
            );

            if (keyEntry == null) {
                throw new Exception("Key alias not found in keystore");
            }

            return keyEntry.getSecretKey();
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento della chiave dal keystore: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rilancia l'eccezione per una migliore tracciabilità
        }
    }
}



