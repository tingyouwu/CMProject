package com.kw.app.commonlib.utils.record;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.CountDownTimer;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.utils.AppLogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wty
 * 录音
 **/
public class RecordManager implements RecordControlListener{

	public static int MAX_RECORD_TIME = 60*1000;
	public static int MIN_RECORD_TIME = 1;
	private File file;
	private Context mContext;
	private long starttime;
	private long endtime;
	private String filename;
	private String filepath;
	private String userid;//账号
	private String conversationid;//账号下的聊天id
	private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
	private static volatile RecordManager recordManager;
	private static Object obj = new Object();
	OnRecordChangeListener onRecordChangeListener;
	private MediaRecorder recorder;
	private int voiceLevel = 4;//音量等级

	CountDownTimer countDowntimer = new CountDownTimer(MAX_RECORD_TIME,500) {
		@Override
		public void onTick(long millisUntilFinished) {
			//剩余时间  millisUntilFinished
			if(onRecordChangeListener != null){
				onRecordChangeListener.onVolumnChanged(getVoiceLevel(voiceLevel));
				onRecordChangeListener.onTimeChanged((int)(millisUntilFinished/1000),filepath);
			}
		}

		@Override
		public void onFinish() {
			//60s到了
		}
	};


	private RecordManager() {
	}

	public static RecordManager getInstance(Context context,String userid,String conversationid,int voiceLevel) {
		if(recordManager == null) {
			synchronized(obj) {
				if(recordManager == null) {
					recordManager = new RecordManager();
				}
				recordManager.init(context,userid,conversationid,voiceLevel);
			}
		}
		return recordManager;
	}

	public void init(Context context,String userid,String conversationid,int voiceLevel) {
		this.mContext = context;
		this.userid = userid;
		this.conversationid = conversationid;
		this.voiceLevel = voiceLevel;
	}

	public void clear() {
		if(this.onRecordChangeListener != null) {
			this.onRecordChangeListener = null;
		}

		if(this.countDowntimer != null){
			this.countDowntimer.cancel();
		}
	}

	public void setOnRecordChangeListener(OnRecordChangeListener listener) {
		this.onRecordChangeListener = listener;
	}

	@Override
	public void startRecording() {
		if(this.recorder == null) {
			this.recorder = new MediaRecorder();
			//设置VoiceRecorder的音频源为麦克风
			this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//设置音频的格式（必须在设置声音编码格式之前设置，否则抛出IllegalStateException异常）
			this.recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			//设置音频的编码为amr
			this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//设置录制的音频通道数。
			this.recorder.setAudioChannels(1);
			//设置所录制的声音的编码位率。
			this.recorder.setAudioEncodingBitRate(12200);
		} else {
			this.recorder.stop();
			this.recorder.reset();
		}

		this.filename = this.getRecordFileName();
		this.filepath = this.getRecordFilePath();
		this.file = new File(this.filepath);
		this.recorder.setOutputFile(this.file.getAbsolutePath());

		try {
			this.recorder.prepare();
			this.recorder.start();
			this.atomicBoolean.set(true);
			this.starttime = (new Date()).getTime();
			this.countDowntimer.cancel();
			this.countDowntimer.start();

		} catch (IOException e) {
			AppLogUtil.d("IOException thrown while trying to record a greeting");
			this.atomicBoolean.set(false);
			this.recorder.release();
			this.countDowntimer.cancel();
			this.recorder = null;
		}
	}

	@Override
	public void cancelRecording() {
		if(this.recorder != null) {
			this.recorder.setOnErrorListener(null);
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			if(this.file != null && this.file.exists() && !this.file.isDirectory()) {
				this.file.delete();
			}
			this.countDowntimer.cancel();
			this.atomicBoolean.set(false);
		}
	}

	@Override
	public int stopRecording() {
		if(this.recorder != null) {
			this.atomicBoolean.set(false);
			this.recorder.setOnErrorListener(null);
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			this.countDowntimer.cancel();
			this.endtime = (new Date()).getTime();
			return (int)(endtime-starttime) / 1000;
		} else {
			return 0;
		}
	}

	@Override
	public boolean isRecording() {
		return this.atomicBoolean.get();
	}

	@Override
	public MediaRecorder getMediaRecorder() {
		return this.recorder;
	}

	@Override
	public String getRecordFilePath() {
		File filepath;
		if(!(filepath=new File(AppConstant.VOICE_PATH)).exists()){
			filepath.mkdirs();
		}
		filepath = new File(filepath.getAbsolutePath() + File.separator + this.filename);
		try{
			if(!filepath.exists()){
				filepath.createNewFile();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		return filepath.getAbsolutePath();
	}

	/**
	 * 获取录音文件名字
	 **/
	protected String getRecordFileName() {
		return System.currentTimeMillis() + ".amr";
	}

	/**
	 * 获取声音的等级
	 * @param maxLevel 自己设定的最大值(音量分的等级数)
	 * @return
	 */
	private int getVoiceLevel(int maxLevel) {
		if (isRecording()) {
			try {
				// mRecorder.getMaxAmplitude()，值域是1-32767
				// 不加1取不到最大值
				return maxLevel * recorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
}
