package com.kw.app.widget.bean;

import java.io.Serializable;

/**
 * @Decription dialog选项数据
 **/
public class DialogOptionModel implements Serializable {

	private static final long serialVersionUID = 1L;
    private String text;
    private String value;

    /**
     * @param text 显示的数据
     * @param value 真正的数据
     **/
	public DialogOptionModel(String text, String value) {
		this.text = text;
		this.value = value;
	}

    public DialogOptionModel(String text, int value) {
        this.text = text;
        this.value = String.valueOf(value);
    }

    public String getText(){
        return text;
    }
    public String getValue(){
        return value;
    }

}