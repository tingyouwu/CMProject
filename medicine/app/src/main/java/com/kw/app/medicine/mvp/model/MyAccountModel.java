package com.kw.app.medicine.mvp.model;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.kw.app.bmoblib.annotation.BmobExceptionCode;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.mvp.contract.IMyAccountContract;
import com.kw.app.widget.ICallBack;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by kuangminan on 2016/9/29.
 */
public class MyAccountModel implements IMyAccountContract.IMyAccountModel {

    @Override
    public void updateHeadImage(final Context context, final Uri uri, final ICallBack<String> callBack) {
        if(!TextUtils.isEmpty(uri.getPath())){
            updateToBmobFile(context,uri.getPath(), callBack);
        }
    }

    private void updateToBmobFile(final Context context, final String compresspath, final ICallBack<String> callBack){
        BmobFile.uploadBatch(new String[]{compresspath}, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> urls) {
                if (urls.size() == 1) {//如果数量相等，则代表文件上传完成
                    updateTomBmob(context,urls.get(0),callBack);
                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
            }

            @Override
            public void onError(int code, String msg) {
                callBack.onFaild(BmobExceptionCode.match(code));
            }
        });
    }

    private void updateTomBmob(final Context context, final String uri,final ICallBack<String> callBack){
        final UserBmob bmob = new UserBmob();
        bmob.setLogourl(uri);
        String mMyid = PreferenceUtil.getInstance().getLastAccount();
        bmob.update(mMyid, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    PreferenceUtil.getInstance().writePreferences(PreferenceUtil.LogoUrl, uri);
                    callBack.onSuccess(uri);
                }else{
                    callBack.onFaild("头像更新失败:" + e.getMessage());
                }
            }
        } );
    }


}