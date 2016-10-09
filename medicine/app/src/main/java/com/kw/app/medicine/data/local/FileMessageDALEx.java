package com.kw.app.medicine.data.local;

import android.text.TextUtils;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.SqliteDao;

import org.json.JSONObject;

import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;

/**
 * 存储文件信息
 **/
public class FileMessageDALEx extends SqliteBaseDALEx {

    public static String FILETYPE = "filetype";
    public static String DURATION = "duration";

	@DatabaseField(primaryKey=true,Type= DatabaseField.FieldType.VARCHAR)
	private String msgid;

	@DatabaseField(Type = DatabaseField.FieldType.INT)
	private int type; //文件信息类型

    @DatabaseField(Type = DatabaseField.FieldType.INT)
    private int duration;//时长

    @DatabaseField(Type= DatabaseField.FieldType.VARCHAR)
    private String localpath;//文件本地路径

    @DatabaseField(Type= DatabaseField.FieldType.VARCHAR)
    private String serverpath;//文件服务路径


    public static FileMessageDALEx get() {
        return (FileMessageDALEx) SqliteDao.getDao(FileMessageDALEx.class);
    }

    /**将FileMessage转成 AgreeAddFriendMessage
     * @param msg 消息
     * @return
     */
    public static FileMessageDALEx convert(Message msg){

        FileMessage fileMessage = (FileMessage) msg.getContent();
        FileMessageDALEx file =new FileMessageDALEx();
        file.setMsgid(msg.getUId());
        if(fileMessage.getFileUrl() != null)
            file.setServerpath(fileMessage.getFileUrl().toString());

        try {
            String extra = fileMessage.getExtra();
            if(!TextUtils.isEmpty(extra)){
                JSONObject json =new JSONObject(extra);
                if(json.has(FILETYPE)) {
                    file.setType(json.getInt(FILETYPE));
                }

                if(json.has(DURATION)){
                    file.setDuration(json.getInt(DURATION));
                }
            }else{
                AppLogUtil.i("FileMessage 的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 是否是语音信息
     **/
    public boolean isVoice(){
        return this.type == FileMessageType.Voice.code?true:false;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getServerpath() {
        return serverpath;
    }

    public void setServerpath(String serverpath) {
        this.serverpath = serverpath;
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