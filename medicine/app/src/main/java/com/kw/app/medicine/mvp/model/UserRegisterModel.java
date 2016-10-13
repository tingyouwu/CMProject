package com.kw.app.medicine.mvp.model;

import android.content.Context;
import android.text.TextUtils;

import com.kw.app.commonlib.utils.FileUtils;
import com.kw.app.commonlib.utils.luban.Luban;
import com.kw.app.commonlib.utils.luban.OnCompressListener;
import com.kw.app.medicine.base.BmobFileManager;
import com.kw.app.medicine.base.BmobUserManager;
import com.kw.app.medicine.base.IFileManager;
import com.kw.app.medicine.base.IUserManager;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IUserRegisterContract;
import com.kw.app.ormlib.OrmModuleManager;
import com.kw.app.widget.ICallBack;

import java.io.File;

/**
 * @author wty
 */
public class UserRegisterModel implements IUserRegisterContract.IUserRegisterModel {

    private IUserManager userManager = BmobUserManager.getInstance();
    private IFileManager fileManager;

    @Override
    public void register(final Context context, final UserDALEx user, final ICallBack<String> callBack) {
        if(!TextUtils.isEmpty(user.getLogourl())){
            //压缩图片
            Luban.get().load(new File(user.getLogourl()))
                       .putGear(Luban.THIRD_GEAR)
                       .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                            }

                            @Override
                            public void onSuccess(File file) {
                                // 压缩完毕就上传头像图片
                                uploadFile(context,file.getAbsolutePath(), user, callBack);
                            }

                            @Override
                            public void onError(Throwable e) {
                                callBack.onFaild("压缩图片失败:" + e.getMessage());
                            }
                        }).launch();
        }else{
            signUp(user,callBack);
        }
    }

    /**
     * @Decription 上传文件
     **/
    private void uploadFile(final Context context, final String compresspath, final UserDALEx data, final ICallBack<String> callBack){

        fileManager = BmobFileManager.getInstance();
        fileManager.uploadFile(context, compresspath, new ICallBack<String>() {
            @Override
            public void onSuccess(String url) {
                data.setLogourl(url);
                //上传完头像后 删掉本地截图文件
                FileUtils.deleteFile(compresspath);
                signUp(data,callBack);
            }

            @Override
            public void onFaild(String msg) {
                FileUtils.deleteFile(compresspath);
                callBack.onFaild(msg);
            }
        });
    }

    /**
     * @Decription 注册用户
     **/
    private void signUp(UserDALEx user,final ICallBack<String> callBack){
        userManager.register(user, new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                //注册成功之后设置一下当前数据库名字
                OrmModuleManager.getInstance().setCurrentDBName(user.getUserid());
                user.saveOrUpdate();
                callBack.onSuccess(user.getUserid());
            }

            @Override
            public void onFaild(String msg) {
                callBack.onFaild(msg);
            }
        });
    }

}
