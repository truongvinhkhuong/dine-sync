package khuong.com.kitchendomain.config;

import khuong.com.kitchendomain.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigOrderListener {
    
    private final KitchenOrderService kitchenOrderService;

    // Use the constant from RabbitMQConfig instead of property placeholder
    @RabbitListener(queues = RabbitMQConfig.NEW_ORDER_QUEUE)
    public void handleNewOrder(Map<String, Object> message) {
        log.info("Received new order notification: {}", message);
        
        // Process the new order notification
        Long orderId = Long.valueOf(message.get("orderId").toString());
        String status = message.get("status") != null ? message.get("status").toString() : "PENDING";
        
        try {
            // Process the order in the kitchen service
            kitchenOrderService.processNewOrder(orderId, status);
            
            log.info("Successfully processed new order with ID: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing order {}: {}", orderId, e.getMessage());
        }
    }
    
    // Use the constant from RabbitMQConfig instead of property placeholder
    @RabbitListener(queues = RabbitMQConfig.ORDER_UPDATES_QUEUE)
    public void handleOrderUpdates(Map<String, Object> message) {
        log.info("Received order update: {}", message);
        
        try {
            Long orderId = Long.valueOf(message.get("orderId").toString());
            String status = message.get("status").toString();
            
            // Update the order status
            kitchenOrderService.updateOrderStatus(orderId, status);
            
            log.info("Order {} status updated to: {}", orderId, status);
        } catch (Exception e) {
            log.error("Error updating order: {}", e.getMessage());
        }
    }
}