package com.kw.app.ronglib.fakeserver;

/**
 * @Decription 模拟出来的用户信息
 **/
public class FakeUser {

    private String userid;//用户id
    private String username;//用户名
    private String userlogo;//用户logo

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserlogo() {
        return userlogo;
    }

    public void setUserlogo(String userlogo) {
        this.userlogo = userlogo;
    }
}