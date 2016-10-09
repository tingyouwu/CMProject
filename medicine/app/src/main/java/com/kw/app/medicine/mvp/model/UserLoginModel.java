package com.kw.app.medicine.mvp.model;

import android.content.Context;


import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.mvp.contract.IUserLoginContract;
import com.kw.app.widget.ICallBack;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author wty
 */
public class UserLoginModel implements IUserLoginContract.IUserLoginModel {

    @Override
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin, final ICallBack<UserBmob> callBack) {
        final UserBmob bu2 = new UserBmob();
        bu2.setUsername(name);
        bu2.setPassword(psw);
        //首先尝试用户名+密码登陆
        bu2.login(new SaveListener<UserBmob>() {

            @Override
            public void done(UserBmob userBmob, BmobException e) {
                if(e==null){
                    callBack.onSuccess(userBmob);
                    saveUserPreference(isAutoLogin,psw, BmobUser.getCurrentUser(UserBmob.class));
                }else {
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    /**
     * @Decription 保存用户数据到preference
     **/
    private void saveUserPreference(boolean isAutoLogin,String psw,UserBmob bmobUser){
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastName, bmobUser.getUsername());
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastAccount, bmobUser.getObjectId());
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LogoUrl, bmobUser.getLogourl());
        if(isAutoLogin){
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, true);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword,psw);
        }else{
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, false);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword, "");
        }
    }

}
