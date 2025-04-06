package khuong.com.kitchendomain.controller;

import khuong.com.kitchendomain.entity.MenuItem;
import khuong.com.kitchendomain.service.MenuManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test/menu")
@RequiredArgsConstructor
public class TestMenuController {
    private final MenuManagementService menuManagementService;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuManagementService.getAllMenuItems());
    }
}