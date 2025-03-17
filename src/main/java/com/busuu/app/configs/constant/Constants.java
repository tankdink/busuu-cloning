package com.busuu.app.configs.constant;

public class Constants {
    public static final String USER = "/users";
    public static final String ROLE = "/roles";
    public static final String LEVEL = "/levels";
    public static final String CHAPTER = "/chapters";
    public static final String COURSE = "/courses";
    public static final String LESSON = "/lessons";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String UPLOAD_AVATAR = "/upload_avatar";
    public static final String PATH_PARAM_ID = "/{id}";

    public static class URI {
        public static final String INDEX = "/index";
    }

    public static class ERROR_CODE {
        public static final String ERR_UNAUTHORIZED = "ERR_UNAUTHORIZED";
        public static final String ERR_CREATE_USER = "ERR_CREATE_USER";
        public static final String ERR_REGISTER_USER = "ERR_REGISTER_USER";
        public static final String ERR_LOGIN_USER = "ERR_LOGIN_USER";

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
        public static final String ERR_UPDATE_LEVEL_BY_ID = "ERR_UPDATE_LEVEL_BY_ID";
        public static final String ERR_DELETE_LEVEL_BY_ID = "ERR_DELETE_LEVEL_BY_ID";

        //Chapter
        public static final String ERR_CREATE_NEW_CHAPTER = "ERR_CREATE_NEW_CHAPTER";
        public static final String ERR_GET_ALL_CHAPTER = "ERR_GET_ALL_CHAPTER";
        public static final String ERR_GET_CHAPTER_BY_ID = "ERR_GET_CHAPTER_BY_ID";
        public static final String ERR_GET_CHAPTER_BY_COURSE_ID_AND_LEVEL_ID = "ERR_GET_CHAPTER_BY_COURSE_ID_AND_LEVEL_ID";
        public static final String ERR_UPDATE_CHAPTER_BY_ID = "ERR_UPDATE_CHAPTER_BY_ID";
        public static final String ERR_DELETE_CHAPTER_BY_ID = "ERR_DELETE_CHAPTER_BY_ID";

    }


}
