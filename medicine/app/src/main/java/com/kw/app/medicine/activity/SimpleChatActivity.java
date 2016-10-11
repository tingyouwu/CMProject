package com.kw.app.medicine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.Gson;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.adapter.SimpleChatAdapter;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.event.RefreshChatEvent;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.xrecyclerview.ProgressStyle;
import com.kw.app.widget.view.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

/**
 * 简易版本聊天界面
 */
public class SimpleChatActivity extends BaseActivity {

    public static final String TARGET = "target";
    public static final int Max_Limit = 100;

    XRecyclerView rc_view;
    EditText edit_msg;
    Button btn_chat_send,btn_chat_pic;
    SimpleChatAdapter adapter;
    LinearLayoutManager layoutManager;

    private List<Message> msgs = new ArrayList<Message>();
    private UserDALEx target;
    private boolean isHasMoreMessage = true;//是否还有更多数据

    public static void startSimpleChatActivity(Context context,UserDALEx user){
        Intent intent = new Intent();
        intent.setClass(context, SimpleChatActivity.class);
        intent.putExtra(SimpleChatActivity.TARGET, user);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshChatEvent event){
        AppLogUtil.i("---聊天页面接收到消息---");
        scrollToBottom();
        adapter.addOne(event.getMsg());
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        target = (UserDALEx)getIntent().getSerializableExtra(TARGET);
        if(target==null)return;

        rc_view = (XRecyclerView) findViewById(R.id.rc_view);
        edit_msg = (EditText) findViewById(R.id.edit_msg);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_pic = (Button)findViewById(R.id.btn_chat_pic);

        initTitle();
        initListener();
        initSwipeLayout();
        initBottomView();
        clearMessagesUnread();
    }

    /**
     * 初始化标题栏
     **/
    private void initTitle(){
        getDefaultNavigation().setTitle(target.getNickname());
        getDefaultNavigation().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.keyboardControl(SimpleChatActivity.this,false,edit_msg);
                finish();
            }
        });
    }

    /**
     * 初始化内容列表
     **/
    private void initSwipeLayout(){
        adapter = new SimpleChatAdapter(this,msgs,target);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        rc_view.setRefreshString("下拉加载更多历史消息","释放加载","正在加载...","加载完成");
        rc_view.setRefreshProgressStyle(ProgressStyle.BallClipRotatePulse);
        rc_view.setLoadingMoreEnabled(false);
        rc_view.clearFootView();
        rc_view.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                Message msg = adapter.getFirstMessage();
                queryMessages(msg);
            }

            @Override
            public void onLoadMore() {
            }

        });
        rc_view.setAdapter(adapter);
        queryMessages(null);
    }

    private void initBottomView(){
        edit_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN||event.getAction()== MotionEvent.ACTION_UP){
                    scrollToBottom();
                }
                return false;
            }
        });

        edit_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setBackgroundResource(R.drawable.login_press);
                    btn_chat_send.setEnabled(true);
                    btn_chat_send.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btn_chat_send.setTextColor(getResources().getColor(R.color.gray_font_3));
                    btn_chat_send.setBackgroundResource(R.drawable.btn_disable);
                    btn_chat_send.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_chat_send.setTextColor(getResources().getColor(R.color.gray_font_3));
        btn_chat_send.setBackgroundResource(R.drawable.btn_disable);
        btn_chat_send.setEnabled(false);

    }

    @Override
    public Object getPresenter() {
        return null;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_simple_chat;
    }


    /**
     * 初始化各种监听
     **/
    private void initListener(){

        btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    /**
     * 发送文本消息
     */
    private void sendMessage(){
        String text=edit_msg.getText().toString();
        if(TextUtils.isEmpty(text.trim())){
            showAppToast("请输入内容");
            return;
        }
        TextMessage textMessage = TextMessage.obtain(text);
        Map<String,Object> map =new HashMap<>();
        map.put("msgid", UUID.randomUUID().toString());//消息id
        map.put("time", System.currentTimeMillis());//当前时间
        textMessage.setExtra(new Gson().toJson(map));
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE,
                target.getUserid(),
                textMessage,
                PreferenceUtil.getInstance().getLastName() + ":" + text,
                "", new IRongCallback.ISendMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        //保存数据库成功 那么就应该显示出来
                        //发送成功 更新
                        adapter.addOne(message);
                        edit_msg.setText("");
                        scrollToBottom();
                    }

                    @Override
                    public void onSuccess(Message message) {
                        adapter.updateLastMessage(message);
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        //发送失败  需要显示重新发送的按钮
                        adapter.updateLastMessage(message);
                    }
                });
    }

    /**
     * 发送本地图片地址
     */
    public void sendImageMessage(){

    }

    /**首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     * @param msg
     */
    public void queryMessages(Message msg){

        if(msg == null){
            /**
             * 根据会话类型的目标 Id，回调方式获取最新的 N 条消息实体。
             * @param conversationType 会话类型。
             * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id 或聊天室 Id。
             * @param count            要获取的消息数量。
             * @param callback         获取最新消息记录的回调，按照时间顺序从新到旧排列。
             */
            RongIMClient.getInstance().getLatestMessages(Conversation.ConversationType.PRIVATE, target.getUserid(), Max_Limit, new RongIMClient.ResultCallback<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messages) {
                    rc_view.refreshComplete();
                    if (messages != null && messages.size() > 0) {
                        Collections.reverse(messages);
                        adapter.addData(0, messages);
                        layoutManager.scrollToPositionWithOffset(messages.size(), 0);

                        if(messages.size()==Max_Limit){
                            //证明可能还有聊天记录
                            isHasMoreMessage = true;
                        }else{
                            isHasMoreMessage = false;
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    rc_view.refreshComplete();
                }
            });
        }else{
            if(isHasMoreMessage){
                /**
                 * 获取会话中，从指定消息之前、指定数量的最新消息实体
                 * @param conversationType 会话类型。不支持传入 ConversationType.CHATROOM。
                 * @param targetId         目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
                 * @param oldestMessageId  最后一条消息的 Id，获取此消息之前的 count 条消息，没有消息第一次调用应设置为:-1。
                 * @param count            要获取的消息数量。
                 * @param callback         获取历史消息记录的回调，按照时间顺序从新到旧排列。
                 */
                RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE, target.getUserid(), msg.getMessageId(), Max_Limit, new RongIMClient.ResultCallback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        if (messages != null && messages.size() > 0) {
                            Collections.reverse(messages);
                            adapter.addData(0, messages);
                            layoutManager.scrollToPositionWithOffset(messages.size(), 0);
                            if(messages.size()==Max_Limit){
                                //证明可能还有聊天记录
                                isHasMoreMessage = true;
                            }else{
                                isHasMoreMessage = false;
                            }
                        }
                        rc_view.refreshComplete();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        rc_view.refreshComplete();
                    }
                });
            }else{
                rc_view.refreshComplete("没有更多历史消息");
            }
        }

    }

    /**
     * @decription 滚动到最底部
     **/
    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount(), 0);
    }

    @Override
    protected void onDestroy() {
        CommonUtil.keyboardControl(this,false,edit_msg);
        clearMessagesUnread();
        super.onDestroy();
    }

    private void clearMessagesUnread(){
        RongIMClient.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.PRIVATE, target.getUserid(), new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

}
