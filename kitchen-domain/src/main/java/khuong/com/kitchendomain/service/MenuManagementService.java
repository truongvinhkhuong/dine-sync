package khuong.com.kitchendomain.service;

import khuong.com.kitchendomain.dto.MenuItemAvailabilityDTO;
import khuong.com.kitchendomain.entity.MenuItem;
import khuong.com.kitchendomain.exception.ResourceNotFoundException;
import khuong.com.kitchendomain.messaging.MenuAvailabilityPublisher;
import khuong.com.kitchendomain.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuManagementService {
    private final MenuItemRepository menuItemRepository;
    private final MenuAvailabilityPublisher menuAvailabilityPublisher;


    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByActiveAndAvailable(true, true, null);
    }

 
    @Transactional
    public void updateMenuItemAvailability(MenuItemAvailabilityDTO availabilityDTO) {
        MenuItem menuItem = menuItemRepository.findById(availabilityDTO.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với ID: " + availabilityDTO.getMenuItemId()));
        
        menuItem.setAvailable(availabilityDTO.isAvailable());
        MenuItem savedItem = menuItemRepository.save(menuItem);
        
        // Thông báo thay đổi trạng thái món ăn
        menuAvailabilityPublisher.publishMenuItemAvailabilityChange(savedItem);
        
        log.info("Cập nhật trạng thái của món ăn {} thành {}", savedItem.getName(), 
                savedItem.isAvailable() ? "có sẵn" : "không có sẵn");
    }

    // update trạng thái sẵn có của nhiều món ăn
    @Transactional
    public void updateMultipleMenuItemsAvailability(List<MenuItemAvailabilityDTO> availabilityDTOs) {
        for (MenuItemAvailabilityDTO dto : availabilityDTOs) {
            updateMenuItemAvailability(dto);
        }
    }
}