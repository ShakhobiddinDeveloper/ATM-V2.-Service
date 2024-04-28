package com.example.repository;

import com.example.entity.CardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CardRepository extends CrudRepository<CardEntity, String> {
    @Query("from CardEntity where visible=true and status='ACTIVE' and number=?1")
    Optional<CardEntity> findByNumber(String number);
}
