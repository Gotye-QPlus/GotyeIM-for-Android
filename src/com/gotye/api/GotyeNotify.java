package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;


public class GotyeNotify {

	private long dbID;
	private long date; // seconds since 1970.1.1 00:00
	private boolean isRead;
	private GotyeChatTarget sender;
	private GotyeChatTarget receiver;
	private GotyeChatTarget from; // source of notify.
									// GotyeNotifyTypeGroupInvite is from some
									// group, etc.

	private boolean agree;
	private boolean isSystemNotify;
	private GotyeNotifyType type;
	private String text;// notify content.

	public long getDbID() {
		return dbID;
	}

	public GotyeNotifyType getType() {
		return type;
	}

	public void setType(GotyeNotifyType type) {
		this.type = type;
	}

	public void setDbID(long dbID) {
		this.dbID = dbID;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public boolean isRead() {
		return isRead;
	}

	public boolean isAgree() {
		return agree;
	}

	public GotyeChatTarget getReceiver() {
		return receiver;
	}

	public void setReceiver(GotyeChatTarget receiver) {
		this.receiver = receiver;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public GotyeChatTarget getSender() {
		return sender;
	}

	public void setSender(GotyeChatTarget sender) {
		this.sender = sender;
	}

	public GotyeChatTarget getFrom() {
		return from;
	}

	public void setFrom(GotyeChatTarget from) {
		this.from = from;
	}

	public boolean isSystemNotify() {
		return isSystemNotify;
	}

	public void setSystemNotify(boolean isSystemNotify) {
		this.isSystemNotify = isSystemNotify;
	}

	 

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// jsonNotify["db_id"] = (int)notify.dbID;
	// jsonNotify["date"] = (int)notify.date;
	// jsonNotify["is_read"] = notify.isRead;
	// jsonNotify["sender_type"] = notify.sender.type;
	// jsonNotify["sender_id"] = notify.sender.id;
	// jsonNotify["sender_name"] = notify.sender.name;
	// jsonNotify["from_type"] = notify.from.type;
	// jsonNotify["from_id"] = notify.from.id;
	// jsonNotify["from_name"] = notify.from.name;
	// jsonNotify["is_system"] = notify.isSystemNotify;
	// jsonNotify["type"] = notify.type;
	// jsonNotify["text"] = notify.text;

	public static GotyeNotify jsonToNotify(String notifyJson) {
		JSONObject obj;
		try {
			obj = new JSONObject(notifyJson);
			return jsonToNotify(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static GotyeNotify jsonToNotify(JSONObject obj) {
		try {
			GotyeNotify notify = new GotyeNotify();
			notify.dbID = obj.getLong("db_id");
			notify.date = obj.getLong("date");
			notify.isRead = obj.getBoolean("is_read");
			int sender_type = obj.getInt("sender_type");
			long sender_id = obj.getLong("sender_id");
			String sender_name = obj.getString("sender_name");
			
			if(sender_type==0){
				GotyeChatTarget sender =new GotyeUser();
				sender.type = GotyeChatTargetType.values()[sender_type];
				sender.name = sender_name;
				sender.Id = sender_id;
				notify.sender = sender;
			}
			
			
			int receiver_type = obj.getInt("receiver_type");
			long receiver_id = obj.getLong("sender_id");
			String receiver_name = obj.getString("receiver_name");
			if(receiver_type==0){
				GotyeChatTarget receiver =new GotyeUser();
				 receiver.type = GotyeChatTargetType.values()[receiver_type];
				 receiver.name = receiver_name;
				 receiver.Id = receiver_id;
				notify.receiver =  receiver;
			}
			
			int from_type = obj.getInt("from_type");
			long from_id = obj.getLong("from_id");
			String from_name = obj.getString("from_name");
			
			if(from_type==2){
				GotyeChatTarget from= new GotyeGroup();
				from.Id = from_id;
				from.name = from_name;
				from.type = GotyeChatTargetType.values()[from_type];
				notify.from = from;
			}
			
			notify.isSystemNotify = obj.getBoolean("is_system");
            notify.agree=obj.getBoolean("agree");
			int type = obj.getInt("type");
			notify.type=GotyeNotifyType.values()[type];
			notify.text = obj.getString("text");
			return notify;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
