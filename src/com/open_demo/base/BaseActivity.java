package com.open_demo.base;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.gotye.api.listener.ChatListener;
import com.gotye.api.listener.DownloadListener;
import com.gotye.api.listener.GroupListener;
import com.gotye.api.listener.NotifyListener;
import com.gotye.api.listener.PlayListener;
import com.gotye.api.listener.RoomListener;
import com.gotye.api.listener.UserListener;

public class BaseActivity extends Activity implements ChatListener,
		DownloadListener, GroupListener, UserListener, RoomListener,PlayListener,NotifyListener {
	public GotyeAPI api=GotyeAPI.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPlayStart(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlaying(int code, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayStop(int code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayStartReal(int code, long roomId, String who) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnterRoom(int code, long lastMsgID, GotyeRoom room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveRoom(int code, GotyeRoom room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetRoomList(int code, List<GotyeRoom> gotyeroom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetRoomMemberList(int code, GotyeRoom room,
			List<GotyeUser> totalMembers, List<GotyeUser> currentPageMembers,
			int pageIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetHistoryMessageList(int code, List<GotyeMessage> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestRoomInfo(int code, GotyeRoom room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifyUserInfo(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchUserList(int code, List<GotyeUser> mList, int pagerIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetFriendList(int code, List<GotyeUser> mList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddBlocked(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveFriend(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveBlocked(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetBlockedList(int code, List<GotyeUser> mList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetProfile(int code, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateGroup(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoinGroup(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveGroup(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDismissGroup(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKickOutUser(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetGroupList(int code, List<GotyeGroup> grouplist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRequestGroupInfo(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetGroupMemberList(int code, List<GotyeUser> allList,
			List<GotyeUser> curList, GotyeGroup group, int pagerIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveGroupInvite(int code, GotyeGroup group,
			GotyeUser sender, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetOfflineMessageList(int code, List<GotyeMessage> messagelist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchGroupList(int code, List<GotyeGroup> mList,
			List<GotyeGroup> curList, int pageIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifyGroupInfo(int code, GotyeGroup gotyeGroup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChangeGroupOwner(int code, GotyeGroup group) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoinGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLeaveGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserDismissGroup(GotyeGroup group, GotyeUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked,
			GotyeUser actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadMedia(int code, String path, String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReleaseMessage(int code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReport(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTalk(int code, boolean isRealTime, int targetType,
			GotyeChatTarget target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTalk(int code, GotyeMessage message, boolean isVoiceReal) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReceiveMessage(int code, GotyeMessage message, boolean unRead) {
		// TODO Auto-generated method stub
		
	}
	 
	@Override
	public void onNotifyStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDecodeMessage(int code, GotyeMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendNotify(int code, GotyeNotify notify) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveNotify(int code, GotyeNotify notify) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveRequestJoinGroup(int code, GotyeGroup group,
			GotyeUser sender, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveReplayJoinGroup(int code, GotyeGroup group,
			GotyeUser sender, String message,boolean isAgree) {
		// TODO Auto-generated method stub
		
	}
 
 

}
