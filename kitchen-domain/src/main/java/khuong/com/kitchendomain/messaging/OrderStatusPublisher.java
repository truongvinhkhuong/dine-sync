package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.smartorder_domain2.order.entity.Order;
import khuong.com.smartorder_domain2.order.entity.OrderItem;
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

        log.info("Gửi cập nhật trạng thái đơn hàng: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.KITCHEN_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_ROUTING_KEY,
                message
        );
    }

    public void publishOrderItemStatusUpdate(OrderItem orderItem) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderItemId", orderItem.getId());
        message.put("orderId", orderItem.getOrder().getId());
        message.put("status", orderItem.getStatus().name());
        message.put("timestamp", System.currentTimeMillis());

        log.info("Gửi cập nhật trạng thái món ăn: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.KITCHEN_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_ROUTING_KEY,
                message
        );
    }
}