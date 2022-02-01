package com.project.timescheduler.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Properties;

public class Mail {
        /** The function will receive every necessary info regarding an event, before creating and sending a mail. **/
        public static void sendMail(String creatorName,
                                    String user,
                                    String name,
                                    String location,
                                    ArrayList<String> participants,
                                    String recipient,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    LocalTime startTime,
                                    LocalTime endTime,
                                    Event.Priority priority,
                                    String attachmentPath)throws Exception{

        System.out.println("Mail is being sent to " + recipient);

        /** Creating and inserting data into properties, as well as creating a session,
         *     in order to set up the account where our E-Mails will be sent from. **/
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.transport.protocl", "smtp");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("timeschedulermail@gmail.com", "Time2022!");
            }
        });

        /** The following shows how we create the messages such as the subject and actual content. **/
        Message message = new MimeMessage(session);
        message.setSubject("New Event Invitation from " + creatorName);

        /** This is for the String/Text that shows all the participants. **/
        int l = participants.size();
        int x = 0;
        String allParticipants = "";
        while(x != l){
                String currentUser = participants.get(x);
                allParticipants = allParticipants + currentUser + ", ";
                x++;
        } allParticipants = allParticipants.substring(0, allParticipants.length() -2);

        /** Since we need to be able to send attachments in our E-Mails and not only text,
         * we need a MimeMultipart in order to include various types of content. **/
        MimeMultipart multipart = new MimeMultipart();

        /** HTML-Message, including all the information/parameters this function was given at the start. **/
        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setContent("<h3>Hello </h3>" + user + "<h3> You were included to the event: </h3>" + name + "<p>"
                                + "<h3>Creator: </h3>" + creatorName + "<p>"
                                + "<h3>Location: </h3>" + location + "<p>"
                                + "<h3>Participants: </h3>" + allParticipants + "<p>"
                                + "<h3>Priority: </h3>" + priority + "<p>"
                                + "<h3>Start: </h3>" + startDate + " - " + startTime + "<p>"
                                + "<h3>End: </h3>" + endDate + " - " + endTime + "<p>","text/html");

        multipart.addBodyPart(messagePart);     //Adding message to a part

        /** This is for the optional option of having an attachment. **/
        if (attachmentPath != "null"){
                MimeBodyPart attachment = new MimeBodyPart();
                attachment.attachFile(new File(attachmentPath));
                multipart.addBodyPart(attachment);      //Adding attachment to a part
        }

        message.setContent(multipart);  //Merging every part together

        /** Setting up the recipient and sending the mail to the "recipient" (an E-Mail address as String). **/
        Address address = new InternetAddress(recipient);
        message.setRecipient(Message.RecipientType.TO, address);
        Transport.send(message);
        System.out.println("Mail has been sent.");
    }
}
