package com.kw.app.medicine.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kw.app.commonlib.base.AppConstant;
import com.kw.app.commonlib.utils.CommonUtil;
import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.base.CMApplication;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.mvp.contract.IUserLoginContract;
import com.kw.app.medicine.mvp.presenter.UserLoginPresenter;
import com.kw.app.medicine.widget.login.LoginInputView;
import com.kw.app.ormlib.OrmModuleManager;
import com.kw.app.widget.activity.BaseActivity;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * @author wty
 * @Description 注册/登陆界面
 **/
public class LoginActivity extends BaseActivity<UserLoginPresenter> implements IUserLoginContract.IUserLoginView{

    @Bind(R.id.login_icon)
    ImageView mloginIcon;
    @Bind(R.id.login_inputview)
    LoginInputView mloginInputview;
    @Bind(R.id.login_version)
    TextView tv_version;
    @Bind(R.id.rl_content)
    RelativeLayout contentlayout;
    @Bind(R.id.img_launch)
    ImageView mImgLaunch;

    @OnClick(R.id.login_signup)
    void goToRegisterActivity(){
        UserRegisterActivity.startUserRegisterActivity(this, AppConstant.ActivityResult.Request_Register);
    }
    @OnClick(R.id.login_forgetpsw)
    void showForgetPswMenu(){
        UserEmailResetPSWActivity.startUserEmailResetPSWActivity(LoginActivity.this);
    }

    private String userid;

    @Override
    public UserLoginPresenter getPresenter() {
        return new UserLoginPresenter();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("");
        getDefaultNavigation().getRootView().setBackgroundColor(Color.TRANSPARENT);
        getDefaultNavigation().getLeftButton().hide();
        CommonUtil.keyboardControl(LoginActivity.this, false, mloginInputview.getAccountInput());

        final boolean isAutoLogin = PreferenceUtil.getInstance().isAutoLogin();
        String name = PreferenceUtil.getInstance().getLastName();
        String psw = PreferenceUtil.getInstance().getLastPassword();
        String logourl = PreferenceUtil.getInstance().getLogoUrl();
        String userid = PreferenceUtil.getInstance().getLastAccount();

        if(PreferenceUtil.getInstance().isFirstLogin() && Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
            //如果是第一次安装应用程序 适配android6.0动态权限
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if(aBoolean){
                                // All requested permissions are granted
                            }else{
                                // At least one permission is denied
                            }
                        }
                    });
            PreferenceUtil.getInstance().writePreferences(PreferenceUtil.IsFirstLogin,false);
        }

        //点击登陆后做的事情
        mloginInputview.setOnLoginAction(new LoginInputView.OnLoginActionListener() {
            @Override
            public void onLogin() {
                CommonUtil.keyboardControl(LoginActivity.this, false, mloginInputview.getAccountInput());
                if (submit()) {
                    mPresenter.login(CMApplication.getInstance(),mloginInputview.getAccount().toString(), mloginInputview.getPassword().toString(), mloginInputview.isRememberPsw());
                }
            }
        });

        if(!TextUtils.isEmpty(logourl)){
            ImageLoaderUtil.loadCircle(mloginIcon.getContext(),logourl,mloginIcon);
        }
        tv_version.setText("V"+ CommonUtil.getVersion(this)+"."+CommonUtil.getVersionCode(this));

        if (name != null) {
            mloginInputview.setAccount(name);
            mloginInputview.setPassword(psw);
            if (!TextUtils.isEmpty(psw)) {
                mloginInputview.setIsRememberPsw(true);
            } else {
                mloginInputview.setIsRememberPsw(false);
            }
        } else {// 第一次使用，默认不记住密码
            mloginInputview.setIsRememberPsw(false);
        }

        if(isAutoLogin){//自动登录就调整到主页面
            UserDALEx user = new UserDALEx();
            user.setUserid(userid);
            user.setNickname(name);
            user.setLogourl(logourl);

            contentlayout.setVisibility(View.GONE);
            mImgLaunch.setVisibility(View.VISIBLE);
            mPresenter.loginAuto(LoginActivity.this,user);
        }else{
            mImgLaunch.setVisibility(View.GONE);
            contentlayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected List<String> validate() {
        List<String> list = super.validate();
        if(!TextUtils.isEmpty(mloginInputview.validata()))
            list.add(mloginInputview.validata());
        return list;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//从选择图片页面返回
            if (requestCode == AppConstant.ActivityResult.Request_Register) {
                userid = data.getStringExtra(UserRegisterActivity.USERID);
                UserDALEx user = UserDALEx.get().findById(userid);
                mloginInputview.getAccountInput().setText(user.getNickname());
                if(!TextUtils.isEmpty(user.getLogourl())){
                    ImageLoaderUtil.load(mloginIcon.getContext(),user.getLogourl(),mloginIcon);
                }
            }
        }
    }

    @Override
    public void finishActivity() {
        //登录成功之后设置一下当前数据库名字
        OrmModuleManager.getInstance().setCurrentDBName(PreferenceUtil.getInstance().getLastAccount());
        new Handler().postDelayed(new Runnable(){

            public void run() {
                MainActivity.startMainActivity(LoginActivity.this);
                finish();
            }

        }, 500);

    }

    @Override
    public void showNoNet() {
        mImgLaunch.setVisibility(View.GONE);
        contentlayout.setVisibility(View.VISIBLE);
        super.showNoNet();
    }

    @Override
    public void showFailed(String msg) {
        mImgLaunch.setVisibility(View.GONE);
        contentlayout.setVisibility(View.VISIBLE);
        super.showFailed(msg);
    }
}
