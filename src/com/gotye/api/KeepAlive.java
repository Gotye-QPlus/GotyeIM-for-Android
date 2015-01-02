package com.gotye.api;

import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author Administrator
 *
 */
public class KeepAlive extends BroadcastReceiver {

	/**
	 * @param name
	 */
	public KeepAlive(final Context ctx, Handler handler) {
		
		oshandler = handler;
		context = ctx;
		// super("keep-alive");
		System.err.println("gotye----->regist receiver");
		IntentFilter filter = new IntentFilter(ACTION_KEEP_ALIVE + "."
				+ ACTION_STAMP);
		context.registerReceiver(KeepAlive.this, filter);
		regist = true;
	}
	
	private Context context;
	private Handler oshandler;
	public String ACTION_STAMP = UUID.randomUUID().toString();
	public static final int ALIVE_INTERVAL = 90000;
	public static final String ACTION_KEEP_ALIVE = "com.gotye.sdk.action_keep_alive";
	private boolean regist = false;
	
	public void startKeepAlive() {
		oshandler.post(new Runnable() {
			
			@Override
			public void run() {
				setAlarm();
			}
		});
		
		Log.d("heart", "START keepalive");
	}
	
	@SuppressLint("NewApi")
	private void setAlarm(){
		try{
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent();
			intent.setAction(ACTION_KEEP_ALIVE + "." + ACTION_STAMP);
			// 实例化pendingintent
			PendingIntent pi = PendingIntent
					.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if(currentapiVersion >= 19){
				am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + ALIVE_INTERVAL, pi);
			}else {
				am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + ALIVE_INTERVAL, pi);
			}
			
//			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//					SystemClock.elapsedRealtime() + GotyePackageSender.KEEP_ALIVE, GotyePackageSender.KEEP_ALIVE, pi);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void stopKeepAlive() {
		oshandler.post(new Runnable() {
			
			@Override
			public void run() {
				if(regist){
					System.err.println("gotye----->unregisterReceiver receiver");
					context.unregisterReceiver(KeepAlive.this);
				}
				
				try{
					AlarmManager am = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					// 实例化intent
					Intent intent = new Intent();
					intent.setAction(ACTION_KEEP_ALIVE + "." + ACTION_STAMP);
					// 实例化pendingintent
					PendingIntent pi = PendingIntent
							.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					am.cancel(pi);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		Log.d("heart", "stop keepalive");
	}

	// @Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if ((ACTION_KEEP_ALIVE + "." + ACTION_STAMP).equals(action)) {
			//Log.e("gotye", "keep alive..");
			GotyeAPI.getInstance().keepalive();
			setAlarm();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		onHandleIntent(intent);
	}
}