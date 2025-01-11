import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.tika.Tika;

@WebServlet("/UploadProposalServlet")
@MultipartConfig(maxFileSize = 1024 * 1024 * 10) // Limite massimo di 10 MB
public class UploadProposalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        // Recupera l'utente autenticato dalla sessione
        String userEmail = (String) request.getSession().getAttribute("user");
        if (userEmail == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Recupera il file caricato
        Part filePart = request.getPart("proposalFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("error", "No file selected or file is empty.");
            request.getRequestDispatcher("home.jsp").forward(request, response);
            return;
        }

        // Analisi e validazione del file con Apache Tika
        Tika tika = new Tika();
        String detectedType;
        try (InputStream fileContent = filePart.getInputStream()) {
            // Analizza il tipo MIME del file
            detectedType = tika.detect(fileContent);
            // Ottieni il nome del file
            String fileName = filePart.getSubmittedFileName();

            // Verifica se il file è del tipo atteso (ad esempio: plain text)
            if (!"text/plain".equals(detectedType) || !fileName.toLowerCase().endsWith(".txt")) {
                request.setAttribute("error", "Invalid file type. Only .txt files are allowed.");
                request.getRequestDispatcher("home.jsp").forward(request, response);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during file validation.");
            request.getRequestDispatcher("home.jsp").forward(request, response);
            return;
        }

        // Protezione contro la vulnerabilità TOCTOU
        synchronized (this) {
            try (InputStream fileContent = filePart.getInputStream()) {
                // Salva il file nel database
                if (saveProposalToDatabase(userEmail, fileContent)) {
                    request.setAttribute("successMessage", "Proposal uploaded successfully.");
                } else {
                    request.setAttribute("error", "Error saving the proposal.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "An error occurred: " + e.getMessage());
            }
        }

        // Torna alla home.jsp con il messaggio
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }

    private boolean saveProposalToDatabase(String email, InputStream proposalContent) {
        String sql = "UPDATE users SET project_proposals = ? WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBlob(1, proposalContent);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

