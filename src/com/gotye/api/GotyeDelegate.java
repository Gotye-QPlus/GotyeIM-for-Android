package com.gotye.api;

import java.util.List;

/**
 * 底层事件名称，具体实现参看GotyeListener实现
 * @author Administrator
 *
 */
public interface GotyeDelegate {
	
	public void onLogin(int code, GotyeUser user);
	
	public void onGetProfile(int code, GotyeUser user);
	
	public void onLogout(int code);
	
	public void onGetUserInfo(int code, GotyeUser user);
	
	public void onModifyUserInfo(int code,GotyeUser user);
	
	public void onSearchUserList(int code, List<GotyeUser> mList, int pagerIndex);
	
	public void onGetFriendList(int code, List<GotyeUser> mList);
	
	public void onGetBlockedList(int code, List<GotyeUser> mList);
	
	public void onAddFriend(int code, GotyeUser user);
	
	public void onAddBlocked(int code, GotyeUser user);
	
	public void onRemoveFriend(int code, GotyeUser user);
	
	public void onRemoveBlocked(int code, GotyeUser user);
	
	
	
	public void onGetRoomList(int code, List<GotyeRoom> gotyeroom);
	
	public void onEnterRoom(int code, long lastMsgID, GotyeRoom room);
	
	public void onLeaveRoom(int code, GotyeRoom room);
	
	public void onGetRoomUserList(int code, GotyeRoom room,List<GotyeUser> totalMembers,List<GotyeUser> currentPageMembers,int pageIndex);
	
	public void onSearchGroupList(int code, List<GotyeGroup> mList, List<GotyeGroup> curList, int pageIndex);
	
	public void onCreateGroup(int code, GotyeGroup group);
	
	public void onJoinGroup(int code, GotyeGroup group );
	
	public void onLeaveGroup(int code, GotyeGroup group );
	
	public void onDismissGroup(int code, GotyeGroup group);
	
	public void onKickOutUser(int code, GotyeGroup group);
	
	public void onGetGroupList(int code, List<GotyeGroup> grouplist);
	
	public void onChangeGroupOwner(int code,GotyeGroup group);
	
	public void onUserJoinGroup(GotyeGroup group, GotyeUser user);
	
	public void onUserLeaveGroup(GotyeGroup group, GotyeUser user);
	
	public void onUserDismissGroup(GotyeGroup group, GotyeUser user);
	
	public void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked, GotyeUser actor);
	
	public void onGetGroupDetailList();
	
	public void onGetGroupInfo(int code, GotyeGroup group);
	
	public void onGetGroupUserList(int code, List<GotyeUser> allList, List<GotyeUser> curList, GotyeGroup group, int pagerIndex);
	
	public void onReceiveGroupInvite(int code,GotyeGroup mGroup, String message, GotyeUser sender);
	
	public void onSendMessage(int code, GotyeMessage message);
	
    public void onReceiveMessage(int code, GotyeMessage message);
	
	public void onDownloadMediaInMessage(int code, GotyeMessage message);
	
	public void onReport(int code, GotyeMessage message);
	
	public void onGetHistoryMessageList(int code, List<GotyeMessage> list);
	
    public void onReleaseMessage(int code);
	
	public void onStartTalk(int code, boolean isRealTime, int targetType, GotyeChatTarget target);
	
	public void onStopTalk(int code, GotyeMessage message, boolean isVoiceReal);
	
	public void onDownloadMedia(int code, String path, String url);
	
    public void onPlayStart(int code, GotyeMessage message);
    
    public void onPlayStartReal(int code, long roomId, String who);
	
	public void onPlaying(int code, int position);
	
	public void onPlayStop(int code);

	public void onModifyGroupInfo(int code, GotyeGroup gotyeGroup);
	
	public void onDecodeMessage(int code, GotyeMessage message);
	
	public void onSendNotify(int code , GotyeNotify notify);
	
	public void onReceiveNotify(int code,GotyeNotify notify);

	
}
