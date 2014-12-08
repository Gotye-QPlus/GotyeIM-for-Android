package com.open_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.LoginListener;
import com.open_demo.main.MainActivity;
import com.open_demo.util.ProgressDialogUtil;

public class LoginPage extends Activity implements LoginListener {
	Button mButLogin, mButLogout;
	EditText mEdtName, mEdtPsd;
	String mUsername;
	String mPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否已经登陆了，否则显示登陆页面
		if (GotyeAPI.getInstance().isOnline()) {
			Intent i = new Intent(LoginPage.this, MainActivity.class);
			startActivity(i);
			this.finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_login);
		initView();
	}

	public void initView() {
		mButLogin = (Button) findViewById(R.id.start);
		mEdtName = (EditText) findViewById(R.id.username);
		mEdtName.setSelection(mEdtName.getText().length());
		mEdtPsd = (EditText) findViewById(R.id.userpsd);
		mButLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (checkUser()) {
					// 登录的时候要传入登录监听，当重复登录时会直接返回登录状态
					GotyeAPI.getInstance().addListerer(LoginPage.this);
					ProgressDialogUtil.showProgress(LoginPage.this, "正在登录...");
					int i = GotyeAPI.getInstance().login(mUsername, checkPsd());
					// 根据返回的code判断
					if (i == GotyeStatusCode.CODE_OK) {
						// 已经登陆
						onLogin(i, null);
					}
				}
			}
		});
	}

	private boolean checkUser() {
		mUsername = mEdtName.getText().toString();
		boolean isValid = true;
		if (mUsername == null || mUsername.length() == 0) {
			Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
			isValid = false;
		}
		return isValid;
	}

	private String checkPsd() {
		mPassword = mEdtPsd.getText().toString();
		if (mPassword == null || mPassword.length() == 0) {
			mPassword = null;
		}
		return mPassword;
	}

	private static final String CONFIG = "login_config";

	public void saveUser(String name, String password) {
		SharedPreferences sp = getSharedPreferences(CONFIG,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("username", name);
		edit.putString("password", password);
		edit.commit();
	}

	public static String[] getUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG,
				Context.MODE_PRIVATE);
		String name = sp.getString("username", null);
		String password = sp.getString("password", null);
		String[] user = new String[2];
		user[0] = name;
		user[1] = password;
		return user;
	}

	@Override
	protected void onDestroy() {
		// 移除监听
		GotyeAPI.getInstance().removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onLogin(int code, GotyeUser user) {
		ProgressDialogUtil.dismiss();
		// 判断登陆是否成功
		if (code == GotyeStatusCode.CODE_OK) {
			saveUser(mUsername, mPassword);
			Intent i = new Intent(LoginPage.this, MainActivity.class);
			startActivity(i);

			Intent toService = new Intent(this, GotyeService.class);
			startService(toService);
			Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
			this.finish();
		} else {
			// 失败,可根据code定位失败原因
			Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLogout(int code) {
		// TODO Auto-generated method stub

	}

}
