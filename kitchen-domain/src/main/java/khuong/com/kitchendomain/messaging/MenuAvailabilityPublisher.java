package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.kitchendomain.entity.MenuItem;
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

    public void publishMenuItemAvailabilityChange(MenuItem menuItem) {
        Map<String, Object> message = new HashMap<>();
        message.put("menuItemId", menuItem.getId());
        message.put("name", menuItem.getName());
        message.put("available", menuItem.isAvailable());
        message.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.MENU_AVAILABILITY_ROUTING_KEY,
                message
        );

        log.info("Đã gửi thông báo thay đổi trạng thái món ăn: {} - {}", 
                menuItem.getName(), menuItem.isAvailable() ? "có sẵn" : "không có sẵn");
    }
}