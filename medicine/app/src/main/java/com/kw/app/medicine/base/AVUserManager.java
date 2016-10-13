package com.kw.app.medicine.base;

import android.content.Context;

import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class AVUserManager implements IUserManager{

    private static AVUserManager ourInstance = new AVUserManager();

    public static AVUserManager getInstance() {
        return ourInstance;
    }

    private AVUserManager() {}

    @Override
    public void register(UserDALEx user, ICallBack<UserDALEx> callBack) {

    }

    @Override
    public void login(Context context, String name, String psw, boolean isAutoLogin, ICallBack<UserDALEx> callBack) {

    }
}
