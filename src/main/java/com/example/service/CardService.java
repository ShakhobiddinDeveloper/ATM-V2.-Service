package com.example.service;

import com.example.dto.CheckingCardDTO;
import com.example.entity.CardEntity;
import com.example.enums.AppLanguage;
import com.example.exp.AppBadException;
import com.example.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final ResourceBundleMessageSourceService resourceBundleMessageSourceService;

    public CardService(CardRepository cardRepository, ResourceBundleMessageSourceService resourceBundleMessageSourceService) {
        this.cardRepository = cardRepository;
        this.resourceBundleMessageSourceService = resourceBundleMessageSourceService;
    }

    public Boolean checking(CheckingCardDTO dto, AppLanguage language) {
        CardEntity cardEntity = getByNumber(dto.getNumber(), language);
        LocalDate expiredDate = cardEntity.getExpiredDate();
        if (LocalDate.now().isAfter(expiredDate)) {
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("card.has.expired", language));
        }
        if (!Objects.equals(cardEntity.getPinCode(), dto.getPinCode())) {
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("pin.code.is.incorrect", language));
        }
        return true;
    }

    private CardEntity getByNumber(String number, AppLanguage language) {
        Optional<CardEntity> cardEntityOptional = cardRepository.findByNumber(number);
        if (cardEntityOptional.isEmpty()) {
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("card.not.found", language));
        }
        return cardEntityOptional.get();
    }
}
