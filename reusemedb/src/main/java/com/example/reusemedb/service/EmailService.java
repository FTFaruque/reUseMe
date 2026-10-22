package com.example.reusemedb.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email:}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        if (sendGridApiKey == null || sendGridApiKey.isEmpty() || fromEmail == null || fromEmail.isEmpty()) {
            logger.warn("SendGrid API key or sender address not configured. Email would have been sent to: {} with subject: {}", to, subject);
            return;
        }

        try {
            Email from = new Email(fromEmail); // Must be a sender verified in SendGrid
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", body);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            var response = sg.api(request);
            if (response.getStatusCode() >= 400) {
                logger.error("Error sending email. Status Code: {} Response: {}", 
                    response.getStatusCode(), response.getBody());
            } else {
                logger.info("Email sent successfully to: {}", to);
            }
        } catch (Exception ex) {
            logger.error("Error sending email: {}", ex.getMessage());
        }
    }
}