package khuong.com.kitchendomain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Table;

@Entity
@Table(name = "option_choices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private MenuItemOption option;

    private String name;
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    private Integer displayOrder = 0;
    private boolean available = true;
    private boolean defaultSelected = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}