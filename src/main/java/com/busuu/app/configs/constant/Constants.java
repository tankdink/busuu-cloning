package com.busuu.app.configs.constant;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    @Value("${api.prefix}")
    public static final String PREFIX = "";
    public static final String USER = "/users";
    public static final String ROLE = "/roles";
    public static final String LEVEL = "/levels";
    public static final String CHAPTER = "/chapters";
    public static final String COURSE = "/courses";
    public static final String LESSON = "/lessons";
    public static final String PROGRESS = "/progresses";
    public static final String AUTH = "/auth";
    public static final String SOCIAL_LOGIN = "/social_login";
    public static final String SOCIAL = "/social";
    public static final String CALLBACK = "/callback";


    public static final String QUESTION = "/questions";
    public static final String ANSWER = "/answers";
    public static final String QUESTION_TYPE = "/{type}";
    public static final String FILL_BLANK = "/fill_blank";
    public static final String TRUE_FALSE = "/true_false";
    public static final String MATCHING_PAIR = "/matching_pair";
    public static final String MULTIPLE_CHOICE = "/multiple_choice";
    public static final String ORDERING_PART = "/ordering_part";
    public static final String KNOWLEDGE = "/knowledge";
    public static final String LANGUAGE = "/languages";
    public static final String GRAMMAR = "/grammars";
    public static final String GRAMMAR_SECTION = "/grammar_sections";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String DETAILS = "/details";
    public static final String UPLOAD_AVATAR = "/upload_avatar";
    public static final String EMAIL_UNIQUE = "/email_unique";
    public static final String ACTIVE_ACCOUNT = "/active_account";
    public static final String REFRESH_TOKEN = "/refresh_token";
    public static final String CHANGE_PASSWORD = "/change_password";
    public static final String GENERATE_OTP = "/generate_otp";
    public static final String CHECK_OTP = "/check_otp";
    public static final String BLOCK = "/block";

    public static final String PATH_PARAM_ID = "/{id}";

    public static class URI {
        public static final String INDEX = "/index";
    }

    public static final Double PASSING_PROGRESS = 80.0;

    public static class SOCIAL_TYPE {
        public static final String FACEBOOK = "facebook";
        public static final String GOOGLE = "google";
    }


    public static class ERROR_CODE {
        public static final String ERR_UNAUTHORIZED = "ERR_UNAUTHORIZED";
        public static final String ERR_CREATE_USER = "ERR_CREATE_USER";
        public static final String ERR_REGISTER_USER = "ERR_REGISTER_USER";
        public static final String ERR_LOGIN_USER = "ERR_LOGIN_USER";
        public static final String ERR_GET_USER = "ERR_GET_USER";
        public static final String ERR_UPDATE_USER = "ERR_UPDATE_USER";

        public static final String ERR_ACTIVE_ACCOUNT = "ERR_ACTIVE_ACCOUNT";
        public static final String ERR_CHANGE_PASSWORD = "ERR_CHANGE_PASSWORD";
        public static final String ERR_GENERATE_OTP = "ERR_GENERATE_OTP";
        public static final String ERR_CHECK_OTP = "ERR_CHECK_OTP";


        // Role
        public static final String ERR_CREATE_ROLE = "ERR_CREATE_ROLE";
        public static final String ERR_GET_ROLE = "ERR_GET_ROLE";
        public static final String ERR_UPDATE_ROLE = "ERR_UPDATE_ROLE";
        public static final String ERR_DELETE_ROLE = "ERR_DELETE_ROLE";

        // Course
        public static final String ERR_CREATE_COURSE = "ERR_CREATE_COURSE";
        public static final String ERR_GET_COURSE = "ERR_GET_COURSE";
        public static final String ERR_UPDATE_COURSE = "ERR_UPDATE_COURSE";
        public static final String ERR_DELETE_COURSE = "ERR_DELETE_COURSE";

        // Lesson
        public static final String ERR_CREATE_LESSON = "ERR_CREATE_LESSON";
        public static final String ERR_GET_LESSON = "ERR_GET_LESSON";
        public static final String ERR_UPDATE_LESSON = "ERR_UPDATE_LESSON";
        public static final String ERR_DELETE_LESSON = "ERR_DELETE_LESSON";

        // Level
        public static final String ERR_CREATE_NEW_LEVEL = "ERR_CREATE_NEW_LEVEL";
        public static final String ERR_GET_ALL_LEVEL = "ERR_GET_ALL_LEVEL";
        public static final String ERR_GET_LEVEL_BY_ID = "ERR_GET_LEVEL_BY_ID";
        public static final String ERR_GET_LEVEL_BY_COURSE_ID = "ERR_GET_LEVEL_BY_COURSE_ID";
        public static final String ERR_GET_LEVEL_BY_CODE = "ERR_GET_LEVEL_BY_CODE";
        public static final String ERR_UPDATE_LEVEL_BY_ID = "ERR_UPDATE_LEVEL_BY_ID";
        public static final String ERR_DELETE_LEVEL_BY_ID = "ERR_DELETE_LEVEL_BY_ID";

        //Chapter
        public static final String ERR_CREATE_NEW_CHAPTER = "ERR_CREATE_NEW_CHAPTER";
        public static final String ERR_GET_ALL_CHAPTER = "ERR_GET_ALL_CHAPTER";
        public static final String ERR_GET_CHAPTER_BY_ID = "ERR_GET_CHAPTER_BY_ID";
        public static final String ERR_GET_CHAPTER_BY_COURSE_ID_AND_LEVEL_ID = "ERR_GET_CHAPTER_BY_COURSE_ID_AND_LEVEL_ID";
        public static final String ERR_UPDATE_CHAPTER_BY_ID = "ERR_UPDATE_CHAPTER_BY_ID";
        public static final String ERR_DELETE_CHAPTER_BY_ID = "ERR_DELETE_CHAPTER_BY_ID";

        // Question
        public static final String ERR_CREATE_QUESTION = "ERR_CREATE_QUESTION";
        public static final String ERR_GET_QUESTION = "ERR_GET_QUESTION";
        public static final String ERR_UPDATE_QUESTION = "ERR_UPDATE_QUESTION";
        public static final String ERR_DELETE_QUESTION = "ERR_DELETE_QUESTION";

        // Matching Pair
        public static final String ERR_CREATE_MATCHING_PAIR = "ERR_CREATE_MATCHING_PAIR";
        public static final String ERR_GET_MATCHING_PAIR = "ERR_GET_MATCHING_PAIR";
        public static final String ERR_UPDATE_MATCHING_PAIR = "ERR_UPDATE_MATCHING_PAIR";
        public static final String ERR_DELETE_MATCHING_PAIR = "ERR_DELETE_MATCHING_PAIR";

        // Multiple Choice
        public static final String ERR_CREATE_MULTIPLE_CHOICE = "ERR_CREATE_MULTIPLE_CHOICE";
        public static final String ERR_GET_MULTIPLE_CHOICE = "ERR_GET_MULTIPLE_CHOICE";
        public static final String ERR_UPDATE_MULTIPLE_CHOICE = "ERR_UPDATE_MULTIPLE_CHOICE";
        public static final String ERR_DELETE_MULTIPLE_CHOICE = "ERR_DELETE_MULTIPLE_CHOICE";

        // Ordering Part
        public static final String ERR_CREATE_ORDERING_PART = "ERR_CREATE_ORDERING_PART";
        public static final String ERR_GET_ORDERING_PART = "ERR_GET_ORDERING_PART";
        public static final String ERR_UPDATE_ORDERING_PART = "ERR_UPDATE_ORDERING_PART";
        public static final String ERR_DELETE_ORDERING_PART = "ERR_DELETE_ORDERING_PART";

        //Language
        public static final String ERR_CREATE_NEW_LANGUAGE = "ERR_CREATE_NEW_LANGUAGE";
        public static final String ERR_GET_ALL_LANGUAGE = "ERR_GET_ALL_LANGUAGE";
        public static final String ERR_GET_LANGUAGE_BY_ID = "ERR_GET_LANGUAGE_BY_ID";
        public static final String ERR_UPDATE_LANGUAGE_BY_ID = "ERR_UPDATE_LANGUAGE_BY_ID";
        public static final String ERR_DELETE_LANGUAGE_BY_ID = "ERR_DELETE_LANGUAGE_BY_ID";

        //Grammar
        public static final String ERR_CREATE_NEW_GRAMMAR = "ERR_CREATE_NEW_GRAMMAR";
        public static final String ERR_GET_ALL_GRAMMAR = "ERR_GET_ALL_GRAMMAR";
        public static final String ERR_GET_GRAMMAR_BY_ID = "ERR_GET_GRAMMAR_BY_ID";
        public static final String ERR_GET_GRAMMAR_BY_LANGUAGE_ID = "ERR_GET_GRAMMAR_BY_LANGUAGE_ID";
        public static final String ERR_UPDATE_GRAMMAR_BY_ID = "ERR_UPDATE_GRAMMAR_BY_ID";
        public static final String ERR_DELETE_GRAMMAR_BY_ID = "ERR_DELETE_GRAMMAR_BY_ID";

        //Grammar section
        public static final String ERR_CREATE_NEW_GRAMMAR_SECTION = "ERR_CREATE_NEW_GRAMMAR_SECTION";
        public static final String ERR_GET_ALL_GRAMMAR_SECTION = "ERR_GET_ALL_GRAMMAR_SECTION";
        public static final String ERR_GET_GRAMMAR_SECTION_BY_ID = "ERR_GET_GRAMMAR_SECTION_BY_ID";
        public static final String ERR_GET_GRAMMAR_SECTION_BY_GRAMMAR_ID = "ERR_GET_GRAMMAR_SECTION_BY_GRAMMAR_ID";
        public static final String ERR_UPDATE_GRAMMAR_SECTION_BY_ID = "ERR_UPDATE_GRAMMAR_SECTION_BY_ID";
        public static final String ERR_DELETE_GRAMMAR_SECTION_BY_ID = "ERR_DELETE_GRAMMAR_SECTION_BY_ID";

        // Progress
        public static final String ERR_GET_PROGRESS = "ERR_GET_PROGRESS";
        public static final String ERR_UPSERT_PROGRESS = "ERR_UPSERT_PROGRESS";
        public static final String ERR_DELETE_PROGRESS = "ERR_DELETE_PROGRESS";
    }


}
