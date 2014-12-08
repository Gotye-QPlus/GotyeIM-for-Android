package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 群对象
 * @author Administrator
 *
 */
public class GotyeGroup extends GotyeChatTarget{
	
	private long capacity;
	
	private String groupInfo;
	
	private String ownerAccount;
	
	private int ownerType;
	
	private boolean needAuthentication;
	
	private boolean hasGotDetail;
	
	private Icon icon;

	
	
	public boolean isHasGotDetail() {
		return hasGotDetail;
	}

	public void setHasGotDetail(boolean hasGotDetail) {
		this.hasGotDetail = hasGotDetail;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public boolean isNeedAuthentication() {
		return needAuthentication;
	}

	public GotyeGroup(long groupId) {
		this.Id=groupId;
		this.type=GotyeChatTargetType.GotyeChatTargetTypeGroup;
	}
	public GotyeGroup() {
		this.type=GotyeChatTargetType.GotyeChatTargetTypeGroup;
	}
	public void setNeedAuthentication(boolean needAuthentication) {
		this.needAuthentication = needAuthentication;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}

	public long getGroupID() {
		return Id;
	}

	public void setGroupID(long groupID) {
		this.Id = groupID;
	}

	public String getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	public String getGroupName() {
		return name;
	}

	public void setGroupName(String groupName) {
		this.name = groupName;
	}

	public String getOwnerAccount() {
		return ownerAccount;
	}

	public void setOwnerAccount(String ownerAccount) {
		this.ownerAccount = ownerAccount;
	}

	public int getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(int ownerType) {
		this.ownerType = ownerType;
	}


	

	@Override
	public String toString() {
		return "GotyeGroup [capacity=" + capacity + ", groupInfo=" + groupInfo
				+ ", ownerAccount=" + ownerAccount + ", ownerType=" + ownerType
				+ ", needAuthentication=" + needAuthentication
				+ ", hasGotDetail=" + hasGotDetail + ", icon=" + icon + "]";
	}

	public static GotyeGroup createGroupJson(JSONObject object) {
		try {
			GotyeGroup group = new GotyeGroup();

			long mCapacity = object.getLong("capacity");
			long mGroupID = object.getLong("groupID");
			String mGroupInfo = object.getString("groupInfo");
			String mGroupName = object.getString("groupName");
			boolean mHasGotDetail = object.getBoolean("hasGotDetail");
			Icon icon = new Icon();
			JSONObject obj = object.getJSONObject("icon");
			String mPath = obj.getString("path");
			String mPath_ex = obj.getString("path_ex");
			String mUrl = obj.getString("url");
			icon.setPath(mPath);
			icon.setPath_ex(mPath_ex);
			icon.setUrl(mUrl);
			
			boolean mAuthentication = object.getBoolean("need_authentication");
			String mOAcount = object.getString("ownerAccount");
			int mOwnAType = object.getInt("ownerType");
			group.setCapacity(mCapacity);
			group.setGroupID(mGroupID);
			group.setGroupInfo(mGroupInfo);
			group.setGroupName(mGroupName);
			group.setHasGotDetail(mHasGotDetail);
			group.setIcon(icon);
			group.setNeedAuthentication(mAuthentication);
			group.setOwnerAccount(mOAcount);
			group.setOwnerType(mOwnAType);
			group.type = GotyeChatTargetType.GotyeChatTargetTypeGroup;
			return group;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static GotyeGroup createGroupJson(String jsonStr) {
		if(jsonStr==null||jsonStr.length()==0){
			return null;
		}
		JSONObject obj;
		try {
			obj = new JSONObject(jsonStr);
			return createGroupJson(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
