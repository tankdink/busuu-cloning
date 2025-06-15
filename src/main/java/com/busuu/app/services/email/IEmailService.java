package com.busuu.app.services.email;

public interface IEmailService {

    void sendEmailActive(String email, String activeCode);
    void sendEmailOtp(String email, String otpCode);
    void sendEmailChangedPassword(String email);
}
