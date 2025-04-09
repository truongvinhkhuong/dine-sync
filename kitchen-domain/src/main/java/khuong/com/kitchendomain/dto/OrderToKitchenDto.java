package khuong.com.kitchendomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderToKitchenDto {
    private Long orderId;
    private String tableNumber;
    private List<KitchenItemDto> items;
    private Long timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitchenItemDto {
        private Long itemId;
        private String itemName;
        private int quantity;
        private String notes;
    }
}