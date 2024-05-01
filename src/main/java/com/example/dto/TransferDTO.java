package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransferDTO {
    private String thisCardNumber;
    private String transferCardNumber;
    private Double amount;
}
