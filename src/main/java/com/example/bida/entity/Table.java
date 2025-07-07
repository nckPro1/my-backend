package com.example.bida.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@jakarta.persistence.Table(name = "tables") // ← rõ ràng annotation để tránh nhầm class
@Data
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @Column(nullable = false)
    private double pricePerHour; // 💰 Giá mỗi giờ – dùng để tính tiền
}
