package com.kw.app.medicine.bmob;

import android.content.Context;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.base.CMApplication;
import com.kw.app.medicine.data.bmob.FriendBmob;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.widget.ICallBack;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class BmobUserModel {

    public Context getContext(){
        return CMApplication.getInstance();
    }

    private static BmobUserModel ourInstance = new BmobUserModel();

    public static BmobUserModel getInstance() {
        return ourInstance;
    }

    private BmobUserModel() {}

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

}
