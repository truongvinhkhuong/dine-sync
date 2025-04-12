package khuong.com.kitchendomain.controller;

import khuong.com.kitchendomain.dto.OrderDTO;
import khuong.com.kitchendomain.dto.OrderItemStatusUpdateDTO;
import khuong.com.kitchendomain.exception.ResourceNotFoundException;
import khuong.com.kitchendomain.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('KITCHEN_STAFF')")
public class KitchenOrderController {
    private final KitchenOrderService kitchenOrderService;

    @GetMapping("/pending")
    public ResponseEntity<List<OrderDTO>> getPendingOrders() {
        return ResponseEntity.ok(kitchenOrderService.getPendingOrders());
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<OrderDTO>> getInProgressOrders() {
        return ResponseEntity.ok(kitchenOrderService.getInProgressOrders());
    }

    @GetMapping("/confirmed")
    public ResponseEntity<List<OrderDTO>> getConfirmedOrders() {
        return ResponseEntity.ok(kitchenOrderService.getConfirmedOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(kitchenOrderService.getOrderById(orderId));
    }

    @PostMapping("/{orderId}/start")
    public ResponseEntity<Void> startProcessingOrder(@PathVariable Long orderId) {
        try {
            kitchenOrderService.startProcessingOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Không tìm thấy đơn hàng: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Trạng thái đơn hàng không hợp lệ: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Lỗi không xác định khi xử lý đơn hàng {}: {}", orderId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Lỗi nội bộ: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        kitchenOrderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable Long orderId) {
        kitchenOrderService.confirmOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/status")
    public ResponseEntity<Void> updateOrderItemStatus(@RequestBody OrderItemStatusUpdateDTO updateDTO) {
        try {
            kitchenOrderService.updateOrderItemStatus(updateDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}