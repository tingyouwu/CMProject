package com.kw.app.medicine.data.avcloud;

import com.avos.avoscloud.AVClassName;
import com.kw.app.avcloudlib.BaseAVObject;
import com.kw.app.avcloudlib.annotation.AVObjectDao;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.FriendRelationDALEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友关系表
 */
@AVClassName("FriendAVCloud")
public class FriendAVCloud extends BaseAVObject {

    private UserBmob user;
    private UserBmob friendUser;//关联的对象
    private int status;

    public static FriendAVCloud get(){
        return AVObjectDao.getDao(FriendAVCloud.class);
    }

    public void save(final List<FriendAVCloud> list){
        List<FriendRelationDALEx> localdalex = bmobToLocal(list);
        FriendRelationDALEx.get().saveOrUpdate(localdalex);
    }

    public List<FriendRelationDALEx> bmobToLocal(List<FriendAVCloud> list){
        List<FriendRelationDALEx> localdalex = new ArrayList<FriendRelationDALEx>();
        for(FriendAVCloud friend:list){
            FriendRelationDALEx dalex = new FriendRelationDALEx();
            dalex.setRelationid(friend.getObjectId());
            dalex.setFriendid(friend.getFriendUser().getObjectId());
            dalex.setStatus(friend.getStatus());
            friend.getFriendUser().save(friend.getFriendUser());//保存用户信息
            localdalex.add(dalex);
        }
        return localdalex;
    }

    public UserBmob getUser() {
        return user;
    }

    public void setUser(UserBmob user) {
        this.user = user;
    }

    public UserBmob getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(UserBmob friendUser) {
        this.friendUser = friendUser;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
