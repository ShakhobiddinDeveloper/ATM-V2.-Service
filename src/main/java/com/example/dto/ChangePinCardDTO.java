package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePinCardDTO {
    private String number;
    private Integer oldPinCode;
    private Integer newPinCode;
}
