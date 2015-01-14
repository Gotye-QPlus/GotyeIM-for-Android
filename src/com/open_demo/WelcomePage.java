package com.open_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.LoginListener;
import com.open_demo.main.MainActivity;
import com.open_demo.util.ProgressDialogUtil;

public class WelcomePage extends FragmentActivity implements LoginListener,
		OnGestureListener {
	private Fragment loginSetting, loginPage;
	private GestureDetector mGesture = null;

	private boolean onLogin = true;

	private int width;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// -1 offline, API will reconnect when network becomes valid
		// 0 not login or logout already
		// 1 online
		int state = GotyeAPI.getInstance().getOnLineState();
		Log.d("login", "state=" + state);
		if (state != 0) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			Intent toService = new Intent(this, GotyeService.class);
			startService(toService);
			finish();
			return;
		}
		setContentView(R.layout.layout_welcome);
		GotyeAPI.getInstance().addListener(this);
		loginSetting = new LoginSettingPage();
		loginPage = new LoginPage();
		showLogin();
		mGesture = new GestureDetector(this, this);
		width=getResources().getDisplayMetrics().widthPixels/2;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	public void showLogin() {
		onLogin = true;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.back_left_in,R.anim.back_right_out); 
		ft.replace(R.id.fragment_container, loginPage, "login");
		
		ft.addToBackStack(null);
		
		ft.commit();
	}

	public void showSetting() {
		if (!onLogin) {
			return;
		}
		onLogin = false;
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.push_left_in,R.anim.push_left_out); 
		ft.replace(R.id.fragment_container, loginSetting, "setting");
		ft.commit();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return mGesture.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// 移除监听
		GotyeAPI.getInstance().removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onLogin(int code, GotyeUser user) {
		ProgressDialogUtil.dismiss();
		// 判断登陆是否成功
		if (code == GotyeStatusCode.CODE_OK
				|| code == GotyeStatusCode.CODE_OFFLINELOGIN_SUCCESS
				|| code == GotyeStatusCode.CODE_RELOGIN_SUCCESS) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			if (code == GotyeStatusCode.CODE_OFFLINELOGIN_SUCCESS) {
				Toast.makeText(this, "您当前处于离线状态", Toast.LENGTH_SHORT).show();
			} else if (code == GotyeStatusCode.CODE_OK) {
				Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
			}
			this.finish();
		} else {
			// 失败,可根据code定位失败原因
			Toast.makeText(this, "登录失败 code=" + code, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onLogout(int code) {
		// TODO Auto-generated method stub
		Log.d("gotye", "onLogout");
	}

	@Override
	public void onReconnecting(int code, GotyeUser currentLoginUser) {
		// TODO Auto-generated method stub
		Log.d("gotye", "onReconnecting");
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > width) {// 向左滑，右边显示
			// this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.push_left_in));
			// this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
			// R.anim.push_left_out));
			showSetting();
		}
		//if (e1.getX() - e2.getX() < -120) {// 向右滑，左边显示
			//showSetting();
		//}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	// 单击
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	// 长按
	public void onLongPress(MotionEvent e) {

	}
}
