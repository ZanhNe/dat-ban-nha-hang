package com.ou.nhahang.dat_ban_nha_hang.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_source")
@Getter
@Setter

@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class PaymentSource extends Base {
    @OneToMany(mappedBy = "paymentSource")
    private List<Transaction> transactions = new ArrayList<>();
}
