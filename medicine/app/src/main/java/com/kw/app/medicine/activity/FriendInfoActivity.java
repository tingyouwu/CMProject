package com.kw.app.medicine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kw.app.commonlib.utils.ImageLoaderUtil;
import com.kw.app.medicine.R;
import com.kw.app.medicine.data.local.UserDALEx;
import com.kw.app.widget.activity.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 联系人详情页面
 */
public class FriendInfoActivity extends BaseActivity {

    public static String TAG = "user";
    UserDALEx user;

    @Bind(R.id.img_user_bg)
    ImageView imgUserBg;//背景
    @Bind(R.id.img_user_icon)
    ImageView imgUserIcon;//头像
    @Bind(R.id.tv_user_name)
    TextView tvUserName;//名字

    public static void startFriendInfoActivity(Context context, UserDALEx user) {
        Intent intent = new Intent(context, FriendInfoActivity.class);
        intent.putExtra(TAG, user);
        context.startActivity(intent);
    }

    @Override
    public Object getPresenter() {
        return null;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        user = (UserDALEx) getIntent().getSerializableExtra(TAG);
        getDefaultNavigation().setTitle("");
        getDefaultNavigation().getLeftButton().setText("返回");
        getDefaultNavigation().getRootView().setBackgroundColor(Color.TRANSPARENT);

        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
        ImageLoaderUtil.loadCircle(this, user.getLogourl(), imgUserIcon);
        tvUserName.setText(user.getNickname());
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_friend_info;
    }

    @OnClick({R.id.btn_call, R.id.btn_send_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
                break;
            case R.id.btn_send_message:
                SimpleChatActivity.startSimpleChatActivity(FriendInfoActivity.this,user);
                break;
        }
    }
}
