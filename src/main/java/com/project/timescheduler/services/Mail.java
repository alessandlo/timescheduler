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

/** Mail class that allows us to connect to our mailing service, set specific content fitting the subject
 * and send mails to notify all relevant users. **/
public class Mail {
        /** This is to differentiate the type of mail that has to be sent and depending on the type, the mail structure
         * is going to be changed accordingly.**/
        public enum Type {
                create, update, remove, delete
        }

        /** This function will receive every necessary information regarding an event, before creating and sending a mail
         * including such.
         * @param creatorName Creator of event
         * @param user The current user (recipient)
         * @param name Event name
         * @param location Event location
         * @param participants All participants
         * @param recipient Email address of recipient
         * @param startDate Start date of event
         * @param endDate End date of event
         * @param startTime Start time of event
         * @param endTime End time of event
         * @param priority Priority of event
         * @param attachmentPath Attachment path
         * @param type Type of mail
         * @throws Exception **/
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
                                    String attachmentPath,
                                    Type type)throws Exception{

        System.out.println("Mail is being sent to " + recipient);

        /** Creating and inserting data into properties, as well as creating a session,
         *     in order to set up the account where our E-Mails will be sent from. **/
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.transport.protocol", "smtp");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("timeschedulermail@gmail.com", "Time2022!");
            }
        });

        /** The following shows how we create the messages such as the subject and actual content. **/
        Message message = new MimeMessage(session);
        if (type == Type.valueOf("create")) {
                System.out.println("Sending creation mail.");
                message.setSubject("[NEW] Event Invitation!");
        } else if (type == Type.valueOf("update")){
                System.out.println("Sending update mail.");
                message.setSubject("[UPDATE] Event " + name + " got updated!");
        } else if (type == Type.valueOf("remove")) {
                System.out.println("Sending removal mail.");
                message.setSubject("[REMOVAL] You got removed from an Event!");
        } else if (type == Type.valueOf("delete")) {
                System.out.println("Sending delete mail.");
                message.setSubject("[CANCEL] Event " + name + " got cancelled!");
        }
        /** This is for the String/Text that shows all the participants. **/
                String allParticipants = "";
                if (participants != null) {
                        int l = participants.size();
                        int x = 0;
                        while (x != l) {
                                String currentUser = participants.get(x);
                                allParticipants = allParticipants + currentUser + ", ";
                                x++;
                }
                allParticipants = allParticipants.substring(0, allParticipants.length() - 2);
        }
        /** Since we need to be able to send attachments in our E-Mails and not only text,
         * we need a MimeMultipart in order to include various types of content. **/
        MimeMultipart multipart = new MimeMultipart();

        /** HTML-Message, including all the information/parameters this function was given at the start. **/
        MimeBodyPart messagePart = new MimeBodyPart();
                if (type == Type.valueOf("create")) {
                        messagePart.setContent("<h3>Hello " + user + "!<br><br>"
                                + "You were included to the event: " + name + "<br><br>"
                                + "Creator: " + creatorName + "<br><br>"
                                + "Location: " + location + "<br><br>"
                                + "Participants: " + allParticipants + "<br><br>"
                                + "Priority: " + priority + "<br><br>"
                                + "Start: " + startDate + " - " + startTime + "<br><br>"
                                + "End: " + endDate + " - " + endTime + "<br><br></h3>", "text/html");
                } else if (type == Type.valueOf("update")){
                        messagePart.setContent("<h3>Hello " + user + "!<br><br>"
                                + "The event: " + name + " got updated! Look below for current details.<br><br>"
                                + "Creator: " + creatorName + "<br><br>"
                                + "Location: " + location + "<br><br>"
                                + "Participants: " + allParticipants + "<br><br>"
                                + "Priority: " + priority + "<br><br>"
                                + "Start: " + startDate + " - " + startTime + "<br><br>"
                                + "End: " + endDate + " - " + endTime + "<br><br></h3>", "text/html");
                } else if (type == Type.valueOf("remove")){
                        messagePart.setContent("<h3>Hello " + user + "!<br><br>"
                                + "You were removed from the event: " + name + "!</h3>","text/html");
                } else if (type == Type.valueOf("delete")){
                        messagePart.setContent("<h3>Hello " + user + "!<br><br>"
                                + "The event: " + name + " got fully cancelled!</h3>","text/html");
                }
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
