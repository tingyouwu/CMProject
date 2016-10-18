package com.kw.app.medicine.base;

import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author :wty
 * 管理朋友信息
 */
public interface IFriendManager {

    /**
     * @Decription 增量更新朋友信息
     **/
    void loadFriends(String updatetime, final ICallBack<List<UserDALEx>> callBack);

    /**
     * @Decription 把userid加为我的好友
     **/
    void addFriend(String userid);

    /**
     * @Decription 同意将user加成我的好友
     **/
    void agreeAddFriend(UserDALEx user, final ICallBack<String> callBack);

}
