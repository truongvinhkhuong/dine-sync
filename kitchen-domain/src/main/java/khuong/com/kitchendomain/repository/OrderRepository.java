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
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.table LEFT JOIN FETCH o.items WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.table LEFT JOIN FETCH o.items WHERE o.table.tableNumber = :tableNumber")
    List<Order> findByTableNumber(@Param("tableNumber") String tableNumber);
}