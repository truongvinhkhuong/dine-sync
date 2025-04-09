package khuong.com.kitchendomain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import khuong.com.kitchendomain.entity.Table;
import khuong.com.kitchendomain.entity.Table.TableStatus;

import java.util.List;
import java.util.Optional;
@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    List<Table> findByStatus(TableStatus status);

    List<Table> findByStatusAndActiveTrue(TableStatus status);
    boolean existsByTableNumber(String tableNumber);
    void deleteByIdAndActiveTrue(Long id);
    List<Table> findByActiveFalse();
    Optional<Table> findByTableNumber(String tableNumber);
  
}