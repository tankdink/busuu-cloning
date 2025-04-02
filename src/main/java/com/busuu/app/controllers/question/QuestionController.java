package com.busuu.app.controllers.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.services.question.IQuestionService;
import com.busuu.app.utils.LocalizationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(Constants.QUESTION)
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final IQuestionService questionService;
    private final LocalizationUtils localizationUtils;

}
