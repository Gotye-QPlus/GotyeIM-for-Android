package com.open_demo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class MyApplication extends Application {
	public static final String DEFAULT_APPKEY = "9c236035-2bf4-40b0-bfbf-e8b6dec54928";
//	public static final String DEFAULT_APPKEY = "7c15b6dd-804e-410a-a1af-0df82d0771c2";
	// public static final String
	// DEFAULT_APPKEY="9c236035-2bf4-40b0-bfbf-e8b6dec54928";
	public static String APPKEY = DEFAULT_APPKEY;
	// public static String APPKEY = "f013a548-2cfd-42d3-8111-da556c5c0951";
	public static String IP = null;
	public static int PORT = -1;

	@Override
	public void onCreate() {
		super.onCreate();
		loadSelectedKey(this);
		CrashApplication.getInstance(this).onCreate();
	}

	public static void loadSelectedKey(Context context) {
		SharedPreferences spf = context.getSharedPreferences("gotye_api",
				Context.MODE_PRIVATE);
		APPKEY = spf.getString("selected_key", DEFAULT_APPKEY);
		String ip_port = spf.getString("selected_ip_port", null);
		if (!TextUtils.isEmpty(ip_port)) {
			String[] ipPort = ip_port.split(":");
			if (ipPort != null && ipPort.length >= 2) {
				try {
					int port = Integer.parseInt(ipPort[1]);
					IP = ipPort[0];
					PORT = port;
				} catch (Exception e) {

				}

			}
		}

	}
}
