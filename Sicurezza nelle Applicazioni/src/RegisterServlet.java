import java.io.*;
import utils.InputSanitizer;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.MultipartConfig;
import org.apache.tika.Tika;
import org.mindrot.jbcrypt.BCrypt;

@MultipartConfig(maxFileSize = 1024 * 1024 * 5) //Limite dimensione delle immagini a 5MB

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        // Recupera dati dal form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Verifica se i parametri sono nulli
        if (email == null || password == null || confirmPassword == null) {
        	request.setAttribute("error", "Missing required fields or image size exceeds the maximum limit of 5 MB.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Verifica la validità dell'email
        if (!InputSanitizer.isValidEmail(email)) {
            request.setAttribute("error", "Invalid email format.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Verifica la validità della password
        String passwordValidationError = InputSanitizer.isValidPassword(password);
        if (passwordValidationError != null) {
            request.setAttribute("error", passwordValidationError);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        // Verifica che la password non contenga caratteri non validi
        String passwordCharError = InputSanitizer.noInvalidCharacters(password);
        if (passwordCharError != null) {
            request.setAttribute("error", passwordCharError);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Verifica che la password e la conferma siano identiche
        if (!password.equals(confirmPassword)) {
        	request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Genera l'hash della password con BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        
        // Caricamento dell'immagine del profilo
        Part profileImagePart = request.getPart("profileImage");
        InputStream profileImageInputStream = null;
        if (profileImagePart != null && profileImagePart.getSize() > 0) {
        	// Mitigazione TOCTOU: Lavora direttamente sull'InputStream senza passare da risorse temporanee
            profileImageInputStream = profileImagePart.getInputStream();
            // Usa Apache Tika per validare il tipo MIME
            Tika tika = new Tika();
            String mimeType = tika.detect(profileImageInputStream);
            // Permette solo tipi di file immagine validi
            String fileName = profileImagePart.getSubmittedFileName();
            if ((!mimeType.equals("image/png") && !mimeType.equals("image/jpeg")) ||
            (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg"))) {
                 request.setAttribute("error", "Invalid file. Only PNG, JPG, and JPEG images are allowed.");
                 request.getRequestDispatcher("register.jsp").forward(request, response);
                 return;
            }
            // Riporta l'InputStream nella posizione iniziale per il caricamento nel DB
            profileImageInputStream = profileImagePart.getInputStream();
            } else {
            	request.setAttribute("error", "Profile Image required.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Verifica che l'email non sia già registrata
            String checkQuery = "SELECT email FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
            	request.setAttribute("error", "Email already registered.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            // Inserisci l'utente nel database
            String insertQuery = "INSERT INTO users (email, password_hash, profile_image) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, email);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setBlob(3, profileImageInputStream);
                insertStmt.executeUpdate();
            }
            // Registrazione completata con successo
            request.setAttribute("successMessage", "Registration completed successfully!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore nella connessione al database: " + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
    }
}
