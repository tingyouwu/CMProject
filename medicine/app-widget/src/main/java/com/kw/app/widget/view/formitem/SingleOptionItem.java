package com.kw.app.widget.view.formitem;

public class SingleOptionItem {
	
	/** 选项显示的值 */
	private String option;
	
	/** 输入框显示的文字 */
	private String text;
	
	/** 选项的id */
	private String id;


    public SingleOptionItem(String text, String id) {
        this.option = text;
        this.text = text;
        this.id = id;
    }

	public SingleOptionItem(String option, String text, String id) {
		this.option = option;
		this.text = text;
		this.id = id;
	}
	
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
