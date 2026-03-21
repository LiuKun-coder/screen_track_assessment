package org.example.sc_backend.service;

public interface SmsCodeService {
    void sendCode(String phone, String type);

    void validateCode(String phone, String type, String code);
}
