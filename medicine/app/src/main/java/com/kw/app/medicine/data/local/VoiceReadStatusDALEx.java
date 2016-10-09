package com.kw.app.medicine.data.local;

import android.content.ContentValues;

import com.kw.app.ormlib.SqliteBaseDALEx;
import com.kw.app.ormlib.annotation.DatabaseField;
import com.kw.app.ormlib.annotation.SqliteDao;

/**
 * 语音是否已读状态
 **/
public class VoiceReadStatusDALEx extends SqliteBaseDALEx {

    public static final int Status_Unread = 0;
    public static final int Status_Read = 1;


	@DatabaseField(primaryKey=true,Type= DatabaseField.FieldType.VARCHAR)
	private String msgid;

	@DatabaseField(Type = DatabaseField.FieldType.INT)
	private int status; //0.未播放 1.播放

    public static VoiceReadStatusDALEx get() {
        return (VoiceReadStatusDALEx) SqliteDao.getDao(VoiceReadStatusDALEx.class);
    }

    public boolean isReaded(String msgid){
        VoiceReadStatusDALEx item = findOne("select null from " + TABLE_NAME + " where msgid = ? and status = 1 ",new String[]{msgid});
        return item!=null?true:false;
    }

    public void updateReadStatus(String msgid){
        if(isExist(msgid)){
            ContentValues values = new ContentValues();
            values.put("status", 1);
            getDB().update(TABLE_NAME,values, "msgid =?  ", new String[]{msgid});
        }else{
            VoiceReadStatusDALEx dalex = new VoiceReadStatusDALEx();
            dalex.setMsgid(msgid);
            dalex.setStatus(Status_Read);
            dalex.saveOrUpdate();
        }
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}