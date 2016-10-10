package com.kw.app.medicine.mvp.contract;

import android.content.Context;

import com.kw.app.commonlib.mvp.model.IBaseModel;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.imlib.bean.RongConversation;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public interface IMessageContract {

    interface IMessageModel extends IBaseModel {
        void refreshMessage(Context context, ICallBack<List<RongConversation>> callBack);
    }

    interface IMessageView extends IBaseView {
        boolean checkNet();
        void showNoNet();
        void refreshMessage(List<RongConversation> list);
        void onRefreshComplete();
    }
}
