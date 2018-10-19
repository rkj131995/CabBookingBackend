package com.way2learnonline.mailer;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	private String from = "cabBooking@manh.com";
    private String host = "mailhost.manh.com";//or IP address
	public boolean sendMail( String recipient,String message)
	{
	  
	     //Get the session object  
	      Properties properties = System.getProperties();  
	      properties.setProperty("mail.smtp.host", host); 

	      Session session = Session.getDefaultInstance(properties);  
	  
	     //compose the message  
	      try{  
	         MimeMessage msg = new MimeMessage(session);  
	         msg.setFrom(new InternetAddress(from));  
	         msg.addRecipient(Message.RecipientType.TO,new InternetAddress(recipient));  
	         msg.setSubject("Night Cab Booking");  
	         msg.setText(message);  
	  
	         // Send message  
	         Transport.send(msg);  
	         System.out.println("message sent successfully....");  
	         return true;
	  
	      }catch (MessagingException mex) {mex.printStackTrace();
	      return false;
}  
	   		
	}

		
	}


