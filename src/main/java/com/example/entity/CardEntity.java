package com.example.entity;

import com.example.enums.CardStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "card")
public class CardEntity extends BaseEntity {
    @Column(name = "number", unique = true)
    private String number;
    @Column(name = "pin_code")
    private Integer pinCode;
    @Column(name = "expired_date")
    private LocalDate expiredDate;
    @Column(name = "phone")
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CardStatus status;
    @Column(name = "balance")
    private Double balance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private ClientEntity client;
    @Column(name = "client_id")
    private String clientId;


}
