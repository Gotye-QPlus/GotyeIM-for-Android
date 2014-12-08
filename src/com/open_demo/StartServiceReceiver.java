package com.open_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, GotyeService.class);
		intent.setAction(GotyeService.ACTION_INIT);
		context.startService(intent);
	}

}
