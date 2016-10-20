package com.kw.app.medicine.base;

import android.content.Context;

import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.util.List;

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
    void login(final Context context, final String name, final String psw,  final ICallBack<UserDALEx> callBack);

    /**
     * @Decription 根据输入查询用户
     **/
    void queryUsers(String username,int limit,final ICallBack<List<UserDALEx>> callBack);

    /**
     * @Decription 更新用户信息
     **/
    void getUserInfo(String userid);

    /**
     * @Decription 查找用户信息
     **/
    void queryUserInfo(String userid, final ICallBack<UserDALEx> callBack);

    /**
     * @Decription 更新头像
     **/
    void updateUserIcon(String path,final ICallBack<String> callBack);

    /**
     * @Decription 更新名字
     **/
    void updateUserName(String name,final ICallBack<String> callBack);

    /**
     * @Decription 更新密码
     **/
    void updateUserPSW(String psw,final ICallBack<String> callBack);

    /**
     * @Decription 更新个性签名
     **/
    void updateUserRemark(String remark,final ICallBack<String> callBack);

}
