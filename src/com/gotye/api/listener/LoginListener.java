package com.gotye.api.listener;

import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;


public interface LoginListener  extends GotyeListener{
	 /**
	  * 退出回调
	  * @param code 状态码 参见 {@link GotyeStatusCode}
	  */
	  void onLogout(int code);
	  /**
	   * 登陆回调
	   * @param code 状态码 参见 {@link GotyeStatusCode}
	   * @param currentLoginUser 当前登录用户
	   */
	  void onLogin(int code,GotyeUser currentLoginUser);
}
