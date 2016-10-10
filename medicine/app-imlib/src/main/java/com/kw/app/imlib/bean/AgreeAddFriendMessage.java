package com.kw.app.imlib.bean;

import android.text.TextUtils;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.imlib.messagecontent.CustomzeContactNotificationMessage;

import org.json.JSONObject;

/**同意添加好友请求-仅仅只用于发送同意添加好友的消息
 * 接收到对方发送的同意添加自己为好友的请求时，
 * 需要做两个事情：1、在本地数据库中新建一个会话，
 *                 2、添加对方到自己的好友表中
 */
public class AgreeAddFriendMessage {

    private String msgid;
    private String uid;//发送者的uid
    private String msg;
    private String name;
    private String avatar;//发送者的头像
    private long time;

    public AgreeAddFriendMessage(){}

    /**将ContactNotifiMessage转成 AgreeAddFriendMessage
     * @param msg 消息
     * @return
     */
    public static AgreeAddFriendMessage convert(CustomzeContactNotificationMessage msg){
        AgreeAddFriendMessage agree =new AgreeAddFriendMessage();
        agree.setMsg(msg.getMessage());

        try {
            String extra = msg.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                agree.setMsgid(json.getString("msgid"));
                agree.setTime(json.getLong("time"));
                agree.setName(json.getString("name"));
            }else{
                AppLogUtil.i("AgreeAddFriendMessage 的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agree;
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
