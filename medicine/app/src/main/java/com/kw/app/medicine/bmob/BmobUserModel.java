package com.kw.app.medicine.bmob;

import android.content.Context;
import android.text.TextUtils;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.base.CMApplication;
import com.kw.app.medicine.data.bmob.FriendBmob;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.widget.ICallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class BmobUserModel {

    public static final int DEFAULT_LIMIT=50;

    public Context getContext(){
        return CMApplication.getInstance();
    }

    private static BmobUserModel ourInstance = new BmobUserModel();

    public static BmobUserModel getInstance() {
        return ourInstance;
    }

    private BmobUserModel() {}

    /**查询用户
     * @param username
     * @param limit
     * @param callBack
     */
    public void queryUsers(String username,int limit,final ICallBack<List<UserBmob>> callBack){
        BmobQuery<UserBmob> query = new BmobQuery<>();
        //去掉当前用户
        UserBmob user = UserBmob.getCurrentUser(UserBmob.class);
        query.addWhereNotEqualTo("username",user.getUsername());
        query.addWhereContains("username", username);
        query.setLimit(limit);
        query.order("-createdAt");

        query.findObjects(new FindListener<UserBmob>() {
            @Override
            public void done(List<UserBmob> list, BmobException e) {
                if(e==null){
                    if (list != null && list.size() > 0) {
                        callBack.onSuccess(list);
                    } else {
                        callBack.onFaild("查无此人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });

    }

    /**查询用户信息
     * @param objectId
     * @param callBack
     */
    public void queryUserInfo(String objectId, final ICallBack<UserBmob> callBack){
        BmobQuery<UserBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(new FindListener<UserBmob>() {
            @Override
            public void done(List<UserBmob> list, BmobException e) {
                if(e==null){
                    if (list != null && list.size() > 0) {
                        callBack.onSuccess(list.get(0));
                    } else {
                        callBack.onFaild("查无此人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    /**
     * @更新用户信息
     **/
    public void updateUserInfo(String userid){

        BmobUserModel.getInstance().queryUserInfo(userid, new ICallBack<UserBmob>() {
            @Override
            public void onSuccess(UserBmob user) {
                user.save(user);
            }

            @Override
            public void onFaild(String msg) {

            }
        });

    }

    /**
     * 同意添加好友：添加对方到自己的好友列表中
     */
    public void agreeAddFriend(UserBmob friend, final ICallBack<String> callBack){
        FriendBmob f = new FriendBmob();
        UserBmob user = BmobUser.getCurrentUser(UserBmob.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.setStatus(1);//1表示我添加为好友  0表示我删除这条好友关系
        f.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    callBack.onSuccess("");
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    /**
     * 查询好友
     */
    public void queryFriends(String updatetime, final ICallBack<List<FriendBmob>> callBack){
        BmobQuery<FriendBmob> query = new BmobQuery<FriendBmob>();
        UserBmob user = BmobUser.getCurrentUser(UserBmob.class);
        query.addWhereEqualTo("user", user);

        if(!TextUtils.isEmpty(updatetime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = sdf.parse(updatetime);
                query.addWhereGreaterThanOrEqualTo("updatedAt",new BmobDate(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        query.include("friendUser");
        query.findObjects(new FindListener<FriendBmob>() {
            @Override
            public void done(List<FriendBmob> list, BmobException e) {
                if(e==null){
                    if (list != null && list.size() > 0) {
                        callBack.onSuccess(list);
                    } else {
                        callBack.onFaild("暂无联系人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    /**
     * 删除好友
     * @param f
     * @param listener
     */
    public void deleteFriend(FriendBmob f, final ICallBack<String> listener){
        FriendBmob friend =new FriendBmob();
        friend.setObjectId(f.getObjectId());
        friend.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    listener.onSuccess("删除成功");
                }else{
                    listener.onFaild("删除失败:"+e.getMessage());
                }
            }
        });
    }

    /**
     * 添加对方为自己的好友
     * @param uid
     */
    public void addFriend(String uid){
        UserBmob user =new UserBmob();
        user.setObjectId(uid);
        BmobUserModel.getInstance().agreeAddFriend(user, new ICallBack<String>() {
            @Override
            public void onSuccess(String data) {
                AppLogUtil.i("onSuccess");
            }

            @Override
            public void onFaild(String msg) {
                AppLogUtil.i("onFailure:" + msg);
            }
        });
    }

}
