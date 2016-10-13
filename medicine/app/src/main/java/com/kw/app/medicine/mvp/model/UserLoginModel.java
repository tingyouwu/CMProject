package com.kw.app.medicine.mvp.model;

import android.content.Context;

import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.base.BmobUserManager;
import com.kw.app.medicine.base.IUserManager;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IUserLoginContract;
import com.kw.app.widget.ICallBack;

/**
 * @author wty
 */
public class UserLoginModel implements IUserLoginContract.IUserLoginModel {

    private IUserManager userManager = BmobUserManager.getInstance();

    @Override
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin, final ICallBack<UserDALEx> callBack) {

        userManager.login(context, name, psw, isAutoLogin, new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                callBack.onSuccess(user);
                saveUserPreference(isAutoLogin,psw, user);
            }

            @Override
            public void onFaild(String msg) {
                callBack.onFaild(msg);
            }
        });
    }

    /**
     * @Decription 保存用户数据到preference
     **/
    private void saveUserPreference(boolean isAutoLogin,String psw,UserDALEx user){
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastName, user.getNickname());
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastAccount, user.getUserid());
        PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LogoUrl, user.getLogourl());
        if(isAutoLogin){
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, true);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword,psw);
        }else{
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsAutoLogin, false);
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LastPassword, "");
        }
    }

}
