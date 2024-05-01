package com.example.repository;

import com.example.entity.SmsSendHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsSendHistoryRepository extends CrudRepository<SmsSendHistoryEntity, String> {
    @Query("from SmsSendHistoryEntity where phone=?1 order by createdDate desc limit 1 ")
    SmsSendHistoryEntity findByPhone(String phone);

    @Query("from SmsSendHistoryEntity where createdDate between ?1 and ?2")
    List<SmsSendHistoryEntity> getByGiven(LocalDateTime fromDate, LocalDateTime toDate);

    Long countByPhoneAndCreatedDateBetween(String phone, LocalDateTime from, LocalDateTime to);

    @Query("SELECT count (s) from SmsSendHistoryEntity s where s.phone =?1 and s.createdDate between ?2 and ?3")
    Long countSendPhone(String phone, LocalDateTime from, LocalDateTime to);

}
