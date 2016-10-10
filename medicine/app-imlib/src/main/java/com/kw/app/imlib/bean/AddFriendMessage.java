package com.kw.app.imlib.bean;

import android.text.TextUtils;

import com.kw.app.imlib.messagecontent.CustomzeContactNotificationMessage;
import com.orhanobut.logger.Logger;
import org.json.JSONObject;

/**
 * 添加好友请求
 */
public class AddFriendMessage {

    private String msgid;
    private String uid;//发送者的uid
    private String msg;
    private String name;
    private String avatar;//发送者的头像
    private long time;

    public AddFriendMessage(){}

    /**从CustomzeContactNotificationMessage获取一个NewFriednd的信息
     * @param msg 消息
     * @return
     */
    public static AddFriendMessage convert(CustomzeContactNotificationMessage msg){
        AddFriendMessage add =new AddFriendMessage();
        String content = msg.getMessage();
        add.setMsg(content);
        add.setUid(msg.getSourceUserId());

        try {
            String extra = msg.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                add.setMsgid(json.getString("msgid"));
                add.setTime(json.getLong("time"));
                add.setName(json.getString("name"));
            }else{
                Logger.i("AddFriendMessage的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
