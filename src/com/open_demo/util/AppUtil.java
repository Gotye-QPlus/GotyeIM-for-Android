package com.open_demo.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class AppUtil {
	public static String  getTopAppPackage(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
				.getSystemService("activity");
		RunningTaskInfo currentRun = activityManager.getRunningTasks(1).get(0);
		ComponentName nowApp = currentRun.topActivity;
		String packname = nowApp.getPackageName();
	    return packname;
	}

	public static String  getHomeLauncher(Context context) {
		PackageManager pm =context.getPackageManager(); // 获得PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_HOME);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.MATCH_DEFAULT_ONLY);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		StringBuffer sb = new StringBuffer();
		for (ResolveInfo reInfo : resolveInfos) {
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			sb.append(activityName);
			sb.append(",");
		}
		 return sb.toString();
	}

}
