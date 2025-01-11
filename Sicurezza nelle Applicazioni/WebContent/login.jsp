<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Login</h1>
            <p>Log in with your account credentials</p>
        </header>
        
        <%
		    String errorMessage = (String) request.getAttribute("error");
		    if (errorMessage != null) {
		%>
		        <div id="error-message" class="error-message" style="color: red;">
		            <p><%= errorMessage %></p>
		        </div>
		        <script type="text/javascript">
		            // Nasconde il messaggio di errore dopo 5 secondi
		            setTimeout(function() {
		                var errorMessage = document.getElementById("error-message");
		                if (errorMessage) {
		                    errorMessage.style.display = "none";
		                }
		            }, 5000); // 5000 millisecondi
		        </script>
		<%
		    }
		%>
        
        <% if (request.getAttribute("successMessage") != null) { %>
		    <div style="color: green; text-align: center; margin-bottom: 10px;">
		        <%= request.getAttribute("successMessage") %>
		    </div>
		    <script>
		        // Reindirizza alla pagina di login dopo 3 secondi
		        setTimeout(function() {
		            window.location.href = "home.jsp";
		        }, 3000);
		    </script>
		<% } %>
		
        <form action="LoginServlet" method="post">
            <input type="email" name="email" placeholder="Enter your email" required>
            <input type="password" name="password" placeholder="Enter your password" required>
            
            
            <div class="remember-me">
                <input type="checkbox" id="rememberMe" name="rememberMe">
                <label for="rememberMe">Remember me</label>
            </div>
            
            <button type="submit">Login</button>
        </form>
        <footer>
            <p>Don't have an account? <a href="register.jsp">Register here</a></p>
        </footer>
    </div>
</body>
</html>
