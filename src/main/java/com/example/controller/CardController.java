package com.example.controller;

import com.example.dto.*;
import com.example.entity.CardHistoryEntity;
import com.example.enums.AppLanguage;
import com.example.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardDTO> getByNumber(@PathVariable String cardNumber,
                                               @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("get {}", cardNumber);
        return ResponseEntity.ok(cardService.getByCardNumber(cardNumber, language));
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checking(@RequestBody CheckingCardDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("checking card {}", dto.getNumber());
        return ResponseEntity.ok(cardService.checking(dto, language));
    }

    @PostMapping("/sms-service")
    public ResponseEntity<?> smsService(@RequestBody CardDTO dto,
                                        @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("sms service{}", dto.getNumber());
        return ResponseEntity.ok(cardService.smsService(dto, language));
    }

    @PostMapping("/verification")
    public ResponseEntity<Boolean> smsVerification(@RequestBody VerificationCardDTO dto,
                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("sms verification{}{}", dto.getNumber(), dto.getPhone());
        return ResponseEntity.ok(cardService.verification(dto, language));
    }

    @PutMapping("/change-pin")
    public ResponseEntity<?> changePin(@RequestBody ChangePinCardDTO dto,
                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("change pin{}", dto.getNumber());
        return ResponseEntity.ok(cardService.changePinCode(dto, language));
    }

    @PutMapping("/cashing")
    public ResponseEntity<?> cashing(@RequestBody CashingCardDTO dto,
                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("cashing{}{}", dto.getNumber(), dto.getAmount());
        return ResponseEntity.ok(cardService.cashing(dto, language));
    }

    @PutMapping("/fill-out-card")
    public ResponseEntity<?> fillOutCard(@RequestBody FillOutCardDTO dto,
                                         @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("fill out card{}{}", dto.getNumber(), dto.getAmount());
        return ResponseEntity.ok(cardService.fillOutCard(dto, language));
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferDTO dto,
                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("transfer{}{}{}", dto.getThisCardNumber(), dto.getTransferCardNumber(), dto.getAmount());
        return ResponseEntity.ok(cardService.transfer(dto, language));
    }

    @GetMapping("/history/{cardNumber}")
    public ResponseEntity<List<CardHistoryEntity>> history(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.history(cardNumber));
    }

    @DeleteMapping("/{cardNumber}")
    public ResponseEntity<?> removeCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(cardService.removeCard(cardNumber));
    }
}
