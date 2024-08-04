package com.NK;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // JDBC driver name and database URL
        String jdbcUrl = "jdbc:mysql://localhost:3306/NKcouriers";
        String dbUsername = "root";
        String dbPassword = "78587";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PrintWriter out = response.getWriter();

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

            // SQL query to check if user exists
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            // Execute the query
            rs = stmt.executeQuery();

            if (rs.next()) {
                // User authenticated, create session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                // Redirect to home page or success page
                response.sendRedirect("home.html"); // Replace with your home page URL
            } else {
                // Authentication failed, redirect to error page
                response.sendRedirect("loginfail.html"); // Replace with your error page URL
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
