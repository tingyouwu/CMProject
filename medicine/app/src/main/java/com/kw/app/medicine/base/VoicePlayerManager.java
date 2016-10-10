package com.kw.app.medicine.base;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.utils.record.VoicePlayer;
import java.io.File;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * 管理播放语音
 */
public class VoicePlayerManager {

	public static volatile VoicePlayerManager instance;
	private static Object obj = new Object();
	private VoicePlayer voicePlayer;
	private String playingUrl;
	private String downloadUrl;
	private Context context;

	private VoicePlayerManager(Context context) {
		voicePlayer = new VoicePlayer();
		voicePlayer.setListener(new VoicePlayer.OnStatusChangeListener() {
			@Override
			public void onCompletion() {
				playingUrl = null;
				downloadUrl = null;
				//外部传入事件
				if(listener!=null){
					listener.onStop();
				}
			}

			@Override
			public void onPlaying(String filePath) {
				playingUrl = filePath;
				//外部传入事件
				if(listener!=null){
					listener.onPlaying(filePath);
				}
			}

			@Override
			public void onStop() {
				playingUrl = null;
				downloadUrl = null;
				if(listener!=null){
					listener.onCompletion();
				}
			}
		});
	}


	public static VoicePlayerManager getInstance(Context context) {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new VoicePlayerManager(context);
				}
			}
		}
		return instance;
	}

	public void play(Message msg){
		if(!TextUtils.isEmpty(this.playingUrl)){
			//当前有录音在播放
			voicePlayer.stop();
		}

		if(TextUtils.isEmpty(msg.getUId())){
			Toast.makeText(context,"语音文件路径为空，无法播放",Toast.LENGTH_LONG).show();
		}

		if(!checkFileExsit(msg.getUId())){
			downloadVoice(msg);
		}else{
			voicePlayer.playByPath(AppConstant.VOICE_PATH + File.separator + msg.getUId());
		}
	}

	VoicePlayer.OnStatusChangeListener listener;//外部传进来的listener
	public void setOnStatusChangeListener(VoicePlayer.OnStatusChangeListener listener){
		this.listener = listener;
	}

	/**
	 * @Decription 判断当前文件是否存在
	 **/
	public boolean checkFileExsit(String filename){
		//这里的url为文件id
		File file = new File(AppConstant.VOICE_PATH+"/"+filename);
		return file.exists();
	}

	/**
	 * 录音当前状态
	 **/
	public enum Status{
		Downloading,Playing,Normal
	}

	/**
	 * @Decription 获取当前状态
	 **/
	public Status getStatus(String fileid){
		if(TextUtils.isEmpty(fileid))return Status.Normal;
		if(!TextUtils.isEmpty(playingUrl) &&  (AppConstant.VOICE_PATH+"/"+fileid).equals(playingUrl)){
			//url与播放中的音频匹配
			return Status.Playing;
		}
		if(!TextUtils.isEmpty(downloadUrl) &&  fileid.equals(downloadUrl)){
			return Status.Downloading;
		}
		return Status.Normal;
	}

	public synchronized void stopAndDestory(){
		try {
			if(!TextUtils.isEmpty(playingUrl)) {
				voicePlayer.stop();
			}
			instance = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadVoice(final Message msg){
		this.downloadUrl = msg.getUId();
		RongManager.getInstance().downloadMediaFile(msg, new IRongCallback.IDownloadMediaMessageCallback() {
			@Override
			public void onSuccess(Message message) {
				downloadUrl = null;
				voicePlayer.playByPath(AppConstant.VOICE_PATH+"/"+msg.getUId());
			}

			@Override
			public void onProgress(Message message, int i) {

			}

			@Override
			public void onError(Message message, RongIMClient.ErrorCode errorCode) {
				downloadUrl = null;
			}

			@Override
			public void onCanceled(Message message) {
				downloadUrl = null;
			}
		});
	}

}