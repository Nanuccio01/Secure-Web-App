<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Welcome to the Home Page</h1>
        </header>
        
        <%
		    HttpSession sessioN = request.getSession(false); // Non crea una nuova sessione
		    if (session == null || session.getAttribute("user") == null) {
		        response.sendRedirect("login.jsp"); // Reindirizza alla pagina di login
		        return;
		    }
		%>
        
        <% 
		    String successMessage = (String) request.getAttribute("successMessage"); 
		    if (successMessage != null) { 
		%>
		    <div style="color: green; text-align: center; margin-bottom: 10px;">
		        <%= successMessage %>
		    </div>
		    <script>
		        // Reindirizza alla pagina di login dopo 3 secondi
		        setTimeout(function() {
		            window.location.href = "login.jsp";
		        }, 3000);
		    </script>
		<% 
		    } 
		%>
        
        <p>Welcome, <%= session.getAttribute("user") %>!</p>
        <a href="LogoutServlet"><button>Logout</button></a>
    </div>
    <footer>
        <p>&copy; 2024 Schiralli Gaetano. All Rights Reserved.</p>
    </footer>
</body>
</html>
