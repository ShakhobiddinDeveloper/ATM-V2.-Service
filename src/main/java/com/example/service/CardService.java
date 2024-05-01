package com.example.service;

import com.example.dto.CardDTO;
import com.example.dto.ChangePinCardDTO;
import com.example.dto.CheckingCardDTO;
import com.example.dto.VerificationCardDTO;
import com.example.entity.CardEntity;
import com.example.entity.SmsSendHistoryEntity;
import com.example.enums.AppLanguage;
import com.example.exp.AppBadException;
import com.example.repository.CardRepository;
import com.example.repository.SmsSendHistoryRepository;
import com.example.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class CardService {
    private final CardRepository cardRepository;
    private final ResourceBundleMessageSourceService resourceBundleMessageSourceService;
    @Autowired
    private SmsServerService smsServerService;
    @Autowired
    private SmsSendHistoryRepository smsSendHistoryRepository;

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

    public Boolean smsService(CardDTO dto, AppLanguage language) {
        //send sms
        smsServerService.send(dto.getPhone(), RandomUtil.getRandomSmsCode());
        return true;
    }

    public Boolean verification(VerificationCardDTO dto, AppLanguage language) {
        SmsSendHistoryEntity sendHistoryEntity = smsSendHistoryRepository.findByPhone(dto.getPhone());
        if (!sendHistoryEntity.getMessage().equals(dto.getMessage())) {
            log.warn("sms code invalid {}", dto.getNumber());
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("sms.code.invalid", language));
        }
        CardEntity cardEntity = getByNumber(dto.getNumber(), language);
        cardEntity.setPhone(dto.getPhone());
        return true;
    }

    public Boolean changePinCode(ChangePinCardDTO dto, AppLanguage language) {
        CardEntity cardEntity = getByNumber(dto.getNumber(), language);
        if (!cardEntity.getPinCode().equals(dto.getOldPinCode())) {
            log.warn("change pin code error (old pin code invalid)");
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("pin.code.invalid", language));
        }
        cardEntity.setPinCode(dto.getNewPinCode());
        cardRepository.save(cardEntity);
        return true;
    }
}
