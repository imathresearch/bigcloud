package com.web.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;

import com.api.iMathCloud;
import com.core.util.Encryptor;
import com.core.util.Mail;
import com.core.service.UserController;

public class Register extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject 
    UserController userController;
    // imathcloud943793072
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String userName = request.getParameter("usernamesignup");
        String password = request.getParameter("passwordsignup");
        String passwordRep = request.getParameter("passwordsignup_confirm");
        String eMail = request.getParameter("emailsignup");
             
        if (!password.equals(passwordRep)) {
            response.sendRedirect("registererrorPasswords.html");
            return;
        }
                
        try{
        	iMathCloud.registerUser(userName, password, eMail);
        	String firstName = null;
        	String lastName = null;
        	String organization = null;
        	
        	// Initialisation of Encryptor to encrypt the user password in the database
        	Encryptor.init();
        	/*System.setProperty("jasypt_password", "jasyptkey");
        	String passwordEncryptor = System.getProperty("jasypt_password");

            StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
            strongEncryptor.setPassword(passwordEncryptor);
            org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry registry =
                    org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry.getInstance();
            registry.registerPBEStringEncryptor("STRING_ENCRYPTOR", strongEncryptor);*/
            
        	userController.createUser(userName, firstName, lastName, organization, eMail, password);
        }
        catch(Exception e){
        	response.sendRedirect("registererror.html");
            return;
        }
       
        try {
            Mail mail = new Mail();
            mail.sendWelcomeMail(eMail, userName);
        } catch (Exception e) {
            // Nothing happens so far...
        }
        try {
            request.login(userName, password);
            response.sendRedirect("indexNew.jsp");
            return;
        } catch(ServletException e) {
            response.sendRedirect("loginerror.html");
            return;
        }
        
    }
}
