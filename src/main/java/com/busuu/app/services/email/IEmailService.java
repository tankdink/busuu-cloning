package com.busuu.app.services.email;

public interface IEmailService {

    void sendEmailActive(String email, String activeCode);
    void sendEmailForgotPassword(String email, String otpCode);
}
