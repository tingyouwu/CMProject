package com.kw.app.commonlib.utils.record;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 播放录音文件
 */
public class VoicePlayer {

	private MediaPlayer mMediaPlayer;
	private OnStatusChangeListener listener;
	
	public void setListener(OnStatusChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * 根据文件路径播放声音<BR>
	 * @param filePath
	 */
	public void playByPath(String filePath) {
		try {
			if(mMediaPlayer==null){
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			}
			
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			if(listener!=null)listener.onPlaying(filePath);
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							listener.onCompletion();
						}
					});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		if(listener!=null)listener.onStop();
	}

	public interface OnStatusChangeListener {
		void onCompletion();
		void onPlaying(String filePath);
		void onStop();
	}
}