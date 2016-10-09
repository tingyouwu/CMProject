package com.kw.app.medicine.mvp.contract;

import android.content.Context;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

/**
 * @author wty
 */
public interface IUserLoginContract {

    interface IUserLoginModel extends IBaseModel {
        void login(Context context, String name, String psw, boolean isAutoLogin, ICallBack<UserBmob> callBack);
    }

    interface IUserLoginView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        boolean checkNet();
        void showNoNet();
        void finishActivity();
        void showLoadingView(String loadmsg);
        void dismissLoadingView();
        void updateLoadingMsg(String msg);
        void showFailed(String msg);
    }

}
