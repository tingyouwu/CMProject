package com.kw.app.photolib.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * @Decription 图片model
 */
public class ImageModel implements Serializable{

    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;

    public String path;
    public String name;
    public long time;
    public int type;

    public ImageModel(int type){
        this(type,"","");
    }

    public ImageModel(String path, String name){
        this(TYPE_PICTURE,path,name);
    }

    public ImageModel(int type, String path, String name){
        this.path = path;
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        ImageModel other = (ImageModel) o;
        return TextUtils.equals(this.path, other.path);
    }
}
