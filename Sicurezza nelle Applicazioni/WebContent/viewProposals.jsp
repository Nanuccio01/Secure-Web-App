<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Proposals List</title>
    <link rel="stylesheet" href="styles.css">
    <style>
        .center {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }

        .proposals-table {
            width: 100%;
            max-width: 600px;
            border-collapse: collapse;
            margin-top: 20px;
            border: 1px solid #ddd; /* Thin border for the entire table */
        }

        .proposals-table th,
        .proposals-table td {
            padding: 10px;
            text-align: left;
            border: 1px solid #ddd; /* Thin borders between cells */
        }

        .proposals-table th {
            background-color: #4CAF50;
            color: white;
        }

        .proposals-table tr:hover {
            background-color: #f1f1f1;
        }

        .proposals-table a {
            color: #4CAF50;
            text-decoration: none;
        }

        .proposals-table a:hover {
            text-decoration: underline;
        }

        .back-home-btn {
            margin-top: 20px;
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

        .back-home-btn:hover {
            background-color: #45a049;
        }

        header h1 {
            font-family: 'Arial', sans-serif;
            color: #4CAF50; /* Title color */
            font-size: 2rem;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container center">
        <header>
            <h1>Proposals List</h1>
        </header>
        
        <%
		    if (session == null || session.getAttribute("user") == null) {
		        response.sendRedirect("login.jsp");
		        return;
		    }
		%>
        
        
        <%
            List<String[]> proposals = (List<String[]>) request.getAttribute("proposals");
            String error = (String) request.getAttribute("error");
        %>
        <% if (error != null) { %>
            <div class="error" style="color: red;">
                <%= error %>
            </div>
        <% } %>
        <% if (proposals != null && !proposals.isEmpty()) { %>
            <table class="proposals-table">
                <thead>
                    <tr>
                        <th>User Email</th>
                        <th>Proposal</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (String[] proposal : proposals) { %>
                        <tr>
                            <td><%= proposal[0] %></td>
                            <td>
                                <a href="<%= proposal[1] %>">View</a>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p style="margin-top: 20px;">No proposals available.</p>
        <% } %>
        <a href="home.jsp" class="back-home-btn">Back to Home</a>
    </div>
</body>
</html>
