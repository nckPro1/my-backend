package com.example.bida.repository;

import com.example.bida.entity.Session;
import com.example.bida.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByTableAndEndTimeIsNull(Table table);

    // ✅ THÊM dòng này:
    List<Session> findByTable(Table table);
}
