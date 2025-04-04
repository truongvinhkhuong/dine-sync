package khuong.com.kitchendomain.dto;

import khuong.com.kitchendomain.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrderItemDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private String specialInstructions;
    private String status;

    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .menuItemId(orderItem.getMenuItem().getId())
                .menuItemName(orderItem.getMenuItem().getName())
                .quantity(orderItem.getQuantity())
                .specialInstructions(orderItem.getSpecialNotes())
                .status(orderItem.getStatus().name())
                .build();
    }
}