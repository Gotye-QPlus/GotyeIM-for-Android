package com.gotye.api.listener;

import java.util.List;

import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;

public interface GroupListener  extends GotyeListener{
	/**
	 * 创建群回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 被创建的群
	 */
	void onCreateGroup(int code, GotyeGroup group);

	/**
	 * 加群回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 要加群的群
	 */
	void onJoinGroup(int code, GotyeGroup group);

	/**
	 * 退出群回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 所退出的群
	 */
	void onLeaveGroup(int code, GotyeGroup group);

	/**
	 * 解散群回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 被解散的群
	 */
	void onDismissGroup(int code, GotyeGroup group);

	/**
	 * 踢群成员回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 所在群
	 */
	void onKickOutUser(int code, GotyeGroup group);

	/**
	 * 回去群列表回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param grouplist 群列表
	 */
	void onGetGroupList(int code, List<GotyeGroup> grouplist);

	/**
	 * 获取群详情回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group  群对象
	 */
	void onRequestGroupInfo(int code, GotyeGroup group);

	/**
	 * 获取群成员回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param allList 请求的每页数据集合
	 * @param curList 当前页成员集合
	 * @param group 所在群
	 * @param pagerIndex 页码
	 */
	void onGetGroupMemberList(int code, List<GotyeUser> allList,
			List<GotyeUser> curList, GotyeGroup group, int pagerIndex);

	/**
	 * 接收到群邀请信息
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 邀请发出群
	 * @param sender 邀请人
	 * @param message 邀请信息
	 */
	void onReceiveGroupInvite(int code,GotyeGroup group, GotyeUser sender, String message);
	
	/**
	 * 收到加群请求
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 对方要申请加入的群
	 * @param sender 请求人
	 * @param message 请求信息
	 */
	void onReceiveRequestJoinGroup(int code,GotyeGroup group, GotyeUser sender, String message);
	
	/**
	 * 收到群主是否同意加群处理结果
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param group 当前群
	 * @param sender 发送人 
	 * @param message 同意或拒绝信息
	 * @param isAgree 是否同意
	 */
	void onReceiveReplayJoinGroup(int code,GotyeGroup group, GotyeUser sender, String message,boolean isAgree);

	/**
	 * 获取群离线消息 
	 * @deprecated
	 * @param code
	 * @param messagelist
	 */
	void onGetOfflineMessageList(int code, List<GotyeMessage> messagelist);

	/**
	 * 搜索群结果回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param mList 每页数据结果集合
	 * @param curList 当前数据集合
	 * @param pageIndex 当前页码
	 */
	void onSearchGroupList(int code, List<GotyeGroup> mList,
			List<GotyeGroup> curList, int pageIndex);

	/**
	 * 请求修改群回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param gotyeGroup 被修改后的群对象
	 */
	void onModifyGroupInfo(int code, GotyeGroup gotyeGroup);
	
	
   /**
    * 请求变更群主
    * @param code 状态码 参见 {@link GotyeStatusCode}
    * @param group 所在群
    */
	void onChangeGroupOwner(int code, GotyeGroup group);
	
	/**
	 * 有人加群群通知
	 * @param group 所在群
	 * @param user 新加入的成员
	 */
	void onUserJoinGroup(GotyeGroup group, GotyeUser user);
	
	/**
	 * 有人离开群通知
	 * @param group 所在群
	 * @param user 离开的群成员对象
	 */
	void onUserLeaveGroup(GotyeGroup group, GotyeUser user);
	
	/**
	 * 群被接收通知
	 * @param group 所在群
	 * @param user 解散人
	 */
	void onUserDismissGroup(GotyeGroup group, GotyeUser user);
	
	/**
	 * 群成员被踢出通知
	 * @param group 所在群
	 * @param kicked 被踢出的群成员
	 * @param actor 操作人
	 */
	void onUserKickdFromGroup(GotyeGroup group, GotyeUser kicked, GotyeUser actor);

	/**
	 * 发送申请入群信息回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param notify 通知对象
	 */
	void onSendNotify(int code, GotyeNotify notify);
}
