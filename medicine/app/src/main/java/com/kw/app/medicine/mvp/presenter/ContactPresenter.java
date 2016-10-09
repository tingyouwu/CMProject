package com.kw.app.medicine.mvp.presenter;

import android.content.Context;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IContactContract;
import com.kw.app.medicine.mvp.model.ContactModel;
import com.kw.app.widget.ICallBack;

import java.util.List;

/**
 * @author wty
 */
public class ContactPresenter extends BasePresenter<IContactContract.IContactView> {

    private IContactContract.IContactModel mContactModel;

    public ContactPresenter(){
        mContactModel = new ContactModel();
    }

    /**
     * @Decription 根据本地最新的一条数据 去服务端拿新数据(比较更新时间)
     **/
    public void refreshFriend(Context context){
        if(!mView.checkNet()){
            mView.showNoNet();
            return;
        }

        mContactModel.refreshFriend(context,new ICallBack<List<UserDALEx>>(){
            @Override
            public void onSuccess(List<UserDALEx> data) {
                mView.refreshFriend(data);
            }

            @Override
            public void onFaild(String msg) {

            }
        });
    }

    /**
     * @Decription 从本地获得所有的朋友
     **/
    public void loadAllFriend(){
        mView.refreshFriend(UserDALEx.get().findAllFriend());
    }
}
