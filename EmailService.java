package com.example.ccms_1.Services;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendComplaintRaisedMailToAdmin(
            String adminEmail,
            String userEmail,
            Long complaintId
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("complaintservice184@gmail.com");
            message.setReplyTo(userEmail);
            message.setTo(adminEmail);
            message.setSubject("New Complaint Assigned");
            message.setText(
                    "Complaint ID: " + complaintId +
                            "\nRaised by: " + userEmail
            );
            mailSender.send(message);
        } catch (MailException ex) {
            logMailFailure(adminEmail, complaintId);
        }
    }

    @Async
    public void sendComplaintResolvedMailToUser(
            String userEmail,
            Long complaintId
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("complaintservice184@gmail.com");
            message.setTo(userEmail);
            message.setSubject("Complaint Resolved");
            message.setText(
                    "Your complaint with ID " + complaintId + " has been resolved."
            );
            mailSender.send(message);
        } catch (MailException ex) {
            logMailFailure(userEmail, complaintId);
        }
    }

    @Async
    public void sendSystemHiringAlertMail(
            String systemEmail,
            Long complaintId,
            String location,
            String serviceType
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("complaintservice184@gmail.com");
            message.setTo(systemEmail);
            message.setSubject("⚠ Admin Required – Escalated Complaint");
            message.setText(
                    "Complaint ID: " + complaintId +
                            "\nLocation: " + location +
                            "\nService Type: " + serviceType
            );
            mailSender.send(message);
        } catch (MailException ex) {
            logMailFailure(systemEmail, complaintId);
        }
    }

    @Async
    public void sendUserWaitNotificationMail(
            String userEmail,
            Long complaintId
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("complaintservice184@gmail.com");
            message.setTo(userEmail);
            message.setSubject("Complaint Escalated – Please Wait");
            message.setText(
                    "Your complaint with ID " + complaintId +
                            " has been escalated and is awaiting reassignment."
            );
            mailSender.send(message);
        } catch (MailException ex) {
            logMailFailure(userEmail, complaintId);
        }
    }

    @Async
    public void sendSystemHiringAlertMailWithDelay(
            String systemEmail,
            Long complaintId,
            String description,
            String userName,
            String userEmail,
            String location,
            String serviceType,
            int delayMinutes
    ) {
        try {
            Thread.sleep(delayMinutes * 60 * 1000L);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("complaintservice184@gmail.com");
            message.setTo(systemEmail);
            message.setSubject("⚠ Admin Required – No Admins Available");

            message.setText(
                    "A new complaint has been raised, but no admins are available.\n\n" +
                            "Complaint ID: " + complaintId + "\n" +
                            "Description: " + description + "\n" +
                            "User Name: " + userName + "\n" +
                            "User Email: " + userEmail + "\n" +
                            "Location: " + location + "\n" +
                            "Service Type: " + serviceType + "\n\n" +
                            "Please hire/assign an admin as per the requirement."
            );

            mailSender.send(message);

        } catch (Exception ex) {
            System.err.println("System alert mail failed for complaint: " + complaintId);
        }
    }

    private void logMailFailure(String email, Long complaintId) {
        System.err.println("Mail failed: " + email + " | Complaint: " + complaintId);
    }
}

