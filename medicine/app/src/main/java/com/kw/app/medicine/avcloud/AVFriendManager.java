package com.kw.app.medicine.avcloud;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.kw.app.medicine.base.IFriendManager;
import com.kw.app.medicine.data.avcloud.FriendAVCloud;
import com.kw.app.medicine.data.avcloud.UserAVCloud;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class AVFriendManager implements IFriendManager{

    private static AVFriendManager ourInstance = new AVFriendManager();

    public static AVFriendManager getInstance() {
        return ourInstance;
    }

    private AVFriendManager() {}


    @Override
    public void loadFriends(String updatetime, final ICallBack<List<UserDALEx>> callBack) {
        AVQuery<FriendAVCloud> userQuery1 = AVObject.getQuery(FriendAVCloud.class);
        UserAVCloud me = AVUser.getCurrentUser(UserAVCloud.class);
        userQuery1.whereEqualTo("user",AVObject.createWithoutData("_User",me.getObjectId()));
        userQuery1.include("friendUser");
        userQuery1.limit(1000);

        AVQuery<FriendAVCloud> userQuery2 = AVObject.getQuery(FriendAVCloud.class);

        if(!TextUtils.isEmpty(updatetime)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = sdf.parse(updatetime);
                userQuery2.whereGreaterThanOrEqualTo("updatedAt",date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        AVQuery<FriendAVCloud> userQuery = AVQuery.and(Arrays.asList(userQuery1,userQuery2));
        userQuery.findInBackground(new FindCallback<FriendAVCloud>() {
            @Override
            public void done(List<FriendAVCloud> list, AVException e) {
                if(e==null){
                    if(list != null && list.size()>0){
                        FriendAVCloud.get().save(list);
                        callBack.onSuccess(UserDALEx.get().findAllFriend());
                    }else{
                        callBack.onFaild("暂无联系人");
                    }
                }else{
                    callBack.onFaild("暂无联系人");
                }
            }
        });

    }

    @Override
    public void addFriend(String userid) {

    }

    @Override
    public void agreeAddFriend(UserDALEx user, ICallBack<String> callBack) {
        UserAVCloud me = AVUser.getCurrentUser(UserAVCloud.class);
        UserAVCloud friend = new UserAVCloud();
        friend.setObjectId(user.getUserid());
        FriendAVCloud friendAVCloud = new FriendAVCloud();
        friendAVCloud.setUser(me);
        friendAVCloud.setFriendUser(friend);
    }
}
