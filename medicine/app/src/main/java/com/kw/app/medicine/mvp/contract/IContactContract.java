package com.kw.app.medicine.mvp.contract;

import android.content.Context;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public interface IContactContract {

    interface IContactModel extends IBaseModel {
        void refreshFriend(Context context, ICallBack<List<UserDALEx>> callBack);
    }

    interface IContactView extends IBaseView {
        boolean checkNet();
        void showNoNet();
        void refreshFriend(List<UserDALEx> list);
    }
}
