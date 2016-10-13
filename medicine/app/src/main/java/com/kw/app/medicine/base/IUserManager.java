package com.kw.app.medicine.base;

import android.content.Context;

import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

/**
 * @author :wty
 * 管理用户信息
 */
public interface IUserManager {

    /**
     * @Decription 注册
     **/
    void register(final UserDALEx user, final ICallBack<UserDALEx> callBack);

    /**
     * @Decription 登陆
     **/
    void login(final Context context, final String name, final String psw, final boolean isAutoLogin, final ICallBack<UserDALEx> callBack);

}
