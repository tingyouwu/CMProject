package com.kw.app.medicine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.kw.app.commonlib.mvp.presenter.BasePresenter;
import com.kw.app.medicine.R;
import com.kw.app.widget.activity.BaseActivity;
import com.kw.app.widget.view.sweetdialog.OnDismissCallbackListener;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author wty
 * @Description 用户邮箱重置密码
 **/
public class UserEmailResetPSWActivity extends BaseActivity {

    @Bind(R.id.et_email)
    EditText etEmail;

    public static void startUserEmailResetPSWActivity(Activity activity) {
        Intent intent = new Intent(activity, UserEmailResetPSWActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("重置密码");
        getDefaultNavigation().getLeftButton().setText("老中医");
        getDefaultNavigation().getRightButton().setButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                showLoading("请稍候,正在请求...");

                BmobUser.resetPasswordByEmail(email, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            dismissLoading(new OnDismissCallbackListener("发送成功") {
                                @Override
                                public void onCallback() {
                                    finish();
                                }
                            });
                        }else{
                            dismissLoading(new OnDismissCallbackListener(e.getMessage(), SweetAlertDialog.ERROR_TYPE));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected boolean isEnableStatusBar() {
        return true;
    }

    @Override
    protected List<String> validate() {
        List<String> list = super.validate();
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            list.add("请填写邮箱");
        }
        return list;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_email_resetpsw;
    }
}
