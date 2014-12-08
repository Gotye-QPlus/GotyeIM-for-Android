package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 用户对象
 * @author Administrator
 *
 */
public class GotyeUser extends GotyeChatTarget{
	
	private GotyeGender gender;
	private Icon icon;
	private String nickname;
	private boolean hasGotDetail;
	private boolean isBlocked;
	private boolean isFriend;
	private String info;
	public GotyeUser(String name) {
		this.name=name;
		// TODO Auto-generated constructor stub
		this.type=GotyeChatTargetType.GotyeChatTargetTypeUser;
	}
	public GotyeUser() {
		this.type=GotyeChatTargetType.GotyeChatTargetTypeUser;
	}
	public GotyeGender getGender() {
		return gender;
	}

	public void setGender(GotyeGender gender) {
		this.gender = gender;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isHasGotDetail() {
		return hasGotDetail;
	}

	public void setHasGotDetail(boolean hasGotDetail) {
		this.hasGotDetail = hasGotDetail;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean isFriend() {
		return isFriend;
	}

	public void setFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}
	
	

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o==null){
			return false;
		}
		GotyeUser user=(GotyeUser) o;
		
		if(name.equals(user.getName())){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public String toString() {
		return "GotyeUser [gender=" + gender + ", icon=" + icon + ", name="
				+ name + ", nickname=" + nickname + "]";
	}
	
	public static GotyeUser jsonToUser(String jsonUser){
		if(jsonUser==null||jsonUser.length()==0){
			return null;
		}
		GotyeUser user=new GotyeUser();
		try {
			JSONObject object = new JSONObject(jsonUser);
			user.setGender(GotyeGender.values()[object.getInt("gender")]);
			user.setHasGotDetail(object.getBoolean("hasGotDetail"));
				Icon icon = new Icon();
				JSONObject obj = object.getJSONObject("icon");
				String mPath = obj.getString("path");
				String mPath_ex = obj.getString("path_ex");
				String mUrl = obj.getString("url");
				icon.setPath(mPath);
				icon.setPath_ex(mPath_ex);
				icon.setUrl(mUrl);
			user.setIcon(icon);
			user.setBlocked(object.getBoolean("isBlocked"));
			user.setFriend(object.getBoolean("isFriend"));
			user.setInfo(object.getString("info"));
			user.setName(object.getString("name"));
			user.setNickname(object.getString("nickname"));
			 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return user;
	} 
	public static GotyeUser jsonToUser(JSONObject object){
		GotyeUser user=new GotyeUser();
		try {
			user.setGender(GotyeGender.values()[object.getInt("gender")]);
			user.setHasGotDetail(object.getBoolean("hasGotDetail"));
				Icon icon = new Icon();
				JSONObject obj = object.getJSONObject("icon");
				String mPath = obj.getString("path");
				String mPath_ex = obj.getString("path_ex");
				String mUrl = obj.getString("url");
				icon.setPath(mPath);
				icon.setPath_ex(mPath_ex);
				icon.setUrl(mUrl);
			user.setIcon(icon);
			user.setBlocked(object.getBoolean("isBlocked"));
			user.setFriend(object.getBoolean("isFriend"));
			user.setInfo(object.getString("info"));
			user.setName(object.getString("name"));
			user.setNickname(object.getString("nickname"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return user;
	} 
 
}
