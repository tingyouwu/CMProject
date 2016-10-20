package com.kw.app.medicine.avcloud;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.kw.app.medicine.base.IUserManager;
import com.kw.app.medicine.data.avcloud.UserAVCloud;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.util.Arrays;
import java.util.List;

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

        UserAVCloud avUser = AVUserUtil.convertToAVUser(user);
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e==null){
                    //注册成功
                    UserDALEx user = AVUserUtil.convertToLocal(AVUser.getCurrentUser(UserAVCloud.class));
                    callBack.onSuccess(user);
                }else{
                    //注册失败
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void login(Context context, String name, String psw, final ICallBack<UserDALEx> callBack) {
        UserAVCloud.logInInBackground(name, psw, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if(e == null){
                    //登陆成功
                    callBack.onSuccess(AVUserUtil.convertToLocal(AVUser.getCurrentUser(UserAVCloud.class)));
                }else{
                    //登陆失败
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void queryUsers(String username, final int limit, final ICallBack<List<UserDALEx>> callBack) {
        //包含username 但不能是我自己
        AVQuery<UserAVCloud> userQuery1 = AVObject.getQuery(UserAVCloud.class);
        userQuery1.whereNotEqualTo("username",AVUser.getCurrentUser().getUsername());

        AVQuery<UserAVCloud> userQuery2 = AVObject.getQuery(UserAVCloud.class);
        userQuery2.whereContains("username",username);

        AVQuery<UserAVCloud> userQuery3 = AVQuery.and(Arrays.asList(userQuery1,userQuery2));

        userQuery3.setLimit(limit);
        userQuery3.orderByDescending("createdAt");
        userQuery3.findInBackground(new FindCallback<UserAVCloud>() {
            @Override
            public void done(List<UserAVCloud> list, AVException e) {
                if(e == null){
                    //查找成功
                    if(list != null && list.size()>0){
                        callBack.onSuccess(AVUserUtil.convertToLocal(list));
                    }else{
                        callBack.onFaild("查无此人");
                    }
                }else{
                    //查找失败
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void getUserInfo(String userid) {
        queryUserInfo(userid, new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx data) {
                data.saveOrUpdate();
            }

            @Override
            public void onFaild(String msg) {

            }
        });
    }

    @Override
    public void queryUserInfo(String objectId, final ICallBack<UserDALEx> callBack) {
        AVQuery<UserAVCloud> userQuery = AVObject.getQuery(UserAVCloud.class);
        userQuery.whereEqualTo("objectId",objectId);
        userQuery.findInBackground(new FindCallback<UserAVCloud>() {
            @Override
            public void done(List<UserAVCloud> list, AVException e) {
                if(e==null){
                    if(list != null && list.size()>0){
                        callBack.onSuccess(AVUserUtil.convertToLocal(list.get(0)));
                    }else{
                        callBack.onFaild("查无此人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void updateUserIcon(String path, ICallBack<String> callBack) {
        UserAVCloud avCloud = AVUser.getCurrentUser(UserAVCloud.class);
        avCloud.setLogourl(path);
        avCloud.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){

                }else{

                }
            }
        });
    }

    @Override
    public void updateUserName(String name, ICallBack<String> callBack) {
        UserAVCloud avCloud = AVUser.getCurrentUser(UserAVCloud.class);
        avCloud.setUsername(name);
        avCloud.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){

                }else{

                }
            }
        });
    }

    @Override
    public void updateUserPSW(String psw, ICallBack<String> callBack) {

    }

    @Override
    public void updateUserRemark(String remark, ICallBack<String> callBack) {

    }
}
