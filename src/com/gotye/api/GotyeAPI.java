package com.gotye.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.gotye.api.listener.GotyeListener;
import com.gotye.api.listener.NotifyListener;

/**
 * Gotye IM 入口
 * @author gotye
 *
 */
public final class GotyeAPI {
	// sharePreference配置文件名字
	private static final String SHARE_PREFERENCE_NAME = "gotye_config";
	// 是否有新消息提醒
	private boolean newMsgNotify = true;
	// 不接收群消息
	private boolean notReceiveGroupMsg = false;
	// sharePerference操作对象
	private SharedPreferences spf;
	// 群免打扰对应的groupID
	private ArrayList<Long> disturbGroupIds = new ArrayList<Long>();

	// 保存的应用上下午环境
	private Context context;
	
	private KeepAlive mKeepAlive;
	// 当前登陆用户对象
	private GotyeUser currentLoginUser;
	private static GotyeAPI mInstance = null;
	static GotyeDelegate mListener = new GotyeListenerImp();
	// 所有注册的监听器
	private ArrayList<GotyeListener> listeners = new ArrayList<GotyeListener>();

	// 私有化构造方法，同时启动timer
	private GotyeAPI() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				myHandler.sendEmptyMessage(0);
			}
		}, 0, 50);
	}

	private static Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 驱动回调事件
			getInstance().mainloop();
		}
	};

	// 获取实例
	public static GotyeAPI getInstance() {
		if (null == mInstance) {
			mInstance = new GotyeAPI();
		}
		return mInstance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            当前应用上下午环境
	 * @param appKey
	 *            亲加官网申请的appKey
	 * @param packageName
	 *            应用包名
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public int init(Context context, String appKey, String packageName) {
		this.context = context;
		mKeepAlive = new KeepAlive(context, myHandler);
		mKeepAlive.startKeepAlive();
		spf = context.getSharedPreferences(SHARE_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		return init(appKey, packageName);
	}

	ArrayList<GotyeListener> getListeners() {
		// TODO Auto-generated method stub
		ArrayList<GotyeListener>  copy=new ArrayList<GotyeListener>(listeners.size());
		copy.addAll(listeners);
		return copy;
	}

	/**
	 * 添加注册监听器
	 * 
	 * @param listener
	 */
	public void addListener(GotyeListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * 删除
	 * 
	 * @param listener
	 */
	public void removeListener(GotyeListener listener) {
		if (listener != null && listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	// 登陆完成后把当前登陆对象传过来保存
	void onLoginCallBack(int code, GotyeUser currentLoginUser) {
		if (code == GotyeStatusCode.CODE_OK) {
			this.currentLoginUser = currentLoginUser;
			newMsgNotify = spf.getBoolean("new_msg_notify_"
					+ currentLoginUser.name, true);
			notReceiveGroupMsg = spf.getBoolean("not_receive_group_msg_"
					+ currentLoginUser.name, false);
		}
	}

	// 退出时清理上次登陆缓存数据
	void onLogoutCallBack(int code) {
		if (code == GotyeStatusCode.CODE_OK) {
			// 正常退出
		}

		//listeners.clear();
		disturbGroupIds.clear();
		currentLoginUser = null;
	}

	// -------------------------初始化-----------------------------
	private native int init(String appKey, String packageName);
	
	
	public native void exit();

	// -------------------------tools---------------------------
	/**
	 * 清理缓存
	 * 
	 * @return 操作结果，参见 {@link GotyeStatusCode}
	 */
	public native int clearCache();
	public native void keepalive();
	private native int mainloop();
	private native int isOnLine();
	private native String getLoginuser();
	private native String getTargetDetail(String target, int type,
			boolean forceRequest);
	private native int modifyUserinfo(String nickname, int gender, String info,
			String path);// add param info
	
	private native int inRoom(long roomId);

	private native void markMessagesAsread(String target, int type,
			boolean isread);

	private native void markSingleMessageAsRead(long msgDbid, boolean isRead);

	private native void deleteMessage(long msgDbid);

	private native void clearMessages(String target, int type);

	private native int getUnreadMsgcount(String target, int type);

	private native int getTotalUnreadMsgcount();

	private native int getUnreadMsgcountByType(int type);

	private native String getSessionlist();

	private native void activeSession(String target, int type);

	private native void deactiveSession(String target, int type);

	private native void deleteSession(String target, int type);

	private native void markSessionTop(String target, int type, boolean top);

	private native String getLastMessage(String target, int type);

	private native String getNotifylist();

	private native int markNotifyIsread(long dbID, boolean isRead);

	/**
	 * 清理掉所有未读通知状态
	 */
	public native void clearNotifyUnreadStatus();

	private native void deleteNotify(long dbID);
	//启动或关闭文本敏感词过滤
	private native void enableTextFilter(int type, int enabled);

	/**
	 * 设置每次读取历史消息格式
	 * 
	 * @param increment
	 */
	public native void setMsgReadincrement(int increment);

	/**
	 * 设置每次请求历史记录个数
	 * 
	 * @param increment
	 */
	public native void setMsgRequestincrement(int increment);

	/**
	 * 设置是否每次登录后自动获取离线消息
	 */
	public native void beginRcvOfflineMessge();

	private native int getUnreadNotifycount();

	// ------------------------login----------------------------
	/**
	 * 登录
	 * 
	 * @param username
	 *            登录账号
	 * @param password
	 *            登录密码（若没有密码传null）
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int login(String username, String password);

	/**
	 * 退出登录
	 * 
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int logout();

	// ------------------------user----------------------------
	// private native String requestUserInfo(String username); 删除

	

	private native void restUsersearch();

	private native String getLocalUserSearchlist();

	private native String getLocalUserCurpageSearchlist();

	private native int requestSearchUserlist(int page, String username,
			String nickname, int gender);

	private native String getLocalFriendlist();

	/**
	 * 获取好友列表
	 * 
	 * @return
	 */
	private native int requestFriendlist();

	private native String getLocalBlockedlist();

	/**
	 * 获取黑名单列表
	 * 
	 * @return
	 */
	private native int requestBlockedlist();

	private native int requestAddfriend(String who);

	private native int requestAddblocked(String who);

	private native int removeFriend(String who);

	private native int removebolcked(String who);

	// ----------------------room----------------------------

	/**
	 * 获取聊天室列表
	 * 
	 * @param pageIndex 页码，从0开始
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int requestRoomList(int pageIndex);

	private native String getLocalRoomlist();

	private native int enterRoom(long roomId);

	private native int leaveRoom(long roomId);

	private native boolean supportRealtime(long roomId);

	private native int requestRoomMemberlist(long roomId, int pageIndex);

	private native void clearLocalRoomlist();

	// ---------------------group---------------------------
	private native int createGroup(String groupname, int ownerType,
			boolean needAuth, String groupInfo, String iconPath); // add param

	private native int joinGroup(long groupId);

	private native int leaveGroup(long groupId);

	private native int dismissGroup(long groupId);

	private native int kickoutUser(long groupId, String username);

	private native int requestGrouplist();

	private native int requestModifyGroupinfo(long groupId, String name,
			String info, int type, int need_auth, String imagePath);

	private native int requestGroupMemberlist(long groupId, int pageIndex);

	private native int inviteUserTogroup(String username, long groupId,
			String inviteMessage);
	
    private native int requestJoinGroup(long groupId, String reqMessage);
    
    private native int replyJoinGroup(String username, long group_id, String replyMessage, boolean agree);

	/**
	 * 搜索群
	 * 
	 * @param groupName 要查找的群名，模糊查询
	 * @param pageIndex 页码，从0开始 
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int requestSearchGroup(String groupName, int pageIndex);

	/**
	 * 重置群搜索
	 */
	public native void resetGroupSearch();

	private native String getLocalGroupSearchlist();

	private native String getLocalGroupCurpageSearchlist();

	private native int changeGroupowner(long groupId, String username);

	private native String getLocalGrouplist();

	// --------------------chat-----------------------------
	private native int sendMessage(long msgdbid, byte[] extra, int len);// @

	private native int report(int type, String content, long msgdbid);

	private native int downloadMessage(long msgdbid);
	
	private native String getLocalMessage(String target, int type, boolean more);

	private native String getSessioninfo(String target, int type);//

	private native String sendText(String target, int type, String text,
			byte[] extra, int len);// @

	private native String sendImage(String target, int type, String imagePath,
			byte[] extra, int len);// @

	private native String sendUserData(String target, int type, byte[] data,
			int len, byte[] extra, int elen);// @

	private native String sendFile(String target, int isRoom, String path,
			byte[] extra, int elen);// @

	private native int startTalk(String target, int type, int mode,
			int realtime, int maxDuration);

	private native int decodeMessage(long msgDbid);// @

	/**
	 * 停止录音
	 * 
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int stopTalk();

	

	// -------------------play-----------------------------
	private native int playMessage(long msgdbid);

	/**
	 * 停止播放
	 * 
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int stopPlay();

	// --------------------download-------------------------
	/**
	 * 下载url对应的图片
	 * 
	 * @param url 要下载的url
	 * @return 参见 {@link GotyeStatusCode}
	 */
	public native int downloadMedia(String url);

	public native int downloadAudio(String url);

	/**
	 * 
	 * @return -1  offline, API will reconnect when network becomes valid,<br/>
	 *          0  not login or logout already,</br>
	 *          1  online
	 */
	public int getOnLineState() {
		return isOnLine();
	}

	/**
	 * 判断是否已经进入该聊天室
	 * 
	 * @param room
	 * @return
	 */
	public boolean isInRoom(GotyeRoom room) {
		long result = inRoom(room.getRoomID());
		return result != 0;
	}

	// -------------------------------------------------------------------

	/**
	 * 判断是否设置了新消息提醒
	 * 
	 * @return
	 */

	public boolean isNewMsgNotify() {
		return newMsgNotify;
	}

	/**
	 * 设置群消息免打扰
	 */
	public void setGroupDontdisturb(long groupId) {
		if (disturbGroupIds == null) {
			disturbGroupIds = new ArrayList<Long>();
		}
		if (!disturbGroupIds.contains(groupId)) {
			disturbGroupIds.add(groupId);
			String dontdisturbIds = spf.getString("groupDontdisturb", null);
			if (dontdisturbIds == null) {
				spf.edit()
						.putString("groupDontdisturb", String.valueOf(groupId))
						.commit();
			} else {
				dontdisturbIds += "," + String.valueOf(groupId);
				spf.edit().putString("groupDontdisturb", dontdisturbIds)
						.commit();
			}

		}
	}

	/**
	 * 移除群消息免打扰
	 * 
	 * @param groupId
	 */
	public void removeGroupDontdisturb(long groupId) {
		if (disturbGroupIds == null) {
			return;
		} else {
			disturbGroupIds.remove(groupId);
		}
	}

	/**
	 * 获取当前登陆用户
	 * 
	 * @return
	 */
	public GotyeUser getCurrentLoginUser() {
		return currentLoginUser == null ? currentLoginUser = GotyeUser
				.jsonToUser(getLoginuser()) : currentLoginUser;
	}

	/**
	 * 判断是否设置群消息免打扰
	 * 
	 * @param groupId
	 * @return
	 */
	public boolean isGroupDontdisturb(long groupId) {
		if (disturbGroupIds == null) {
			String dontdisturbIds = spf.getString("groupDontdisturb", null);
			if (dontdisturbIds == null) {
				return false;
			} else {
				disturbGroupIds = new ArrayList<Long>();
				String ids[] = dontdisturbIds.split(",");
				for (String id : ids) {
					disturbGroupIds.add(Long.parseLong(id));
				}
				return disturbGroupIds.contains(groupId);
			}
		} else {
			return disturbGroupIds.contains(groupId);
		}
	}

	/**
	 * 设置新消息是否提醒
	 * 
	 * @param newMsgNotify
	 */
	public void setNewMsgNotify(boolean newMsgNotify) {
		this.newMsgNotify = newMsgNotify;
		spf.edit()
				.putBoolean("new_msg_notify_" + currentLoginUser.name,
						newMsgNotify).commit();
	}

	/**
	 * 判断是否接收群消息
	 */
	public boolean isNotReceiveGroupMsg() {
		return notReceiveGroupMsg;
	}

	/**
	 * 设置是否接收群消息
	 * 
	 * @param notReceiveGroupMsg
	 */
	public void setNotReceiveGroupMsg(boolean notReceiveGroupMsg) {
		this.notReceiveGroupMsg = notReceiveGroupMsg;
		spf.edit()
				.putBoolean("not_receive_group_msg_" + currentLoginUser.name,
						notReceiveGroupMsg).commit();
	}

	/**
	 * 设置群是否免打扰
	 * 
	 * @param groupId
	 * @param disturb
	 */
	public void setDisturb(long groupId, boolean disturb) {
		spf.edit().putBoolean(String.valueOf(groupId), disturb).commit();
	}

	// -------------------------------------------------------------------

	/**
	 * 获取会话列表
	 * 
	 * @return
	 */
	public List<GotyeChatTarget> getSessionList() {
		List<GotyeChatTarget> sessionList = null;
		try {
			String aa = getSessionlist();
			JSONArray array = new JSONArray(aa);
			int len = array.length();
			if (len > 0) {
				sessionList = new ArrayList<GotyeChatTarget>();
				for (int i = 0; i < len; i++) {
					sessionList
							.add(Utils.jsonToSession(array.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sessionList;
	}

	/**
	 * 获取对应Target的最后一条消息
	 * 
	 * @param target
	 * @return
	 */
	public GotyeMessage getLastMessage(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			return GotyeMessage.jsonToMessage(getLastMessage(target.name,
					target.type.ordinal()));
		} else {
			return GotyeMessage.jsonToMessage(getLastMessage(
					String.valueOf(target.Id), target.type.ordinal()));
		}
	}

	/**
	 * 获取对应Target的历史消息记录
	 * 
	 * @param target
	 * @param more
	 *            是否去服务器请求更多（针对聊天室）
	 * @return
	 */
	public List<GotyeMessage> getLocalMessages(GotyeChatTarget target,
			boolean more) {
		String messagesJson;
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			messagesJson = getLocalMessage(target.name, target.type.ordinal(),
					more);
		} else {
			messagesJson = getLocalMessage(String.valueOf(target.Id),
					target.type.ordinal(), more);
		}

		if (messagesJson == null || messagesJson.length() == 0) {
			return null;
		}
		try {
			JSONArray array = new JSONArray(messagesJson);
			int len = array.length();
			if (len > 0) {
				List<GotyeMessage> messages = new ArrayList<GotyeMessage>();
				for (int i = 0; i < len; i++) {
					messages.add(GotyeMessage.jsonToMessage(array
							.getJSONObject(i)));
				}
				return messages;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取对应Target的未读消息个数
	 * 
	 * @param target
	 * @return
	 */
	public int getUnreadMsgcounts(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			return getUnreadMsgcount(target.name, target.type.ordinal());
		} else {
			return getUnreadMsgcount(String.valueOf(target.Id),
					target.type.ordinal());
		}
	}

	public int getTotalUnreadMsgCount() {
		return getTotalUnreadMsgcount();
	}

	/**
	 * 标记对应Target的所有消息为已读
	 */
	public void markMeeagesAsread(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			markMessagesAsread(target.name, target.type.ordinal(), true);
		} else {
			markMessagesAsread(String.valueOf(target.Id), target.type.ordinal(),
					true);
		}
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 标记对应消息为已读
	 * 
	 * @param message
	 */
	public void markSingleMessageAsRead(GotyeMessage message) {
		markSingleMessageAsRead(message.getDbId(), true);
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 删除消息
	 * 
	 * @param message
	 */
	public void deleteMessage(GotyeMessage message) {
		deleteMessage(message.getDbId());
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 清除对应Target所有消息
	 * 
	 * @param target
	 */
	public void clearMessages(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			clearMessages(target.name, target.type.ordinal());
		} else {
			clearMessages(String.valueOf(target.Id), target.type.ordinal());
		}
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 获取对应消息类型的所有未读个数
	 * 
	 * @param type
	 * @return
	 */
	public int getUnreadMsgcountByType(GotyeMessageType type) {
		return getUnreadMsgcountByType(type.ordinal());
	}

	/**
	 * 激活对应的会话session（这样收到的对应该Target的消息自动标记为已读）
	 * 
	 * @param target
	 */
	public void activeSession(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			activeSession(target.name, target.type.ordinal());
		} else {
			activeSession(String.valueOf(target.Id), target.type.ordinal());
		}
	}

	/**
	 * 设置指定通知为已读
	 * 
	 * @param nofity
	 */
	public void markNotifyIsread(GotyeNotify nofity) {
		markNotifyIsread(nofity.getDbID(), true);
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 删除通知
	 * 
	 * @param nofity
	 */
	public void deleteNotify(GotyeNotify nofity) {
		deleteNotify(nofity.getDbID());
	}

	public int getUnreadNotifyCount() {
		return getUnreadNotifycount();
	}

	/**
	 * 取消激活会话
	 * 
	 * @param target
	 */
	public void deactiveSession(GotyeChatTarget target) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			deactiveSession(target.name, target.type.ordinal());
		} else {
			deactiveSession(String.valueOf(target.Id), target.type.ordinal());
		}
	}

	/**
	 * 设置会话置顶
	 * 
	 * @param target
	 * @param markTop
	 */
	public void markSessionTop(GotyeChatTarget target, boolean markTop) {
		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			markSessionTop(target.name, target.type.ordinal(), markTop);
		} else {
			markSessionTop(String.valueOf(target.Id), target.type.ordinal(),
					markTop);
		}
		// session 变动
		sessionStateChanged();
	}

	/**
	 * 获取通知列表
	 * 
	 * @return
	 */
	public List<GotyeNotify> getNotifyList() {
		String notifyJsons = getNotifylist();
		List<GotyeNotify> notifiyList = null;
		try {
			JSONArray notifies = new JSONArray(notifyJsons);
			int len = notifies.length();
			if (len > 0) {
				notifiyList = new ArrayList<GotyeNotify>();
				for (int i = 0; i < len; i++) {
					notifiyList.add(GotyeNotify.jsonToNotify(notifies
							.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notifiyList;
	}

	/**
	 * 获取本地缓存好友列表
	 * 
	 * @return
	 */
	public List<GotyeUser> getLocalFriendList() {
		String json = getLocalFriendlist();
		List<GotyeUser> users = null;
		try {
			JSONArray array = new JSONArray(json);
			int len = array.length();
			if (len > 0) {
				users = new ArrayList<GotyeUser>();
				for (int i = 0; i < len; i++) {
					users.add(GotyeUser.jsonToUser(array.getJSONObject(i)));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public int requestFriendList(){
		return requestFriendlist();
	}

	public List<GotyeUser> getLocalBlockedList() {
		String json = getLocalBlockedlist();
		List<GotyeUser> users = null;
		try {
			JSONArray array = new JSONArray(json);
			int len = array.length();
			if (len > 0) {
				users = new ArrayList<GotyeUser>();
				for (int i = 0; i < len; i++) {
					users.add(GotyeUser.jsonToUser(array.getJSONObject(i)));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return users;
	}

	
	public List<GotyeGroup> getLocalGroupSearchList() {
		String jsonGroups = getLocalGroupSearchlist();
		if (jsonGroups != null && jsonGroups.length() > 0) {
			try {
				JSONArray groups = new JSONArray(jsonGroups);
				int len = groups.length();
				if (len > 0) {
					List<GotyeGroup> groupList = new ArrayList<GotyeGroup>();
					for (int i = 0; i < len; i++) {
						groupList.add(GotyeGroup.createGroupJson(groups
								.getJSONObject(i)));
					}
					return groupList;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
	public List<GotyeGroup> getLocalGroupCurpageSearchList() {
		String jsonGroups = getLocalGroupCurpageSearchlist();
		if (jsonGroups != null && jsonGroups.length() > 0) {
			try {
				JSONArray groups = new JSONArray(jsonGroups);
				int len = groups.length();
				if (len > 0) {
					List<GotyeGroup> groupList = new ArrayList<GotyeGroup>();
					for (int i = 0; i < len; i++) {
						groupList.add(GotyeGroup.createGroupJson(groups
								.getJSONObject(i)));
					}
					return groupList;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
	/**
	 * 获取本地缓存群列表
	 * 
	 * @return
	 */
	public List<GotyeGroup> getLocalGroupList() {
		String jsonGroups = getLocalGrouplist();
		if (jsonGroups != null && jsonGroups.length() > 0) {
			try {
				JSONArray groups = new JSONArray(jsonGroups);
				int len = groups.length();
				if (len > 0) {
					List<GotyeGroup> groupList = new ArrayList<GotyeGroup>();
					for (int i = 0; i < len; i++) {
						groupList.add(GotyeGroup.createGroupJson(groups
								.getJSONObject(i)));
					}
					return groupList;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @return
	 */
	public int sendMessage(GotyeMessage message) {
		byte[] extraData = message.getExtraData();
		int len = extraData == null ? 0 : extraData.length;
		if (message.getDbId() > 0) {
			return sendMessage(message.getDbId(), extraData, len);
		} else {
			String jsonMessage = null;
			GotyeChatTargetType receiverType = message.getReceiver().type;
			if (message.getType() == GotyeMessageType.GotyeMessageTypeText) {
				if (receiverType == GotyeChatTargetType.GotyeChatTargetTypeUser) {
					jsonMessage = sendText(message.getReceiver().name,
							message.getReceiver().type.ordinal(),
							message.getText(), extraData, len);
				} else {
					String tar = String.valueOf(message.getReceiver().Id);
					jsonMessage = sendText(tar,
							message.getReceiver().type.ordinal(),
							message.getText(), extraData, len);
				}
			} else if (message.getType() == GotyeMessageType.GotyeMessageTypeImage) {
				if (receiverType == GotyeChatTargetType.GotyeChatTargetTypeUser) {
					jsonMessage = sendImage(message.getReceiver().name,
							message.getReceiver().type.ordinal(), message
									.getMedia().getPath_ex(), extraData, len);
				} else {
					String tar = String.valueOf(message.getReceiver().Id);
					jsonMessage = sendImage(tar,
							message.getReceiver().type.ordinal(), message
									.getMedia().getPath_ex(), extraData, len);
				}
			} else if (message.getType() == GotyeMessageType.GotyeMessageTypeUserData) {

				if (receiverType == GotyeChatTargetType.GotyeChatTargetTypeUser) {
					if (message.getMedia() != null) {
						jsonMessage = sendFile(message.getReceiver().name,
								message.getReceiver().type.ordinal(), message
										.getMedia().getPath(), extraData, len);
					} else {
						byte[] userData = message.getUserData();
						if (userData == null) {
							throw new NullPointerException("userData is null");
						}
						jsonMessage = sendUserData(message.getReceiver().name,
								message.getReceiver().type.ordinal(), userData,
								userData.length, extraData, len);
					}
				} else {
					String tar = String.valueOf(message.getReceiver().Id);
					if (message.getMedia() != null) {
						jsonMessage = sendFile(tar,
								message.getReceiver().type.ordinal(), message
										.getMedia().getPath(), extraData, len);
					} else {
						byte[] userData = message.getUserData();
						if (userData == null) {
							throw new NullPointerException("userData is null");
						}
						jsonMessage = sendUserData(tar,
								message.getReceiver().type.ordinal(), userData,
								userData.length, extraData, len);
					}
				}
			}
			try {
				JSONObject obj = new JSONObject(jsonMessage);
				int code = obj.getInt("code");
				GotyeMessage temp = GotyeMessage.jsonToMessage(obj
						.getJSONObject("message"));
				message.setDbId(temp.getDbId());
				message.setMedia(temp.getMedia());
				message.setStatus(GotyeMessage.STATUS_SENDING);
				return code;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
	}

	 

	/**
	 * 修改用户信息
	 * 
	 * @param nickname
	 * @param gender
	 * @param info
	 * @param path
	 * @return
	 */
	public int requestModifyUserInfo(GotyeUser forModify, String path) {
		int result = modifyUserinfo(forModify.getNickname(), forModify
				.getGender().ordinal(), forModify.getInfo(), path);
		return result;
	}

	public void restUserSearch() {
		restUsersearch();
	}
	
	public List<GotyeUser> getLocalUserSearchList(){
		String userListStr= getLocalUserSearchlist();
		List<GotyeUser> users = null;
		try {
			JSONArray array = new JSONArray(userListStr);
			int len = array.length();
			if (len > 0) {
				users = new ArrayList<GotyeUser>();
				for (int i = 0; i < len; i++) {
					users.add(GotyeUser.jsonToUser(array.getJSONObject(i)));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return users;
	}
	public List<GotyeUser> getLocalUserCurpageSearchList(){
		String userListStr= getLocalUserCurpageSearchlist();
		List<GotyeUser> users = null;
		try {
			JSONArray array = new JSONArray(userListStr);
			int len = array.length();
			if (len > 0) {
				users = new ArrayList<GotyeUser>();
				for (int i = 0; i < len; i++) {
					users.add(GotyeUser.jsonToUser(array.getJSONObject(i)));
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return users;
	}

	public int requestSearchUserList(int page, String username,
			String nickname, int gender) {
		return requestSearchUserlist(page, username, nickname, gender);
	}

	/**
	 * 举报
	 * 
	 * @param type
	 * @param content
	 * @param message
	 * @return
	 */
	public int report(int type, String content, GotyeMessage message) {
		return report(type, content, message.getDbId());
	}

	/**
	 * 播放消息
	 * 
	 * @param message
	 * @return
	 */
	public int playMessage(GotyeMessage message) {
		return playMessage(message.getDbId());
	}

	// ---------------------------------------user

	/**
	 * 获取用户详情
	 * 
	 * @param name
	 * @param forceRequest
	 *            该参数为true时返回当前缓存数据，并从服务器获取最新数据通过回调返回，若为false，当存在缓存时只返回缓存数据，
	 *            若不存在缓存则去服务器请求并通过回调返回
	 * @return 本地缓存数据，若本地没有，返回null
	 */
	public GotyeUser requestUserInfo(String name, boolean forceRequest) {
		String result = getTargetDetail(name,
				GotyeChatTargetType.GotyeChatTargetTypeUser.ordinal(),
				forceRequest);
		return GotyeUser.jsonToUser(result);
	}
    /**
     * 获取黑名单列表
     * @return 请求状态
     */
	public int requestBlockedList() {
		return requestBlockedlist();
	}

	/**
	 * 请求添加好友
	 * 
	 * @param friend 被添加好友对象
	 * @return 请求状态
	 */
	public int requestAddFriend(GotyeUser friend) {
		return requestAddfriend(friend.getName());
	}

	/**
	 * 请求添加为黑名单
	 * 
	 * @param friend
	 * @return 请求状态
	 */
	public int requestAddBlocked(GotyeUser friend) {
		return requestAddblocked(friend.getName());
	}

	/**
	 * 请求删除好友
	 * 
	 * @param friend 被删除的好友对象
	 * @return 请求状态
	 */
	public int removeFriend(GotyeUser friend) {
		return removeFriend(friend.getName());
	}

	/**
	 * 请求删除黑名单
	 * 
	 * @param friend 被从黑名单删除的用户对象
	 * @return 请求状态
	 */
	public int removeBolcked(GotyeUser friend) {
		return removebolcked(friend.getName());
	}

	// ---------------------------------------end user

	// ----------------------------------------room

	/**
	 * 请求获取聊天室成员列表
	 * 
	 * @param room 当前房间
	 * @param pageNumber
	 * @return 请求状态
	 */
	public int requestRoomMemberlist(GotyeRoom room, int pageNumber) {
		return requestRoomMemberlist(room.getRoomID(), pageNumber);
	}

	/**
	 * 清除本地房间信息缓存
	 */
	public void clearLocalRoomList(){
		clearLocalRoomlist();
	}
	
	/**
	 * 创建群
	 * @param group 要创建的群对象
	 * @param groupIconPath 要创建的群头像本地文件路径
	 * @return 请求状态
	 */
	
	public int createGroup(GotyeGroup group,String groupIconPath){
		return createGroup(group.getGroupName(),group.getOwnerType(),group.isNeedAuthentication(),group.getGroupInfo(),groupIconPath);
	}
	
	/**
	 * 获取本地缓存聊天室列表
	 * @return 本地缓存聊天室列表
	 */
	public List<GotyeRoom> getLocalRoomList() {
		String roomJsonStr = getLocalRoomlist();
		if (roomJsonStr != null && roomJsonStr.length() > 0) {
			try {
				JSONArray rooms = new JSONArray(roomJsonStr);
				int len = rooms.length();
				if (len > 0) {
					List<GotyeRoom> roomList = new ArrayList<GotyeRoom>();
					for (int i = 0; i < len; i++) {
						roomList.add(GotyeRoom.createRoomJson(rooms
								.getJSONObject(i)));
					}
					return roomList;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 请求进入聊天室
	 * 
	 * @param room 当前聊天室对象
	 * @return  请求状态
	 */
	public int enterRoom(GotyeRoom room) {
		return enterRoom(room.getRoomID());
	}

	/**
	 * 请求离开聊天室
	 * 
	 * @param room 当前聊天室
	 * @return 请求状态
	 */
	public int leaveRoom(GotyeRoom room) {
		return leaveRoom(room.getRoomID());
	}

	/**
	 * 判断该聊天室是否支持实时语音
	 * 
	 * @param room 当前聊天室
	 * @return 返回该聊天室是否支持实时语音结果
	 */
	public boolean supportRealtime(GotyeRoom room) {
		return supportRealtime(room.getRoomID());
	}

	/**
	 * 获取聊天室详情
	 * 
	 * @param roomId 要获取详情的聊天室id
	 * @param forceRequest
	 *            该参数为true时返回当前缓存数据，并从服务器获取最新数据通过回调返回，若为false，当存在缓存时只返回缓存数据，
	 *            若不存在缓存则去服务器请求并通过回调返回
	 * @return 返回本地缓存的聊天室，可能为null
	 */
	public GotyeRoom requestRoomInfo(long roomId, boolean forceRequest) {
		String result = getTargetDetail(String.valueOf(roomId),
				GotyeChatTargetType.GotyeChatTargetTypeRoom.ordinal(),
				forceRequest);
		return GotyeRoom.createRoomJson(result);
	}

	// ---------------------------------------room end

	// ---------------------------------------group

	/**
	 * 获取群详情
	 * 
	 * @param groupId 要获取群信息的对应群id
	 * @param forceRequest
	 *            该参数为true时返回当前缓存数据，并从服务器获取最新数据通过回调返回，若为false，当存在缓存时只返回缓存数据，
	 *            若不存在缓存则去服务器请求并通过回调返回
	 * @return 返回本地缓存群信息对象
	 */
	public GotyeGroup requestGroupInfo(long groupId, boolean forceRequest) {
		String result = getTargetDetail(String.valueOf(groupId),
				GotyeChatTargetType.GotyeChatTargetTypeGroup.ordinal(),
				forceRequest);
		return GotyeGroup.createGroupJson(result);
	}

/**
 * 请求修改群信息	 
 * @param group 要修改的群对象
 * @param imagePath 新的群图标本地文件路径
 * @return 请求状态
 */
	public int requestModifyGroupInfo(GotyeGroup group, String imagePath) {
		return requestModifyGroupinfo(group.getGroupID(), group.getGroupName(),
				group.getGroupInfo(), group.getOwnerType(),
				group.isNeedAuthentication() ? 1 : 0, imagePath);
	}

	/**
	 * 请求加入群
	 * 
	 * @param group 当前群对象
	 * @return 请求状态
	 */
	public int joinGroup(GotyeGroup group) {
		return joinGroup(group.getGroupID());
	}

	/**
	 * 请求离开群
	 * 
	 * @param group 当前群对象
	 * @return 请求状态
	 */
	public int leaveGroup(GotyeGroup group) {
		return leaveGroup(group.getGroupID());
	}

	/**
	 * 请求解散群
	 * 
	 * @param group 当前群对象
	 * @return 请求状态
	 */
	public int dismissGroup(GotyeGroup group) {
		return dismissGroup(group.getGroupID());
	}

	/**
	 * 请求踢人
	 * 
	 * @param group 当前群对象
	 * @param user 被踢出的群成员对象
	 * @return 请求状态
	 */
	public int kickOutUser(GotyeGroup group, GotyeUser user) {
		return kickoutUser(group.getGroupID(), user.getName());
	}

	/**
	 * 从服务器获取当前登陆用户群列表
	 * @return 请求状态
	 */
	public int requestGroupList() {
		return requestGrouplist();
	}

	/**
	 * 请求获取群成员列表
	 * 
	 * @param group 当前群对象
	 * @param pageIndex 页码（0开始）
	 * @return 请求状态
	 */
	public int requestGroupMemberList(GotyeGroup group, int pageIndex) {
		return requestGroupMemberlist(group.getGroupID(), pageIndex);
	}

	/**
	 * 要求好友入群
	 * 
	 * @param user 被邀请的用户
	 * @param group 当前群对象
	 * @param message 邀请信息
	 * @return 请求状态
	 */
	public int inviteUserToGroup(GotyeUser user, GotyeGroup group,
			String message) {
		return inviteUserTogroup(user.getName(), group.getGroupID(), message);
	}

	
	/**
	 * 请求加入群
	 * @param group 当前群对象
	 * @param reqMessage 请求信息
	 * @return 请求状态
	 */
	public int requestJoinGroup(GotyeGroup group,String reqMessage){
		return requestJoinGroup(group.getGroupID(),reqMessage);
	}
	
	/**
	 * 处理入群申请
	 * @param user 申请人
	 * @param group 当前群
	 * @param agreeMsg 同意或拒绝信息
	 * @param isAgree 是否同意加入
	 * @return 请求状态
	 */
	public int replyJoinGroup(GotyeUser user,GotyeGroup group,String agreeMsg,boolean isAgree){
		return replyJoinGroup(user.getName(),group.getGroupID(),agreeMsg,isAgree);
	}
	/**
	 * 请求变更群主
	 * 
	 * @param group 当前群对象
	 * @param user 请求变更的新群主
	 * @return 请求状态
	 */
	public int changeGroupowner(GotyeGroup group, GotyeUser user) {
		return changeGroupowner(group.getGroupID(), user.getName());
	}

	// ---------------------------------------end group
	// ---------------------------------------talk

	/**
	 * 开始录制语音消息
	 * 
	 * @param target
	 * @param mode
	 *            变声模式
	 * @param realtime
	 *            若为true时表示实时语音
	 * @param maxDuration
	 *            录制的最长时间(ms)
	 * @return 请求状态
	 */
	public int startTalk(GotyeChatTarget target, WhineMode mode,
			boolean realtime, int maxDuration) {
		if (target instanceof GotyeUser) {
			return startTalk(target.name, target.type.ordinal(),
					mode.ordinal(), realtime ? 1 : 0, maxDuration);
		} else {
			String id = String.valueOf(target.Id);
			return startTalk(id, target.type.ordinal(), mode.ordinal(),
					realtime ? 1 : 0, maxDuration);
		} 
	 
	}

	// ---------------------------------------end talk

	/**
	 * 删除会话
	 * 
	 * @param target 会话对象
	 */
	public void deleteSession(GotyeChatTarget target) {

		if (target.type == GotyeChatTargetType.GotyeChatTargetTypeUser) {
			deleteSession(target.name, target.type.ordinal());
		} else {
			deleteSession(String.valueOf(target.Id), target.type.ordinal());
		}
		// session 变动
		sessionStateChanged();
		// GotyeNotifyManager.getInstance().onNotifyStateChanged();
	}

	/**
	 * 下载消息
	 * 
	 * @param message 被下载的消息对象
	 * @return 请求状态
	 */
	public int downloadMessage(GotyeMessage message) {
		if(message.getDbId()<=0){
			return GotyeStatusCode.CODE_UNKNOW_ERROR;
		}
		return downloadMessage(message.getDbId());
	}

	// 会话列表变更通知
	private void sessionStateChanged() {
		for (GotyeListener listener : listeners) {
			if (listener != null && listener instanceof NotifyListener) {
				((NotifyListener) listener).onNotifyStateChanged();
			}
		}
	}

	/**
	 * 反编译音频消息（只能处理语音类型消息）
	 * @param meessage 当前语音类型消息对象
	 * @return 请求状态
	 */
	public int decodeMessage(GotyeMessage message) {
		return decodeMessage(message.getDbId());
	}

	/**
	 * 启动或关闭敏感词过滤
	 * @param type 聊天类型
	 * @param enabled 是否开启
	 */
	public void enableTextFilter(GotyeChatTargetType type, boolean enabled){
		enableTextFilter(type.ordinal(),enabled?1:0);
	}
	static {
		System.loadLibrary("gotye");
		System.loadLibrary("gotyeapi");
	}

	static void dispatchEvent(int eventCode, String json) {
		DispathEvent.dispatchEvent(eventCode, json);
	}

}
