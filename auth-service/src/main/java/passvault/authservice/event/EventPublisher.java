package passvault.authservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import passvault.authservice.config.RabbitMQConfig;
import passvault.authservice.model.User;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegistered(User user) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_REGISTERED_QUEUE, event);
        log.info("Published UserRegisteredEvent for user: {}", user.getUsername());
    }

    public void publishPasswordChanged(User user) {
        PasswordChangedEvent event = PasswordChangedEvent.builder()
                .userId(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .changedAt(Instant.now())
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.PASSWORD_CHANGED_QUEUE, event);
        log.info("Published PasswordChangedEvent for user: {}", user.getUsername());
    }
}
