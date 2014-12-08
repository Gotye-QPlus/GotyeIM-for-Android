package com.open_demo.bean;

import com.gotye.api.GotyeUser;

public class GotyeUserProxy {
	public GotyeUser gotyeUser;
	public String firstChar;

	public GotyeUserProxy(GotyeUser gotyeUser) {
		this.gotyeUser = gotyeUser;
	}
	@Override
	public boolean equals(Object o) {
		if(o==null){
			return false;
		}
		GotyeUser user=(GotyeUser) o;
		return user.name.equals(gotyeUser.name);
	}
}
