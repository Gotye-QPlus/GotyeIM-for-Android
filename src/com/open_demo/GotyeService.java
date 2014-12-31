package com.open_demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.NotifyListener;
import com.open_demo.main.MainActivity;
import com.open_demo.util.AppUtil;

public class GotyeService extends Service implements NotifyListener {
	private GotyeAPI api;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		api = GotyeAPI.getInstance();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String user[] = getUser(intent, this);
		String hasUserName = user[0];
		String hasPassWord = user[1];
		if (!TextUtils.isEmpty(hasUserName)) {
			api.init(this, MyApplication.APPKEY, getPackageName());
			api.login(hasUserName, hasPassWord);
			api.addListener(this);
		}
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Intent i=new Intent("gotye_action_keep_service");
		sendBroadcast(i);
		GotyeAPI.getInstance().removeListener(this);
		Intent localIntent = new Intent();
		localIntent.setClass(this, GotyeService.class); // 銷毀時重新啟動Service
		this.startService(localIntent);
		super.onDestroy();
	}

	public static String[] getUser(Intent intent, Context context) {
		if (intent != null) {
			String[] user = new String[2];
			user[0] = intent.getStringExtra("name");
			user[1] = intent.getStringExtra("pwd");
			if (!TextUtils.isEmpty(user[0])) {
				return user;
			}
		}
		SharedPreferences sp = context.getSharedPreferences(LoginPage.CONFIG,
				Context.MODE_PRIVATE);
		String name = sp.getString("username", null);
		String password = sp.getString("password", null);
		String[] user = new String[2];
		user[0] = name;
		user[1] = password;
		return user;
	}

	@SuppressWarnings("deprecation")
	private void notify(String msg) {
		String currentPackageName = AppUtil.getTopAppPackage(getBaseContext());
		if (currentPackageName.equals(getPackageName())) {
			return;
		}
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		Notification notification = new Notification(R.drawable.ic_launcher,
				msg, System.currentTimeMillis());
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("notify", 1);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, getString(R.string.app_name),
				msg, pendingIntent);
		notificationManager.notify(0, notification);
	}

	@Override
	public void onReceiveMessage(int code, GotyeMessage message, boolean unRead) {
		// TODO Auto-generated method stub
		String msg = null;

		if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
			msg = message.getSender().name + "发来了一条消息";
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
			msg = message.getSender().name + "发来了一条图片消息";
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
			msg = message.getSender().name + "发来了一条语音消息";
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
			msg = message.getSender().name + "发来了一条自定义消息";
		} else {
			msg = message.getSender().name + "发来了一条群邀请信息";
		}
		notify(msg);
	}

	@Override
	public void onSendMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveNotify(int code, GotyeNotify notify) {
		String msg = notify.getSender().name + "邀请您加入群[";
		if (!TextUtils.isEmpty(notify.getFrom().name)) {
			msg += notify.getFrom().name + "]";
		} else {
			msg += notify.getFrom().Id + "]";
		}
		notify(msg);
	}

	@Override
	public void onRemoveFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifyStateChanged() {
		// TODO Auto-generated method stub

	}
}
