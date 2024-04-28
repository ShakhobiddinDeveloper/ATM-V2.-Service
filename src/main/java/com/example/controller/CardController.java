package com.example.controller;

import com.example.dto.CheckingCardDTO;
import com.example.enums.AppLanguage;
import com.example.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/check")
    public ResponseEntity<?> checking(@RequestBody CheckingCardDTO dto,
                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(cardService.checking(dto, language));
    }


}
