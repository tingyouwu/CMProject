package com.kw.app.ronglib.fakeserver;

public class FakeServer {

    public static final String APP_KEY = "mgb7ka1nbkvig";
    public static final String APP_SECRET = "P14ukWPuXEXsNB";

    /**
     * 获取融云Token, 通过调用融云ServerApi获得.
     * @param user
     * @param callback
     */
    public static void getToken(FakeUser user, HttpUtil.OnResponse callback) {
        final String HTTP_GET_TOKEN = "https://api.cn.ronghub.com/user/getToken.json";
        HttpUtil.Header header = HttpUtil.getRcHeader(APP_KEY, APP_SECRET);
        String body = "userId=" + user.getUserid() + "&name=" + user.getUsername() + "&portraitUri=" + user.getUserlogo();
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.post(HTTP_GET_TOKEN, header, body, callback);
    }
}