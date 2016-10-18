package com.kw.app.medicine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.kw.app.medicine.R;
import com.kw.app.medicine.adapter.SearchUserAdapter;
import com.kw.app.medicine.avcloud.AVUserManager;
import com.kw.app.medicine.base.IUserManager;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.ICallBack;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.ClearEditText;
import com.kw.app.widget.view.loadingview.LoadingView;
import com.kw.app.widget.view.xrecyclerview.ProgressStyle;
import com.kw.app.widget.view.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 搜索好友
 */
public class SearchUserActivity extends BaseActivity {

    @Bind(R.id.filter_edit)
    ClearEditText filterEdit;
    @Bind(R.id.rc_search)
    XRecyclerView rcSearch;
    @Bind(R.id.lv_loading)
    LoadingView lvLoading;

    public static void startSearchUserActivity(Context context) {
        Intent intent = new Intent(context, SearchUserActivity.class);
        context.startActivity(intent);
    }

    private IUserManager userManager = AVUserManager.getInstance();
    SearchUserAdapter adapter;
    private List<UserDALEx> mDataList = new ArrayList<UserDALEx>();

    @OnClick(R.id.btn_search)
    public void onSearchClick(View view) {
        query();
    }

    public void query() {
        String name = filterEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showAppToast("请填写用户名");
            return;
        }
        userManager.queryUsers(name, 50, new ICallBack<List<UserDALEx>>() {
            @Override
            public void onSuccess(List<UserDALEx> list) {
                adapter.retsetData(list);
                rcSearch.refreshComplete();
            }

            @Override
            public void onFaild(String msg) {
                adapter.clearData();
                rcSearch.refreshComplete();
                showAppToast(msg);
            }
        });
    }

    @Override
    public Object getPresenter() {
        return null;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("搜索好友");

        adapter = new SearchUserAdapter(this,mDataList);
        rcSearch.setLayoutManager(new LinearLayoutManager(this));

        rcSearch.setLoadingMoreProgressStyle(ProgressStyle.LineSpinFadeLoader);
        rcSearch.setRefreshProgressStyle(ProgressStyle.BallClipRotatePulse);
        rcSearch.setLoadingMoreEnabled(false);
        rcSearch.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                // 下拉刷新根据最近修改时间这个限定去服务端拿
                query();
            }

            @Override
            public void onLoadMore() {
                // 本地加载更多
            }

        });
        rcSearch.setAdapter(adapter);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_search_user;
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }
}
