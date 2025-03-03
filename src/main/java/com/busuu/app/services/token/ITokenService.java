package com.busuu.app.services.token;

import com.busuu.app.entities.Token;
import com.busuu.app.entities.User;

public interface ITokenService {
    Token addToken (User user, String token, boolean isMobile);

    Token refreshToken (String refreshToken, User user) throws Exception;
}
