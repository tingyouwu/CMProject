package com.kw.app.medicine.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.medicine.R;
import com.kw.app.widget.activity.BaseActivity;
import butterknife.Bind;

/**
 * 作者：samsung on 2016/9/27 17:33
 * 邮箱：kuangminan456123@163.com
 */
public class UpdatePasswordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.old_password)
    EditText mOldPws;
    @Bind(R.id.new_password)
    EditText mNewPws;
    @Bind(R.id.new_password2)
    EditText mNewPws2;
    @Bind(R.id.update_pswd_confirm)
    Button mUpdateConfirm;

    @Override
    public Object getPresenter() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        AppLogUtil.d("KMA ...onInitView");
        getDefaultNavigation().setTitle("密码修改");
        mUpdateConfirm.setOnClickListener(this);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_update_psw;
    }

    @Override
    public void onClick(View v) {
        String old = mOldPws.getText().toString().trim();
        String new1 = mNewPws.getText().toString().trim();
        String new2 = mNewPws2.getText().toString().trim();

        if (TextUtils.isEmpty(old)) {
            showAppToast("旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(new1)) {
            showAppToast("新密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(new2)) {
            showAppToast("确认密码不能为空");
            return;
        }
    }

}
