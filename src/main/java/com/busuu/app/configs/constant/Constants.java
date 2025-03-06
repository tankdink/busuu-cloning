package com.busuu.app.configs.constant;

public class Constants {
    public static final String USER = "/users";
    public static final String ROLE = "/roles";
    public static final String LEVEL = "/levels";
    public static final String CHAPTER = "/chapters";
    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String UPLOAD_AVATAR = "/upload_avatar";
    public static final String PATH_PARAM_ID = "/{id}";

    public static class URI {
        public static final String INDEX = "/index";
    }

    public static class ERROR_CODE {
        public static final String ERR_UNAUTHORIZED = "ERR_UNAUTHORIZED";
        public static final String ERR_REGISTER_USER = "ERR_REGISTER_USER";
        public static final String ERR_LOGIN_USER = "ERR_LOGIN_USER";
        public static final String ERR_CREATE_ROLE = "ERR_CREATE_ROLE";
        public static final String ERR_GET_ROLE = "ERR_GET_ROLE";
        public static final String ERR_UPDATE_ROLE = "ERR_UPDATE_ROLE";
        public static final String ERR_DELETE_ROLE = "ERR_DELETE_ROLE";

        //Level entity
        public static final String ERR_CREATE_NEW_LEVEL = "ERR_CREATE_NEW_LEVEL";
        public static final String ERR_GET_ALL_LEVEL = "ERR_GET_ALL_LEVEL";
        public static final String ERR_GET_LEVEL_BY_ID = "ERR_GET_LEVEL_BY_ID";
        public static final String ERR_UPDATE_LEVEL_BY_ID = "ERR_UPDATE_LEVEL_BY_ID";
        public static final String ERR_DELETE_LEVEL_BY_ID = "ERR_DELETE_LEVEL_BY_ID";
    }


}
