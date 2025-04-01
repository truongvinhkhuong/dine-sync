package khuong.com.kitchendomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemAvailabilityDTO {
    private Long menuItemId;
    private Boolean available;
}