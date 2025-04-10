package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.kitchendomain.entity.Order;
import khuong.com.kitchendomain.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderStatusPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrderStatusUpdate(Order order) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("status", order.getStatus().name());
        message.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDER_UPDATE_ROUTING_KEY,
                message
        );

        log.info("Đã gửi cập nhật trạng thái đơn hàng: {} - {}", order.getId(), order.getStatus());
    }

    public void publishOrderItemStatusUpdate(OrderItem orderItem) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderItemId", orderItem.getId());
        message.put("orderId", orderItem.getOrder().getId());
        message.put("menuItemId", orderItem.getMenuItem().getId());
        message.put("status", orderItem.getStatus().name());
        message.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDER_UPDATE_ROUTING_KEY,
                message
        );

        log.info("Đã gửi cập nhật trạng thái món ăn: {} - {}", orderItem.getId(), orderItem.getStatus());
    }

    public void publishOrderItemStatusChange(Long orderItemId, String status) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderItemId", orderItemId);
        message.put("status", status);
        message.put("timestamp", System.currentTimeMillis());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_ROUTING_KEY,
                message
        );

        log.info("Đã gửi thông báo cập nhật trạng thái món ăn #{}: {}", orderItemId, status);
    }
}