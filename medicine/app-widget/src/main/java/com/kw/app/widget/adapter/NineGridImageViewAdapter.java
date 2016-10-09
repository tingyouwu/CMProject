package com.kw.app.widget.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.kw.app.widget.view.imageview.ColorFilterImageView;

import java.util.List;

/**
 * Created by Jaeger on 16/2/24.
 *
 * Email: chjie.jaeger@gamil.com
 * GitHub: https://github.com/laobie
 */
public abstract class NineGridImageViewAdapter<T> {
    public abstract void onDisplayImage(Context context, ImageView imageView, T t);

    public void onItemImageClick(Context context,View v, int index, List<T> list) {
    }

    public ImageView generateImageView(Context context) {
        ColorFilterImageView imageView = new ColorFilterImageView(context);
        //把图片按比例扩大/缩小到View的宽度，居中显示
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}