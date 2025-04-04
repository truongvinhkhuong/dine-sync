package khuong.com.kitchendomain.entity;

import jakarta.persistence.*;
import khuong.com.kitchendomain.entity.enums.OptionType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_item_options")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemOption implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false, length = 100)
    private String name;

    private BigDecimal price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice;

    @Column(name = "default_option")
    private boolean defaultOption;

    private boolean available = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "option_type", length = 20)
    private OptionType optionType;

    @Column(name = "min_selections")
    private Integer minSelections = 0;

    @Column(name = "max_selections")
    private Integer maxSelections = 1;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<OptionChoice> choices;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}