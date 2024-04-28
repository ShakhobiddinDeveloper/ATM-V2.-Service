package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "client")
public class ClientEntity extends BaseEntity {
    private String name;
    private String surname;
    @Column(name = "middle_name")
    private String middleName;
    private String phone;


}
