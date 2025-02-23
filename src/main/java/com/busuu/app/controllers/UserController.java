package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.services.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController(Constants.USER)
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;


}
