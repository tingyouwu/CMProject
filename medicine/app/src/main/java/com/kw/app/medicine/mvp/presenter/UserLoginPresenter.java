package com.kw.app.medicine.mvp.presenter;

import android.content.Context;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.imlib.fakeserver.FakeServer;
import com.kw.app.imlib.fakeserver.FakeUser;
import com.kw.app.imlib.fakeserver.HttpUtil;
import com.kw.app.medicine.data.bmob.UserBmob;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IUserLoginContract;
import com.kw.app.medicine.mvp.model.UserLoginModel;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.rong.imlib.RongIMClient;

/**
 * @author wty
 */
public class UserLoginPresenter extends BasePresenter<IUserLoginContract.IUserLoginView> {

    private IUserLoginContract.IUserLoginModel mUserLoginModel;

    public UserLoginPresenter(){
        mUserLoginModel = new UserLoginModel();
    }

    /**
     * 登录验证过程：
     * 1.先去bmob服务器验证一下账号和密码
     * 2.再通过账号和密码 去融云获取token
     * 3.通过token连接上融云服务器
     **/
    public void login(final Context context, final String name, final String psw, final boolean isAutoLogin){
        if(!mView.checkNet()){
            mView.showNoNet();
            return;
        }

        mView.showLoading("正在验证用户名...");
        mUserLoginModel.login(context,name, psw, isAutoLogin,new ICallBack<UserDALEx>() {
            @Override
            public void onSuccess(UserDALEx user) {
                AppLogUtil.d("验证账号密码成功");
                mView.showLoading("正在获取token许可...");
                getToken(context,user);
            }

            @Override
            public void onFaild(String msg) {
                AppLogUtil.d(msg);
                mView.dismissLoading(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
            }
        });
    }

    /**
     * 自动登录过程：
     * 1.通过账号和密码 去融云获取token
     * 2.通过token连接上融云服务器
     **/
    public void loginAuto(final Context context,final UserBmob user){
        if(!mView.checkNet()){
            mView.showNoNet();
            return;
        }
        getTokenAuto(context,user);
    }

    /**
     * @Decription 客户端从融云服务器获取token令牌
     **/
    private void getToken(final Context context, final UserDALEx user){
        FakeUser fakeuser = new FakeUser();
        fakeuser.setUserid(user.getUserid());
        fakeuser.setUsername(user.getNickname());
        fakeuser.setUserlogo(user.getLogourl());
        FakeServer.getToken(fakeuser, new HttpUtil.OnResponse() {
            @Override
            public void onResponse(int code, String body) {
                if (code != 200) {
                    mView.dismissLoading(new OnDismissCallbackListener(body, SweetAlertDialog.ERROR_TYPE));
                    return;
                }
                String token;
                try {
                    JSONObject jsonObj = new JSONObject(body);
                    token = jsonObj.getString("token");
                    AppLogUtil.d("成功获取Token:"+token);
                } catch (JSONException e) {
                    AppLogUtil.d("Token 解析失败!");
                    mView.dismissLoading(new OnDismissCallbackListener("Token 解析失败!", SweetAlertDialog.ERROR_TYPE));
                    return;
                }
                mView.showLoading("正在连接服务器...");
                connect(context,user,token);
            }
        });
    }

    /**
     * @Decription 客户端从融云服务器获取token令牌
     **/
    private void getTokenAuto(final Context context, final UserBmob user){
        FakeUser fakeuser = new FakeUser();
        fakeuser.setUserid(user.getObjectId());
        fakeuser.setUsername(user.getUsername());
        fakeuser.setUserlogo(user.getLogourl());
        FakeServer.getToken(fakeuser, new HttpUtil.OnResponse() {
            @Override
            public void onResponse(int code, String body) {
                if (code != 200) {
                    mView.showFailed(body);
                    return;
                }
                String token;
                try {
                    JSONObject jsonObj = new JSONObject(body);
                    token = jsonObj.getString("token");
                    AppLogUtil.d("成功获取Token:"+token);
                } catch (JSONException e) {
                    AppLogUtil.d("Token 解析失败!");
                    mView.showFailed("Token 解析失败!");
                    return;
                }
                connectAuto(context,user,token);
            }
        });
    }

    /**
     * @Decription 连接到融云服务器
     **/
    private void connect(final Context context, final UserDALEx user, String token) {
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    // Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                    AppLogUtil.d("LoginActivity--onTokenIncorrect");
                    getToken(context,user);
                }

                @Override
                public void onSuccess(String userid) {
                    AppLogUtil.d("LoginActivity--onSuccess---gotoMainActivity" + userid);
                    mView.dismissLoading(null);
                    mView.finishActivity();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    AppLogUtil.d("LoginActivity--onError" + errorCode.getMessage());
                    mView.dismissLoading(new OnDismissCallbackListener(errorCode.getMessage(), SweetAlertDialog.ERROR_TYPE) );
                }
            });
    }

    /**
     * @Decription 连接到融云服务器
     **/
    private void connectAuto(final Context context, final UserBmob user, String token) {
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    // Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                    AppLogUtil.d("LoginActivity--onTokenIncorrect");
                    getTokenAuto(context,user);
                }

                @Override
                public void onSuccess(String userid) {
                    AppLogUtil.d("LoginActivity--onSuccess---gotoMainActivity" + userid);
                    mView.finishActivity();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    AppLogUtil.d("LoginActivity--onError" + errorCode.getMessage());
                    mView.showFailed(errorCode.getMessage());
                }
            });
    }

}
