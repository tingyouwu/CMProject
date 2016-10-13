package com.kw.app.avcloudlib;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.kw.app.avcloudlib.annotation.AVAnnotationCache;
import com.kw.app.avcloudlib.annotation.AVObjectDao;

/**
 * learnCloud 管理器
 */
public class AVCloudModuleManager {

    public static final String AVCloud_AppId = "nkwRXV6Y4erBpSivYh2YOXxU-gzGzoHsz";
    public static final String AVCloud_AppKey = "WPP7GUDL8VTviukIdawdMsS0";

    public static void init(Context applicationContext){
        AVOSCloud.initialize(applicationContext,AVCloud_AppId, AVCloud_AppKey);
    }

    private static volatile AVCloudModuleManager sInstance = null;

    /** AVCloud对象中字段 缓存，注入时用到 */
    private AVAnnotationCache avAnnotationCache;

    public static AVCloudModuleManager getInstance() {
        if (sInstance == null) {
            synchronized (AVCloudModuleManager.class) {
                if (sInstance == null) {
                    sInstance = new AVCloudModuleManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 功能描述：退出应用调用该方法，用于清除所有缓存对象
     **/
    public void clearCache(){
        if(avAnnotationCache != null)avAnnotationCache.clear();//清除掉bmob缓存对象
        AVObjectDao.clear();
    }

    public AVAnnotationCache getAVAnnotationCache() {
        if(avAnnotationCache == null)
            synchronized (this){
                if(avAnnotationCache == null){
                    avAnnotationCache = new AVAnnotationCache();
                }
            }
        return avAnnotationCache;
    }

}
