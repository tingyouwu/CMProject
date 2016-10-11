package com.kw.app.medicine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewMultiItemAdapter;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobUser;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.TextMessage;

/**
 * @author :wty
 * 简易版聊天页面  只包含图片与文字
 */
public class SimpleChatAdapter extends BaseRecyclerViewMultiItemAdapter<Message> {
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    private final long TIME_INTERVAL = 10 * 60 * 1000;//显示时间间隔:10分钟

    private String currentUid="";
    private UserDALEx target;

    public SimpleChatAdapter(Context context, List<Message> data, UserDALEx target) {
        super(context, data);
        this.target = target;

        currentUid = BmobUser.getCurrentUser(UserBmob.class).getObjectId();
        addItemType(TYPE_RECEIVER_TXT, R.layout.item_chat_received_message);
        addItemType(TYPE_SEND_TXT, R.layout.item_chat_sent_message);
        addItemType(TYPE_SEND_IMAGE,R.layout.item_chat_sent_image);
        addItemType(TYPE_RECEIVER_IMAGE, R.layout.item_chat_received_image);
    }

    public Message getFirstMessage() {
        if (null != mData && mData.size() > 0) {
            return mData.get(0);
        } else {
            return null;
        }
    }

    public void updateLastMessage(Message msg) {
        int oldposition = mData.size()-1;
        mData.remove(oldposition);
        mData.add(msg);
        notifyItemRangeChanged(oldposition+1,mData.size()-oldposition);
    }

    @Override
    protected void bindView(BaseRecyclerViewHolder helper, Message item, int position) {
        //首先处理一下头像
        ImageView icon = helper.getView(R.id.iv_avatar);
        if(item.getSenderUserId().equals(currentUid)){
            //显示我自己的头像
            ImageLoaderUtil.loadCircle(mContext, PreferenceUtil.getInstance().getLogoUrl(),R.mipmap.img_contact_default,icon);
        }else{
            ImageLoaderUtil.loadCircle(mContext,target.getLogourl(),R.mipmap.img_contact_default,icon);
        }

        TextView tv_time = helper.getView(R.id.tv_time);
        tv_time.setText(TimeUtil.getChatTime(false, item.getSentTime()));
        tv_time.setVisibility(shouldShowTime(position)? View.VISIBLE:View.GONE);

        switch (helper.getItemViewType()){
            case TYPE_SEND_TXT:
                handleSendTextMessage(helper,item);
                break;
            case TYPE_RECEIVER_TXT:
                handleReceiveTextMessage(helper,item);
                break;
            case TYPE_SEND_IMAGE:
                handleSendImageMessage(helper,item);
                break;
            case TYPE_RECEIVER_IMAGE:
                handleReceiveImageMessage(helper,item);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getItemMultiViewType(int position) {
        Message message = mData.get(position);
        MessageContent content = message.getContent();

        if(content instanceof TextMessage){
            //文本消息
            return message.getSenderUserId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }else if(content instanceof ImageMessage){
            //图片消息
            return message.getSenderUserId().equals(currentUid) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }
        return -1;
    }

    /**
     * @Decription 处理我发出去的文本信息
     **/
    private void handleSendTextMessage(BaseRecyclerViewHolder helper,Message msg){
        TextView content = helper.getView(R.id.tv_message);
        ImageView iv_failed_resend = helper.getView(R.id.iv_fail_resend);
        ProgressBar pb_load = helper.getView(R.id.progress_load);

        TextMessage textMessage = (TextMessage)(msg.getContent());
        content.setText(textMessage.getContent());

        if(msg.getSentStatus()== Message.SentStatus.FAILED){
            //发送失败
            iv_failed_resend.setVisibility(View.VISIBLE);
            pb_load.setVisibility(View.GONE);
        }else if(msg.getSentStatus()== Message.SentStatus.SENDING){
            //发送中
            iv_failed_resend.setVisibility(View.GONE);
            pb_load.setVisibility(View.VISIBLE);
        }else if(msg.getSentStatus()== Message.SentStatus.RECEIVED || msg.getSentStatus()== Message.SentStatus.SENT
                || msg.getSentStatus()== Message.SentStatus.READ || msg.getSentStatus()== Message.SentStatus.DESTROYED){
            //对方已接收  已发送  对方已读  对方已销毁
            iv_failed_resend.setVisibility(View.GONE);
            pb_load.setVisibility(View.GONE);
        }

        iv_failed_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * @Decription 处理别人发给我的文本信息
     **/
    private void handleReceiveTextMessage(BaseRecyclerViewHolder helper,Message msg){
        TextMessage textMessage = (TextMessage)(msg.getContent());
        TextView content = helper.getView(R.id.tv_message);
        content.setText(textMessage.getContent());
    }

    /**
     * @Decription 处理我发出去的文本信息
     **/
    private void handleSendImageMessage(BaseRecyclerViewHolder helper,Message msg){

        ImageView iv_failed_resend = helper.getView(R.id.iv_fail_resend);
        ImageView iv_picture = helper.getView(R.id.iv_picture);
        ProgressBar pb_load = helper.getView(R.id.progress_load);

        ImageMessage messagecontent = (ImageMessage) msg.getContent();

        if(msg.getSentStatus()== Message.SentStatus.FAILED){
            //发送失败
            iv_failed_resend.setVisibility(View.VISIBLE);
            pb_load.setVisibility(View.GONE);
        }else if(msg.getSentStatus()== Message.SentStatus.SENDING){
            //发送中
            iv_failed_resend.setVisibility(View.GONE);
            pb_load.setVisibility(View.VISIBLE);
        }else if(msg.getSentStatus()== Message.SentStatus.RECEIVED || msg.getSentStatus()== Message.SentStatus.SENT
                || msg.getSentStatus()== Message.SentStatus.READ || msg.getSentStatus()== Message.SentStatus.DESTROYED){
            //对方已接收  已发送  对方已读  对方已销毁
            iv_failed_resend.setVisibility(View.GONE);
            pb_load.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(messagecontent.getLocalUri().toString())){
            ImageLoaderUtil.load(mContext,messagecontent.getLocalUri().toString(),iv_picture);
        }else if(!TextUtils.isEmpty(messagecontent.getRemoteUri().toString())){
            ImageLoaderUtil.load(mContext,messagecontent.getRemoteUri().toString(),iv_picture);
        }

        iv_failed_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * @Decription 处理别人发给我的图片信息
     **/
    private void handleReceiveImageMessage(BaseRecyclerViewHolder helper,Message msg){
    }

    /**
     * @Decription 判断当前位置是否需要显示时间（条件：前后创建时间间隔大于10分钟）
     **/
    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = mData.get(position - 1).getSentTime();
        long curTime = mData.get(position).getSentTime();
        return curTime - lastTime > TIME_INTERVAL;
    }
}
