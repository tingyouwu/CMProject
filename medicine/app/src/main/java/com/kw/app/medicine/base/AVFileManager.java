package com.kw.app.medicine.base;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.kw.app.widget.ICallBack;

import java.io.FileNotFoundException;

/**
 * @author :wty
 * 管理文件
 */
public class AVFileManager implements IFileManager{

    private static AVFileManager ourInstance = new AVFileManager();

    public static AVFileManager getInstance() {
        return ourInstance;
    }

    private AVFileManager() {}


    @Override
    public void uploadFile(Context context, String uploadname, final String filepath, final ICallBack<String> callBack) {
        try {
            final AVFile file = AVFile.withAbsoluteLocalPath(uploadname,filepath);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e == null){
                        //上传成功
                        callBack.onSuccess(file.getUrl());
                    }else{
                        //上传失败
                        callBack.onFaild(e.getMessage());
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callBack.onFaild("找不到文件");
        }
    }
}
