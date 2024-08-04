package com.NK;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/StoreLocalCourierService")
@MultipartConfig(maxFileSize = 16177215) // Upload file size up to 16MB
public class Localcont extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private String dbURL = "jdbc:mysql://localhost:3306/NKcouriers";
    private String dbUser = "root";
    private String dbPass = "78587";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        int age = Integer.parseInt(request.getParameter("age"));
        String dob = request.getParameter("dob");
        String phno = request.getParameter("phno");
        String fromAddress = request.getParameter("fromAddress");
        String toAddress = request.getParameter("toAddress");
        String courierDescription = request.getParameter("courierDescription");
        
        InputStream inputStream = null; // Input stream of the upload file
        Part filePart = request.getPart("productPhoto");
        if (filePart != null) {
            inputStream = filePart.getInputStream();
        }
        
        Connection conn = null; // Connection to the database
        String message = null;  // Message will be sent back to client
        
        try {
            // Connects to the database
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Constructs SQL statement
            String sql = "INSERT INTO local_couriers (name, age, dob, phno, from_address, to_address, courier_description, product_photo) "
                       + "values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, dob);
            statement.setString(4, phno);
            statement.setString(5, fromAddress);
            statement.setString(6, toAddress);
            statement.setString(7, courierDescription);
            
            if (inputStream != null) {
                statement.setBlob(8, inputStream);
            }

            // Sends the statement to the database server
            int row = statement.executeUpdate();
            if (row > 0) {
                message = "File uploaded and saved into database";
            }
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                // Closes the database connection
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Sets the message in request scope
            request.setAttribute("Message", message);

            // Forwards to the message page
            getServletContext().getRequestDispatcher("/servicesucces.html").forward(request, response);
        }
    }
}
