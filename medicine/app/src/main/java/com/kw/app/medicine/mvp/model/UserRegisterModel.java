package com.kw.app.medicine.mvp.model;

import android.content.Context;
import android.text.TextUtils;

import com.kw.app.commonlib.utils.FileUtils;
import com.kw.app.medicine.base.CloudManager;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IUserRegisterContract;
import com.kw.app.ormlib.OrmModuleManager;
import com.kw.app.widget.ICallBack;

import java.io.File;

/**
 * @author wty
 */
public class UserRegisterModel implements IUserRegisterContract.IUserRegisterModel {
    private String logoPath;

    @Override
    public void register(final Context context, final UserDALEx user, final ICallBack<String> callBack) {
        if(!TextUtils.isEmpty(user.getLogourl())){
            uploadFile(context, user.getLogourl(), new ICallBack<String>() {
                @Override
                public void onSuccess(String url) {
                    //上传完头像后 本地截图文件
                    logoPath = user.getLogourl();
                    user.setLogourl(url);
                    signUp(user,callBack);
                }

                @Override
                public void onFaild(String msg) {
                    FileUtils.deleteFile(user.getLogourl());
                    callBack.onFaild(msg);
                }
            });
        }else{
            signUp(user,callBack);
        }
    }

    /**
     * @Decription 上传文件
     **/
    private void uploadFile(final Context context, final String compresspath, final ICallBack<String> callBack){
        File file = new File(compresspath);
        CloudManager.getInstance().getFileManager().uploadFile(context, "head_"+ file.getName(),compresspath, new ICallBack<String>() {
            @Override
            public void onSuccess(String url) {
                callBack.onSuccess(url);
            }

            @Override
            public void onFaild(String msg) {
                callBack.onFaild(msg);
            }
        });
    }

    /**
     * @Decription 注册用户
     **/
    private void signUp(final UserDALEx user, final ICallBack<String> callBack){
        CloudManager.getInstance().getUserManager().register(user, new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                //注册成功之后设置一下当前数据库名字
                OrmModuleManager.getInstance().setCurrentDBName(user.getUserid());
                user.saveOrUpdate();
                FileUtils.deleteFile(logoPath);
                callBack.onSuccess(user.getUserid());
            }

            @Override
            public void onFaild(String msg) {
                //上传完头像后 删掉本地截图文件
                FileUtils.deleteFile(logoPath);
                callBack.onFaild(msg);
            }
        });
    }

}
