package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter

public class User extends Base {
    @Column(name = "username", length = 255, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;

    @Column(name = "avatar", length = 255, nullable = true)
    private String avatar;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "phone", length = 255, unique = true, nullable = false)
    private String phone;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    public enum UserStatus {
        ACTIVE,
        BANNED
    }

    @Column(name = "status", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "workplace_restaurant_id", nullable = true)
    private Restaurant workplace;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    // @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    // private List<Restaurant> managedRestaurants = new ArrayList<>();

    // @OneToMany(mappedBy = "waiter", cascade = CascadeType.ALL)
    // private List<RestaurantTableSession> restaurantTableSessions = new
    // ArrayList<>();

    public User() {
    }
}
