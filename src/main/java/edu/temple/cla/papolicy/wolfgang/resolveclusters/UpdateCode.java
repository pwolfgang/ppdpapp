/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.papolicy.wolfgang.resolveclusters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.web.HttpRequestHandler;

/**
 *
 * @author Paul Wolfgang
 */
public class UpdateCode implements HttpRequestHandler {
    
    private DataSource dataSource;
    
    public UpdateCode() {}
    
    public UpdateCode(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private static final String updateQuery = 
            "update Bills_Data_1977_12 set code=? where ID=?";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ID = request.getParameter("ID");
        String code = request.getParameter("code");
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, code);
            stmt.setString(2, ID);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new ServletException("SQL Error",ex);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {/* ignore */}
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {/* ignore */}
        } 
    }

}
