package com.open_demo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.open_demo.util.ToastUtil;

@SuppressLint("CutPasteId")
public class LoginSettingPage extends Fragment {
	private SharedPreferences spf;
	private TextView appkey;
	private AutoCompleteTextView ipView;
	private EditText newAppKeyView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_login_setting, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		appkey = (TextView) getView().findViewById(R.id.appkey);
		ipView = (AutoCompleteTextView) getView().findViewById(R.id.ip_port);
		newAppKeyView = (EditText) getView().findViewById(R.id.new_appkey);

		appkey.setText(MyApplication.APPKEY);
		if (!TextUtils.isEmpty(MyApplication.IP)) {
			ipView.setText(MyApplication.IP+":"+MyApplication.PORT);
		} else {
			ipView.setText("");
		}
		spf = getActivity().getSharedPreferences("gotye_api",
				Context.MODE_PRIVATE);
		initAutoComplete(ipView);
		getView().findViewById(R.id.add_new).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						addNewAppKey(newAppKeyView.getText().toString());
					}
				});
		appkey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAppkeys();
			}
		});
		getView().findViewById(R.id.sure).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						saveConfig();
					}
				});
	}

	private void addNewAppKey(String appkey) {
		if (TextUtils.isEmpty(appkey.trim())) {
			ToastUtil.show(getActivity(), "无效appkey");
		}
		String keys = spf.getString("keys", null);
		if (TextUtils.isEmpty(keys)) {
			spf.edit().putString("keys", MyApplication.APPKEY + "," + appkey)
					.commit();
		} else {
			if (!keys.contains(appkey)) {
				spf.edit().putString("keys", keys + "," + appkey).commit();
			}
		}
		ToastUtil.show(getActivity(), "添加成功!");
		((EditText) getView().findViewById(R.id.new_appkey)).setText("");
	}

	private String currentSelected;
	private String beforeShow;

	private void showAppkeys() {
		String keys = spf.getString("keys", null);
		if (TextUtils.isEmpty(keys)) {
			keys = MyApplication.APPKEY;
		}
		final String[] keyArray = keys.split(",");

		int len = keyArray.length;

		int currentSelectedIndex = 0;
		if (currentSelected == null) {
			currentSelected = MyApplication.APPKEY;
		}
		beforeShow = currentSelected;
		for (int i = 0; i < len; i++) {
			if (currentSelected.equals(keyArray[i])) {
				currentSelectedIndex = i;
				break;
			}
		}
		Dialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle("请选择AppKey")
				.setIcon(R.drawable.ic_launcher)
				.setSingleChoiceItems(keyArray, currentSelectedIndex,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentSelected = keyArray[which];
								appkey.setText(currentSelected);
							}
						})
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentSelected = beforeShow;
						appkey.setText(currentSelected);
					}
				}).create();
		alertDialog.show();
	}

	private void saveConfig() {
		if (!TextUtils.isEmpty(currentSelected)) {
			spf.edit().putString("selected_key", currentSelected).commit();
			MyApplication.APPKEY = currentSelected;
		}
		String ip = ipView.getText().toString();
		if (!TextUtils.isEmpty(ip.trim())) {
			ip = ip.replace("：", ":");
			if (ip.contains(":")) {
				String[] ip_port = ip.split(":");
				if (ip_port != null && ip_port.length >= 2) {
					try {
						int port = Integer.parseInt(ip_port[1]);
						MyApplication.IP = ip_port[0];
						MyApplication.PORT = port;
						spf.edit().putString("selected_ip_port", ip).commit();
						String ipports = spf.getString("ipports", null);
						if (ipports == null) {
							ipports = ip;
						} else {
							if(!ipports.contains(ip)){
								ipports += "," + ip;
							}
						}
						spf.edit().putString("ipports", ipports).commit();
						Intent reInit = new Intent(getActivity(),
								GotyeService.class);
						reInit.setAction(GotyeService.ACTION_INIT);
						getActivity().startService(reInit);
					} catch (NumberFormatException e) {

					}
				}
			}
		} else {
			//恢复到默认
			MyApplication.IP=null;
			MyApplication.PORT=-1;
			Intent reInit = new Intent(getActivity(),
					GotyeService.class);
			reInit.setAction(GotyeService.ACTION_INIT);
			getActivity().startService(reInit);
			spf.edit().remove("selected_ip_port").commit();
		}
		((WelcomePage) getActivity()).showLogin();
	}

	private void initAutoComplete(AutoCompleteTextView auto) {

		String longhistory = spf.getString("ipports", null);
		if (longhistory == null) {
			return;
		}
		String[] hisArrays = longhistory.split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, hisArrays);
		// 只保留最近的50条的记录
		if (hisArrays.length > 50) {
			String[] newArrays = new String[50];
			System.arraycopy(hisArrays, 0, newArrays, 0, 50);
			adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_dropdown_item_1line, newArrays);
		}
		auto.setAdapter(adapter);
		auto.setDropDownHeight(250);
		auto.setThreshold(1);
		auto.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
					view.showDropDown();
				}
			}
		});
	}

}
