package com.kw.app.commonlib.mvp.presenter;

import com.kw.app.commonlib.mvp.view.IBaseView;

/**
 * @author wty
 * BasePresenter是所有Presenter的基类
 * (Alt+Enter 自动实现接口函数)
 */
public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {
    protected V mView;

    @Override
    public void attachView(V mvpView) {
        this.mView = mvpView;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

}
