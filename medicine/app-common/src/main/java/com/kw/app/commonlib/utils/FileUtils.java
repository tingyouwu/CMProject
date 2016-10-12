package com.kw.app.commonlib.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.kw.app.commonlib.base.AppConstant;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Decription 文件处理工具类
 */
public class FileUtils {

    public static final String POSTFIX = ".JPEG";
    private static final int MB = 1024 * 1024;//1MB

    public static File createCameraFile(Context context) {
        return createMediaFile(AppConstant.CAMERA_PATH);
    }
    public static File createCropFile(Context context) {
        return createMediaFile(AppConstant.CROP_PATH);
    }

    /**
     * @Decription 检查是否有存储卡
     **/
    public synchronized static boolean checkSDcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @Decription SD卡是否有足够的空间 以50M为标准
     */
    public synchronized static boolean hasEnoughMemory() {
        return getSDAvailableSize() > 50 * MB; // 50M
    }

    /**
     * @Decription 计算SD卡的剩余空间
     * @return 剩余空间
     */
    public synchronized static long getSDAvailableSize() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return getAvailableSize(Environment.getExternalStorageDirectory()
                    .toString());
        }
        return 0;
    }

    /**
     * @Decription 计算剩余空间
     */
    private synchronized static long getAvailableSize(String path) {
        StatFs fileStats = new StatFs(path);
        fileStats.restat(path);
        return (long) fileStats.getAvailableBlocks() * fileStats.getBlockSize(); // 注意与fileStats.getFreeBlocks()的区别
    }

    /**
     * @Decription 判断文件是否存在
     **/
    public static boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    private static File createMediaFile(String parentPath){
        File temp = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        try {
            File root = new File(parentPath);
            if(!root.exists()){
                root.mkdirs();
            }

            File picFile = new File(parentPath,timeStamp+POSTFIX);
            if(!picFile.exists()){
                    picFile.createNewFile();
            }
            temp = picFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 删除文件安全方式：
     */
    public static void deleteFile(String filepath){
        deleteFile(new File(filepath));
    }

    /**
     * 删除文件安全方式
     */
    public static void deleteFile(File file) {
        if(file==null || !file.exists())return;
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    /**
     * 安全删除文件.
     */
    private static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }

}
