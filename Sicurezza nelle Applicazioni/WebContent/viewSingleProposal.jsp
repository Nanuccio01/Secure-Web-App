<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Proposal Details</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            width: 90%;
            max-width: 600px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
        }

        header h1 {
            font-family: 'Arial', sans-serif;
            color: #4CAF50; /* Title color */
            font-size: 2rem;
            margin-bottom: 20px;
            text-align: center;
        }

        .proposal-content {
            white-space: pre-wrap;
            background-color: #f4f4f4;
            padding: 15px;
            border: 1px solid #ddd;
            margin: 20px 0;
            width: 100%;
            font-family: 'Courier New', Courier, monospace;
            border-radius: 5px;
            overflow-wrap: break-word;
        }

        .error {
            color: red;
            font-weight: bold;
            margin: 20px 0;
        }

        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }

        .button-group a {
            padding: 12px 20px;
            font-size: 1rem;
            background-color: #4CAF50;
            color: #fff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            font-family: 'Arial', sans-serif;
        }

        .button-group a:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>Proposal Details</h1>
        </header>
        
        <%
		    if (session == null || session.getAttribute("user") == null) {
		        response.sendRedirect("login.jsp");
		        return;
		    }
		%>
		
        <%
            String proposalContent = (String) request.getAttribute("proposalContent");
            String error = (String) request.getAttribute("error");
        %>
        <% if (error != null) { %>
            <div class="error">
                <%= error %>
            </div>
        <% } else if (proposalContent != null) { %>
            <div class="proposal-content">
                <%= proposalContent %>
            </div>
        <% } else { %>
            <p>No proposal found.</p>
        <% } %>
        <div class="button-group">
            <a href="ViewProposalsServlet">Back to Proposals List</a>
            <a href="home.jsp">Back to Home</a>
        </div>
    </div>
</body>
</html>
