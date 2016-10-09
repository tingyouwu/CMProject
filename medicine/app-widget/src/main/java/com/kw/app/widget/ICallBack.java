package com.kw.app.widget;

/**
 * @author wty
 * 反馈类
 */
public interface ICallBack<T> {
    void onSuccess(T data);
    void onFaild(String msg);
}
