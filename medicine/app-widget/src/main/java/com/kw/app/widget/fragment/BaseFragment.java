package com.kw.app.widget.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kw.app.commonlib.mvp.IBase;
import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.mvp.view.IBaseView;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 * @date 2016-07-09
 * 功能描述：基本fragment，其他所有fragment需要继承
 *
 * 基础类做了懒加载数据
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBase<P> {

    protected P mPresenter;
    protected BaseActivity activity;
    protected View mRootView;
    protected boolean isInitData = false;//是否已经初始化数据
    protected boolean isInitView = false;//是否已经加载视图
    protected Bundle bundle;

    public BaseFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, mRootView);
        }
        this.isInitView = true;
        return mRootView;
    }

    @Nullable
    @Override
    public View getView() {
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = savedInstanceState;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isInitView){
            //setUserVisibleHint在onCreateView之前调用的，
            //在视图未初始化的时候就使用的话，会有空指针异常
            onInitData();
            doWorkOnResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onInitData();
            doWorkOnResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mPresenter != null && this instanceof IBaseView) {
            mPresenter.detachView();
            mPresenter = null;
            activity = null;
        }
        isInitData = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //onDestroyView的执行和Activity的onDestroy不一样，不会销毁当前的页面，所以Fragment的所有成员变量的引用都还在
        // 我们在onCreateView的时候，先判断该取到的数据是否为空，比如Fragment的根视图rootView，
        // 网络请求获取到的数据等，如果不为空就不用再次执行。
        this.isInitView = false;
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
    }

    /**
     * 功能描述：解决Fragment getActivity空指针问题
     **/
    public void setActivity(BaseActivity activity){
        this.activity = activity;
    }

    /**
     * 功能描述：当fragment处于可见并且已经初始之后，做一些操作
     **/
    public void doWorkOnResume(){

    }

    /**
     * 功能描述：初始化Fragment的标题栏
     **/
    public void initFragmentActionBar(String title){
        if(activity == null)return;
        activity.getDefaultNavigation().setTitle(title);
        activity.getDefaultNavigation().getLeftButton().hide();
        activity.getDefaultNavigation().getRightButton().hide();
    }

    /**
     * 功能描述：初始化view以及数据
     **/
    private void onInitData(){
        if(!isInitData){
            isInitData = true;
            mPresenter = getPresenter();
            if (mPresenter != null && this instanceof IBaseView) {
                mPresenter.attachView((IBaseView) this);
            }
            onInitView(bundle);
        }
    }

    /**
     * @Decription 检查网络是否连接
     **/
    public boolean checkNet() {
        return activity.checkNet();
    }

    /**
     * @Decription 弹框提示
     **/
    public void onToast(OnDismissCallbackListener callback){
        activity.onToast(callback);
    }

    public void showSuccess(final String msg){
        onToast(new OnDismissCallbackListener(msg, SweetAlertDialog.SUCCESS_TYPE));
    }

    public void showFailed(final String msg){
        onToast(new OnDismissCallbackListener(msg, SweetAlertDialog.ERROR_TYPE));
    }

    public void showAppToast(final String msg){
        activity.showAppToast(msg);
    }

    /**
     * @Decription 提示加载中
     **/
    public void showLoading(String msg){
        activity.showLoading(msg);
    }

    public void dismissLoading(){
        dismissLoading(null);
    }

    public void dismissLoading(final OnDismissCallbackListener callback){
        activity.dismissLoading(callback);
    }
}
