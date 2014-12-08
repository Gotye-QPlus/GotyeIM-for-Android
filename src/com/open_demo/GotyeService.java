package com.open_demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.NotifyListener;
import com.open_demo.main.MainActivity;
import com.open_demo.util.AppUtil;

public class GotyeService extends Service implements NotifyListener {
	public static final String ACTION_INIT = "gotye.action.init";

	// public static final String ACTION_RUN_BACKGROUND =
	// "gotye.action.run_in_background";
	// public static final String ACTION_RUN_ON_UI = "gotye.action.run_on_ui";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		GotyeAPI.getInstance().addListerer(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (ACTION_INIT.equals(intent.getAction())) {
				GotyeAPI.getInstance().init(getBaseContext(),
						MyApplication.APPKEY, MyApplication.PACKAGENAME);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		GotyeAPI.getInstance().removeListener(this);
		GotyeAPI.getInstance().serviceDestoryNotify();
		super.onDestroy();
	}

	private void notify(String msg) {
		String currentPackageName = AppUtil.getTopAppPackage(getBaseContext());
		if (currentPackageName.equals(MyApplication.PACKAGENAME)) {
			return;
		}
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		Notification notification = new Notification(R.drawable.ic_launcher,
				getString(R.string.app_name), System.currentTimeMillis());
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
			msg = message.getSender().name + ":" + message.getText();
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
			msg = message.getSender().name + ":图片消息";
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
			msg = message.getSender().name + ":语音消息";
		} else if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
			msg = message.getSender().name + ":自定义消息";
		} else {
			msg = message.getSender().name + ":群邀请信息";
		}
		notify(msg);
	}

	@Override
	public void onSendMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveNotify(int code,GotyeNotify notify) {
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
