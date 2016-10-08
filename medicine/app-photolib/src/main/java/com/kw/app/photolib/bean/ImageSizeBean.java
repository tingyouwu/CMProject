package com.kw.app.photolib.bean;

import java.io.Serializable;

/**
 * 
* @ClassName: ImageSize
* @Description: 保持图片宽高
* @author wty
*
 */
public class ImageSizeBean implements Serializable {

	private int width;
	private int height;

	public ImageSizeBean(int width, int height){
		this.width = width;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
