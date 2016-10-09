package com.kw.app.medicine.data.local;

import android.database.Cursor;
import android.text.TextUtils;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.SqliteDao;
import com.kw.app.ronglib.messagecontent.CustomzeContactNotificationMessage;
import com.orhanobut.logger.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Decription
 * 本地存储系统信息
 **/
public class SystemMessageDALEx extends SqliteBaseDALEx {

    public static final int UNREAD=0;//未读
    public static final int READED=1;//已读

    public static final String STATUS = "status";
    public static final String TIME = "time";

    @DatabaseField(primaryKey = true,Type = DatabaseField.FieldType.VARCHAR)
    private String msgid;
    @DatabaseField(Type = DatabaseField.FieldType.VARCHAR)
    private String uid;//发送者的uid
    @DatabaseField(Type = DatabaseField.FieldType.VARCHAR)
    private String msg;//备注信息
    @DatabaseField(Type = DatabaseField.FieldType.INT)
    private int status;//是否已读  0未读 1已读
    @DatabaseField(Type = DatabaseField.FieldType.INT)
    private int type;//消息类型 0 好友邀请 1同意加好友 2拒绝加好友
    @DatabaseField(Type = DatabaseField.FieldType.INT,isLongType = true)
    private long time;

    private String name;
    private String avator;

    public static SystemMessageDALEx get() {
        return SqliteDao.getDao(SystemMessageDALEx.class);
    }

    /**
     * 把 CustomzeContactNotificationMessage 转成 SystemMessageDALEx
     **/
    public static SystemMessageDALEx convert(CustomzeContactNotificationMessage msg){
        SystemMessageDALEx add =new SystemMessageDALEx();
        String content = msg.getMessage();
        add.setMsg(content);
        add.setStatus(UNREAD);
        add.setUid(msg.getSourceUserId());

        try {
            String extra = msg.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                add.setMsgid(json.getString("msgid"));
                add.setTime(json.getLong("time"));
            }else{
                Logger.i("CustomzeContactNotificationMessage 的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add;
    }

    /**获取本地所有系统信息
     * @return
     */
    public List<SystemMessageDALEx> getAllMessage(){
        String sql = String.format("select * from %s order by %s desc",TABLE_NAME,TIME);
        return findList(sql);
    }

    /**
     * 是否有新的未读消息
     * @return
     */
    public boolean hasUnreadMessage(){
        List<SystemMessageDALEx> infos =getUnReadMessage();
        if(infos!=null && infos.size()>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取未读的消息条目
     * @return
     */
    public int getUnReadMessageCount(){
        List<SystemMessageDALEx> infos =getUnReadMessage();
        if(infos!=null && infos.size()>0){
            return infos.size();
        }else{
            return 0;
        }
    }

    /**
     * 获取所有未读信息
     * @return
     */
    private List<SystemMessageDALEx> getUnReadMessage(){
        String sql = String.format("select * from %s where %s = %s",TABLE_NAME,STATUS,UNREAD);
        return findList(sql);
    }

    /**
     * 批量更新未读的状态为已读
     */
    public void updateBatchStatus(){
        List<SystemMessageDALEx> infos =getUnReadMessage();
        if(infos!=null && infos.size()>0){
            int size =infos.size();
            List<SystemMessageDALEx> all =new ArrayList<>();
            for (int i = 0; i < size; i++) {
                SystemMessageDALEx msg =infos.get(i);
                msg.setStatus(READED);
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
    public void updateNewFriend(SystemMessageDALEx friend,int status){
        friend.setStatus(status);
        friend.saveOrUpdate();
    }

    /**
     * 删除指定的添加请求
     * @param friend
     */
    public void deleteMsg(SystemMessageDALEx friend){
        friend.deleteById(friend.getMsgid());
    }

    @Override
    protected void onSetCursorValueComplete(Cursor cursor) {
        UserDALEx user = UserDALEx.get().findById(getUid());
        if(user != null){
            setName(user.getNickname());
            setAvator(user.getLogourl());
        }
    }

    public enum SystemMessageType{
        AddFriend(1,"添加好友邀请"),AgreeAdd(2,"同意添加请求"),RefuseAdd(3,"拒绝添加");
        public int code;
        public String label;
        SystemMessageType(int code, String label){
            this.code = code;
            this.label = label;
        }
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

    public int getStatus() {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }
}
