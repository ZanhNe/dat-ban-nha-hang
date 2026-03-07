package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

// import java.util.ArrayList;
// import java.util.List;

import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "restaurant")
@Data
@EqualsAndHashCode(callSuper = true)
public class Restaurant extends Base {
    @Column(name = "logo", length = 255, nullable = false)
    private String logo;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    public enum RestaurantStatus {
        OPENING,
        CLOSED,
        PENDING,
        SUSPENDED,
        REJECTED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "base_deposit_value", nullable = false)
    private Long baseDepositValue;

    public enum DepositType {
        FIXED,
        PER_GUEST,
        NONE
    }

    @Column(name = "deposit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DepositType depositPolicy;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    public enum CommissionType {
        PERCENTAGE,
        FIXED
    }

    @Column(name = "commission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;

    @Column(name = "base_commission_value", nullable = false)
    private Long baseCommissionValue;

    // @OneToMany(mappedBy = "workplace")
    // // mappedBy phải trỏ chính xác vào tên biến 'workplace' ở class User
    // private List<User> employees = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @OneToMany(mappedBy = "restaurant")
    private List<TableArea> tableAreas;

    @OneToMany(mappedBy = "restaurant")
    private List<Menu> menus;

    @OneToMany(mappedBy = "restaurant")
    private List<OperationTime> operationTimes;

    @OneToMany(mappedBy = "restaurant")
    private List<Booking> bookings;

    // Restaurant – Cuisine (nhiều–nhiều)
    @ManyToMany
    @JoinTable(
        name = "restaurant_cuisine",
        joinColumns = @JoinColumn(name = "restaurant_id"), 
        inverseJoinColumns = @JoinColumn(name = "cuisine_id")
    )
    private Set<Cuisine> cuisines = new HashSet<>();

    // Restaurant – LegalDoc (nhiều–nhiều)
    @ManyToMany
    @JoinTable(name = "restaurant_legal_doc", 
    joinColumns = @JoinColumn(name = "restaurant_id"), 
    inverseJoinColumns = @JoinColumn(name = "legal_doc_id")
)
    private Set<LegalDoc> legalDocs = new HashSet<>();

    public Restaurant() {
        
    }
}
