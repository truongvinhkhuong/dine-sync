package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.smartorder_domain2.menu.entity.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MenuAvailabilityPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishMenuItemAvailabilityUpdate(MenuItem menuItem) {
        Map<String, Object> message = new HashMap<>();
        message.put("menuItemId", menuItem.getId());
        message.put("available", menuItem.getAvailable());
        message.put("timestamp", System.currentTimeMillis());

        log.info("Gửi cập nhật trạng thái sẵn có của món ăn: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.KITCHEN_EXCHANGE,
                RabbitMQConfig.MENU_AVAILABILITY_ROUTING_KEY,
                message
        );
    }
}