package com.open_demo;

import com.gotye.api.GotyeAPI;

import android.app.Application;

public class MyApplication extends Application {
//	public static final String APPKEY = "388d424f-5293-44b5-bfd7-28666d8ad685";
//	public static final String APPKEY = "6d1c291e-75a8-4a6a-acd8-22cbb951ce80";
	public static final String APPKEY = "9c236035-2bf4-40b0-bfbf-e8b6dec54928";
	public static final String PACKAGENAME = "com.open_demo";
	@Override
	public void onCreate() {
		super.onCreate();
		//异常拦截记录
		CrashApplication.getInstance(this).onCreate();
		//初始化
		GotyeAPI.getInstance().init(getBaseContext(), APPKEY, PACKAGENAME);
	}
}
