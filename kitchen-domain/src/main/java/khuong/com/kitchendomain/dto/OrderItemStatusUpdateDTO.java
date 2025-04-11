package khuong.com.kitchendomain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemStatusUpdateDTO {
    private Long orderItemId;
    private String status;
}