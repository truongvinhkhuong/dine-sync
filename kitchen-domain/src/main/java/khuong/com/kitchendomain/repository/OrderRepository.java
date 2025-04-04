package khuong.com.kitchendomain.repository;

import khuong.com.kitchendomain.entity.Order;
import khuong.com.kitchendomain.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}