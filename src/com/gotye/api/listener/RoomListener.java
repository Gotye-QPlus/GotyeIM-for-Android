package com.gotye.api.listener;

import java.util.List;

import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;

public interface RoomListener  extends GotyeListener{
	/**
	 * 进入聊天室回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param lastMsgID 该聊天室最后一条消息id
	 * @param room 当前聊天室对象
	 */
	void onEnterRoom(int code, long lastMsgID, GotyeRoom room);

	/**
	 * 离开聊天室
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param room 当前聊天室对象
	 */
	void onLeaveRoom(int code, GotyeRoom room);

	/**
	 * 回去聊天室列表
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param roomList 聊天室列表
	 */
	void onGetRoomList(int code, List<GotyeRoom> roomList);

	/**
	 * 获取聊天室成员列表
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param room 当前聊天室 
	 * @param totalMembers 每页结果集合
	 * @param currentPageMembers 当前页集合
	 * @param pageIndex 当前页码
	 */
	void onGetRoomMemberList(int code, GotyeRoom room,
			List<GotyeUser> totalMembers, List<GotyeUser> currentPageMembers,
			int pageIndex);

	/**
	 * 获取历史消息回调
	 * @deprecated
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param list 历史消息列表
	 */
	void onGetHistoryMessageList(int code, List<GotyeMessage> list);
 
	/**
	 * 获取聊天室详情
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param room 聊天室对象
	 */
	void onRequestRoomInfo(int code,GotyeRoom room);
}
