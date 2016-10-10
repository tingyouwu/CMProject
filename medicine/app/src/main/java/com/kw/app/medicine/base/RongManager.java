package com.kw.app.medicine.base;

import android.content.Context;
import android.text.TextUtils;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.medicine.data.local.FileMessageDALEx;
import com.kw.app.medicine.data.local.NewFriendDALEx;
import com.kw.app.medicine.data.local.SystemMessageDALEx;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.data.local.VoiceReadStatusDALEx;
import com.kw.app.medicine.event.RefreshChatEvent;
import com.kw.app.medicine.event.RefreshEvent;
import com.kw.app.medicine.event.RefreshMessageTabEvent;
import com.kw.app.ronglib.bean.AddFriendMessage;
import com.kw.app.ronglib.bean.AgreeAddFriendMessage;
import com.kw.app.ronglib.messagecontent.CustomzeContactNotificationMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

/**
 * @author wty
 * 融云管理者
 **/
public class RongManager {

	private static volatile RongManager sInstance = null;
	private Context mContext;

	public static RongManager getInstance(){
		return sInstance;
	}

	public static RongManager init(Context context) {
		if (sInstance == null) {
			synchronized (RongManager.class) {
				if (sInstance == null) {
					sInstance = new RongManager(context);
				}
			}
		}
		return sInstance;
	}

	private RongManager(Context appliationContext) {
		this.mContext = appliationContext;
		RongIMClient.init(appliationContext);
		RongIMClient.getInstance().setOnReceiveMessageListener(new RongReceiveMessageListener());
		try {
			//在这里注册自定义消息
			RongIMClient.getInstance().registerMessageType(CustomzeContactNotificationMessage.class);
			RongIMClient.getInstance().registerMessageType(FileMessage.class);
		} catch (AnnotationNotFoundException e) {
			e.printStackTrace();
		}
	}

