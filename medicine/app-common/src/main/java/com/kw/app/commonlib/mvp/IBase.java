package com.kw.app.commonlib.mvp;

import android.os.Bundle;
import android.view.View;

/**
 * @author wty
 * 基础事件
 */
public interface IBase<P> {
    /**
     * 功能描述：获取当前的presenter
     **/
    P getPresenter();

    /**
     * 功能描述：给view绑定数据
     **/
    void onInitView(Bundle savedInstanceState);

    /**
     * 功能描述：获得当前页面最顶层的view
     **/
    View getView();

    /**
     * 功能描述：获得布局文件Id
     **/
    int getLayoutResource();
}
