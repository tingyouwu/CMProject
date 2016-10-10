package com.kw.app.commonlib.base;

import android.content.Context;

/**
 * @author wty
 * 功能描述：本应用对应的一些常量
 **/
public class AppConstant {

    public static String APP_PATH;
    public static String SAVEIMAGE_PATH; //保存图片路径
    public static String IMAGE_PATH;//图片路径
    public static String VOICE_PATH;//录音路径
    public static String DOC_PATH;//文件路径
    public static String CAMERA_PATH;//拍照路径
    public static String CROP_PATH;//裁剪路径
    public static String CACHE_PATH;//缓存文件路径

    public static void init(Context applicationContext){
        APP_PATH = android.os.Environment.getExternalStorageDirectory().getPath() + "/"+applicationContext.getPackageName();
        SAVEIMAGE_PATH = APP_PATH + "/SaveImages";
        IMAGE_PATH = APP_PATH + "/Images";
        VOICE_PATH = APP_PATH  + "/voice";
        DOC_PATH = APP_PATH  + "/file";
        CAMERA_PATH = APP_PATH + "/CameraImage/";
        CROP_PATH = APP_PATH + "/CropImage/";
        CACHE_PATH = APP_PATH + "/CacheFile/";
    }

    public static class ActivityResult{
        public final static int Request_Camera = 100;
        public final static int Request_Image = 101;
        public final static int Request_Preview = 102;
        public final static int Request_Register = 103;
    }

}
