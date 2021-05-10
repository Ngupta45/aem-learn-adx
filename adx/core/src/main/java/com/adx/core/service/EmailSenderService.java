package com.adx.core.service;


import java.util.List;

public interface EmailSenderService {
    void sendMail(List<String> content);
}
