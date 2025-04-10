package khuong.com.kitchendomain.service;

import khuong.com.kitchendomain.dto.OrderDTO;
import khuong.com.kitchendomain.dto.OrderItemStatusUpdateDTO;
import khuong.com.kitchendomain.dto.OrderToKitchenDto;
import khuong.com.kitchendomain.entity.MenuItem;
import khuong.com.kitchendomain.entity.Order;
import khuong.com.kitchendomain.entity.OrderItem;
import khuong.com.kitchendomain.entity.Table;
import khuong.com.kitchendomain.entity.enums.OrderItemStatus;
import khuong.com.kitchendomain.entity.enums.OrderStatus;
import khuong.com.kitchendomain.exception.ResourceNotFoundException;
import khuong.com.kitchendomain.messaging.OrderStatusPublisher;
import khuong.com.kitchendomain.repository.MenuItemRepository;
import khuong.com.kitchendomain.repository.OrderItemRepository;
import khuong.com.kitchendomain.repository.OrderRepository;
import khuong.com.kitchendomain.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KitchenOrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusPublisher orderStatusPublisher;
    private final MenuItemRepository menuItemRepository;
    @Autowired
    private TableRepository tableRepository;
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getPendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        return pendingOrders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getInProgressOrders() {
        List<Order> inProgressOrders = orderRepository.findByStatus(OrderStatus.IN_PROGRESS);
        return inProgressOrders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));
        return OrderDTO.fromEntity(order);
    }

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

    @Transactional
    public void processNewOrder(OrderToKitchenDto orderDto) {
        log.info("Processing new order: {}", orderDto);
        
        // Kiểm tra xem đơn hàng đã tồn tại chưa
        if (orderRepository.existsById(orderDto.getOrderId())) {
            log.info("Đơn hàng với ID {} đã tồn tại", orderDto.getOrderId());
            return;
        }
        
        try {
            // Tạo đơn hàng mới
            Order order = new Order();
            order.setId(orderDto.getOrderId());
            order.setStatus(OrderStatus.CONFIRMED);
            
            // Tìm hoặc tạo bàn
            Table table = findOrCreateTable(orderDto.getTableNumber());
            order.setTable(table);
            
            // Tạo các món ăn trong đơn hàng
            List<OrderItem> orderItems = createOrderItems(orderDto.getItems(), order);
            order.setItems(orderItems);
            
            // Lưu đơn hàng
            orderRepository.save(order);
            
            log.info("Đã tạo đơn hàng mới với ID: {}", orderDto.getOrderId());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng mới: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xử lý đơn hàng mới", e);
        }
    }

    private Table findOrCreateTable(String tableNumber) {
        // Tìm bàn theo số bàn
        // Nếu không tìm thấy, tạo mới
        return tableRepository.findByTableNumber(tableNumber)
                .orElseGet(() -> {
                    Table newTable = new Table();
                    newTable.setTableNumber(tableNumber);
                    newTable.setStatus(Table.TableStatus.OCCUPIED);
                    newTable.setActive(true);
                    return tableRepository.save(newTable);
                });
    }

    private List<OrderItem> createOrderItems(List<OrderToKitchenDto.KitchenItemDto> items, Order order) {
        return items.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    
                    // Tạo hoặc tìm MenuItem
                    MenuItem menuItem = findOrCreateMenuItem(item.getItemId(), item.getItemName());
                    orderItem.setMenuItem(menuItem);
                    
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setSpecialNotes(item.getNotes());
                    orderItem.setStatus(OrderItemStatus.PENDING);
                    orderItem.setUnitPrice(BigDecimal.ZERO); // Mặc định giá đơn vị là 0
                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private MenuItem findOrCreateMenuItem(Long itemId, String itemName) {
        return menuItemRepository.findById(itemId)
                .orElseGet(() -> {
                    MenuItem newMenuItem = new MenuItem();
                    newMenuItem.setId(itemId);
                    newMenuItem.setName(itemName);
                    newMenuItem.setPrice(BigDecimal.ZERO); // Giá mặc định
                    newMenuItem.setDescription("Imported from domain-2");
                    newMenuItem.setActive(true);
                    return menuItemRepository.save(newMenuItem);
                });
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to {}", orderId, status);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setStatus(newStatus);
        orderRepository.save(order);
        

        orderStatusPublisher.publishOrderStatusUpdate(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByTableNumber(String tableNumber) {
        List<Order> orders = orderRepository.findByTableNumber(tableNumber);
        return orders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processNewOrder(Long orderId, String status) {
        log.info("Processing new order: {} with status: {}", orderId, status);
        
        // Kiểm tra xem đơn hàng đã tồn tại chưa
        if (orderRepository.existsById(orderId)) {
            log.info("Đơn hàng với ID {} đã tồn tại", orderId);
            return;
        }
        
        try {
            // Tạo đơn hàng mới
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.valueOf(status));
            
            // Lưu đơn hàng (không có thông tin chi tiết)
            // Thông tin chi tiết sẽ được cập nhật sau khi nhận được từ domain-2
            orderRepository.save(order);
            
            log.info("Đã tạo đơn hàng mới với ID: {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng mới: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xử lý đơn hàng mới", e);
        }
    }
}