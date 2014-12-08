package com.gotye.api;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.open_demo.util.FileUtil;
/**
 * 消息对象
 * @author gotye
 *
 */
public class GotyeMessage {
	public static final int STATUS_CREATE = 0;

	public static final int ACK_UNREAD = 1;
	public static final int ACK_READ = 2;

	public static final int STATUS_SENDING = 3;
	public static final int STATUS_SENT = 4;
	public static final int STATUS_SENDFAILED = 5;
	private long date;

	private long dbId;

	private long id;

	private Media media, extra;

	private byte[] userData, extraData;
	private GotyeChatTarget receiver;

	private int receiver_type;

	private GotyeChatTarget sender;

	private int sender_type;

	private String text;

	private GotyeMessageType type;
	private int status;

	public int getStatus() {
		return status;
	}

	private GotyeMessage() {

	}

//	public Media getExtra() {
//		return extra;
//	}

	public void putExtraData(byte[] extraData) {
		this.extraData = extraData;
	}

	public byte[] getExtraData() {
		return extraData;
	}

//	public void setExtra(Media extra) {
//		this.extra = extra;
//	}

	public byte[] getUserData() {
		return userData;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getDbId() {
		return dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public GotyeChatTarget getReceiver() {
		return receiver;
	}

	public void setReceiver(GotyeChatTarget receiver) {
		this.receiver = receiver;
	}

	public int getReceiverType() {
		return receiver_type;
	}

	public void setReceiverType(int receiver_type) {
		this.receiver_type = receiver_type;
	}

	public GotyeChatTarget getSender() {
		return sender;
	}

	public void setSender(GotyeChatTarget sender) {
		this.sender = sender;
	}

	public int getSenderType() {
		return sender_type;
	}

	public void setSenderType(int sender_type) {
		this.sender_type = sender_type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public GotyeMessageType getType() {
		return type;
	}

	public void setType(GotyeMessageType type) {
		this.type = type;
	}

	public static GotyeMessage createMessage(GotyeChatTarget receiver) {
		GotyeMessage message = new GotyeMessage();
		message.receiver = receiver;
		message.setDate(System.currentTimeMillis() / 1000);
		return message;
	}

	public static GotyeMessage createMessage(GotyeChatTarget sender,
			GotyeChatTarget receiver) {
		GotyeMessage message = new GotyeMessage();
		message.sender = sender;
		message.receiver = receiver;
		message.setDate(System.currentTimeMillis() / 1000);
		return message;
	}

	//
	public static GotyeMessage createTextMessage(GotyeChatTarget receiver,
			String text) {
		GotyeMessage message = new GotyeMessage();
		message.receiver = receiver;
		message.text = text;
		message.setDate(System.currentTimeMillis() / 1000);
		message.type = GotyeMessageType.GotyeMessageTypeText;
		return message;
	}

	public static GotyeMessage createTextMessage(GotyeChatTarget sender,
			GotyeChatTarget receiver, String text) {
		GotyeMessage message = new GotyeMessage();
		message.sender = sender;
		message.receiver = receiver;
		message.text = text;
		message.setDate(System.currentTimeMillis() / 1000);
		message.type = GotyeMessageType.GotyeMessageTypeText;
		return message;
	}

	public static GotyeMessage createImageMessage(GotyeChatTarget receiver,
			String imagePath) {
		GotyeMessage message = new GotyeMessage();
		message.receiver = receiver;
		Media media = new Media();
		media.setPath_ex(imagePath);
		media.setType(GotyeMediaType.GotyeMediaTypeImage);
		message.media = media;
		message.type = GotyeMessageType.GotyeMessageTypeImage;
		return message;
	}

	//
	public static GotyeMessage createImageMessage(GotyeChatTarget sender,
			GotyeChatTarget receiver, String imagePath) {
		GotyeMessage message = new GotyeMessage();
		message.sender = sender;
		message.receiver = receiver;
		message.setDate(System.currentTimeMillis() / 1000);
		Media media = new Media();
		media.setPath_ex(imagePath);
		media.setType(GotyeMediaType.GotyeMediaTypeImage);
		message.media = media;
		message.type = GotyeMessageType.GotyeMessageTypeImage;
		return message;
	}

	public static GotyeMessage createUserDataMessage(GotyeChatTarget receiver,
			String dataPath) {
		GotyeMessage message = new GotyeMessage();
		message.receiver = receiver;
		Media media = new Media();
		media.setPath(dataPath);
		message.setDate(System.currentTimeMillis() / 1000);
		media.setType(GotyeMediaType.GotyeMediaTypeUserData);
		message.type = GotyeMessageType.GotyeMessageTypeUserData;
		message.media = media;
		return message;
	}

	public static GotyeMessage createUserDataMessage(GotyeChatTarget sender,
			GotyeChatTarget receiver, String dataPath) {
		GotyeMessage message = new GotyeMessage();
		message.sender = sender;
		message.receiver = receiver;
		Media media = new Media();
		media.setPath(dataPath);
		message.setDate(System.currentTimeMillis() / 1000);
		media.setType(GotyeMediaType.GotyeMediaTypeUserData);
		message.media = media;
		message.type = GotyeMessageType.GotyeMessageTypeUserData;
		return message;
	}

	public static GotyeMessage createUserDataMessage(GotyeChatTarget receiver,
			byte[] data, int len) {
		if(data==null){
            throw new NullPointerException("data is null");
		}
		if(data.length<len){
			throw new ArrayIndexOutOfBoundsException("len bigger than data lenght");
		}
		GotyeMessage message = new GotyeMessage();
		message.receiver = receiver;
		message.userData=data;
		message.type = GotyeMessageType.GotyeMessageTypeUserData;
		return message;

	}

	public static GotyeMessage createUserDataMessage(GotyeChatTarget sender,
			GotyeChatTarget receiver, byte[] data, int len) {
		if(data==null){
            throw new NullPointerException("data is null");
		}
		if(data.length<len){
			throw new ArrayIndexOutOfBoundsException("len > data length");
		}
		GotyeMessage message = new GotyeMessage();
		message.sender = sender;
		message.receiver = receiver;
		message.userData=data;
		message.type = GotyeMessageType.GotyeMessageTypeUserData;
		return message;
	}

	public static GotyeMessage jsonToMessage(String jsonMessage) {
		if (jsonMessage == null || jsonMessage.length() == 0) {
			return null;
		}
		try {
			return jsonToMessage(new JSONObject(jsonMessage));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GotyeMessage jsonToMessage(JSONObject obj) {
		GotyeMessage msg = new GotyeMessage();
		try {
			msg.date = obj.getLong("date");
			msg.dbId = obj.getLong("dbID");
			msg.id = obj.getLong("id");
			int type = obj.getInt("type");
			GotyeMessageType gt = GotyeMessageType.values()[type];
			msg.type = gt;

			msg.receiver_type = obj.getInt("receiver_type");
			if (msg.receiver_type == 0) {
				GotyeUser user = new GotyeUser(obj.getString("receiver"));
				msg.receiver = user;
			} else if (msg.receiver_type == 1) {
				GotyeRoom room = new GotyeRoom();
				room.Id = Long.parseLong(obj.getString("receiver"));
				msg.receiver = room;
			} else if (msg.receiver_type == 2) {
				GotyeGroup group = new GotyeGroup();
				group.Id = Long.parseLong(obj.getString("receiver"));
				msg.receiver = group;
			}

			msg.sender_type = obj.getInt("sender_type");

			if (msg.sender_type == 0) {
				GotyeUser user = new GotyeUser(obj.getString("sender"));
				msg.sender = user;
			} else if (msg.sender_type == 1) {
				GotyeRoom room = new GotyeRoom();
				room.Id = Long.parseLong(obj.getString("sender"));
				msg.sender = room;
			} else if (msg.sender_type == 2) {
				GotyeGroup group = new GotyeGroup();
				group.Id = Long.parseLong(obj.getString("sender"));
				msg.sender = group;
			}
			msg.media = Media.jsonToMedia(obj.getJSONObject("media"));
			if(msg.type==GotyeMessageType.GotyeMessageTypeUserData&&msg.media.getType()==GotyeMediaType.GotyeMediaTypeUserData){
				msg.userData=FileUtil.getBytes(msg.media.getPath());
			}
			msg.extra=Media.jsonToMedia(obj.getJSONObject("extra"));
			if(msg.extra!=null){
				msg.extraData=FileUtil.getBytes(msg.extra.getPath());
			}
			msg.status = obj.getInt("status");
			msg.text = obj.getString("text");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public String toString() {
		return "GotyeMsgList [date=" + date + ", id=" + id + ", media=" + media
				+ ", receiver=" + receiver + ", receiver_type=" + receiver_type
				+ ", sender=" + sender + ", sender_is_type=" + sender_type
				+ ", text=" + text + ", type=" + type + "]";
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		GotyeMessage message = (GotyeMessage) object;
		if (message.getDbId() == this.dbId) {
			return true;
		} else {
			return false;
		}
	}

}
