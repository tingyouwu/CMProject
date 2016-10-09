package com.kw.app.medicine.mvp.presenter;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.mvp.contract.IMyAccountContract;
import com.kw.app.medicine.mvp.model.MyAccountModel;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;
import com.orhanobut.logger.Logger;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by kuangminan on 2016/9/29.
 */
public class MyAccountPresenter extends BasePresenter<IMyAccountContract.IMyAccountView> {

    IMyAccountContract.IMyAccountModel mMyAccountModel;

    public MyAccountPresenter(){
        mMyAccountModel = new MyAccountModel();
    }

    public void updateHeadImage(final Context context, final Uri uri, final ImageView mHeadView){
        if(!mView.checkNet()){
            mView.showNoNet();
            return;
        }
        mView.showLoading("请稍候，正在更新中...");

        mMyAccountModel.updateHeadImage(context, uri,new ICallBack<String>() {
            @Override
            public void onSuccess(final String bomburi) {
                mView.dismissLoading(new OnDismissCallbackListener("头像更新成功") {
                    @Override
                    public void onCallback() {
                        Logger.d("KMA ...uri:"+uri.getPath());
                        Logger.d("KMA ...bomburi:"+bomburi);

                        ImageLoaderUtil.loadCircle(context, PreferenceUtil.getInstance().getLogoUrl(), R.mipmap.icon_launcher,mHeadView);
                    }
                });
            }

            @Override
            public void onFaild(String msg) {
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }

}