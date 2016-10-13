package com.kw.app.medicine.base;

import android.content.Context;

import com.kw.app.widget.ICallBack;

/**
 * @author :wty
 * 上传下载文件
 */
public interface IFileManager {

    /**
     * @Decription 上传文件
     **/
    void uploadFile(final Context context, final String filepath, final ICallBack<String> callBack);

}
