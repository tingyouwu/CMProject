package com.kw.app.medicine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.FileUtils;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.commonlib.utils.record.OnRecordChangeListener;
import com.kw.app.commonlib.utils.record.RecordManager;
import com.kw.app.medicine.R;
import com.kw.app.medicine.adapter.ChatAdapter;
import com.kw.app.medicine.data.local.FileMessageDALEx;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.event.RefreshChatEvent;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.xrecyclerview.ProgressStyle;
import com.kw.app.widget.view.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
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
import io.rong.message.FileMessage;
import io.rong.message.TextMessage;

/**
 * 聊天界面
 */
public class ChatActivity extends BaseActivity {

    public static final String TARGET = "target";
    public static final int Max_Limit = 100;

    Toast toast;
    XRecyclerView rc_view;
    EditText edit_msg;
    Button btn_speak,btn_chat_voice,btn_chat_keyboard,btn_chat_send,btn_chat_add;
    LinearLayout layout_more,layout_add;

    // 语音有关
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    TextView tv_picture,tv_camera,tv_location;
    ImageView iv_record;
    ChatAdapter adapter;
    LinearLayoutManager layoutManager;

    private List<Message> msgs = new ArrayList<Message>();
    private Drawable[] drawable_Anims;// 话筒动画
    private UserDALEx target;
    private boolean isHasMoreMessage = true;//是否还有更多数据
    private RecordManager recordManager;

