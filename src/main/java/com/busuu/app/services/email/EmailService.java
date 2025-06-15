package com.busuu.app.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;

    private void sendMessages(String to, String subject, String text) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        emailSender.send(message);
    }

    @Override
    public void sendEmailActive(String email, String activeCode) {
        String subject = "Confirm Your Busuu Account";
        String text = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <title>Busuu Activation Email</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: 'Helvetica Neue', Arial, sans-serif;\n" +
                "      background-color: #f4f7fa;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      color: #333333;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background: #ffffff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background: linear-gradient(135deg, #0056b3, #00aaff);\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 30px 20px;\n" +
                "    }\n" +
                "    .header img {\n" +
                "      max-height: 60px;\n" +
                "      margin-bottom: 10px;\n" +
                "    }\n" +
                "    .header h1 {\n" +
                "      margin: 0;\n" +
                "      font-size: 24px;\n" +
                "      font-weight: 600;\n" +
                "    }\n" +
                "    .content {\n" +
                "      padding: 30px 20px;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .content p {\n" +
                "      font-size: 16px;\n" +
                "      line-height: 1.6;\n" +
                "      margin: 0 0 20px;\n" +
                "    }\n" +
                "    .code {\n" +
                "      font-size: 28px;\n" +
                "      font-weight: bold;\n" +
                "      color: #0056b3;\n" +
                "      background: #e6f0fa;\n" +
                "      padding: 15px 20px;\n" +
                "      border-radius: 6px;\n" +
                "      margin: 20px auto;\n" +
                "      display: inline-block;\n" +
                "      letter-spacing: 2px;\n" +
                "    }\n" +
                "    .cta-button {\n" +
                "      display: inline-block;\n" +
                "      padding: 14px 30px;\n" +
                "      background: #00aaff;\n" +
                "      color: #ffffff;\n" +
                "      font-size: 16px;\n" +
                "      font-weight: 600;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 6px;\n" +
                "      margin: 20px 0;\n" +
                "      transition: background 0.3s ease;\n" +
                "    }\n" +
                "    .cta-button:hover {\n" +
                "      background: #0056b3;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      background: #f9f9f9;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      font-size: 14px;\n" +
                "      color: #666666;\n" +
                "    }\n" +
                "    .footer a {\n" +
                "      color: #00aaff;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "    .footer a:hover {\n" +
                "      text-decoration: underline;\n" +
                "    }\n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "      .container {\n" +
                "        margin: 10px;\n" +
                "        border-radius: 0;\n" +
                "      }\n" +
                "      .header h1 {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "      .header img {\n" +
                "        max-height: 50px;\n" +
                "      }\n" +
                "      .code {\n" +
                "        font-size: 24px;\n" +
                "      }\n" +
                "      .content p {\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "      .cta-button {\n" +
                "        padding: 12px 20px;\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <img src=\"https://res.cloudinary.com/didu61lbz/image/upload/v1749396947/svgviewert_b56rgr.png\" alt=\"Busuu Logo\">\n" +
                "      <h1>Welcome to Busuu!</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Hello " + email + ",</p>\n" +
                "      <p>Thank you for registering an account at Busuu. Below is your activation code to complete the registration process:</p>\n" +
                "      <div class=\"code\">" + activeCode + "</div>\n" +
                "      <p>Please use this code within 24 hours to activate your account.</p>\n" +
                "      <a href=\"[Activation Link]\" class=\"cta-button\">Activate Account Now</a>\n" +
                "      <p>If you have any questions, feel free to contact us at <a href=\"mailto:support@busuu.com\">support@busuu.com</a>.</p>\n" +
                "      <p>Wish you a joyful learning experience!</p>\n" +
                "      <p>The Busuu Team</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>© 2025 Busuu. All rights reserved.</p>\n" +
                "      <p><a href=\"https://www.busuu.com\">Visit our website</a> | <a href=\"mailto:support@busuu.com\">Contact Support</a></p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        sendMessages(email, subject, text);
    }

    @Override
    public void sendEmailOtp(String email, String otpCode) {
        String subject = "Password Reset Request At Busuu";
        String text = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <title>Busuu Forgot Password</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: 'Helvetica Neue', Arial, sans-serif;\n" +
                "      background-color: #f4f7fa;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      color: #333333;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background: #ffffff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background: linear-gradient(135deg, #0056b3, #00aaff);\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 30px 20px;\n" +
                "    }\n" +
                "    .header img {\n" +
                "      max-height: 60px;\n" +
                "      margin-bottom: 10px;\n" +
                "    }\n" +
                "    .header h1 {\n" +
                "      margin: 0;\n" +
                "      font-size: 24px;\n" +
                "      font-weight: 600;\n" +
                "    }\n" +
                "    .content {\n" +
                "      padding: 30px 20px;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .content p {\n" +
                "      font-size: 16px;\n" +
                "      line-height: 1.6;\n" +
                "      margin: 0 0 20px;\n" +
                "    }\n" +
                "    .code {\n" +
                "      font-size: 28px;\n" +
                "      font-weight: bold;\n" +
                "      color: #0056b3;\n" +
                "      background: #e6f0fa;\n" +
                "      padding: 15px 20px;\n" +
                "      border-radius: 6px;\n" +
                "      margin: 20px auto;\n" +
                "      display: inline-block;\n" +
                "      letter-spacing: 2px;\n" +
                "    }\n" +
                "    .cta-button {\n" +
                "      display: inline-block;\n" +
                "      padding: 14px 30px;\n" +
                "      background: #00aaff;\n" +
                "      color: #ffffff;\n" +
                "      font-size: 16px;\n" +
                "      font-weight: 600;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 6px;\n" +
                "      margin: 20px 0;\n" +
                "      transition: background 0.3s ease;\n" +
                "    }\n" +
                "    .cta-button:hover {\n" +
                "      background: #0056b3;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      background: #f9f9f9;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      font-size: 14px;\n" +
                "      color: #666666;\n" +
                "    }\n" +
                "    .footer a {\n" +
                "      color: #00aaff;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "    .footer a:hover {\n" +
                "      text-decoration: underline;\n" +
                "    }\n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "      .container {\n" +
                "        margin: 10px;\n" +
                "        border-radius: 0;\n" +
                "      }\n" +
                "      .header h1 {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "      .header img {\n" +
                "        max-height: 50px;\n" +
                "      }\n" +
                "      .code {\n" +
                "        font-size: 24px;\n" +
                "      }\n" +
                "      .content p {\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "      .cta-button {\n" +
                "        padding: 12px 20px;\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <img src=\"https://res.cloudinary.com/didu61lbz/image/upload/v1749396947/svgviewert_b56rgr.png\" alt=\"Busuu Logo\">\n" +
                "      <h1>Password Reset Request</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Hello " + email + ",</p>\n" +
                "      <p>We received a request to reset your password for your Busuu account. Below is your One-Time Password (OTP) to proceed with the reset:</p>\n" +
                "      <div class=\"code\">" + otpCode + "</div>\n" +
                "      <p>Please use this OTP within 15 minutes to reset your password. If you didn’t request this, please ignore this email or contact us immediately.</p>\n" +
                "      <a href=\"[Reset Password Link]\" class=\"cta-button\">Reset Password Now</a>\n" +
                "      <p>If you have any questions, feel free to contact us at <a href=\"mailto:support@busuu.com\">support@busuu.com</a>.</p>\n" +
                "      <p>Best regards,</p>\n" +
                "      <p>The Busuu Team</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>© 2025 Busuu. All rights reserved.</p>\n" +
                "      <p><a href=\"https://www.busuu.com\">Visit our website</a> | <a href=\"mailto:support@busuu.com\">Contact Support</a></p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        sendMessages(email, subject, text);
    }

    @Override
    public void sendEmailChangedPassword(String email) {
        String subject = "Busuu Password Changed Successfully";
        String text = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <title>Busuu Password Change Notification</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: 'Helvetica Neue', Arial, sans-serif;\n" +
                "      background-color: #f4f7fa;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      color: #333333;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background: #ffffff;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);\n" +
                "      overflow: hidden;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background: linear-gradient(135deg, #0056b3, #00aaff);\n" +
                "      color: #ffffff;\n" +
                "      text-align: center;\n" +
                "      padding: 30px 20px;\n" +
                "    }\n" +
                "    .header img {\n" +
                "      max-height: 60px;\n" +
                "      margin-bottom: 10px;\n" +
                "    }\n" +
                "    .header h1 {\n" +
                "      margin: 0;\n" +
                "      font-size: 24px;\n" +
                "      font-weight: 600;\n" +
                "    }\n" +
                "    .content {\n" +
                "      padding: 30px 20px;\n" +
                "      text-align: center;\n" +
                "    }\n" +
                "    .content p {\n" +
                "      font-size: 16px;\n" +
                "      line-height: 1.6;\n" +
                "      margin: 0 0 20px;\n" +
                "    }\n" +
                "    .cta-button {\n" +
                "      display: inline-block;\n" +
                "      padding: 14px 30px;\n" +
                "      background: #00aaff;\n" +
                "      color: #ffffff;\n" +
                "      font-size: 16px;\n" +
                "      font-weight: 600;\n" +
                "      text-decoration: none;\n" +
                "      border-radius: 6px;\n" +
                "      margin: 20px 0;\n" +
                "      transition: background 0.3s ease;\n" +
                "    }\n" +
                "    .cta-button:hover {\n" +
                "      background: #0056b3;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      background: #f9f9f9;\n" +
                "      padding: 20px;\n" +
                "      text-align: center;\n" +
                "      font-size: 14px;\n" +
                "      color: #666666;\n" +
                "    }\n" +
                "    .footer a {\n" +
                "      color: #00aaff;\n" +
                "      text-decoration: none;\n" +
                "    }\n" +
                "    .footer a:hover {\n" +
                "      text-decoration: underline;\n" +
                "    }\n" +
                "    @media only screen and (max-width: 600px) {\n" +
                "      .container {\n" +
                "        margin: 10px;\n" +
                "        border-radius: 0;\n" +
                "      }\n" +
                "      .header h1 {\n" +
                "        font-size: 20px;\n" +
                "      }\n" +
                "      .header img {\n" +
                "        max-height: 50px;\n" +
                "      }\n" +
                "      .content p {\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "      .cta-button {\n" +
                "        padding: 12px 20px;\n" +
                "        font-size: 14px;\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <img src=\"https://res.cloudinary.com/didu61lbz/image/upload/v1749396947/svgviewert_b56rgr.png\" alt=\"Busuu Logo\">\n" +
                "      <h1>Password Changed Successfully</h1>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Hello " + email + ",</p>\n" +
                "      <p>We’re writing to inform you that your Busuu account password has been successfully changed.</p>\n" +
                "      <p>If you made this change, no further action is required. You can continue enjoying your learning journey with Busuu!</p>\n" +
                "      <p>If you did not request this change, please secure your account immediately by resetting your password or contacting our support team.</p>\n" +
                "      <a href=\"https://www.busuu.com/reset-password\" class=\"cta-button\">Reset Password</a>\n" +
                "      <p>For any questions or assistance, feel free to contact us at <a href=\"mailto:support@busuu.com\">support@busuu.com</a>.</p>\n" +
                "      <p>Thank you for choosing Busuu!</p>\n" +
                "      <p>The Busuu Team</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>© 2025 Busuu. All rights reserved.</p>\n" +
                "      <p><a href=\"https://www.busuu.com\">Visit our website</a> | <a href=\"mailto:support@busuu.com\">Contact Support</a></p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
        sendMessages(email, subject, text);
    }
}
