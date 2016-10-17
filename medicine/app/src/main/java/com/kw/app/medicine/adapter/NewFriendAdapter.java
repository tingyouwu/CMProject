package com.kw.app.medicine.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.imlib.messagecontent.CustomzeContactNotificationMessage;
import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.FriendRequstActivity;
import com.kw.app.medicine.bmob.BmobUserModel;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.NewFriendDALEx;
import com.kw.app.widget.BaseRecyclerViewHolder;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.adapter.BaseRecyclerViewAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.bmob.v3.BmobUser;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * @Decription 搜索好友 适配器
 */
public class NewFriendAdapter extends BaseRecyclerViewAdapter<NewFriendDALEx> {
    public NewFriendAdapter(Context context, List data) {
        super(context, R.layout.item_new_friend, data);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder helper, final NewFriendDALEx item, int position) {
        ImageView icon = helper.getView(R.id.iv_recent_avatar);
        TextView name = helper.getView(R.id.tv_recent_name);
        final TextView tv_status = helper.getView(R.id.tv_status);
        final TextView msg = helper.getView(R.id.tv_recent_msg);
        final Button agree = helper.getView(R.id.btn_aggree);

        name.setText(item.getName());
        msg.setText(item.getMsg());
        ImageLoaderUtil.loadCircle(mContext,item.getAvatar(),R.mipmap.img_contact_default,icon);

        long status = item.getStatus();
        if(status == NewFriendDALEx.VerifyStatus.Status_Verify_None.code || status == NewFriendDALEx.VerifyStatus.Status_Verify_Readed.code){
            //未添加 /已读未添加
            agree.setText("同意");
            agree.setVisibility(View.VISIBLE);
            tv_status.setVisibility(View.GONE);
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    agreeAdd(item, new ICallBack<String>() {
                        @Override
                        public void onSuccess(String data) {
                            tv_status.setText("已添加");
                            tv_status.setVisibility(View.VISIBLE);
                            agree.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFaild(String msg) {
                            ((BaseActivity) mContext).showAppToast("添加好友失败:"+msg);
                        }
                    });
                }
            });

            helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击跳转到 同意或者拒绝页面
                    FriendRequstActivity.startNewFriendActivity(mContext,item.getMsgid());
                }
            });

        }else if(status == NewFriendDALEx.VerifyStatus.Status_Verify_Refuse.code){
            tv_status.setText("已拒绝");
            tv_status.setVisibility(View.VISIBLE);
            agree.setVisibility(View.GONE);
        }else{
            tv_status.setText("已添加");
            tv_status.setVisibility(View.VISIBLE);
            agree.setVisibility(View.GONE);
        }
    }

    /**
     * @Decription 同意添加到好友列表
     **/
    private void agreeAdd(final NewFriendDALEx add, final ICallBack<String> callBack){
        final UserBmob user = new UserBmob();
        user.setObjectId(add.getUid());
        BmobUserModel.getInstance().agreeAddFriend(user, new ICallBack<String>() {
            @Override
            public void onSuccess(String data) {
                sendAgreeAddFriendMessage(add,callBack);
            }

            @Override
            public void onFaild(String msg) {
                callBack.onFaild(msg);
            }
        });
    }

    /**
     * 发送同意添加好友的请求
     */
    private void sendAgreeAddFriendMessage(final NewFriendDALEx user, final ICallBack<String> callBack){
        UserBmob currentUser = BmobUser.getCurrentUser(UserBmob.class);
        CustomzeContactNotificationMessage message = CustomzeContactNotificationMessage.obtain(CustomzeContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE,
                PreferenceUtil.getInstance().getLastAccount(),
                user.getUid(), "已同意您的好友请求");
        message.setUserInfo(new UserInfo(currentUser.getObjectId(), currentUser.getUsername(), Uri.parse(currentUser.getLogourl())));

        Map<String,Object> map =new HashMap<>();
        map.put("msgid", UUID.randomUUID().toString());//消息id
        map.put("time", System.currentTimeMillis());//当前时间
        map.put("name",PreferenceUtil.getInstance().getLastName());//用户名
        message.setExtra(new Gson().toJson(map));

        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE,
                user.getUid(),
                message,
                currentUser.getUsername()+"已同意您的好友请求",
                "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {
                        NewFriendDALEx.get().updateNewFriend(user, NewFriendDALEx.VerifyStatus.Status_Verified.code);
                        callBack.onSuccess("");
                        sendTxtMessage(user);
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        callBack.onFaild("同意失败");
                    }
                });
    }

    /**
     * 发送一个文本消息
     **/
    private void sendTxtMessage(NewFriendDALEx user){
        TextMessage textMessage = TextMessage.obtain("我通过了你的好友验证请求，我们可以开始聊天了!");
        Map<String,Object> map =new HashMap<>();
        map.put("msgid", UUID.randomUUID().toString());//消息id
        map.put("time", System.currentTimeMillis());//当前时间
        textMessage.setExtra(new Gson().toJson(map));
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE,
                user.getUid(),
                textMessage,
                "我通过了你的好友验证请求，我们可以开始聊天了!",
                "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }
                });
    }
}
