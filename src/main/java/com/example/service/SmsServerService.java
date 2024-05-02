package com.example.service;

import com.example.entity.SmsSendHistoryEntity;
import com.example.repository.SmsSendHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SmsServerService {
    @Value("${my.iformagic.com.username}")
    private String email;
    @Value("${my.iformagic.com.password}")
    private String password;
    @Value("${sms.fly.uz.url}")
    private String url;
    @Autowired
    private SmsSendHistoryRepository smsSendHistoryRepository;

    public void send(String phone, String text) {
        log.info("send sms {}{}", phone, text);
        SmsSendHistoryEntity smsSendHistoryEntity = new SmsSendHistoryEntity();
        smsSendHistoryEntity.setPhone(phone);
        smsSendHistoryEntity.setMessage(text);
        smsSendHistoryRepository.save(smsSendHistoryEntity);
        sendSmsHTTP(phone, text);
    }

    public void sendSmsHTTP(String phone, String text) {
        String token = "Bearer " + getTokenWithAuthorization();
        OkHttpClient client = new OkHttpClient();

        if (phone.startsWith("+")) {
            phone = phone.substring(1);
        }
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("mobile_phone", phone)
                .addFormDataPart("message", text)
                .addFormDataPart("from", "4546")
                .build();
        Request request = new Request.Builder()
                .url(url + "/api/message/sms/send")
                .method("POST", body)
                .header("Authorization", token)
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("sms service {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getTokenWithAuthorization() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .addFormDataPart("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url + "/api/auth/login")
                .method("POST", body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException();
            } else {
                assert response.body() != null;
                JSONObject object = new JSONObject(response.body().string());
                JSONObject data = object.getJSONObject("data");
                Object token = data.get("token");
                return token.toString();
            }
        } catch (IOException e) {
            log.error("Sms service getTokenWithAuthorization {}", e.getMessage());
            throw new RuntimeException();
        }
    }

}
