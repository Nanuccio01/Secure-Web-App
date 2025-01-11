import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import utils.KeyManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@WebFilter("/*") // Applica il filtro a tutte le richieste
public class RememberMeFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione, se necessaria
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Se la sessione è già attiva e ha "user", nessun problema
        HttpSession session = httpRequest.getSession(false);
        String user = (session != null) ? (String) session.getAttribute("user") : null;

        // Se l'utente non è in sessione, controlla il cookie "rememberMe"
        if (user == null) {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("rememberMe".equals(cookie.getName())) {
                    	try {
	                        String decryptedEmail = decrypt(cookie.getValue());
	                        if (decryptedEmail != null && isValidUser(decryptedEmail)) {
	                            // Autentica l'utente e crea la sessione
	                            session = httpRequest.getSession(true);
	                            session.setAttribute("user", decryptedEmail);
	                            session.setMaxInactiveInterval(15 * 60); // Timeout di 15 minuti
	                            break;
	                        }
                    	} catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        // Passa il controllo alla prossima risorsa (JSP o servlet)
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Pulizia, se necessaria
    }

 // Metodo per convalidare l'email nel database
    private static boolean isValidUser(String email) {
        if (email == null || email.isEmpty()) {
            return false; // Email non valida
        }

        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1); // Ottiene il numero di righe trovate
                    return count > 0; // Se almeno una riga esiste, l'utente è valido
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Se qualcosa va storto, ritorna false
    }

    // Metodo per decrittografare il valore del cookie
    private static String decrypt(String encryptedData) throws Exception {
        // Carica la chiave segreta (deve essere la stessa usata per la crittografia)
        SecretKey secretKey = KeyManager.loadSecretKey();

        // Configura il cifrario in modalità DECRYPT_MODE
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decodifica il Base64 e decrittografa i dati
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedBytes);

        return new String(decryptedData); // Converte i byte decifrati in stringa
    }
}
