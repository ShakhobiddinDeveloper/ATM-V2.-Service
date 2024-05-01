package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sms_send_history")
public class SmsSendHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "message")
    private String message;
    @Column(name = "phone")
    private String phone;
    @Column(name = "created_date")
    protected LocalDateTime createdDate = LocalDateTime.now();

}
