package com.kw.app.photolib.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Decription 文件夹model
 */
public class FolderModel implements Serializable{
    public String name;
    public String path;
    private String firstImagePath;
    private int imageNum;
    public List<ImageModel> images = new ArrayList<ImageModel>();

    @Override
    public boolean equals(Object o) {
        FolderModel other = (FolderModel) o;
        return TextUtils.equals(other.path, path);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
    }
}
