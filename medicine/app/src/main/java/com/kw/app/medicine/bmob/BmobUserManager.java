package com.kw.app.medicine.bmob;

import android.content.Context;

import com.kw.app.medicine.base.IUserManager;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class BmobUserManager implements IUserManager {

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

    @Override
    public void queryUsers(String username, int limit, final ICallBack<List<UserDALEx>> callBack) {
        BmobQuery<UserBmob> query = new BmobQuery<>();
        //去掉当前用户
        final UserBmob user = UserBmob.getCurrentUser(UserBmob.class);
        query.addWhereNotEqualTo("username",user.getUsername());
        query.addWhereContains("username", username);
        query.setLimit(limit);
        query.order("-createdAt");

        query.findObjects(new FindListener<UserBmob>() {
            @Override
            public void done(List<UserBmob> list, BmobException e) {
                if(e==null){
                    if (list != null && list.size() > 0) {
                        callBack.onSuccess(user.convert(list));
                    } else {
                        callBack.onFaild("查无此人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void getUserInfo(String userid) {
        queryUserInfo(userid, new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                user.saveOrUpdate();
            }

            @Override
            public void onFaild(String msg) {

            }
        });
    }

    @Override
    public void queryUserInfo(String objectId, final ICallBack<UserDALEx> callBack) {
        BmobQuery<UserBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(new FindListener<UserBmob>() {
            @Override
            public void done(List<UserBmob> list, BmobException e) {
                if(e==null){
                    if (list != null && list.size() > 0) {
                        callBack.onSuccess(UserBmob.convertToDALEx(list.get(0)));
                    } else {
                        callBack.onFaild("查无此人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }
}
