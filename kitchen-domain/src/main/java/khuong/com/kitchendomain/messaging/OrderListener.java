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

    // nhận đơn mới
    @RabbitListener(queues = RabbitMQConfig.NEW_ORDER_QUEUE)
    public void handleNewOrder(OrderToKitchenDto orderDto) {
        log.info("Nhận thông báo đơn hàng mới: {}", orderDto);
        
        try {
            kitchenOrderService.processNewOrder(orderDto);
            log.info("Đã xử lý đơn hàng mới với ID: {}", orderDto.getOrderId());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng mới: {}", e.getMessage(), e);
            // ... adđ logic gửi đơn hàng vào dead letter queue 
        }
    }

    // update trạng thái đơn hàng
    @RabbitListener(queues = RabbitMQConfig.ORDER_UPDATES_QUEUE)
    public void handleOrderUpdates(Map<String, Object> message) {
        log.info("Received order update: {}", message);
        
        try {
            Long orderId = Long.valueOf(message.get("orderId").toString());
            String status = message.get("status").toString();
            
            kitchenOrderService.updateOrderStatus(orderId, status);
            log.info("Order {} status updated to: {}", orderId, status);
        } catch (Exception e) {
            log.error("Error updating order: {}", e.getMessage(), e);
        }
    }
}