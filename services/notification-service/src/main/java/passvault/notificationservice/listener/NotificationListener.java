package passvault.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import passvault.notificationservice.config.RabbitMQConfig;
import passvault.notificationservice.event.PasswordChangedEvent;
import passvault.notificationservice.event.UserRegisteredEvent;
import passvault.notificationservice.service.EmailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTERED_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received user registered event: {}", event.getEmail());
        emailService.sendWelcomeEmail(event.getEmail(), event.getUsername());
    }

    @RabbitListener(queues = RabbitMQConfig.PASSWORD_CHANGED_QUEUE)
    public void handlePasswordChanged(PasswordChangedEvent event) {
        log.info("Received password changed event: {}", event.getEmail());
        emailService.sendPasswordChangedEmail(event.getEmail());
    }
}
