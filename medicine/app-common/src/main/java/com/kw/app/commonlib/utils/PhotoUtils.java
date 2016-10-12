package com.kw.app.commonlib.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Decription 图片处理工具类
 */
public class PhotoUtils {

    /**
     * @Decription 截图
     * @param uri 图片源路径
     *
     * 根据我们的分析与总结，图片的来源有拍照和相册，而可采取的操作有
     * 使用Bitmap并返回数据
     * 使用Uri不返回数据
     *  前面我们了解到，使用Bitmap有可能会导致图片过大，而不能返回实际大小的图片，我将采用大图Uri，小图Bitmap的数据存储方式。
     **/
    public static void cropSmallImage(Activity activity, Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
        //裁剪框比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //图片输出大小
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //不启用人脸识别
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @Decription 截图
     * @param uri 图片源路径
     *
     * 根据我们的分析与总结，图片的来源有拍照和相册，而可采取的操作有
     * 使用Bitmap并返回数据
     * 使用Uri不返回数据
     *  前面我们了解到，使用Bitmap有可能会导致图片过大，而不能返回实际大小的图片，我将采用大图Uri，小图Bitmap的数据存储方式。
     **/
    public static void cropBigImage(Activity activity, Uri uri, int requestCode,String output_path) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// 才能出剪辑的小方框，不然没有剪辑功能，只能选取图片
        //裁剪框比例
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        //图片输出大小
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output_path);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //不启用人脸识别
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * @Description 获取图片的宽高比例(需要考虑旋转角度)
     **/
    public static float getImageWidthHeightSize(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */

        int degree = readPictureDegree(path);
        if(degree==0 || degree==180){
            return ((float) (options.outWidth))/(options.outHeight);
        }
        return ((float) (options.outHeight))/(options.outWidth);
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 保存bitmap到sd卡filePath文件中 如果有，则删除
     *
     * @param bmp     　bitmap
     * @param filePath     图片名
     * @return
     */
    public static boolean saveBitmap2file(Bitmap bmp, String filePath) {
        if (bmp == null) {
            return false;
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            stream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

}
