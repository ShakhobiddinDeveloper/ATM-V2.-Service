package com.example.service;

import com.example.dto.*;
import com.example.entity.CardEntity;
import com.example.entity.CardHistoryEntity;
import com.example.entity.SmsSendHistoryEntity;
import com.example.enums.AppLanguage;
import com.example.exp.AppBadException;
import com.example.repository.CardHistoryRepository;
import com.example.repository.CardRepository;
import com.example.repository.SmsSendHistoryRepository;
import com.example.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    @Autowired
    private CardHistoryRepository cardHistoryRepository;

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

    public Boolean cashing(CashingCardDTO dto, AppLanguage language) {
        CardEntity cardEntity = getByNumber(dto.getNumber(), language);
        if (cardEntity.getBalance() < dto.getAmount()) {
            log.warn("cashing error {}", dto.getNumber());
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("insufficient.funds.account", language));
        }
        cardEntity.setBalance(cardEntity.getBalance() - dto.getAmount());
        cardRepository.save(cardEntity);
        // card history save
        CardHistoryEntity cardHistoryEntity = new CardHistoryEntity();
        cardHistoryEntity.setCardNumber(dto.getNumber());
        cardHistoryEntity.setOutlay(dto.getAmount());
        cardHistoryEntity.setLastBalance(cardEntity.getBalance());
        cardHistoryEntity.setDescription("Cashing");
        cardHistoryRepository.save(cardHistoryEntity);
        return true;
    }

    public Boolean fillOutCard(FillOutCardDTO dto, AppLanguage language) {
        CardEntity cardEntity = getByNumber(dto.getNumber(), language);
        cardEntity.setBalance(cardEntity.getBalance() + dto.getAmount());
        cardRepository.save(cardEntity);
        // card history save
        CardHistoryEntity cardHistoryEntity = new CardHistoryEntity();
        cardHistoryEntity.setCardNumber(dto.getNumber());
        cardHistoryEntity.setIncome(dto.getAmount());
        cardHistoryEntity.setLastBalance(cardEntity.getBalance());
        cardHistoryEntity.setDescription("Fill out card");
        cardHistoryRepository.save(cardHistoryEntity);
        return true;
    }

    public Boolean transfer(TransferDTO dto, AppLanguage language) {
        CardEntity thisCardEntity = getByNumber(dto.getThisCardNumber(), language);
        CardEntity transferCardEntity = getByNumber(dto.getTransferCardNumber(), language);
        if (thisCardEntity.getBalance() < dto.getAmount()) {
            log.warn("transfer error this amount invalid{}", dto.getThisCardNumber());
            throw new AppBadException(resourceBundleMessageSourceService.getMessage("insufficient.funds.account", language));
        }
        thisCardEntity.setBalance(thisCardEntity.getBalance() - dto.getAmount());
        cardRepository.save(thisCardEntity);
        // history save
        CardHistoryEntity thisCardHistoryEntity = new CardHistoryEntity();
        thisCardHistoryEntity.setCardNumber(thisCardEntity.getNumber());
        thisCardHistoryEntity.setOutlay(dto.getAmount());
        thisCardHistoryEntity.setLastBalance(thisCardEntity.getBalance());
        thisCardHistoryEntity.setDescription("transfer card " + dto.getTransferCardNumber());
        cardHistoryRepository.save(thisCardHistoryEntity);
        transferCardEntity.setBalance(transferCardEntity.getBalance() + dto.getAmount());
        cardRepository.save(transferCardEntity);
        // history save
        CardHistoryEntity cardHistoryEntity = new CardHistoryEntity();
        cardHistoryEntity.setCardNumber(thisCardEntity.getNumber());
        cardHistoryEntity.setIncome(dto.getAmount());
        cardHistoryEntity.setLastBalance(thisCardEntity.getBalance());
        cardHistoryEntity.setDescription("transfer card " + dto.getThisCardNumber());
        cardHistoryRepository.save(cardHistoryEntity);
        return true;
    }

    public List<CardHistoryEntity> history(String cardNumber) {
        List<CardHistoryEntity> historyEntityList = cardHistoryRepository.findByCardNumber(cardNumber);
        return historyEntityList;
    }

    public Boolean removeCard(String cardNumber) {
        cardRepository.removeCard(cardNumber);
        return true;
    }

    public CardDTO getByCardNumber(String cardNumber, AppLanguage language) {
        CardEntity cardEntity = getByNumber(cardNumber, language);
        CardDTO cardDTO = new CardDTO();
        cardDTO.setNumber(cardEntity.getNumber());
        cardDTO.setPhone(cardEntity.getPhone());
        cardDTO.setBalance(cardEntity.getBalance());
        return cardDTO;
    }
}
