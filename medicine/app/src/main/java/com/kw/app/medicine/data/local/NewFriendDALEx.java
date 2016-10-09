package com.kw.app.medicine.data.local;

import android.database.Cursor;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.DatabaseField.FieldType;
import com.kw.app.ormlib.annotation.SqliteDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @Decription
 * 本地好友请求表
 **/
public class NewFriendDALEx extends SqliteBaseDALEx {
    public static final String STATUS = "status";
    public static final String TIME = "time";

    @DatabaseField(primaryKey = true,Type = FieldType.VARCHAR)
    private String msgid;
    @DatabaseField(Type = FieldType.VARCHAR)
    private String uid;//发送者的uid
    @DatabaseField(Type = FieldType.VARCHAR)
    private String msg;
    @DatabaseField(Type = FieldType.INT)
    private long status;
    @DatabaseField(Type = FieldType.INT)
    private long time;

    private String name;
    private String avatar;//发送者的头像

    public static NewFriendDALEx get() {
        return SqliteDao.getDao(NewFriendDALEx.class);
    }

    /**获取本地所有的邀请信息
     * @return
     */
    public List<NewFriendDALEx> getAllNewFriend(){
        String sql = String.format("select * from %s order by %s desc",TABLE_NAME,TIME);
        return findList(sql);
    }

    /**
     * 是否有新的好友邀请
     * @return
     */
    public boolean hasNewFriendInvitation(){
        List<NewFriendDALEx> infos =getNoVerifyNewFriend();
        if(infos!=null && infos.size()>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取未读的好友邀请
     * @return
     */
    public int getNewInvitationCount(){
        List<NewFriendDALEx> infos =getNoVerifyNewFriend();
        if(infos!=null && infos.size()>0){
            return infos.size();
        }else{
            return 0;
        }
    }

    /**
     * 获取所有未读未验证的好友请求
     * @return
     */
    private List<NewFriendDALEx> getNoVerifyNewFriend(){
        String sql = String.format("select * from %s where %s = %s",TABLE_NAME,STATUS,TIME,VerifyStatus.Status_Verify_None.code);
        return findList(sql);
    }

    /**
     * 批量更新未读未验证的状态为已读
     */
    public void updateBatchStatus(){
        List<NewFriendDALEx> infos =getNoVerifyNewFriend();
        if(infos!=null && infos.size()>0){
            int size =infos.size();
            List<NewFriendDALEx> all =new ArrayList<>();
            for (int i = 0; i < size; i++) {
                NewFriendDALEx msg =infos.get(i);
                msg.setStatus(VerifyStatus.Status_Verify_Readed.code);
                all.add(msg);
            }
            saveOrUpdate(infos);
        }
    }

    /**
     * 修改指定好友请求的状态
     * @param friend
     * @param status
     * @return
     */
    public void updateNewFriend(NewFriendDALEx friend,int status){
        friend.setStatus(status);
        friend.saveOrUpdate();
    }

    @Override
    protected void onSetCursorValueComplete(Cursor cursor) {
        UserDALEx user = UserDALEx.get().findById(getUid());
        if(user != null){
            setName(user.getNickname());
            setAvatar(user.getLogourl());
        }
    }

    /**
     * 删除指定的添加请求
     * @param friend
     */
    public void deleteNewFriend(NewFriendDALEx friend){
        friend.deleteById(friend.getMsgid());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public enum VerifyStatus{
        Status_Verify_None(0,"未读未添加"),Status_Verify_Readed(2,"已读未添加"),Status_Verified(1,"已添加"),
        Status_Verify_Refuse(3,"拒绝"),Status_Verify_Me_Send(4,"我发出的好友请求");
        public int code;
        public String label;
        VerifyStatus(int code, String label){
            this.code = code;
            this.label = label;
        }
    }
}
