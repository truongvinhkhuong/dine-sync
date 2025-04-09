package khuong.com.kitchendomain.repository;

import khuong.com.kitchendomain.entity.Order;
import khuong.com.kitchendomain.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.table.tableNumber = :tableNumber")
    List<Order> findByTableNumber(@Param("tableNumber") String tableNumber);
}