package com.kw.app.medicine.bean.conversation;

import android.content.Context;

import com.kw.app.medicine.R;
import com.kw.app.medicine.data.local.SystemMessageDALEx;
import com.kw.app.ronglib.bean.RongConversation;

/**
 * 系统消息
 */
public class SystemMessageConversation extends RongConversation {

    private SystemMessageDALEx system;

    public SystemMessageConversation(SystemMessageDALEx system){
        this.system = system;
        this.cName = "系统消息";
    }

    @Override
    public Object getAvatar() {
        return R.mipmap.new_friends_icon;
    }

    @Override
    public String getLastMessageContent() {
        return system.getMsg();
    }

    @Override
    public long getLastMessageTime() {
        return system.getTime();
    }

    @Override
    public int getUnReadCount() {
        return system.getUnReadMessageCount();
    }

    @Override
    public void readAllMessages() {
        system.updateBatchStatus();
    }

    @Override
    public void onClick(Context context) {
//        SystemMessageActivity.startSystemMessageActivity(context);
    }

    @Override
    public void onLongClick(Context context) {
    }
}
