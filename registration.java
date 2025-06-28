package vote.com.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class registration extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/votingdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "samar";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get form parameters
        String fname = request.getParameter("fname");
        String cardno = request.getParameter("cardno");
        String con = request.getParameter("con");
        String address = request.getParameter("address");
        String dob = request.getParameter("dob");
        String email = request.getParameter("email");
        String pin = request.getParameter("pin_Number");

        // Basic input validation
        if (fname == null || fname.trim().isEmpty() ||
                cardno == null || cardno.trim().isEmpty() ||
                con == null || con.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                dob == null || dob.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                pin == null || pin.trim().isEmpty()) {

            out.print("All fields are required. Please fill all fields.");
            RequestDispatcher rd = request.getRequestDispatcher("registration.html");
            rd.include(request, response);
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare SQL statement
            String sql = "INSERT INTO register (fname, cardno, con, address, dob, email, pin_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(sql);

            // Set parameters
            ps.setString(1, fname.trim());
            ps.setString(2, cardno.trim());
            ps.setString(3, con.trim());
            ps.setString(4, address.trim());
            ps.setString(5, dob);
            ps.setString(6, email.trim().toLowerCase());
            ps.setString(7, pin.trim());

            // Execute update
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                out.print("<h3 style='color: green;'>Successfully your account has been created... PLEASE LOGIN</h3>");
                RequestDispatcher rd = request.getRequestDispatcher("loginpage.html");
                rd.include(request, response);
            } else {
                out.print("<h3 style='color: red;'>Failed account creation. Please try again.</h3>");
                RequestDispatcher rd = request.getRequestDispatcher("registration.html");
                rd.include(request, response);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            out.print("<h3 style='color: red;'>Database driver error. Please contact administrator.</h3>");
            RequestDispatcher rd = request.getRequestDispatcher("registration.html");
            rd.include(request, response);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());

            // Check for specific SQL errors
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                out.print("<h3 style='color: red;'>Email or Card Number already exists. Please use different credentials.</h3>");
            } else {
                out.print("<h3 style='color: red;'>Database error occurred. Please try again later.</h3>");
            }

            RequestDispatcher rd = request.getRequestDispatcher("registration.html");
            rd.include(request, response);

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            out.print("<h3 style='color: red;'>An unexpected error occurred. Please try again.</h3>");
            RequestDispatcher rd = request.getRequestDispatcher("registration.html");
            rd.include(request, response);

        } finally {
            // Close resources
            try {
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        out.close();
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}