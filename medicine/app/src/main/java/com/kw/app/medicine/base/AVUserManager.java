package com.kw.app.medicine.base;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
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
    public void register(UserDALEx user, final ICallBack<UserDALEx> callBack) {
        AVUser avuser = new AVUser();
        avuser.setUsername(user.getNickname());
        avuser.setPassword(user.getPassword());
        avuser.setEmail(user.getEmail());
        avuser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    //注册成功
                    callBack.onSuccess(new UserDALEx());
                }else{
                    //注册失败
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void login(Context context, String name, String psw, ICallBack<UserDALEx> callBack) {

    }
}
