package com.web.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.core.util.Security;

public class ChangePassword extends HttpServlet {
    
    @Inject
    Security security;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getUserPrincipal().getName();
        String passwordOld = request.getParameter("passwordOld");
        String passwordNew = request.getParameter("passwordNew");
        String passwordNewConf = request.getParameter("passwordNewConf");
        
        System.out.println("old password " + passwordOld);
        System.out.println("new password 1 " + passwordNew);
        System.out.println("new password 2 " + passwordNewConf);
        
        
        if (!passwordNew.equals(passwordNewConf)) {
            response.sendRedirect("registererrorPasswords.html");
            return;
        }
        
        // We log out and login again to make sure that the old password is correct
        request.logout();
        
        try {
            request.login(userName, passwordOld);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("loginerror.html");
            return;
        }
        
        // Here everything is fine, so we proceed with the password change
        request.logout();
        try {
            security.updateSystemPassword(userName, passwordNew);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("loginerror.html");
            return;
        }
        
        // We log in again with the new password
        try {
            request.login(userName, passwordNew);
            response.sendRedirect("indexNew.jsp");
            return;
        } catch (Exception e) {
            response.sendRedirect("loginerror.html");
            return;
        }
        
    }
}
