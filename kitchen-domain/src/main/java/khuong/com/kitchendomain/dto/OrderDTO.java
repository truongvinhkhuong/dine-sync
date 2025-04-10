package khuong.com.kitchendomain.dto;

import khuong.com.kitchendomain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrderDTO {
    private Long id;
    private String tableNumber;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

    public static OrderDTO fromEntity(Order order) {
        OrderDTO.OrderDTOBuilder builder = OrderDTO.builder()
                .id(order.getId())
                .status(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN")
                .note(order.getNote())
                .createdAt(order.getCreatedAt());
        
        // Xử lý table null
        if (order.getTable() != null) {
            builder.tableNumber(order.getTable().getTableNumber());
        } else {
            builder.tableNumber("Unknown");
        }
        
        // Xử lý items null
        if (order.getItems() != null) {
            builder.items(order.getItems().stream()
                    .map(OrderItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            builder.items(Collections.emptyList());
        }
        
        return builder.build();
    }
}