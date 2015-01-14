package com.open_demo.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.open_demo.LoginPage;
import com.open_demo.R;
import com.open_demo.base.BaseFragment;
import com.open_demo.util.BitmapUtil;
import com.open_demo.util.ToastUtil;

@SuppressLint("NewApi")
public class SettingFragment extends BaseFragment{
	private static final int REQUEST_PIC = 1;
	private GotyeUser user;
	private ImageView iconImageView;
	private EditText nickName;
	private EditText info;
	private GotyeAPI api;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_setting, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		api = GotyeAPI.getInstance();
		api.addListener(this);
		user = api.getCurrentLoginUser();
		api.requestUserInfo(user.getName(), true);
		initView();
		int state=api.getOnLineState();
		if(state!=1){
			setErrorTip(0);
		}else{
			setErrorTip(1);
		}
	}

	private void initView() {
		iconImageView = (ImageView) getView().findViewById(R.id.icon);
		nickName = (EditText) getView().findViewById(R.id.nick_name);
		info=(EditText) getView().findViewById(R.id.info_name);
		Button btn = (Button) getView().findViewById(R.id.logout_btn);
		btn.setText("退出");
		nickName.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		nickName.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					String text = arg0.getText().toString();
					if (!"".equals(text)) {
						GotyeUser forModify=new GotyeUser(user.getName());
						forModify.setNickname(text);
						forModify.setInfo(info.getText().toString().trim());
						forModify.setGender(user.getGender());
						String headPath=null;
					int code=api.requestModifyUserInfo(forModify, headPath);
					Log.d("", ""+code);
					}
					return true;
				}
				return false;
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int code=api.logout();
				int x=code;
				Log.d("", "code"+code+""+x);
				if(code==GotyeStatusCode.CODE_NOT_LOGIN){
					Intent toLogin=new Intent(getActivity(), LoginPage.class);
					startActivity(toLogin);
					getActivity().finish();
				}
			}
		});
		getView().findViewById(R.id.icon_layout).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						takePic();
					}
				});

		iconImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				takePic();
			}
		});
		CheckBox receiveNewMsg = ((CheckBox) getView().findViewById(
				R.id.new_msg));
		receiveNewMsg.setChecked(api.isNewMsgNotify());
		receiveNewMsg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				api.setNewMsgNotify(arg1);
			}
		});
		CheckBox noTipAllGroupMessage = ((CheckBox) getView().findViewById(
				R.id.group_msg));
		noTipAllGroupMessage.setChecked(api.isNotReceiveGroupMsg());
		noTipAllGroupMessage
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						api.setNotReceiveGroupMsg(arg1);
					}
				});
		
		SharedPreferences spf=getActivity().getSharedPreferences("fifter_cfg", Context.MODE_PRIVATE);
		boolean fifter=spf.getBoolean("fifter", false);
		CheckBox msgFifter = ((CheckBox) getView().findViewById(
				R.id.msg_filter));
		msgFifter.setChecked(fifter);
		msgFifter
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0,
					boolean arg1) {
				SharedPreferences spf=getActivity().getSharedPreferences("fifter_cfg", Context.MODE_PRIVATE);
				spf.edit().putBoolean("fifter",arg1).commit();
			}
		});
		getView().findViewById(R.id.clear_cache).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						int code=api.clearCache();
						Toast.makeText(getActivity(), "清理完成!",
								Toast.LENGTH_SHORT).show();
					}
				});

		setUserInfo(user);
	}

	boolean hasRequest = false;

	private void setUserInfo(GotyeUser user) {
		if (user.getIcon() == null && !hasRequest) {
			hasRequest = true;
			api.requestUserInfo(user.getName(), true);
		} else {
			Bitmap bm = BitmapUtil.getBitmap(user.getIcon().path);
			if (bm != null) {
				iconImageView.setImageBitmap(bm);
			}
		}
		nickName.setText(user.getNickname());
		info.setText(user.getName());
	}
	@Override
	public void onResume() {
		super.onResume();
	}

	private void takePic() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		getActivity().startActivityForResult(intent, REQUEST_PIC);
		Intent intent;
		intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/jpeg");
		getActivity().startActivityForResult(intent, REQUEST_PIC);
	}

	public void hideKeyboard() {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.hideSoftInputFromWindow(nickName.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onDestroy() {
		api.removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onRequestUserInfo(int code, GotyeUser user) {
		if(user!=null&&user.getName().equals(this.user.getName())){
			setUserInfo(user);
		}
	}

	@Override
	public void onModifyUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		if (code == 0) {
			setUserInfo(user);
			// ToastUtil.show(getActivity(), "修改成功!");
		} else {
			ToastUtil.show(getActivity(), "修改失败!");
		}
	}

	public void modifyUserIcon(String smallImagePath) {
		String name = nickName.getText().toString().trim();
		GotyeUser forModify=new GotyeUser(user.getName());
		forModify.setNickname(name);
		forModify.setInfo(user.getInfo());
		forModify.setGender(user.getGender());
		api.requestModifyUserInfo(forModify,smallImagePath);
	}
	@Override
	public void onLogin(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		setErrorTip(1);
	}

	@Override
	public void onLogout(int code) {
		if(code == 0){
			return;
		}
		setErrorTip(0);
	}

	@Override
	public void onReconnecting(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		setErrorTip(-1);
	}

	private void setErrorTip(int code) {
		if (code == 1) {
			getView().findViewById(R.id.error_tip).setVisibility(View.GONE);
		} else {
			getView().findViewById(R.id.error_tip).setVisibility(View.VISIBLE);
			if (code == -1) {
				getView().findViewById(R.id.loading)
						.setVisibility(View.VISIBLE);
				((TextView) getView().findViewById(R.id.showText))
						.setText("连接中...");
				getView().findViewById(R.id.error_tip_icon).setVisibility(
						View.GONE);
			}else{
				getView().findViewById(R.id.loading).setVisibility(View.GONE);
				((TextView) getView().findViewById(R.id.showText))
						.setText("未连接");
				getView().findViewById(R.id.error_tip_icon).setVisibility(
						View.VISIBLE);
			}
			
		}
	}
}
