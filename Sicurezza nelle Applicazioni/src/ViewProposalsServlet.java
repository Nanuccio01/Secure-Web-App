import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.transport.commons_text.StringEscapeUtils;

@WebServlet("/ViewProposalsServlet")
public class ViewProposalsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        // Verifica autenticazione utente
        String userEmail = (String) request.getSession().getAttribute("user");
        if (userEmail == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Recupera azione richiesta
        String action = request.getParameter("action");
        if ("viewProposal".equals(action)) {
            // Visualizza una proposta specifica
            String proposalId = request.getParameter("proposalId");
            if (proposalId != null && !proposalId.isEmpty()) {
                showProposal(request, response, proposalId);
            } else {
                request.setAttribute("error", "Missing proposal ID.");
                request.getRequestDispatcher("home.jsp").forward(request, response);
            }
        } else {
            // Visualizza tutte le proposte
            showAllProposals(request, response);
        }
    }

    private void showAllProposals(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String[]> proposals = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT email, project_proposals FROM users");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String email = rs.getString("email");
                byte[] blobData = rs.getBytes("project_proposals");

                if (blobData != null) {
                    proposals.add(new String[]{email, "ViewProposalsServlet?action=viewProposal&proposalId=" + email});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error while displaying the proposals.");
            request.getRequestDispatcher("home.jsp").forward(request, response);
            return;
        }

        request.setAttribute("proposals", proposals);
        request.getRequestDispatcher("viewProposals.jsp").forward(request, response);
    }

    private void showProposal(HttpServletRequest request, HttpServletResponse response, String proposalId) throws ServletException, IOException {
        String proposalContent = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT project_proposals FROM users WHERE email = ?")) {

            // Verifica la validità dell'input per evitare SQL Injection
            if (proposalId == null || proposalId.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid proposal ID.");
            }

            pstmt.setString(1, proposalId.trim()); // Evita spazi e input non validi

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] blobData = rs.getBytes("project_proposals");

                    if (blobData != null) {
                        // Converti i byte in una stringa e sanitizza per prevenire XSS
                        proposalContent = sanitizeContent(new String(blobData, "UTF-8"));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error while retrieving the proposal.");
            request.getRequestDispatcher("home.jsp").forward(request, response);
            return;
        }

        if (proposalContent != null) {
            request.setAttribute("proposalContent", proposalContent);
            request.getRequestDispatcher("viewSingleProposal.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Proposal not found.");
            request.getRequestDispatcher("home.jsp").forward(request, response);
        }
    }

	private String sanitizeContent(String content) {
	    if (content == null) {
	        return null;
	    }
	    // Usa Apache Commons Text per escapare i caratteri HTML
	    return org.apache.commons.text.StringEscapeUtils.escapeHtml4(content);
	}

}

