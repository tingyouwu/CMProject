package com.kw.app.medicine.mvp.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.activity.SearchUserActivity;
import com.kw.app.medicine.adapter.ContactAdapter;
import com.kw.app.medicine.bean.FriendBean;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.medicine.event.RefreshContactEvent;
import com.kw.app.medicine.event.RefreshEvent;
import com.kw.app.medicine.mvp.contract.IContactContract;
import com.kw.app.medicine.mvp.presenter.ContactPresenter;
import com.kw.app.widget.fragment.BaseFragment;
import com.kw.app.widget.view.ClearEditText;
import com.kw.app.widget.view.DivItemDecoration;
import com.kw.app.widget.view.MyTextWatcher;
import com.kw.app.widget.view.SideBar;
import com.kw.app.widget.view.loadingview.LoadingState;
import com.kw.app.widget.view.loadingview.LoadingView;
import com.kw.app.widget.view.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 通讯录
 * @author wty
 */
public class ContactFragment extends BaseFragment<ContactPresenter> implements IContactContract.IContactView {

    @Bind(R.id.filter_edit)
    ClearEditText et_filter;
    @Bind(R.id.filter_letters)
    SideBar filter_letters;
    @Bind(R.id.listview_dynamic)
    XRecyclerView listview;
    @Bind(R.id.dynamic_fl_loading)
    LoadingView mLoadingView;
    @Bind(R.id.tv_letter)
    TextView tv_letter;

    ContactAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private List<FriendBean> mDataList = new ArrayList<>();

    @Override
    public ContactPresenter getPresenter() {
        return new ContactPresenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        filter_letters.setTextView(tv_letter);
        adapter = new ContactAdapter(getContext(),mDataList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        listview.setLayoutManager(linearLayoutManager);
        listview.addItemDecoration(new DivItemDecoration(2, true));
        listview.setLoadingMoreEnabled(false);
        listview.setPullRefreshEnabled(false);
        listview.setAdapter(adapter);

        filter_letters.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s);
                if (position != -1) {
                    linearLayoutManager.scrollToPositionWithOffset(position-1+2,0);
                }
            }
        });

        // 根据输入框输入值的改变来过滤搜索
        et_filter.addTextChangedListener(new MyTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                sortFriend(s.toString());
            }
        });

        mLoadingView.withLoadedEmptyText("暂时没有联系人")
                .withBtnEmptyEnnable(false)
                .withBtnNoNetEnnable(false)
                .build();
        mPresenter.loadAllFriend();
        mPresenter.refreshFriend(getContext());
    }


    @Override
    public int getLayoutResource() {
        return R.layout.fragment_contact;
    }

    @Override
    public void initFragmentActionBar(String title) {
        super.initFragmentActionBar(title);
        activity.getDefaultNavigation().setRightButton("添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchUserActivity.startSearchUserActivity(getActivity());
            }
        });
    }

    @Override
    public void showNoNet() {
        if(adapter.getItemCount()==0){
            mLoadingView.setState(LoadingState.STATE_NO_NET);
        }else{
            mLoadingView.setVisibility(View.GONE);
            AppMsg.makeText(activity, "网络连接失败，请检查网路", AppMsg.STYLE_INFO).show();
        }
    }



    @Override
    public void refreshFriend(List<UserDALEx> list) {
        adapter.clearData();
        if(list.size()!=0){
            filter_letters.setLettersList(getSortLetter(list));
            List<FriendBean> friendlist = getContactList(list);
            adapter.retsetData(friendlist);
            mLoadingView.setVisibility(View.GONE);
            listview.setNoMore(list.size() + "位联系人");
        }else{
            if(adapter.getItemCount()==0){
                mLoadingView.setState(LoadingState.STATE_EMPTY);
                listview.setNoMore("");
            }else{
                mLoadingView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * @根据关键字搜索列表
     **/
    public void sortFriend(String sortstring){
        List<UserDALEx> list = UserDALEx.get().findAllFriendByFilter(sortstring);
        refreshFriend(list);
    }

    /**
     * 获取排序的首字母
     **/
    public List<String> getSortLetter(List<UserDALEx> list){
        Map<String,String> letterMap = new LinkedHashMap<>();
        List<String> letters = new ArrayList<String>();

        for(UserDALEx user:list){
            String sortString = user.getPinyin().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                letterMap.put(sortString,sortString);
            }else{
                letterMap.put("#","#");
            }
        }

        letters.addAll(letterMap.values());
        return letters;
    }

    /**
     * 获取列表
     **/
    public List<FriendBean> getContactList(List<UserDALEx> list){
        List<FriendBean> contactlist = new ArrayList<>();
        for(UserDALEx user:list){
            FriendBean model = new FriendBean();
            String sortString = user.getPinyin().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                model.setSortkey(sortString);
            }else{
                model.setSortkey("#");
            }
            model.setUser(user);
            contactlist.add(model);
        }

        return contactlist;
    }

    /**
     * 刷新列表
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshContactEvent event){
        AppLogUtil.i("--联系人列表接收到刷新消息---");
        //因为新增`新朋友`这种会话类型
        mPresenter.refreshFriend(getContext());
    }

}
