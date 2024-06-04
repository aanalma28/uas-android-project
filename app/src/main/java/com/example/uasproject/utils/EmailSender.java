package com.example.uasproject.utils;

import android.util.Log;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.os.AsyncTask;

public class EmailSender extends AsyncTask<Void, Void, Void>{

    private String email, subject, msgBody;

    public EmailSender(String email, String subject, String msgBody){
        this.email = email.trim();
        this.subject = subject;
        this.msgBody = msgBody;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            if (!isValidEmail(email)) {
                Log.e("Email", "Invalid email address: " + email);
                return null;
            }
//            Setup mail server
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587"); // Use port 587 for TLS
            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

//            Authenticate the session
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator(){
                        protected PasswordAuthentication getPasswordAuthentication(){
                            return new PasswordAuthentication("croft.idn@gmail.com", "acja ylaj uejg aedo");
                        }
                    });

//            Create a default MimeMessage object
            Message message = new MimeMessage(session);

//            Set From: Header field
            message.setFrom(new InternetAddress("croft.idn@gmail.com"));

//            Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

//            Set Subject: header field
            message.setSubject(subject);

//            Set message body
            message.setText(msgBody);

//            send message
            Transport.send(message);

            Log.d("Email", "Email sent successfully.");

        }catch(Exception e){
            Log.e("Email", "Error sending email" + e.getMessage());
        }

        return null;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}
