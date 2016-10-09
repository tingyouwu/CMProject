package com.kw.app.medicine.widget.login;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kw.app.commonlib.utils.NetWorkUtils;
import com.kw.app.medicine.R;
import com.kw.app.widget.view.CheckBoxLabel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginInputView extends LinearLayout implements OnClickListener{
	
	// 登录按钮
	@Bind(R.id.btn_login) Button btn_submit;
	@Bind(R.id.et_account) EditText et_account;
	@Bind(R.id.et_password) EditText et_password;
	@Bind(R.id.btn_login_rememberpsw) CheckBoxLabel btn_login_rememberpsw;

	public LoginInputView(Context context) {
		this(context,null);
	}

	public LoginInputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.layout_login_input, this);
		ButterKnife.bind(this);
		
		btn_submit.setOnClickListener(this);
		et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					if(onLoginAction!=null)onLoginAction.onLogin();
				}
				return false;
			}
		});
	}

	public boolean isRememberPsw(){
		return btn_login_rememberpsw.isCheck(); 
	}
	
	public String getAccount(){
		return et_account.getText().toString().trim();
	}
	
	public String getPassword(){
		return et_password.getText().toString().trim();
	}
	
	public void setAccount(String account){
		et_account.setText(account);
		et_account.setSelection(account.length());
	}
	
	public void setPassword(String password){
		et_password.setText(password);
	}
	
	public void setIsRememberPsw(boolean isRemember){
		btn_login_rememberpsw.setCheck(isRemember);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			if(onLoginAction!=null)onLoginAction.onLogin();
			break;
		default:
			break;
		}
	}

	/**
	 * @Decription 校验账号密码
	 **/
	public String validata() {
		String uin = et_account.getText().toString().trim();
		String pwd = et_password.getText().toString().trim();
		String validate = "";
		// 帐号要在6-13位之间
		if (uin.length() == 0) {
			validate = "请填写帐号";
		} else if (pwd.length() == 0) {
			validate = "请填写密码";
		}
		else if (!NetWorkUtils.isNetworkConnected(getContext())) {
			validate = getContext().getString(R.string.network_failed);
		}
		return validate;
	}
	
	public void setOnLoginAction(OnLoginActionListener onLoginAction) {
		this.onLoginAction = onLoginAction;
	}

	private OnLoginActionListener onLoginAction;
	public interface OnLoginActionListener{
		void onLogin();
	}

	public EditText getAccountInput(){
		return et_account;
	}

	public EditText getPswInput(){
		return et_password;
	}

}