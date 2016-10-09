package com.kw.app.widget.view.formitem;


/**
 * @Decription 表单内容通用协议
 **/
public interface IFormContent {
    /**
     * 是否只读
     **/
    void setIsReadOnly(boolean isReadOnly);
    /**
     * 验证数据
     **/
    boolean validate();
    /**
     * 获取输入数据
     **/
    String getValue();
    /**
     * 设置数据
     **/
    void setValue(String value);
    /**
     * 设置提示语
     **/
    void setHint(String hint);
    /**
     * 清除提示语
     **/
    void clearValue();
}