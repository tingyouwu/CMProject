package com.kw.app.medicine.data.avcloud;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;
import com.kw.app.avcloudlib.BaseAVObject;
import com.kw.app.avcloudlib.annotation.AVObjectDao;
import com.kw.app.medicine.avcloud.AVUserUtil;
import com.kw.app.medicine.data.local.FriendRelationDALEx;
import com.kw.app.medicine.data.local.UserDALEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友关系表
 */
@AVClassName("FriendAVCloud")
public class FriendAVCloud extends BaseAVObject {

    //此处为我们的默认实现，当然你也可以自行实现
    public static final Creator CREATOR = AVObjectCreator.instance;
    public static FriendAVCloud get(){
        return AVObjectDao.getDao(FriendAVCloud.class);
    }

    public void save(final List<FriendAVCloud> list){
        List<FriendRelationDALEx> localdalex = AVCloudToLocal(list);
        FriendRelationDALEx.get().saveOrUpdate(localdalex);
    }

    public List<FriendRelationDALEx> AVCloudToLocal(List<FriendAVCloud> list){
        List<FriendRelationDALEx> localdalex = new ArrayList<FriendRelationDALEx>();
        for(FriendAVCloud friend:list){
            FriendRelationDALEx dalex = new FriendRelationDALEx();
            dalex.setRelationid(friend.getObjectId());
            dalex.setFriendid(friend.getFriendUser().getObjectId());
            dalex.setStatus(friend.getStatus());
            UserDALEx userDALEx = AVUserUtil.convertToLocal(friend.getFriendUser());//保存用户信息
            userDALEx.saveOrUpdate();
            localdalex.add(dalex);
        }
        return localdalex;
    }



    public UserAVCloud getUser() {
        return this.getAVUser("user",UserAVCloud.class);
    }

    public void setUser(UserAVCloud user) {
        this.put("user", AVObject.createWithoutData("_User",user.getObjectId()));//用户表
    }

    public UserAVCloud getFriendUser() {
        return this.getAVUser("friendUser",UserAVCloud.class);
    }

    public void setFriendUser(UserAVCloud friendUser) {
        this.put("friendUser", AVObject.createWithoutData("_User",friendUser.getObjectId()));//用户表
    }

    public int getStatus() {
        return this.getInt("status");
    }

    public void setStatus(int status) {
        this.put("status",status);
    }
}
