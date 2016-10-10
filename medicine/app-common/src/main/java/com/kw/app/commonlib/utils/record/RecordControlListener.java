package com.kw.app.commonlib.utils.record;

import android.media.MediaRecorder;

/**
 * @author wty
 * 录音
 **/
public interface RecordControlListener {

    void startRecording();

    void cancelRecording();

    /**
     * 停止录音 并且返回 时间长度（s）
     **/
    int stopRecording();

    boolean isRecording();

    MediaRecorder getMediaRecorder();

    String getRecordFilePath();

}
