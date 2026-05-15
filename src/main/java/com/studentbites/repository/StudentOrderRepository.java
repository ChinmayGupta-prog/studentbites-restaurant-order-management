package com.studentbites.repository;

import com.studentbites.model.StudentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentOrderRepository extends JpaRepository<StudentOrder, Long> {
    List<StudentOrder> findTop8ByOrderByCreatedAtDesc();

    List<StudentOrder> findAllByOrderByCreatedAtDesc();

    List<StudentOrder> findTop8ByEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    long countByEmailIgnoreCase(String email);
}
