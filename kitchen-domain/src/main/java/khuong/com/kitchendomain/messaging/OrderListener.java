package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.kitchendomain.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {
    private final KitchenOrderService kitchenOrderService;

    @RabbitListener(queues = RabbitMQConfig.NEW_ORDER_QUEUE)
    public void handleNewOrder(Map<String, Object> message) {
        log.info("Nhận thông báo đơn hàng mới: {}", message);
        
        // Xử lý thông báo đơn hàng mới
        Long orderId = Long.valueOf(message.get("orderId").toString());
        
        // Có thể thực hiện các hành động khác như gửi thông báo đến giao diện người dùng
        log.info("Đã nhận đơn hàng mới với ID: {}", orderId);
    }
}