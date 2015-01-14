package com.open_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.open_demo.util.ProgressDialogUtil;

public class LoginPage extends Fragment {
	Button mButLogin, mButLogout;
	EditText mEdtName, mEdtPsd;
	String mUsername;
	String mPassword;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_login, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	public void initView() {
		mButLogin = (Button) getView().findViewById(R.id.start);
		mEdtName = (EditText) getView().findViewById(R.id.username);
		mEdtPsd = (EditText) getView().findViewById(R.id.userpsd);
		String user[] = getUser(LoginPage.this.getActivity());
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
					saveUser(mUsername, mEdtPsd.getText().toString().trim());
					Intent login = new Intent(getActivity(), GotyeService.class);
					login.setAction(GotyeService.ACTION_LOGIN);
					login.putExtra("name", mUsername);
					if (TextUtils.isEmpty(mEdtPsd.getText().toString().trim())) {
						// login.putExtra("pwd", null);
					} else {
						login.putExtra("pwd", mEdtPsd.getText().toString()
								.trim());
					}
					getActivity().startService(login);
					ProgressDialogUtil.showProgress(
							LoginPage.this.getActivity(), "正在登录...");
				}
			}
		});
	}

	private boolean checkUser() {
		mUsername = mEdtName.getText().toString();
		boolean isValid = true;
		if (mUsername == null || mUsername.length() == 0) {
			Toast.makeText(this.getActivity(), "请输入用户名", Toast.LENGTH_SHORT)
					.show();
			isValid = false;
		}
		return isValid;
	}

	public static final String CONFIG = "login_config";

	public void saveUser(String name, String password) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		SharedPreferences sp = getActivity().getSharedPreferences(CONFIG,
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

	

	
}
