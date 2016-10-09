package com.kw.app.medicine.mvp.contract;

import android.content.Context;
import android.net.Uri;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

/**
 * Created by kuangminan on 2016/9/29.
 */
public interface IMyAccountContract {
    interface IMyAccountModel extends IBaseModel {
        void updateHeadImage(Context context, Uri uri, ICallBack<String> callBack);
    }

    interface IMyAccountView extends IBaseView {
        void showLoading(String loadmsg);
        void dismissLoading(OnDismissCallbackListener callback);
        boolean checkNet();
        void showNoNet();
        //void finishActivity(String userid);
    }
}
