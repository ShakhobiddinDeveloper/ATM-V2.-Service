package com.example.repository;

import com.example.entity.SmsSendHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsSendHistoryRepository extends CrudRepository<SmsSendHistoryEntity, String> {
    @Query("from SmsSendHistoryEntity where phone=?1 order by createdDate desc limit 1 ")
    SmsSendHistoryEntity findByPhone(String phone);
}
