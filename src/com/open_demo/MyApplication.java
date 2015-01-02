package com.open_demo;

//import com.appdynamics.eumagent.runtime.Instrumentation;
//import com.appdynamics.eumagent.runtime.Instrumentation;
import com.gotye.api.GotyeAPI;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
//	public static final String APPKEY = "388d424f-5293-44b5-bfd7-28666d8ad685";
//	public static final String APPKEY = "6d1c291e-75a8-4a6a-acd8-22cbb951ce80";
	public static final String APPKEY = "9c236035-2bf4-40b0-bfbf-e8b6dec54928";
//	public static final String APPKEY = "44e0ad71-d768-4b81-9a2b-5c6d1668b76e";
	//public static final String APPKEY = "04525b84-81cd-4934-b2eb-568db9795c26";
	
	@Override
	public void onCreate() { 
		super.onCreate();
		//异常拦截记录
		CrashApplication.getInstance(this).onCreate();
		//初始化          
		
		SpeechUtility.createUtility(MyApplication.this, SpeechConstant.APPID +"=547e8752");
		//Android手机测试性能
		//Instrumentation.start("AD-AAB-AAA-PWM", getApplicationContext());
		
	}
}
