package khuong.com.kitchendomain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@jakarta.persistence.Table(name = "tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String tableNumber;
    
    private String notes;
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    private TableStatus status;
    
    private Boolean active = true;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum TableStatus {
        AVAILABLE,
        OCCUPIED
    }
}