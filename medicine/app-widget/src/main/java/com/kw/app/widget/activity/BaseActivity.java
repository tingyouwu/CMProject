package com.kw.app.widget.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.devspark.appmsg.AppMsg;
import com.kw.app.commonlib.mvp.IBase;
import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.commonlib.utils.NetWorkUtils;
import com.kw.app.commonlib.utils.SystemBarTintManager;
import com.kw.app.widget.R;
import com.kw.app.widget.view.navigation.NavigationText;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 * 所有activity的基类
 * 一个高大上的名字：模版方法设计模式
 **/
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IBase<P> {

    protected NavigationText navigation;
    protected P mPresenter;
    protected View mRootView;
    protected SystemBarTintManager tintManager;//沉浸式状态栏
    public SweetAlertDialog loadingdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getPresenter();
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.attachView((IBaseView) this);
        }

        initStatusBar();
        mRootView = LayoutInflater.from(this).inflate(getLayoutResource(), null);
        setContentView(mRootView);
        ButterKnife.bind(this, mRootView);
        onInitView(savedInstanceState);
        setNavigation(getDefaultNavigation());
    }

    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.detachView();
            mPresenter = null;
        }

        if (loadingdialog != null && loadingdialog.isShowing()) {
            loadingdialog.dismiss();
        }
    }

    /**
     * 功能描述：是否设置沉浸式(默认不打开)
     * @return
     */
    protected boolean isEnableStatusBar() {
        return false;
    }

    @TargetApi(19)
    protected void initStatusBar() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && isEnableStatusBar()) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
        else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP && isEnableStatusBar()) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    /**
     * 功能描述：设置状态栏的颜色
     **/
    protected void setStatusBarTintRes(int color) {
        if (tintManager != null) {
            tintManager.setStatusBarTintResource(color);
        }
    }

    /**
     * 功能描述：设置标题栏
     **/
    private void setNavigation(View navigation){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setCustomView(navigation, layoutParams);
            actionBar.show();
        }
    }

    public NavigationText getDefaultNavigation(){
        if(navigation==null){
            navigation = new NavigationText(this);
        }
        return navigation;
    }

    /**
     * @Decription 校验数据
     **/
    protected List<String> validate(){
        return new ArrayList<String>();
    }

    /**
     * @Decription 提交数据
     * 第一步：校验数据
     * 第二步：校验网络是否联通
     **/
    protected boolean submit(){
        List<String> validate = validate();
        if(validate.size()!=0){
            showFailed(validate.get(0));
            return false;
        }else if(!NetWorkUtils.isNetworkConnected(this)){
            showFailed(getString(R.string.network_failed));
            return false;
        }else{
            return true;
        }
    }

    /**
     * @Decription 检查网络是否连接
     **/
    public boolean checkNet() {
        return NetWorkUtils.isNetworkConnected(this);
    }

    /**
     * @Decription 显示无网络
     **/
    public void showNoNet(){
        showFailed(getString(R.string.network_failed));
    }

    /**
     * @Decription 弹框提示
     **/
    public void onToast(OnDismissCallbackListener callback){
        SweetAlertDialog dialog = new SweetAlertDialog(this,callback.alertType);
        dialog.setTitleText(callback.msg)
                .setConfirmText("确定")
                .setConfirmClickListener(callback)
                .changeAlertType(callback.alertType);
        dialog.show();
    }

    public void showSuccess(final String msg){
        onToast(new OnDismissCallbackListener(msg, SweetAlertDialog.SUCCESS_TYPE));
    }

    public void showFailed(final String msg){
        onToast(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
    }

    /**
     * @Decription 提示加载中
     **/
    public void showLoading(String msg){
        if(this.isFinishing())return;
        if(loadingdialog==null || !loadingdialog.isShowing()){
            loadingdialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText(msg);
            loadingdialog.setCancelable(false);
            loadingdialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                int countDestroyBack = 0;
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        countDestroyBack++;
                        if(countDestroyBack == 3){
                            loadingdialog.dismiss();
                        }
                    }
                    return false;
                }
            });
            loadingdialog.show();

        }else{
            loadingdialog.setTitleText(msg);
        }
    }

    public void dismissLoading(){
        dismissLoading(null);
    }

    public void dismissLoading(final OnDismissCallbackListener callback){
        if(loadingdialog!=null && loadingdialog.isShowing()){
            new CountDownTimer(500,1000) {
                //一些提交会比较快 所以需要500ms缓冲
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if(callback == null){
                        loadingdialog.dismiss();
                    }else{
                        loadingdialog.setTitleText(callback.msg)
                                .setConfirmText("确定")
                                .setConfirmClickListener(callback)
                                .changeAlertType(callback.alertType);
                    }
                }
            }.start();
        }
    }

    /**
     * 提交前提示确认按钮
     **/
    protected void confirmSubmit(String title){
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        dialog.setTitleText(title);

        dialog.setConfirmText("确定");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                submit();
            }
        });
        dialog.setCancelText("取消");
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * @Decription 显示Toast
     **/
    public void showAppToast(String msg){
        AppMsg.makeText(this,msg,AppMsg.STYLE_INFO).show();
    }

}
