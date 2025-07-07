package com.example.bida.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@jakarta.persistence.Table(name = "sessions") // Dùng rõ ràng tránh nhầm lẫn với Table class
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long totalMinutes;

    private Double totalPrice;

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public Table getTable() {
        return table;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Long getTotalMinutes() {
        return totalMinutes;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setTotalMinutes(Long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
