package khuong.com.kitchendomain.repository;

import khuong.com.kitchendomain.entity.OrderItem;
import khuong.com.kitchendomain.entity.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByStatus(OrderItemStatus status);
}