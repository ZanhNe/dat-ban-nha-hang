package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data // Này thì đỡ phần Getter/Setter cho lẹ
@MappedSuperclass // Tương đương __abstract__ = True bên Flask
public class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
