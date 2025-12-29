package passvault.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@passvault.com}")
    private String fromAddress;

    @Value("${notification.email.simulate:true}")
    private boolean simulateEmail;

    public void sendEmail(String to, String subject, String body) {
        if (simulateEmail) {
            log.info("=== SIMULATED EMAIL ===");
            log.info("To: {}", to);
            log.info("From: {}", fromAddress);
            log.info("Subject: {}", subject);
            log.info("Body:\n{}", body);
            log.info("=======================");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendWelcomeEmail(String email, String username) {
        String subject = "Welcome to PassVault!";
        String body = String.format(
                "Hello %s,\n\n" +
                        "Welcome to PassVault! Your account has been created successfully.\n\n" +
                        "You can now securely store and manage your passwords.\n\n" +
                        "Best regards,\n" +
                        "The PassVault Team",
                username != null ? username : "there");
        sendEmail(email, subject, body);
    }

    public void sendPasswordChangedEmail(String email) {
        String subject = "Password Changed - PassVault";
        String body = "Hello,\n\n" +
                "Your password has been changed successfully.\n\n" +
                "If you did not make this change, please contact support immediately.\n\n" +
                "Best regards,\n" +
                "The PassVault Team";
        sendEmail(email, subject, body);
    }
}
