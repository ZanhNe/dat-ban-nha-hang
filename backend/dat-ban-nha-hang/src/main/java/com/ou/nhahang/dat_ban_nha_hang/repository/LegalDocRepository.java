package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.LegalDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalDocRepository extends JpaRepository<LegalDoc, Long> {
}
