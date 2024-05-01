package com.example.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "card_history")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardHistoryEntity extends BaseEntity {
    @Column(name = "card_number")
    private String cardNumber;
    //kirim
    @Column(name = "income")
    private Double income;
    //chiqim
    @Column(name = "outlay")
    private Double outlay;
    // oxirgi balanca
    @Column(name = "last_balance")
    private Double lastBalance;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
