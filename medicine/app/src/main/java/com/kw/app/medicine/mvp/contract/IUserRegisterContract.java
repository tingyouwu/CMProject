package com.kw.app.medicine.mvp.contract;

import android.content.Context;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

/**
 * @author wty
 */
public interface IUserRegisterContract {

    interface IUserRegisterModel extends IBaseModel {
        void register(Context context, UserDALEx user, ICallBack<String> callBack);
    }

    interface IUserRegisterView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        boolean checkNet();
        void showNoNet();
        void finishActivity(String userid);
    }

}
