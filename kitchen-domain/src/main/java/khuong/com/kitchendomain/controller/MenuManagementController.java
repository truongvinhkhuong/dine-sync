package khuong.com.kitchendomain.controller;

import khuong.com.kitchendomain.dto.MenuItemAvailabilityDTO;
import khuong.com.kitchendomain.service.MenuManagementService;
import khuong.com.smartorder_domain2.menu.entity.MenuItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kitchen/menu")
@RequiredArgsConstructor
@PreAuthorize("hasRole('KITCHEN_STAFF')")
public class MenuManagementController {
    private final MenuManagementService menuManagementService;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuManagementService.getAllMenuItems());
    }

    @GetMapping("/available")
    public ResponseEntity<List<MenuItem>> getAvailableMenuItems() {
        return ResponseEntity.ok(menuManagementService.getAvailableMenuItems());
    }

    @PutMapping("/availability")
    public ResponseEntity<Void> updateMenuItemAvailability(@RequestBody MenuItemAvailabilityDTO availabilityDTO) {
        menuManagementService.updateMenuItemAvailability(availabilityDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/availability/batch")
    public ResponseEntity<Void> updateMultipleMenuItemsAvailability(@RequestBody List<MenuItemAvailabilityDTO> availabilityDTOs) {
        menuManagementService.updateMultipleMenuItemsAvailability(availabilityDTOs);
        return ResponseEntity.ok().build();
    }
}