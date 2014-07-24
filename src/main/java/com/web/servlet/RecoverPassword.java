package com.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.api.iMathCloud;
import com.core.data.MainDB;
import com.core.model.BC_User;
import com.core.service.UserController;
import com.core.util.Encryptor;
import com.core.util.Mail;
import com.core.util.Security;

import java.util.UUID;


public class RecoverPassword extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject UserController userController;
    @Inject MainDB db;
    @Inject Security security;
    
    
    // imathcloud943793072
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	Encryptor.init();
    	
        String eMail = request.getParameter("emailsignup");
        List<BC_User> users;
		try {
			users = db.getBC_UserDB().findByMail(eMail);
		} catch (Exception e1) {
			response.sendRedirect("recoverPasswordError.html");
        	return;
		}
        
        if(users.size() == 0){
        	response.sendRedirect("recoverPasswordError.html");
        	return;
        }
        
        // Generate an random password
        String randomPassword = UUID.randomUUID().toString();
        
        String userName = users.get(0).getUserName();
        try {
            security.updateSystemPassword(userName, randomPassword);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("loginerror.html");
            return;
        }
        
        try {
            Mail mail = new Mail();
            mail.sendRecoverPasswordMail(eMail, userName, randomPassword);
            response.sendRedirect("recoverPasswordInfo.html");
            
        } catch (Exception e) {
            // Nothing happens so far...
        }
        
             
        
        
    }
}