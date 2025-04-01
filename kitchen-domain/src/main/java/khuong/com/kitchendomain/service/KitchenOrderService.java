package khuong.com.kitchendomain.service;

import khuong.com.kitchendomain.dto.OrderDTO;
import khuong.com.kitchendomain.dto.OrderItemStatusUpdateDTO;
import khuong.com.kitchendomain.messaging.OrderStatusPublisher;
import khuong.com.smartorder_domain2.menu.dto.exception.ResourceNotFoundException;
import khuong.com.smartorder_domain2.order.entity.Order;
import khuong.com.smartorder_domain2.order.entity.OrderItem;
import khuong.com.smartorder_domain2.order.enums.OrderItemStatus;
import khuong.com.smartorder_domain2.order.enums.OrderStatus;
import khuong.com.smartorder_domain2.order.repository.OrderItemRepository;
import khuong.com.smartorder_domain2.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KitchenOrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusPublisher orderStatusPublisher;

    // get all đơn hàng đang chờ xử lý
    public List<OrderDTO> getPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.CONFIRMED);
        return pendingOrders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //get all đơn hàng đang được xử lý

    public List<OrderDTO> getInProgressOrders() {
        List<Order> inProgressOrders = orderRepository.findByStatus(OrderStatus.IN_PROGRESS);
        return inProgressOrders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }


    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));
        return OrderDTO.fromEntity(order);
    }

    // update menu item status
    @Transactional
    public void updateOrderItemStatus(OrderItemStatusUpdateDTO updateDTO) {
        OrderItem orderItem = orderItemRepository.findById(updateDTO.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với ID: " + updateDTO.getOrderItemId()));
        
        OrderItemStatus newStatus = OrderItemStatus.valueOf(updateDTO.getStatus());
        orderItem.setStatus(newStatus);
        orderItemRepository.save(orderItem);
        
        updateOrderStatusIfNeeded(orderItem.getOrder());

        orderStatusPublisher.publishOrderItemStatusUpdate(orderItem);
    }

   // start processing order
    @Transactional
    public void startProcessingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.IN_PROGRESS);
            orderRepository.save(order);
            
            // Gửi thông báo cập nhật trạng thái
            orderStatusPublisher.publishOrderStatusUpdate(order);
        } else {
            throw new IllegalStateException("Đơn hàng không ở trạng thái chờ xử lý");
        }
    }

    // done order
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        // check tất cả các món ăn đã hoàn thành chưa
        boolean allItemsCompleted = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.COMPLETED);
        
        if (!allItemsCompleted) {
            throw new IllegalStateException("Không thể hoàn thành đơn hàng khi còn món ăn chưa hoàn thành");
        }
        
        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);
        
        // Gửi thông báo cập nhật trạng thái
        orderStatusPublisher.publishOrderStatusUpdate(order);
    }

    // update order status
    private void updateOrderStatusIfNeeded(Order order) {
        // Nếu tất cả các món ăn đã hoàn thành, đánh dấu đơn hàng là sẵn sàng
        boolean allItemsCompleted = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.COMPLETED);
        
        if (allItemsCompleted && order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.READY);
            orderRepository.save(order);
            
            // Gửi thông báo cập nhật trạng thái
            orderStatusPublisher.publishOrderStatusUpdate(order);
        }
    }
}