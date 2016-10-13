package com.kw.app.medicine.base;

import android.content.Context;

import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class BmobUserManager implements IUserManager{

    private static BmobUserManager ourInstance = new BmobUserManager();

    public static BmobUserManager getInstance() {
        return ourInstance;
    }

    private BmobUserManager() {}

    @Override
    public void register(UserDALEx user, final ICallBack<UserDALEx> callBack) {
        final UserBmob bmob = new UserBmob();
        bmob.setAnnotationField(user);
        bmob.signUp(new SaveListener<UserBmob>() {
            @Override
            public void done(UserBmob userBmob, BmobException e) {
                if(e==null){
                    //注册成功
                    callBack.onSuccess(userBmob.convertToDALEx(userBmob));
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void login(Context context, String name, final String psw, final ICallBack<UserDALEx> callBack) {
        final UserBmob bu2 = new UserBmob();
        bu2.setUsername(name);
        bu2.setPassword(psw);
        //首先尝试用户名+密码登陆
        bu2.login(new SaveListener<UserBmob>() {
            @Override
            public void done(UserBmob userBmob, BmobException e) {
                if(e==null){
                    callBack.onSuccess(userBmob.convertToDALEx(userBmob));
                }else {
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }
}
