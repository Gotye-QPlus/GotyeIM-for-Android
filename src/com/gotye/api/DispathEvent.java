package com.gotye.api;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DispathEvent {
	/**
	 * 事件包装
	 * @param eventCode 事件code
	 * @param json 参数
	 */
	public static void dispatchEvent(int eventCode, String json) {
		Log.d("gotye", "dispatchEvent(ecode:" + eventCode + ", args:" + json
				+ ")");
		System.out.println("dispatchEvent(ecode:" + eventCode + ", args:"
				+ json + ")");

		GotyeEventCode code = GotyeEventCode.values()[eventCode];

		switch (code) {
		case GotyeEventCodeLogin:
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser User = GotyeUser.jsonToUser(obj);

				Log.d("JSON", "mCode = " + mCode);
				DispatchListener.onListener(code, mCode, User);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodeLogout:
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				DispatchListener.onListener(code, mCode);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeGetProfile: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser user = GotyeUser.jsonToUser(obj);

				DispatchListener.onListener(code, mCode, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeGetUserInfo:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				JSONObject obj = Object.getJSONObject("user");
				GotyeUser mUser = GotyeUser.jsonToUser(obj);
				DispatchListener.onListener(code, mCode, mUser);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodeModifyUserInfo: {
			JSONObject Object;
			try {
				Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				GotyeUser newUser = GotyeUser.jsonToUser(Object
						.getJSONObject("user"));
				DispatchListener.onListener(code, mCode, newUser);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeGetFriendList: {
			List<GotyeUser> mList = null;
			try {

				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				try {
					JSONArray Array = Object.getJSONArray("friendlist");
					if (Array != null) {
						int len = Array.length();
						if (len > 0) {
							mList = new ArrayList<GotyeUser>();
							for (int i = 0; i < len; i++) {
								mList.add(GotyeUser.jsonToUser(Array
										.getJSONObject(i)));
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				DispatchListener.onListener(code, mCode, mList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeGetBlockedList: {
			List<GotyeUser> mList = null;
			try {

				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				try {
					JSONArray Array = Object.getJSONArray("blockedlist");
					if (Array != null) {
						int len = Array.length();
						if (len > 0) {
							mList = new ArrayList<GotyeUser>();
							for (int i = 0; i < len; i++) {
								mList.add(GotyeUser.jsonToUser(Array
										.getJSONObject(i)));
							}

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				DispatchListener.onListener(code, mCode, mList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeSearchUserList: {
			List<GotyeUser> mList = null;
			try {
				mList = new ArrayList<GotyeUser>();
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				int mPagerIndex = Object.getInt("pageIndex");
				JSONArray Array = Object.getJSONArray("allList");
				if (Array != null) {
					int len = Array.length();
					for (int i = 0; i < len; i++) {
						GotyeUser user = GotyeUser.jsonToUser(Array
								.getJSONObject(i));
						mList.add(user);
					}
				}

				DispatchListener.onListener(code, mCode, mList, mPagerIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;

		case GotyeEventCodeAddFriend: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser user = GotyeUser.jsonToUser(obj);

				DispatchListener.onListener(code, mCode, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeAddBlocked: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser user = GotyeUser.jsonToUser(obj);

				DispatchListener.onListener(code, mCode, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeRemoveFriend: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser user = GotyeUser.jsonToUser(obj);

				DispatchListener.onListener(code, mCode, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeRemoveBlocked: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				JSONObject obj = object.getJSONObject("user");
				GotyeUser user = GotyeUser.jsonToUser(obj);

				DispatchListener.onListener(code, mCode, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeGetRoomList: {
			List<GotyeRoom> mListRoom = null;
			try {

				JSONObject object = new JSONObject(json);
				int mCodee = object.getInt("code");
				int mPageIndex = object.getInt("pageIndex");
				try {
					JSONArray Array = object.getJSONArray("allRoomList");
					if (Array != null) {
						int len = Array.length();
						if (len > 0) {
							mListRoom = new ArrayList<GotyeRoom>();
							for (int i = 0; i < len; i++) {
								GotyeRoom gotyeRoom = GotyeRoom
										.createRoomJson(Array.getJSONObject(i));
								mListRoom.add(gotyeRoom);
							}
						}

					}
				} catch (JSONException e) {
					// e.printStackTrace();
				}

				DispatchListener.onListener(code, mCodee, mListRoom);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeEnterRoom: {
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				// long mLastMsgID = object.getLong("lastMsgID");
				// long mRoomID = object.getLong("roomID");
				JSONObject obj = object.getJSONObject("room");
				GotyeRoom room = GotyeRoom.createRoomJson(obj);

				DispatchListener.onListener(code, mCod, 0l, room);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeLeaveRoom:
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				JSONObject obj = object.getJSONObject("room");
				GotyeRoom room = GotyeRoom.createRoomJson(obj);

				DispatchListener.onListener(code, mCod, room);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeGetUserList:
			try {

				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				int pageIndex = object.getInt("pageIndex");
				JSONArray allMemberList = object.getJSONArray("allMemberList");
				int len = allMemberList.length();
				List<GotyeUser> totlaMembers = new ArrayList<GotyeUser>();
				if (len > 0) {
					for (int i = 0; i < len; i++) {
						totlaMembers.add(GotyeUser.jsonToUser(allMemberList
								.getJSONObject(i)));
					}
				}

				JSONArray curPageMemberList = object
						.getJSONArray("curPageMemberList");
				len = curPageMemberList.length();
				List<GotyeUser> curPageMembers = new ArrayList<GotyeUser>();
				if (len > 0) {
					for (int i = 0; i < len; i++) {
						curPageMembers
								.add(GotyeUser.jsonToUser(curPageMemberList
										.getJSONObject(i)));
					}
				}
				GotyeRoom room = GotyeRoom.createRoomJson(object
						.getJSONObject("room"));
				DispatchListener.onListener(code, mCod, room, totlaMembers, curPageMembers,
						pageIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeGetHistoryMessageList:
			List<GotyeMessage> mList = null;
			try {
				mList = new ArrayList<GotyeMessage>();
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				try {
					JSONArray Array = object.getJSONArray("msglist");
					if (Array != null) {
						int len = Array.length();
						for (int i = 0; i < len; i++) {
							GotyeMessage gMsg = GotyeMessage
									.jsonToMessage(Array.getJSONObject(i));

							mList.add(gMsg);
						}
					}
				} catch (JSONException e) {
					// e.printStackTrace();
				}

				DispatchListener.onListener(code, mCod, mList);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeReleaseMessage:
			break;

		case GotyeEventCodeSearchGroup: {
			List<GotyeGroup> allList = null;
			List<GotyeGroup> curList = null;

			try {
				allList = new ArrayList<GotyeGroup>();
				curList = new ArrayList<GotyeGroup>();
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				int mPagerIndex = object.getInt("pageIndex");

				JSONArray array = object.getJSONArray("allList");
				int len = array.length();
				for (int i = 0; i < len; i++) {
					allList.add(GotyeGroup.createGroupJson(array
							.getJSONObject(i)));
				}
				JSONArray array2 = object.getJSONArray("curPageList");
				int lenn = array.length();
				for (int i = 0; i < lenn; i++) {
					curList.add(GotyeGroup.createGroupJson(array2
							.getJSONObject(i)));
				}
				DispatchListener.onListener(code, mCode, allList, curList, mPagerIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeCreateGroup: {
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				JSONObject obje = object.getJSONObject("group");
				GotyeGroup group = GotyeGroup.createGroupJson(obje);

				DispatchListener.onListener(code, mCod, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;

		case GotyeEventCodeJoinGroup: {
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				// long mGroupId = object.getLong("groupID");
				JSONObject obj = object.getJSONObject("group");
				GotyeGroup group = GotyeGroup.createGroupJson(obj);

				DispatchListener.onListener(code, mCod, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;

		case GotyeEventCodeLeaveGroup:
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				DispatchListener.onListener(code, mCod, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodeDismissGroup: {
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				GotyeGroup group = GotyeGroup.createGroupJson(object.getJSONObject("group"));
				DispatchListener.onListener(code, mCod, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

			break;

		case GotyeEventCodeKickoutUser: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				GotyeGroup group= GotyeGroup.createGroupJson(object.getJSONObject("group"));

				DispatchListener.onListener(code, mCode, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

			break;

		case GotyeEventCodeChangeGroupOwner:
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				DispatchListener.onListener(code, mCode, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case GotyeEventCodeUserJoinGroup:{
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				GotyeUser user = GotyeUser.jsonToUser(object.
						getJSONObject("user"));
				DispatchListener.onListener(code, mCode, group, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
			break;
			
		case GotyeEventCodeUserLeaveGroup:{
			try {
				JSONObject object = new JSONObject(json);
				int mCode = 0;
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				GotyeUser user = GotyeUser.jsonToUser(object.
						getJSONObject("user"));
				DispatchListener.onListener(code, mCode, group, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;
			
		case GotyeEventCodeUserDismissGroup:{
			try {
				JSONObject object = new JSONObject(json);
				int mCode = 0;
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				GotyeUser user = GotyeUser.jsonToUser(object.
						getJSONObject("user"));
				DispatchListener.onListener(code, mCode, group, user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;
			
		case GotyeEventCodeUserKickedFromGroup:{
			try {
				JSONObject object = new JSONObject(json);
				int mCode = 0;
				GotyeGroup group = GotyeGroup.createGroupJson(object
						.getJSONObject("group"));
				GotyeUser kicked = GotyeUser.jsonToUser(object.
						getJSONObject("kicked"));
				GotyeUser actor = GotyeUser.jsonToUser(object.getJSONObject("actor"));
				DispatchListener.onListener(code, mCode, group, kicked, actor);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeGetGroupList: {
			List<GotyeGroup> groupList = null;
			try {
				groupList = new ArrayList<GotyeGroup>();
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				// int mPageIndex = Object.getInt("pageIndex");
				try {
					JSONArray Array = Object.getJSONArray("grouplist");
					if (Array != null) {
						int len = Array.length();
						for (int i = 0; i < len; i++) {
							GotyeGroup group = GotyeGroup.createGroupJson(Array
									.getJSONObject(i));
							groupList.add(group);
						}
					}
				} catch (JSONException e) {
					// e.printStackTrace();
				}

				DispatchListener.onListener(code, mCode, groupList, 0);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;

		case GotyeEventCodeGetGroupInfo: {
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				JSONObject object = Object.getJSONObject("group");
				GotyeGroup group = GotyeGroup.createGroupJson(object);

				DispatchListener.onListener(code, mCode, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;
		case GotyeEventCodeModifyGroupInfo:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				JSONObject object = Object.getJSONObject("group");
				GotyeGroup group = GotyeGroup.createGroupJson(object);

				DispatchListener.onListener(code, mCode, group);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case GotyeEventCodeGetGroupUserList: {
			List<GotyeUser> allList = null;
			List<GotyeUser> curList = null;
			GotyeGroup group = null;
			try {
				allList = new ArrayList<GotyeUser>();
				curList = new ArrayList<GotyeUser>();
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				int mPagerIndex = Object.getInt("pageIndex");

				try {
					JSONArray array = Object.getJSONArray("allMemberList");
					int len = array.length();
					for (int i = 0; i < len; i++) {
						GotyeUser user = GotyeUser.jsonToUser(array
								.getJSONObject(i));
						allList.add(user);
					}
					JSONArray array2 = Object.getJSONArray("curPageMemberList");
					int lenn = array2.length();
					for (int j = 0; j < lenn; j++) {
						GotyeUser user = GotyeUser.jsonToUser(array2
								.getJSONObject(j));
						curList.add(user);
					}

					JSONObject object = Object.getJSONObject("group");
					group = GotyeGroup.createGroupJson(object);

				} catch (JSONException e) {
					// TODO: handle exception
				}

				DispatchListener.onListener(code, mCode, allList, curList, group, mPagerIndex);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeReceiveNotify: {
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				 GotyeNotify notify=GotyeNotify.jsonToNotify(Object.getJSONObject("notify"));
				DispatchListener.onListener(code, mCode,notify);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
			break;

		case GotyeEventCodeGetOfflineMessageList: {
			ArrayList<GotyeMessage> mLists = null;
			try {
				mLists = new ArrayList<GotyeMessage>();
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				try {
					JSONArray Array = Object.getJSONArray("mgslist");
					if (Array != null) {
						for (int i = 0; i < Array.length(); i++) {
							GotyeMessage message = GotyeMessage
									.jsonToMessage(Object);
							mLists.add(message);
						}
					}
				} catch (JSONException e) {
					// e.printStackTrace();
				}
				DispatchListener.onListener(code, mCode, mLists);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

			break;

		case GotyeEventCodeSendMessage:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				JSONObject objcet = Object.getJSONObject("message");
				GotyeMessage message = GotyeMessage.jsonToMessage(objcet);

				DispatchListener.onListener(code, mCode, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeReceiveMessage: {
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				GotyeMessage message = GotyeMessage.jsonToMessage(Object
						.getJSONObject("message"));
				Log.d("ListenerBack", "1111111111111111");
				DispatchListener.onListener(code, mCode, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodeDownloadMessage:
			try {
				JSONObject Object1 = new JSONObject(json);
				int mCode1 = Object1.getInt("code");
				GotyeMessage message = GotyeMessage.jsonToMessage(Object1.getJSONObject("message"));

				DispatchListener.onListener(code, mCode1, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeStartTalk:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				boolean mIsReal = Object.getBoolean("is_realtime");
				int mTarType = Object.getInt("target_type");
				GotyeChatTarget mTarget = new GotyeChatTarget();
				if (mTarType == 0) {
					mTarget.name = Object.getString("target");
				} else if (mTarType == 1) {
					mTarget.Id = Object.getInt("target");
				} else if (mTarType == 2) {
					mTarget.Id = Object.getInt("target");
				}

				DispatchListener.onListener(code, mCode, mIsReal, mTarType, mTarget);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeStopTalk:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				boolean isVoiceRealTime=Object.getBoolean("realtime");
				if (mCode == 0) {
					if (isVoiceRealTime == false) {
						JSONObject obj = Object.getJSONObject("message");
						GotyeMessage message = GotyeMessage.jsonToMessage(obj);

						DispatchListener.onListener(code, mCode, message, isVoiceRealTime);
					} else {
						DispatchListener.onListener(code, mCode, null, isVoiceRealTime);
					}
				} else {
					DispatchListener.onListener(code, mCode, null, null);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodeDownloadMedia: {
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				String mPath = Object.getString("path");
				String mUrl = Object.getString("url");

				DispatchListener.onListener(code, mCode, mPath, mUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodePlayStart:
			try {
				JSONObject Object1 = new JSONObject(json);
				int mCode1 = Object1.getInt("code");
				// isRealTime = Object1.getBoolean("is_realtime");
				if (mCode1 == 0) {
					// if (isRealTime == false) {
					GotyeMessage message = GotyeMessage.jsonToMessage(Object1);
					DispatchListener.onListener(code, mCode1, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case GotyeEventCodeRealPlayStart:
			try {
				JSONObject Object1 = new JSONObject(json);
				int mCode1 = Object1.getInt("code");
				GotyeRoom romom = GotyeRoom.createRoomJson(Object1
						.getJSONObject("room"));
				GotyeUser speaker = GotyeUser.jsonToUser(Object1
						.getJSONObject("speaker"));
				if (mCode1 == 0) {
					DispatchListener.onListener(code, mCode1, romom.getRoomID(),
							speaker.getName());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodePlaying: {
			try {
				JSONObject object = new JSONObject(json);
				int mCode = object.getInt("code");
				int mPosition = object.getInt("position");

				DispatchListener.onListener(code, mCode, mPosition);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;

		case GotyeEventCodePlayStop:
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");

				DispatchListener.onListener(code, mCod);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;

		case GotyeEventCodeReport:
			try {
				JSONObject object = new JSONObject(json);
				int mCod = object.getInt("code");
				GotyeMessage message = GotyeMessage.jsonToMessage(object);

				DispatchListener.onListener(code, mCod, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case GotyeEventCodeDecodeFinished:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				JSONObject objcet = Object.getJSONObject("message");
				GotyeMessage message = GotyeMessage.jsonToMessage(objcet);

				DispatchListener.onListener(code, mCode, message);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
			
		case GotyeEventCodeSendNotify:
			try {
				JSONObject Object = new JSONObject(json);
				int mCode = Object.getInt("code");
				GotyeNotify notify=GotyeNotify.jsonToNotify(Object.getJSONObject("notify"));
				DispatchListener.onListener(code, mCode,notify);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}

	}

}
