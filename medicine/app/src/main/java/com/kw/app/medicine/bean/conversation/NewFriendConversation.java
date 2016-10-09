package com.kw.app.medicine.bean.conversation;

import android.content.Context;

import com.kw.app.medicine.R;
import com.kw.app.ronglib.bean.RongConversation;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;

/**
 * 新朋友申请
 */
public class NewFriendConversation extends RongConversation {

    private Conversation conversation;
    private MessageContent lastMsg;

    public NewFriendConversation(Conversation conversation){
        this.conversation = conversation;
        cType = conversation.getConversationType();
        cId = conversation.getTargetId();
        cName="系统通知";
        //获取最后一条消息
        lastMsg =conversation.getLatestMessage();
    }

    @Override
    public Object getAvatar() {
        return R.mipmap.new_friends_icon;
    }

    @Override
    public String getLastMessageContent() {
        if(lastMsg!=null){
            if(lastMsg instanceof ContactNotificationMessage){
                return ((ContactNotificationMessage)lastMsg).getMessage();
            }else {
                return "[未知]";
            }
        }else{//防止消息错乱
            return "";
        }
    }

    @Override
    public long getLastMessageTime() {
        return conversation.getSentTime();
    }

    @Override
    public int getUnReadCount() {
        return 0;
    }

    @Override
    public void readAllMessages() {

    }

    @Override
    public void onClick(Context context) {
    }

    @Override
    public void onLongClick(Context context) {
    }
}
