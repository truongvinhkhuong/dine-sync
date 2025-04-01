package khuong.com.kitchendomain.controller;

import khuong.com.kitchendomain.dto.OrderDTO;
import khuong.com.kitchendomain.dto.OrderItemStatusUpdateDTO;
import khuong.com.kitchendomain.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kitchen/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('KITCHEN_STAFF')")
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

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(kitchenOrderService.getOrderById(orderId));
    }

    @PostMapping("/{orderId}/start")
    public ResponseEntity<Void> startProcessingOrder(@PathVariable Long orderId) {
        kitchenOrderService.startProcessingOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        kitchenOrderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/status")
    public ResponseEntity<Void> updateOrderItemStatus(@RequestBody OrderItemStatusUpdateDTO updateDTO) {
        kitchenOrderService.updateOrderItemStatus(updateDTO);
        return ResponseEntity.ok().build();
    }
}