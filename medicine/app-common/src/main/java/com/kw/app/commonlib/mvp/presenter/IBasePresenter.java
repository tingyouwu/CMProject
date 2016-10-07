package com.kw.app.commonlib.mvp.presenter;

import com.kw.app.commonlib.mvp.view.IBaseView;

/**
 * @author wty
 * IBasePresenter是所有Presenter的基类
 */
public interface IBasePresenter<V extends IBaseView> {

    void attachView(V view);

    void detachView();
}
