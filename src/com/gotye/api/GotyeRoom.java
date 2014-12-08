package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 聊天室对象
 * @author Administrator
 *
 */
public class GotyeRoom extends GotyeChatTarget {

	private boolean isTop;

	private int userLimit;

	private int curUerCount;

	private Icon icon;

	public long getRoomID() {
		return Id;
	}
	public GotyeRoom() {
		// TODO Auto-generated constructor stub
		this.type=GotyeChatTargetType.GotyeChatTargetTypeRoom;
	}

	public void setRoomID(long roomID) {
		this.Id = roomID;
	}

	public String getRoomName() {
		return name;
	}

	public void setRoomName(String roomName) {
		this.name = roomName;
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public int getUserLimit() {
		return userLimit;
	}

	public void setUserLimit(int userLimit) {
		this.userLimit = userLimit;
	}

	public int getCurUerCount() {
		return curUerCount;
	}

	public void setCurUerCount(int curUerCount) {
		this.curUerCount = curUerCount;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "GotyeRoom [roomID=" + Id + ", roomName=" + name + ", isTop="
				+ isTop + ", userLimit=" + userLimit + ", curUerCount="
				+ curUerCount + ", icon=" + icon + "]";
	}

	public static GotyeRoom createRoomJson(JSONObject object) {

		GotyeRoom gotyeRoom = new GotyeRoom();
		int mCapacity;
		try {
			mCapacity = object.getInt("capacity");
			Icon icon = new Icon();
			JSONObject obj = object.getJSONObject("icon");
			String mPath = obj.getString("path");
			String mPath_ex = obj.getString("path_ex");
			String mUrl = obj.getString("url");
			icon.setPath(mPath);
			icon.setPath_ex(mPath_ex);
			icon.setUrl(mUrl);

			long mRoomId = object.getLong("id");
			boolean mIsTop = object.getBoolean("isTop");
			String mRoomName = object.getString("name");
			int mOnLineNumber = object.getInt("onlineNumber");

			gotyeRoom.setRoomID(mRoomId);
			gotyeRoom.setRoomName(mRoomName);
			gotyeRoom.setTop(mIsTop);
			gotyeRoom.setCurUerCount(mOnLineNumber);
			gotyeRoom.setUserLimit(mCapacity);
			gotyeRoom.setIcon(icon);
			gotyeRoom.type = GotyeChatTargetType.GotyeChatTargetTypeRoom;
			return gotyeRoom;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GotyeRoom createRoomJson(String jsonRoom) {
		JSONObject obj;
		try {
			obj = new JSONObject(jsonRoom);
			return createRoomJson(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
