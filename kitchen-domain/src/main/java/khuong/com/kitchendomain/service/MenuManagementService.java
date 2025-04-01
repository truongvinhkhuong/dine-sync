package khuong.com.kitchendomain.service;

import khuong.com.kitchendomain.dto.MenuItemAvailabilityDTO;
import khuong.com.kitchendomain.messaging.MenuAvailabilityPublisher;
import khuong.com.smartorder_domain2.menu.dto.exception.ResourceNotFoundException;
import khuong.com.smartorder_domain2.menu.entity.MenuItem;
import khuong.com.smartorder_domain2.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        
        menuItem.setAvailable(availabilityDTO.getAvailable());
        menuItemRepository.save(menuItem);
        
        // Gửi thông báo cập nhật trạng thái
        menuAvailabilityPublisher.publishMenuItemAvailabilityUpdate(menuItem);
        
        log.info("Đã cập nhật trạng thái sẵn có của món ăn ID {}: {}", 
                menuItem.getId(), menuItem.getAvailable());
    }

    // update trạng thái sẵn có của nhiều món ăn
    @Transactional
    public void updateMultipleMenuItemsAvailability(List<MenuItemAvailabilityDTO> availabilityDTOs) {
        for (MenuItemAvailabilityDTO dto : availabilityDTOs) {
            updateMenuItemAvailability(dto);
        }
    }
}