    public static void startChatActivity(Context context,UserDALEx user){
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra(ChatActivity.TARGET, user);
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
        AppLogUtil.i("---会话页接收到消息---");
        scrollToBottom();
        adapter.addOne(event.getMsg());
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        target = (UserDALEx)getIntent().getSerializableExtra(TARGET);
        if(target==null)return;

        rc_view = (XRecyclerView) findViewById(R.id.rc_view);
        edit_msg = (EditText) findViewById(R.id.edit_msg);
        btn_speak = (Button) findViewById(R.id.btn_speak);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);

        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);

        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_location = (TextView) findViewById(R.id.tv_location);

        initTitle();
        initListener();
        initSwipeLayout();
        initVoiceView();
        initBottomView();

        /**
         * 清除某个会话中的未读消息数
         * conversationType 会话类型
         * targetId         会话目标ID
         */
        RongIMClient.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.PRIVATE, target.getUserid(), new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    /**
     * 初始化标题栏
     **/
    private void initTitle(){
        getDefaultNavigation().setTitle(target.getNickname());
        getDefaultNavigation().getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.keyboardControl(ChatActivity.this,false,edit_msg);
                finish();
            }
        });
    }

    /**
     * 初始化内容
     **/
    private void initSwipeLayout(){
        adapter = new ChatAdapter(this,msgs,target);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        rc_view.setRefreshProgressStyle(ProgressStyle.BallClipRotatePulse);
        rc_view.setLoadingMoreEnabled(false);
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

        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
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
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                    btn_chat_add.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                        btn_chat_add.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 初始化语音布局
     * @param
     * @return void
     */
    private void initVoiceView() {

        drawable_Anims = new Drawable[] {
                getResources().getDrawable(R.mipmap.voice_land_1),
                getResources().getDrawable(R.mipmap.voice_land_2),
                getResources().getDrawable(R.mipmap.voice_land_3),
                getResources().getDrawable(R.mipmap.voice_land_4),
                getResources().getDrawable(R.mipmap.voice_land_5),
                getResources().getDrawable(R.mipmap.voice_land_6),
                getResources().getDrawable(R.mipmap.voice_land_7),
                getResources().getDrawable(R.mipmap.voice_land_8),
                getResources().getDrawable(R.mipmap.voice_land_9)};// 话筒动画

        btn_speak.setOnTouchListener(new VoiceTouchListener());

        recordManager = RecordManager.getInstance(this, PreferenceUtil.getInstance().getLastAccount(),target.getUserid(),drawable_Anims.length);

        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {
            @Override
            public void onVolumnChanged(int value) {
                iv_record.setImageDrawable(drawable_Anims[value-1]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                AppLogUtil.d("剩余时间:"+recordTime+"s");
                if(recordTime==0){
                    //一分钟结束
                    // 需要重置按钮
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    //取消录音框
                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音文件
                    sendVoiceMessage(recordManager.getRecordFilePath(),60);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    public Object getPresenter() {
        return null;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_chat;
    }

    /**
     * 显示录音时间过短的Toast
     * @Title: showShortToast
     * @return void
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    /**
     * 初始化各种监听
     **/
    private void initListener(){
        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.VISIBLE) {
                    //隐藏底部
                    layout_add.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
            }
        });

        btn_chat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    CommonUtil.keyboardControl(ChatActivity.this, false, edit_msg);
                } else {
                    layout_more.setVisibility(View.GONE);
                }
            }
        });

        btn_chat_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_msg.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                CommonUtil.keyboardControl(ChatActivity.this, false, edit_msg);
            }
        });

        btn_chat_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditState();
            }
        });

        btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        tv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVideoMessage();
            }
        });

        tv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageMessage();
            }
        });

        tv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLocationMessage();
            }
        });
    }

    /**
     * 显示编辑状态 显示输入法
     * @return void
     */
    private void showEditState() {
        edit_msg.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_msg.requestFocus();
        layout_more.setVisibility(View.GONE);
        showSoftInputView();
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_msg, 0);
        }
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
                    }

                    @Override
                    public void onSuccess(Message message) {
                        //发送成功 更新
                        adapter.addOne(message);
                        edit_msg.setText("");
                        scrollToBottom();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        //发送失败  需要显示重新发送的按钮
                    }
                });
    }

    /**
     * 发送本地图片地址
     */
    public void sendImageMessage(){

    }

    /**
     * 发送语音消息
     * @Title: sendVoiceMessage
     * @param  local
     * @param  length
     * @return void
     */
    private void sendVoiceMessage(final String local, int length) {
        FileMessage fileMessage = FileMessage.obtain(Uri.fromFile(new File(local)));
        Map<String,Object> map =new HashMap<>();
        map.put(FileMessageDALEx.FILETYPE, FileMessageDALEx.FileMessageType.Voice.code);//录音文件
        map.put(FileMessageDALEx.DURATION,length);//时长
        fileMessage.setExtra(new Gson().toJson(map));
        Message myMessage = Message.obtain(target.getUserid(), Conversation.ConversationType.PRIVATE, fileMessage);
        RongIMClient.getInstance().sendMediaMessage(myMessage, "收到一条语音", "", new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int i) {
                //发送进度 显示转圈圈
            }

            @Override
            public void onAttached(Message message) {
                //保存数据库成功 显示出来
                adapter.addOne(message);
                scrollToBottom();
            }

            @Override
            public void onSuccess(Message message) {
                //发送成功  隐藏转圈圈
                adapter.updateLastMessage(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                //发送失败 显示重新发送的按钮
                adapter.updateLastMessage(message);
            }
        });
    }

    /**
     * 发送视频文件
     */
    private void sendVideoMessage(){

    }

    /**
     * 发送地理位置
     */
    public void sendLocationMessage(){

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
                    if (messages != null && messages.size() > 0) {
                        Collections.reverse(messages);
                        adapter.addData(0, messages);
                        layoutManager.scrollToPositionWithOffset(messages.size() - 1+2, 0);

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
                            layoutManager.scrollToPositionWithOffset(messages.size() - 1+2, 0);
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
            }
        }

    }

    /**
     * @decription 滚动到最底部
     **/
    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() + 1, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == View.VISIBLE) {
                layout_more.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        CommonUtil.keyboardControl(this,false,edit_msg);
        super.onDestroy();
    }

    /**
     * 长按说话
     * @author smile
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!FileUtils.checkSDcard()) {
                        showAppToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                            AppLogUtil.i("放弃发送语音");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(recordManager.getRecordFilePath(),recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

}
