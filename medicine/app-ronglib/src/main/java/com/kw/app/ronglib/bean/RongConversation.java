package com.kw.app.ronglib.bean;

import android.content.Context;

import java.io.Serializable;

import io.rong.imlib.model.Conversation.ConversationType;

/**
 * 会话model
 */
public abstract class RongConversation implements Serializable{

    protected String cId;//会话id(根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。)

    protected ConversationType cType;//会话类型(单聊  讨论组  群聊  聊天室 客服 等等)

    protected String cName;//会话名称

    abstract public Object getAvatar();//获取头像-用于会话界面显示

    abstract public long getLastMessageTime();//获取最后一条消息的时间

    abstract public String getLastMessageContent();//获取最后一条消息的内容

    abstract public int getUnReadCount();//获取未读会话个数

    abstract public void readAllMessages();//将所有消息标记为已读

    abstract public void onClick(Context context);//点击事件

    abstract public void onLongClick(Context context);//长按事件

    public String getcName() {
        return cName;
    }

    public String getcId(){
        return cId;
    }

}
