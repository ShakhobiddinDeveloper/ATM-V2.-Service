package com.example.repository;

import com.example.entity.CardHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CardHistoryRepository extends CrudRepository<CardHistoryEntity, String> {
    List<CardHistoryEntity> findByCardNumber(String cardNumber);
}
