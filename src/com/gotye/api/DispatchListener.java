package com.gotye.api;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件分发
 * @author Administrator
 *
 */
public class DispatchListener {

	static void onListener(GotyeEventCode code, int statusCode, Object... param) {
		GotyeDelegate mListener = GotyeAPI.getInstance().mListener;
		switch (code) {
		case GotyeEventCodeLogin:
			if (mListener != null) {
				mListener.onLogin(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeLogout:
			if (mListener != null) {
				mListener.onLogout(statusCode);
			}
			break;

		case GotyeEventCodeGetProfile:
			if (mListener != null) {
				mListener.onGetProfile(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeGetUserInfo:
			if (mListener != null) {
				mListener.onGetUserInfo(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeModifyUserInfo:
			if (mListener != null) {
				mListener.onModifyUserInfo(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeGetFriendList:
			if (mListener != null) {
				mListener.onGetFriendList(statusCode,
						(List<GotyeUser>) param[0]);
			}
			break;

		case GotyeEventCodeGetBlockedList:
			if (mListener != null) {
				mListener.onGetBlockedList(statusCode,
						(List<GotyeUser>) param[0]);
			}
			break;

		case GotyeEventCodeSearchUserList:
			if (mListener != null) {
				mListener.onSearchUserList(statusCode,
						(List<GotyeUser>) param[0], (Integer) param[1]);
			}
			break;

		case GotyeEventCodeAddFriend:
			if (mListener != null) {
				mListener.onAddFriend(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeAddBlocked:
			if (mListener != null) {
				mListener.onAddBlocked(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeRemoveFriend:
			if (mListener != null) {
				mListener.onRemoveFriend(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeRemoveBlocked:
			if (mListener != null) {
				mListener.onRemoveBlocked(statusCode, (GotyeUser) param[0]);
			}
			break;

		case GotyeEventCodeGetRoomList:
			if (mListener != null) {
				mListener.onGetRoomList(statusCode, (List<GotyeRoom>) param[0]);
			}
			break;

		case GotyeEventCodeEnterRoom:
			if (mListener != null) {
				mListener.onEnterRoom(statusCode, (Long) param[0],
						(GotyeRoom) param[1]);
			}
			break;

		case GotyeEventCodeLeaveRoom:
			if (mListener != null) {
				mListener.onLeaveRoom(statusCode, (GotyeRoom) param[0]);
			}
			break;

		case GotyeEventCodeGetUserList:
			if (mListener != null) {
				mListener.onGetRoomUserList(statusCode, (GotyeRoom) param[0],
						(ArrayList<GotyeUser>) param[1],
						(ArrayList<GotyeUser>) param[2], (Integer) param[3]);
			}
			break;

		case GotyeEventCodeGetHistoryMessageList:

			if (mListener != null) {
				mListener.onGetHistoryMessageList(statusCode,
						(List<GotyeMessage>) param[0]);
			}
			break;

		case GotyeEventCodeReleaseMessage:
			break;

		case GotyeEventCodeSearchGroup:
			if (mListener != null) {
				mListener.onSearchGroupList(statusCode,
						(List<GotyeGroup>) param[0],
						(List<GotyeGroup>) param[1], (Integer) param[2]);
			}
			break;

		case GotyeEventCodeCreateGroup:
			if (mListener != null) {
				mListener.onCreateGroup(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeJoinGroup:
			if (mListener != null) {
				mListener.onJoinGroup(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeLeaveGroup:
			if (mListener != null) {
				mListener.onLeaveGroup(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeDismissGroup:
			if (mListener != null) {
				mListener.onDismissGroup(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeKickoutUser:
			if (mListener != null) {
				mListener.onKickOutUser(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeChangeGroupOwner:
			if (mListener != null) {
				mListener.onChangeGroupOwner(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeUserJoinGroup:
			if (mListener != null) {
				mListener.onUserJoinGroup((GotyeGroup) param[0],
						(GotyeUser) param[1]);
			}
			break;

		case GotyeEventCodeUserLeaveGroup:
			if (mListener != null) {
				mListener.onUserLeaveGroup((GotyeGroup) param[0],
						(GotyeUser) param[1]);
			}
			break;

		case GotyeEventCodeUserDismissGroup:
			if (mListener != null) {
				mListener.onUserDismissGroup((GotyeGroup) param[0],
						(GotyeUser) param[1]);
			}
			break;

		case GotyeEventCodeUserKickedFromGroup:
			if (mListener != null) {
				mListener.onUserKickdFromGroup((GotyeGroup) param[0],
						(GotyeUser) param[1], (GotyeUser) param[2]);
			}
			break;

		case GotyeEventCodeGetGroupList:
			if (mListener != null) {
				mListener.onGetGroupList(statusCode,
						(List<GotyeGroup>) param[0]);
			}
			break;

		case GotyeEventCodeGetGroupInfo:
			if (mListener != null) {
				mListener.onGetGroupInfo(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeModifyGroupInfo:
			if (mListener != null) {
				mListener.onModifyGroupInfo(statusCode, (GotyeGroup) param[0]);
			}
			break;

		case GotyeEventCodeGetGroupUserList:
			if (mListener != null) {
				mListener.onGetGroupUserList(statusCode,
						(List<GotyeUser>) param[0], (List<GotyeUser>) param[1],
						(GotyeGroup) param[2], (Integer) param[3]);
			}
			break;

		case GotyeEventCodeReceiveNotify:
			if (mListener != null) {
				mListener.onReceiveNotify(statusCode, (GotyeNotify) param[0]);
			}
			break;

		case GotyeEventCodeGetOfflineMessageList:
			// if (mListener != null) {
			// mListener.onGetOfflineMessageList(statusCode,
			// (ArrayList<GotyeMessage>) param[0]);
			// }
			break;

		case GotyeEventCodeSendMessage:
			if (mListener != null) {
				mListener.onSendMessage(statusCode, (GotyeMessage) param[0]);
			}
			break;

		case GotyeEventCodeReceiveMessage:
			if (mListener != null) {
				mListener.onReceiveMessage(statusCode, (GotyeMessage) param[0]);
			}
			break;

		case GotyeEventCodeDownloadMessage:
			if (mListener != null) {
				mListener.onDownloadMediaInMessage(statusCode,
						(GotyeMessage) param[0]);
			}
			break;

		case GotyeEventCodeStartTalk:
			if (mListener != null) {
				mListener.onStartTalk(statusCode, (Boolean) param[0],
						(Integer) param[1], (GotyeChatTarget) param[2]);
			}
			break;

		case GotyeEventCodeStopTalk:
			if (mListener != null) {
				Object param0 = param[0];
				Object param1 = param[1];
				GotyeMessage msg = null;
				Boolean isRealTime = false;
				if (param0 != null) {
					msg = (GotyeMessage) param0;
				}
				if (param1 != null) {
					isRealTime = (Boolean) param1;
				}
				mListener.onStopTalk(statusCode, msg, isRealTime);
			}
			break;

		case GotyeEventCodeDownloadMedia:
			if (mListener != null) {
				mListener.onDownloadMedia(statusCode, (String) param[0],
						(String) param[1]);
			}
			break;

		case GotyeEventCodePlayStart:
			if (mListener != null) {
				mListener.onPlayStart(statusCode, (GotyeMessage) param[0]);

			}
			break;
		case GotyeEventCodeRealPlayStart:
			if (mListener != null) {
				mListener.onPlayStartReal(statusCode, (Long) param[0],
						(String) param[1]);
			}
			break;
		case GotyeEventCodePlaying:
			if (mListener != null) {
				mListener.onPlaying(statusCode, (Integer) param[0]);
			}
			break;

		case GotyeEventCodePlayStop:
			if (mListener != null) {
				mListener.onPlayStop(statusCode);
			}
			break;
 
		case GotyeEventCodeReport:
			if (mListener != null) {
				mListener.onReport(statusCode, (GotyeMessage) param[0]);
			}
			break;
			
		case GotyeEventCodeDecodeFinished:
			if(mListener != null){
				mListener.onDecodeMessage(statusCode, (GotyeMessage)param[0]);
			}
			break;
			
		case GotyeEventCodeSendNotify:
			if(mListener != null){
				mListener.onSendNotify(statusCode, (GotyeNotify)param[0]);
			}
			break;

		}
	}
}
