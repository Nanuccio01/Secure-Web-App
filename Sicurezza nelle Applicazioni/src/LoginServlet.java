import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.InputSanitizer;
import utils.KeyManager;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	// Recupero dei dati dal form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Trasforma la mail tutta in minuscolo
        email = email.toLowerCase();

        // Verifica la validità dell'email
        if (!InputSanitizer.isValidEmail(email)) {
            request.setAttribute("error", "Invalid email format.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        // Verifica che la password non contenga caratteri non validi
        String passwordValidationError = InputSanitizer.noInvalidCharacters(password);
        if (passwordValidationError != null) {
            request.setAttribute("error", passwordValidationError);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Controllo delle credenziali utente
        if (authenticateUser(email, password)) {
            // Creazione di una nuova sessione sicura
            HttpSession session = request.getSession();
            if (!session.isNew()) {
                session.invalidate(); // Invalida eventuali sessioni esistenti
                session = request.getSession(true); // Crea una nuova sessione
            }
            if ("on".equals(rememberMe)) {
                // Creazione del cookie con crittografia
                try {
                    String encryptedEmail = encrypt(email); 
                    Cookie rememberMeCookie = new Cookie("rememberMe", encryptedEmail);
                    rememberMeCookie.setHttpOnly(true); // Protegge il cookie da accessi JavaScript
                    rememberMeCookie.setSecure(true); // Assicura che il cookie venga inviato solo su HTTPS
                    rememberMeCookie.setPath("/"); // Disponibile in tutto il sito
                    rememberMeCookie.setMaxAge(60 * 60 * 24); // Timeout di 1 giorni
                    rememberMeCookie.setComment("SameSite=Strict"); // SameSite=Strict per maggiore sicurezza

                    response.addCookie(rememberMeCookie);
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Error encrypting cookie data" + e.getMessage());
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                }
            }
            session.setAttribute("user", email);
            session.setMaxInactiveInterval(15 * 60); // Timeout di 15 minuti
            
            // Login completato con successo
            request.setAttribute("successMessage", "Login completed successfully!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Credenziali errate
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
    }

    private boolean authenticateUser(String email, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT password_hash FROM users WHERE email = ?")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return BCrypt.checkpw(password, storedHash);
                } 
            }
        } catch (SQLException e) {
            // Log dell'errore per il debug del server
            System.err.println("SQL error during authentication " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private String encrypt(String data) throws Exception {
        SecretKey secretKey = KeyManager.loadSecretKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }
}

