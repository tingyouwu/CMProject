package com.kw.app.medicine.data.bmob;

import com.kw.app.bmoblib.BaseBmobObject;
import com.kw.app.bmoblib.annotation.BmobObjectDao;
import com.kw.app.medicine.data.local.FriendRelationDALEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友关系表
 */
public class FriendBmob extends BaseBmobObject {

    private UserBmob user;
    private UserBmob friendUser;//关联的对象
    private int status;

    public static FriendBmob get(){
        return BmobObjectDao.getDao(FriendBmob.class);
    }

    public void save(final List<FriendBmob> list){
        List<FriendRelationDALEx> localdalex = bmobToLocal(list);
        FriendRelationDALEx.get().saveOrUpdate(localdalex);
    }

    public List<FriendRelationDALEx> bmobToLocal(List<FriendBmob> list){
        List<FriendRelationDALEx> localdalex = new ArrayList<FriendRelationDALEx>();
        for(FriendBmob friend:list){
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
