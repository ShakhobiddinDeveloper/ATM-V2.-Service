package com.example.controller;

import com.example.dto.CardDTO;
import com.example.dto.ChangePinCardDTO;
import com.example.dto.CheckingCardDTO;
import com.example.dto.VerificationCardDTO;
import com.example.enums.AppLanguage;
import com.example.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/check")
    public ResponseEntity<Boolean> checking(@RequestBody CheckingCardDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("checking card {}", dto.getNumber());
        return ResponseEntity.ok(cardService.checking(dto, language));
    }

    @PostMapping("/sms-service")
    public ResponseEntity<?> smsService(@RequestBody CardDTO dto,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(cardService.smsService(dto, language));
    }

    @PostMapping("/verification")
    public ResponseEntity<Boolean> smsVerification(@RequestBody VerificationCardDTO dto,
                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(cardService.verification(dto, language));
    }

    @PutMapping("/change-pin")
    public ResponseEntity<?> changePin(@RequestBody ChangePinCardDTO dto,
                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(cardService.changePinCode(dto, language));
    }

}
