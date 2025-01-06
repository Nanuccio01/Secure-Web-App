import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Ottieni la sessione corrente, se esiste
        HttpSession session = request.getSession(false);

        if (session != null) {
            // Invalida la sessione
            session.invalidate();
        }

        // Rimuovi eventuali cookie "rememberMe"
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (cookie.getName().equals("rememberMe")) {
                    cookie.setMaxAge(0); // Elimina il cookie
                    cookie.setPath("/"); // Imposta lo stesso percorso
                    response.addCookie(cookie); // Aggiunge il cookie eliminato alla risposta
                }
            }
        }

        // Imposta il messaggio di successo
        request.setAttribute("successMessage", "Logout effettuato con successo!");
        
        // Reindirizza alla pagina di login
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}

