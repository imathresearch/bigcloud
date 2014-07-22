package com.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;

import javax.inject.Inject;

import com.core.data.MainDB;
import com.core.service.UserController;
import com.core.util.Constants;



/**
 * Implements a set of utilities to create users in the jboss 
 * @author iMath
 *
 */

public class Security {
	
	@Inject private UserController uc;
    
    public void updateSystemPassword(String userName, String password) throws Exception {
        //TODO: Refactor as soon as possible!
        eraseUserLine(Constants.ROLES_DOMAIN_FILE, userName);
        eraseUserLine(Constants.USERS_DOMAIN_FILE, userName);
        eraseUserLine(Constants.ROLES_FILE, userName);
        eraseUserLine(Constants.USERS_FILE, userName);
        createSystemUser(userName, password, "WebAppUser");
        uc.updatePassword(userName, password);
    }
    
    /*public void createLinuxUser(String userName) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(Constants.ADD_USER_LINUX, "-d",  "/home/" + userName, "-m", userName, "-g", Constants.IMATHSYSTEMGROUP);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        Process p = pb.start();
        p.waitFor();
    }*/
    
    public void createSystemUser(String userName, String password, String role) throws Exception {
        // We add the system user
        //Process p = Runtime.getRuntime().exec(Constants.ADD_USER_CLI + " -a " + userName + " " + password + " > /dev/tty");
        ProcessBuilder pb = new ProcessBuilder(Constants.ADD_USER_CLI, "-a",  userName, password);
        
      
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
                           
        Process p = pb.start();
        int signal = p.waitFor();
        
        System.out.println("SIGNAL " + signal);
        
        // We add the role of the user if role is not null
        if (role != null) {
            String line = userName + "=" + role;
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.ROLES_FILE, true), "UTF-8"));
            writer.append(line + "\n");
            writer.close();
        }
    }
    
    /*private static String loadStream(InputStream s) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=br.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }*/
    
    private void eraseUserLine(String fileName, String userName) throws Exception {
        String tempFileName = fileName + ".temp";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFileName, false), "UTF-8"));
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.length() <= userName.length()+1)
                writer.append(line + "\n");
            else {
                String sub = line.substring(0, userName.length()+1);
                if (!sub.equals(userName+"=")) {
                    writer.append(line + "\n");
                }
            }
        }
        writer.close();
        br.close();
        File tempFile = new File(tempFileName);
        File rigthFile = new File(fileName);
        tempFile.renameTo(rigthFile);
        
    }
}
