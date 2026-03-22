package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "legal_doc")
@Getter
@Setter

public class LegalDoc extends Base {
    @Column(name = "file", length = 255, nullable = false)
    private String file;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    public enum LegalDocType {
        BUSINESS_REGISTRATION,
        FOOD_SAFETY_LICENSE,
        FIRE_SAFETY_LICENSE,
        IDENTITY_CARD,
        ALCOHOL_LICENSE,
        OTHER
    }

    public enum LegalStatus {
        VALID,
        EXPIRED
    }

    @Column(name = "type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private LegalDocType type;

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private LegalStatus status;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public LegalDoc() {

    }

}
