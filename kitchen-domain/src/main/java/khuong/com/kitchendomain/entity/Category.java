package khuong.com.kitchendomain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_categories") // Đảm bảo tên bảng là menu_categories
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    private boolean active = true;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<MenuItem> menuItems = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}