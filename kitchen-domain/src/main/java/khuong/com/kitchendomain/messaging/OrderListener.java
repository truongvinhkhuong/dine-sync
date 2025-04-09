package khuong.com.kitchendomain.messaging;

import khuong.com.kitchendomain.config.RabbitMQConfig;
import khuong.com.kitchendomain.dto.OrderToKitchenDto;
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

    @RabbitListener(queues = "${spring.kitchen.queue.orders}")
    public void handleNewOrder(OrderToKitchenDto orderDto) {
        log.info("Nhận thông báo đơn hàng mới: {}", orderDto);
        
        try {
            // Xử lý đơn hàng mới
            kitchenOrderService.processNewOrder(orderDto);
            log.info("Đã xử lý đơn hàng mới với ID: {}", orderDto.getOrderId());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng mới: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${spring.kitchen.queue.order-updates}")
    public void handleOrderUpdates(Map<String, Object> message) {
        log.info("Received order update: {}", message);
        
        try {
            Long orderId = Long.valueOf(message.get("orderId").toString());
            String status = message.get("status").toString();
            
            kitchenOrderService.updateOrderStatus(orderId, status);
            log.info("Order status updated successfully: {}", orderId);
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
        }
    }
}