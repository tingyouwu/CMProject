package com.kw.app.medicine.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.commonlib.utils.ScreenUtil;
import com.kw.app.commonlib.utils.TimeUtil;
import com.kw.app.commonlib.utils.record.VoicePlayer;
import com.kw.app.medicine.R;
import com.kw.app.medicine.base.RongManager;
import com.kw.app.medicine.base.VoicePlayOnClickListener;
import com.kw.app.medicine.base.VoicePlayerManager;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.FileMessageDALEx;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.data.local.VoiceReadStatusDALEx;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.adapter.BaseRecyclerViewMultiItemAdapter;

import java.util.List;

import cn.bmob.v3.BmobUser;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

/**
 * @author :wty
 */
public class ChatAdapter extends BaseRecyclerViewMultiItemAdapter<Message> {
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VIDEO =8;
    private final int TYPE_RECEIVER_VIDEO = 9;

    private final long TIME_INTERVAL = 10 * 60 * 1000;//显示时间间隔:10分钟

    private String currentUid="";
    private UserDALEx target;

    public ChatAdapter(Context context, List<Message> data, UserDALEx target) {
        super(context, data);
        this.target = target;

        currentUid = BmobUser.getCurrentUser(UserBmob.class).getObjectId();
        addItemType(TYPE_RECEIVER_TXT, R.layout.item_chat_received_message);
        addItemType(TYPE_SEND_TXT, R.layout.item_chat_sent_message);
        addItemType(TYPE_SEND_IMAGE,R.layout.item_chat_sent_image);
        addItemType(TYPE_RECEIVER_IMAGE, R.layout.item_chat_received_image);
        addItemType(TYPE_SEND_LOCATION, R.layout.item_chat_sent_location);
        addItemType(TYPE_RECEIVER_LOCATION,R.layout.item_chat_received_location);
        addItemType(TYPE_SEND_VOICE, R.layout.item_chat_sent_voice);
        addItemType(TYPE_RECEIVER_VOICE,R.layout.item_chat_received_voice);
        addItemType(TYPE_SEND_VIDEO, R.layout.item_chat_sent_message);
        addItemType(TYPE_RECEIVER_VIDEO, R.layout.item_chat_received_message);
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
        notifyItemRangeChanged(oldposition,mData.size()-oldposition);
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
            case TYPE_RECEIVER_TXT:
                handleReceiveTextMessage(helper,item);
                break;
            case TYPE_RECEIVER_IMAGE:
                break;
            case TYPE_RECEIVER_LOCATION:
                break;
            case TYPE_RECEIVER_VIDEO:
                break;
            case TYPE_RECEIVER_VOICE:
                handleReceiveVoiceMessage(helper,item);
                break;
            case TYPE_SEND_IMAGE:
                break;
            case TYPE_SEND_LOCATION:
                break;
            case TYPE_SEND_TXT:
                handleSendTextMessage(helper,item);
                break;
            case TYPE_SEND_VIDEO:
                break;
            case TYPE_SEND_VOICE:
                handleSendVoiceMessage(helper,item);
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
        }else if(content instanceof LocationMessage){
            //定位信息
            return message.getSenderUserId().equals(currentUid) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(content instanceof FileMessage){
            //文件信息

            RongManager.FileMessageType type = RongManager.getInstance().getFileType(message);
            if(type != null && type== RongManager.FileMessageType.Voice) {
                return message.getSenderUserId().equals(currentUid) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
            }
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
     * @Decription 处理别人发给我的语音信息
     **/
    private void handleReceiveVoiceMessage(BaseRecyclerViewHolder holder, final Message msg){
        TextView duration = holder.getView(R.id.tv_voice_length);
        ImageView iv_unread = holder.getView(R.id.img_voice_unread);
        ImageView iv_voice = holder.getView(R.id.iv_voice);
        ProgressBar progressBar = holder.getView(R.id.progress_load);
        TextView lengthspace = holder.getView(R.id.tv_lengthspace);
        LinearLayout layout_voice = holder.getView(R.id.layout_voice);
        final VoicePlayerManager.Status status;
        final FileMessageDALEx dalex = FileMessageDALEx.convert(msg);
        duration.setText(dalex.getDuration()+"''");

        int screenWidth = ScreenUtil.getScreenWidth(mContext);
        int maxWidth = ScreenUtil.px2dip(mContext,screenWidth)-240;
        lengthspace.setMaxWidth(ScreenUtil.dp2px(mContext,maxWidth));
        String lengthFakeText = "";
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<dalex.getDuration()/2;i++){
            sb.append(" ");
        }
        lengthFakeText = sb.toString();
        lengthspace.setText(lengthFakeText);

        status = VoicePlayerManager.getInstance(mContext).getStatus(msg.getUId());

        layout_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPlaying = (status == VoicePlayerManager.Status.Playing);
                if(isPlaying){
                    VoicePlayerManager.getInstance(mContext).stopAndDestory();
                }else{
                    VoicePlayerManager.getInstance(mContext).setOnStatusChangeListener(new VoicePlayer.OnStatusChangeListener() {
                        @Override
                        public void onCompletion() {

                        }

                        @Override
                        public void onPlaying(String filePath) {
                            VoiceReadStatusDALEx.get().updateReadStatus(dalex.getMsgid());
                        }

                        @Override
                        public void onStop() {

                        }
                    });
                    VoicePlayerManager.getInstance(mContext).play(msg);
                }
            }
        });

        boolean isRead = VoiceReadStatusDALEx.get().isReaded(msg.getUId());
        if(!isRead){
            iv_unread.setVisibility(View.VISIBLE);
        }else{
            iv_unread.setVisibility(View.GONE);
        }
    }

    /**
     * @Decription 处理我发出去的语音
     **/
    private void handleSendVoiceMessage(BaseRecyclerViewHolder helper, final Message msg){
        TextView duration = helper.getView(R.id.tv_voice_length);
        ImageView iv_voice = helper.getView(R.id.iv_voice);
        ImageView iv_failed_resend = helper.getView(R.id.iv_fail_resend);
        ProgressBar progressBar = helper.getView(R.id.progress_load);
        TextView lengthspace = helper.getView(R.id.tv_lengthspace);
        final LinearLayout layout_voice = helper.getView(R.id.layout_voice);
        final VoicePlayerManager.Status status;
        AnimationDrawable anim = null;

        final FileMessageDALEx dalex = FileMessageDALEx.convert(msg);
        duration.setText(dalex.getDuration()+"''");

        int screenWidth = ScreenUtil.getScreenWidth(mContext);
        final int maxWidth = ScreenUtil.px2dip(mContext,screenWidth)-240;
        lengthspace.setMaxWidth(ScreenUtil.dp2px(mContext,maxWidth));

        String lengthFakeText = "";
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<dalex.getDuration()/2;i++){
                sb.append(" ");
        }
        lengthFakeText = sb.toString();
        lengthspace.setText(lengthFakeText);

        status = VoicePlayerManager.getInstance(mContext).getStatus(msg.getUId());

        boolean isDownloading = (status == VoicePlayerManager.Status.Downloading);
        final boolean isPlaying = (status == VoicePlayerManager.Status.Playing);

        if(isDownloading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

        if(isPlaying){
            iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
            anim = (AnimationDrawable) iv_voice.getDrawable();
            anim.start();
        }else{
            iv_voice.setImageResource(R.mipmap.voice_left3);
        }

        layout_voice.setOnClickListener(new VoicePlayOnClickListener(anim,msg){
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    VoicePlayerManager.getInstance(mContext).stopAndDestory();
                    if(getAnim() != null){
                        getAnim().stop();
                    }
                }else{
                    VoicePlayerManager.getInstance(mContext).setOnStatusChangeListener(new VoicePlayer.OnStatusChangeListener() {
                        @Override
                        public void onCompletion() {
                            if(getAnim() != null){
                                getAnim().stop();
                            }
                        }

                        @Override
                        public void onPlaying(String filePath) {
                            VoiceReadStatusDALEx.get().updateReadStatus(dalex.getMsgid());
                        }

                        @Override
                        public void onStop() {

                        }
                    });
                    VoicePlayerManager.getInstance(mContext).play(msg);
                }
            }
        });

        if(msg.getSentStatus()== Message.SentStatus.FAILED){
            //发送失败
            iv_failed_resend.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else if(msg.getSentStatus()== Message.SentStatus.SENDING){
            //发送中
            iv_failed_resend.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else if(msg.getSentStatus()== Message.SentStatus.RECEIVED || msg.getSentStatus()== Message.SentStatus.SENT
                || msg.getSentStatus()== Message.SentStatus.READ || msg.getSentStatus()== Message.SentStatus.DESTROYED){
            //对方已接收  已发送  对方已读  对方已销毁
            iv_failed_resend.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        iv_failed_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
