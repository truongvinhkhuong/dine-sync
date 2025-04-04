package khuong.com.kitchendomain.repository;

import khuong.com.kitchendomain.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByActiveAndAvailable(boolean active, boolean available, org.springframework.data.domain.Pageable pageable);
}