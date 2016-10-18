package com.kw.app.medicine.bmob;

import android.text.TextUtils;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.base.IFriendManager;
import com.kw.app.medicine.data.bmob.FriendBmob;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.UserDALEx;
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

/**
 * @author :wty
 * 管理用户信息  好友信息
 */
public class BmobFriendManager implements IFriendManager{

    private static BmobFriendManager ourInstance = new BmobFriendManager();

    public static BmobFriendManager getInstance() {
        return ourInstance;
    }

    private BmobFriendManager() {}


    @Override
    public void loadFriends(String updatetime, final ICallBack<List<UserDALEx>> callBack) {
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
                        FriendBmob.get().save(list);
                        callBack.onSuccess(UserDALEx.get().findAllFriend());
                    } else {
                        callBack.onFaild("暂无联系人");
                    }
                }else{
                    callBack.onFaild(e.getMessage());
                }
            }
        });
    }

    @Override
    public void addFriend(String userid) {
        UserDALEx user =UserDALEx.get();
        user.setUserid(userid);
        agreeAddFriend(user, new ICallBack<String>() {
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

    @Override
    public void agreeAddFriend(UserDALEx friend, final ICallBack<String> callBack) {
        FriendBmob f = new FriendBmob();
        UserBmob userBmob = BmobUser.getCurrentUser(UserBmob.class);

        UserBmob friendBmob = new UserBmob();
        friendBmob.setAnnotationField(friend);

        f.setUser(userBmob);
        f.setFriendUser(friendBmob);
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
}
