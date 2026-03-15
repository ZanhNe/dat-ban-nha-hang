package com.ou.nhahang.dat_ban_nha_hang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
