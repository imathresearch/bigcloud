package com.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import
java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {
    // TODO: put this into protected external files
    public static String cr1 = "bigcloud@imathresearch.com";
    public static String cr2 = "bigcloud943793072";
    
    public static void sendBasicMail(String to, String subject, String body) throws Exception {
        Session session = getSession();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(cr1));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void sendHTMLMail(String to, String subject, String htmlBody) throws Exception {
        Session session = getSession();
        try {
            Multipart mp = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(cr1));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(mp);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void sendWelcomeMail(String to, String username) throws Exception {
        String html="";
        InputStream in = this.getClass().getResourceAsStream("welcomeTemplate.html");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while((line = br.readLine()) != null) {
            html += line + "\n";
        }
        br.close();
        html = html.replace("[USERNAME]", username);
        String url = Constants.BIGCLOUD_HOST;
        if (!Constants.BIGCLOUD_PORT.equals("80")) {
            url += ":" + Constants.BIGCLOUD_PORT;
        }
        url += "/bigCloud";
        html = html.replace("[URL_BIGCLOUD]", url);
        Mail.sendHTMLMail(to, "Welcome to BigCloud", html);
    }
    
    public void sendRecoverPasswordMail(String to, String username, String newPassword) throws Exception {
        String html="";
        InputStream in = this.getClass().getResourceAsStream("recoverPassTemplate.html");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while((line = br.readLine()) != null) {
            html += line + "\n";
        }
        br.close();
        html = html.replace("[USERNAME]", username);
        html = html.replace("[PASSWORD]", newPassword);
        String url = Constants.BIGCLOUD_HOST;
        if (!Constants.BIGCLOUD_PORT.equals("80")) {
            url += ":" + Constants.BIGCLOUD_PORT;
        }
        url += "/bigCloud";
        html = html.replace("[URL_BIGCLOUD]", url);
        Mail.sendHTMLMail(to, "Recover BigCloud Password", html);
    }
    
    
    
    
    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.alwaysdata.com");
        props.put("mail.smtp.port", "587");
 
        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(cr1, cr2);
            }
          });
        return session;
    }
    
    
}
