package com.mjc.school.controller;

public final class RestApiConst {

    public static final String API_ROOT_PATH = "/api/v{apiVersion}";

    public static final String AUTHOR_API_ROOT_PATH = API_ROOT_PATH + "/authors";
    public static final String NEWS_API_ROOT_PATH = API_ROOT_PATH + "/news";

    public static final String TAG_API_ROOT_PATH = API_ROOT_PATH + "/tags";
    public static final String COMMENTS_API_ROOT_PATH = API_ROOT_PATH + "/comments";

    private RestApiConst(){
    }
}
