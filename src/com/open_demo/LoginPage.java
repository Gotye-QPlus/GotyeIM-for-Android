package com.open_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
		GotyeAPI.getInstance().addListener(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_login);

		// TODO Auto-generated method stub
		int state = GotyeAPI.getInstance().getOnLineState();
		Log.d("login", "state=" + state);
		if (state != 0) {
			Intent i = new Intent(LoginPage.this, MainActivity.class);
			startActivity(i);
			Intent toService = new Intent(LoginPage.this, GotyeService.class);
			startService(toService);
			finish();
			return;
		}
		initView();

		// -1 offline, API will reconnect when network becomes valid
		// 0 not login or logout already
		// 1 online

	}

	public void initView() {
		mButLogin = (Button) findViewById(R.id.start);
		mEdtName = (EditText) findViewById(R.id.username);
		mEdtPsd = (EditText) findViewById(R.id.userpsd);
		String user[] = getUser(LoginPage.this);
		String hasUserName = user[0];
		String hasPassWord = user[1];
		mUsername = hasUserName;
		mPassword = hasPassWord;
		if (mUsername != null) {
			mEdtName.setText(hasUserName);
			mEdtName.setSelection(mEdtName.getText().length());
		}
		mButLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (checkUser()) {
					// 登录的时候要传入登录监听，当重复登录时会直接返回登录状态
					GotyeAPI.getInstance().addListener(LoginPage.this);
					saveUser(mUsername, mEdtPsd.getText().toString().trim());
					Intent login = new Intent(getBaseContext(),
							GotyeService.class);
					login.setAction(GotyeService.ACTION_LOGIN);
					login.putExtra("name", mUsername);
					if (TextUtils.isEmpty(mEdtPsd.getText().toString().trim())) {
						// login.putExtra("pwd", null);
					} else {
						login.putExtra("pwd", mEdtPsd.getText().toString()
								.trim());
					}
					startService(login);
					ProgressDialogUtil.showProgress(LoginPage.this, "正在登录...");
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

	public static final String CONFIG = "login_config";

	public void saveUser(String name, String password) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(CONFIG,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putString("username", name);
		if (TextUtils.isEmpty(password)) {
			edit.putString("password", null);
		} else {
			edit.putString("password", password.trim());
		}

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
		if (code == GotyeStatusCode.CODE_OK
				|| code == GotyeStatusCode.CODE_OFFLINELOGIN_SUCCESS
				|| code == GotyeStatusCode.CODE_RELOGIN_SUCCESS) {

			saveUser(mUsername, mPassword);
			Intent i = new Intent(LoginPage.this, MainActivity.class);
			startActivity(i);

			if (code == GotyeStatusCode.CODE_OFFLINELOGIN_SUCCESS) {
				Toast.makeText(this, "您当前处于离线状态", Toast.LENGTH_SHORT).show();
			} else if (code == GotyeStatusCode.CODE_OK) {
				Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
			}
			this.finish();
		} else {
			// 失败,可根据code定位失败原因
			Toast.makeText(this, "登录失败 code=" + code, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onLogout(int code) {
		// TODO Auto-generated method stub
		Log.d("gotye", "onLogout");
	}

	@Override
	public void onReconnecting(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		Log.d("gotye", "onReconnecting");
	}
}
