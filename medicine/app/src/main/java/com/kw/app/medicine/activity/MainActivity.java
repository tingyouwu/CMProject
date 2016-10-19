package com.kw.app.medicine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.base.CMNotificationManager;
import com.kw.app.medicine.data.local.SystemMessageDALEx;
import com.kw.app.medicine.event.RefreshContactTabEvent;
import com.kw.app.medicine.event.RefreshMessageTabEvent;
import com.kw.app.medicine.mvp.view.fragment.ContactFragment;
import com.kw.app.medicine.mvp.view.fragment.MessageFragment;
import com.kw.app.medicine.mvp.view.fragment.MySelfFragment;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.TabStripView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import io.rong.imlib.RongIMClient;

/**
 * @Description 主界面activity
 * @author wty
 **/
public class MainActivity extends BaseActivity {

    @Bind(R.id.navigateTabBar)
    TabStripView navigateTabBar;

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    public static void startMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        //对应xml中的containerId
        navigateTabBar.setFrameLayoutId(R.id.main_container);
        //对应xml中的navigateTabTextColor
        navigateTabBar.setTabTextColor(getResources().getColor(R.color.gray_font_3));
        //对应xml中的navigateTabSelectedTextColor
        navigateTabBar.setSelectedTabTextColor(getResources().getColor(R.color.colorPrimary));

        //恢复选项状态
        navigateTabBar.onRestoreInstanceState(savedInstanceState);

        navigateTabBar.addTab(MessageFragment.class, new TabStripView.TabParam(R.mipmap.ic_tab_home_normal, R.mipmap.ic_tab_home_pressed, "消息"));
        navigateTabBar.addTab(ContactFragment.class, new TabStripView.TabParam(R.mipmap.ic_tab_contact_normal, R.mipmap.ic_tab_contact_pressed, "联系人"));
        navigateTabBar.addTab(MySelfFragment.class, new TabStripView.TabParam(R.mipmap.ic_tab_setting_normal, R.mipmap.ic_tab_setting_pressed, "设置"));

    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存当前选中的选项状态
        navigateTabBar.onSaveInstanceState(outState);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageTab();
        //进入应用后，通知栏应取消
        CMNotificationManager.clearNotification(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RongIMClient.getInstance().disconnect();
        EventBus.getDefault().unregister(this);
        System.exit(0);
    }

    /**
     * 刷新message tab红点
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshMessageTabEvent event){
        AppLogUtil.d("---MainActivity 刷新 message Tab---");
        updateMessageTab();
    }

    /**
     * 刷新contact tab红点
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshContactTabEvent event){
        AppLogUtil.d("---MainActivity 刷新 contact Tab---");
    }

    /**
     * 更新Message Tab
     **/
    private void updateMessageTab(){
        final int system_unread = SystemMessageDALEx.get().getUnReadMessageCount();
        RongIMClient.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                int totalUnreadCount = integer + system_unread;
                //开发者根据自己需求自行处理接下来的逻辑
                navigateTabBar.updateUnread("消息",totalUnreadCount);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

}