	class RongReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
		/**
		 * 收到消息的处理。
		 * @param message 收到的消息实体。
		 * @param left 剩余未拉取消息数目。
		 * @return
		 */
		@Override
		public boolean onReceived(Message message, int left) {
			MessageContent content = message.getContent();
			if(content instanceof CustomzeContactNotificationMessage){
				handleCustomzeContactNotificationMessage((CustomzeContactNotificationMessage) content);
			}else if (content instanceof TextMessage || content instanceof ImageMessage || content instanceof LocationMessage){
				handleChatMessage(message);
				//如果在聊天页面  刷新一下
				EventBus.getDefault().post(new RefreshChatEvent(message));
				//刷新消息tab红点状态
				EventBus.getDefault().post(new RefreshMessageTabEvent());
			}else if(content instanceof FileMessage){
				//文件信息  （语音文件  普通文件 ...）
				handleFileMessage(message);
				//如果在聊天页面  刷新一下
				EventBus.getDefault().post(new RefreshChatEvent(message));
				//刷新消息tab红点状态
				EventBus.getDefault().post(new RefreshMessageTabEvent());
			}
			//发送页面刷新的广播
			EventBus.getDefault().post(new RefreshEvent());
			return false;
		}
	}

	/**
	 * 处理一下自定义的通知信息
	 **/
	private void handleCustomzeContactNotificationMessage(CustomzeContactNotificationMessage contactNotificationMessage){

		//更新一下本地用户信息
		BmobUserModel.getInstance().updateUserInfo(contactNotificationMessage.getSourceUserId());

		if (contactNotificationMessage.getOperation().equals(CustomzeContactNotificationMessage.CONTACT_OPERATION_REQUEST)) {
			//对方发来好友邀请
			AppLogUtil.d("对方发来好友邀请");
			AddFriendMessage add = AddFriendMessage.convert(contactNotificationMessage);

			NewFriendDALEx friend = NewFriendDALEx.get();
			friend.setMsgid(add.getMsgid());
			friend.setMsg(add.getMsg());
			friend.setStatus(NewFriendDALEx.VerifyStatus.Status_Verify_None.code);
			friend.setUid(contactNotificationMessage.getSourceUserId());
			friend.setTime(add.getTime());
			friend.setName(add.getName());

			//本地好友请求表做下校验，本地没有的才允许显示通知栏--有可能离线消息会有些重复
			if(!friend.isExist(friend.getMsgid())){
				friend.saveOrUpdate();
				if(CommonUtil.isAppIsInBackground(mContext))
				    CMNotificationManager.showNotification(mContext,friend.getName(),friend.getMsg(),null);
			}

			SystemMessageDALEx system = SystemMessageDALEx.convert(contactNotificationMessage);
			system.setType(SystemMessageDALEx.SystemMessageType.AddFriend.code);
			if(!system.isExist(system.getMsgid())){
				system.saveOrUpdate();
			}

		} else if (contactNotificationMessage.getOperation().equals(CustomzeContactNotificationMessage.CONTACT_OPERATION_ACCEPT_RESPONSE)) {
			//对方同意我的好友请求 此时需要做的事情：1、添加对方为好友，2、显示通知
			AppLogUtil.d("对方同意我的好友邀请");
			AgreeAddFriendMessage agree = AgreeAddFriendMessage.convert(contactNotificationMessage);
			BmobUserModel.getInstance().addFriend(contactNotificationMessage.getSourceUserId());
			SystemMessageDALEx system = SystemMessageDALEx.convert(contactNotificationMessage);
			system.setType(SystemMessageDALEx.SystemMessageType.AgreeAdd.code);

			if(!system.isExist(system.getMsgid())){
				system.saveOrUpdate();
			}

			if(CommonUtil.isAppIsInBackground(mContext))
				CMNotificationManager.showNotification(mContext, agree.getName(), agree.getMsg(), null);

		}else if(contactNotificationMessage.getOperation().equals(ContactNotificationMessage.CONTACT_OPERATION_REJECT_RESPONSE)){
			//对方拒绝我的好友请求
			AppLogUtil.d("对方拒绝我的好友请求");
		}
	}

	/**
	 * @处理一下聊天信息
	 **/
	private void handleChatMessage(Message message){
		if(CommonUtil.isAppIsInBackground(mContext)){
			MessageContent content = message.getContent();
			String notificationmsg = "[未知]";
			String name = "陌生人";
			if(content instanceof TextMessage){
				notificationmsg = ((TextMessage)content).getContent();
			}else if(content instanceof ImageMessage){
				notificationmsg =  "[图片]";
			}else if(content instanceof LocationMessage){
				notificationmsg = "[位置]";
			}

			UserDALEx user = UserDALEx.get().findById(message.getSenderUserId());
			if(user != null){
				name = user.getNickname();
			}
			CMNotificationManager.showNotification(mContext,name,notificationmsg,null);
		}
	}

	/**
	 * @Decription 处理文件信息
	 **/
	private void handleFileMessage(Message message){
			String notificationmsg = "[未知]";
			String name = "陌生人";
			//存储一下文件信息
			FileMessageType type = getFileType(message);
			if(type != null && type== FileMessageType.Voice){
				//语音信息
				VoiceReadStatusDALEx readStatus = new VoiceReadStatusDALEx();
				readStatus.setMsgid(message.getUId());
				readStatus.setStatus(VoiceReadStatusDALEx.Status_Unread);
				readStatus.saveOrUpdate();
				notificationmsg = "[语音]";
			}
			UserDALEx user = UserDALEx.get().findById(message.getSenderUserId());
			if(user != null){
				name = user.getNickname();
			}
			if(CommonUtil.isAppIsInBackground(mContext)) {
				CMNotificationManager.showNotification(mContext, name, notificationmsg, null);
			}
	}

	/**
	 * @Decription 下载媒体文件
	 **/
	public void downloadMediaFile(final Message message, final IRongCallback.IDownloadMediaMessageCallback callback){
		RongIMClient.getInstance().downloadMediaMessage(message,callback);
	}

	/**
	 * @Decription 获取文件类型
	 **/
	public FileMessageType getFileType(Message message){
		FileMessage fileMessage = (FileMessage) message.getContent();
		FileMessageType type = null;
		try {
			String extra = fileMessage.getExtra();
			if(!TextUtils.isEmpty(extra)){
				JSONObject json =new JSONObject(extra);
				if(json.has(FileMessageDALEx.FILETYPE)) {
					type = FileMessageType.Voice;
				}

			}else{
				AppLogUtil.i("FileMessage 的extra为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	public enum FileMessageType{
		Voice(1,"语音"),Video(2,"视频"),File(3,"文件");
		public int code;
		public String label;
		FileMessageType(int code, String label){
			this.code = code;
			this.label = label;
		}
	}

}
