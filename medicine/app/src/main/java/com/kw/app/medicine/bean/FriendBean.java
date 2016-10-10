package com.kw.app.medicine.bean;

import com.kw.app.medicine.data.local.UserDALEx;

/**
 * 联系人model
 */
public class FriendBean {
    private UserDALEx user;
    private String sortkey;


    public UserDALEx getUser() {
        return user;
    }

    public void setUser(UserDALEx user) {
        this.user = user;
    }

    public String getSortkey() {
        return sortkey;
    }

    public void setSortkey(String sortkey) {
        this.sortkey = sortkey;
    }
}
