<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="javax.servlet.http.*,java.util.*,java.io.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        /* Regolazioni di layout per pagina home */
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 20px;
            padding: 20px;
            margin: 0 auto;
            max-width: 800px;
        }

        h1 {
            text-align: center;
            color: green;
            margin-bottom: 20px;
        }

        .section {
            width: 100%;
            text-align: center;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .section h2 {
            margin-bottom: 10px;
            font-size: 1.5em;
            color: #333;
        }

        .section button {
            padding: 10px 20px;
            font-size: 1em;
            color: white;
            background-color: green;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .section button:hover {
            background-color: #004d00;
        }

        footer {
            margin-top: 30px;
            text-align: center;
            font-size: 0.9em;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Welcome to the Home Page</h1>
            
            <!-- Script che gestisce il Logout -->
            <%
			    session = request.getSession(false);
			    if (session == null || session.getAttribute("user") == null) {
			        if (request.getAttribute("logoutMessage") != null) { 
			%>
			        <div style="color: green; text-align: center; margin-bottom: 10px;">
			            <%= request.getAttribute("logoutMessage") %>
			        </div>
			        <script>
			            // Reindirizza alla pagina di login dopo 3 secondi
			            setTimeout(function() {
			                window.location.href = "login.jsp";
			            }, 3000);
			        </script>
			<%
			        } else {
			        	response.sendRedirect("login.jsp");
				        return;
			        }
			    }
			    String currentUser = (String) session.getAttribute("user");
			%>

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
			        // Ricarica la pagina home dopo 3 secondi
			        setTimeout(function() {
			            window.location.href = "home.jsp";
			        }, 3000);
			    </script>
			<% } %>
            
            <p>Welcome, <b><%= currentUser %></b>!</p>
        </header>

        <!-- Caricamento proposta progettuale -->
        <div class="section">
            <h2>Choose and upload your project proposal (Max 10MB)</h2>
            <form action="UploadProposalServlet" method="post" enctype="multipart/form-data">
                <input type="file" name="proposalFile" accept=".txt" required>
                <button type="submit">Upload the project proposal</button>
            </form>
        </div>
        
        <!-- Visualizza proposte -->
        <div class="section">
            <h2>View all project proposals uploaded by colleagues</h2>
            <a href="ViewProposalsServlet">
                <button>Show all project proposals</button>
            </a>
        </div>

        <!-- Logout -->
        <div class="section">
            <h2>Exit from your account</h2>
            <a href="LogoutServlet">
                <button>Logout</button>
            </a>
        </div>
    </div>

    <footer>
        <p>&copy; 2024 Schiralli Gaetano. All Rights Reserved.</p>
    </footer>
</body>
</html>

