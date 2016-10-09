package com.kw.app.medicine.event;

import io.rong.imlib.model.Message;

/**
 * @Decription 刷新聊天页面
 **/
public class RefreshChatEvent {

    private Message msg;

    public RefreshChatEvent(Message msg){
        this.msg = msg;
    }

    public Message getMsg() {
        return msg;
    }
}
