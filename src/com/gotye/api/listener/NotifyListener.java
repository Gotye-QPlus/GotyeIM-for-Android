package com.gotye.api.listener;

import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;

public interface NotifyListener  extends GotyeListener{
	/**
	 * 收到消息通知
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param message  消息对象
	 * @param unRead 是否已读
	 */
	void onReceiveMessage(int code, GotyeMessage message, boolean unRead);

	/**
	 * 发送消息回调
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param message 被发送的消息对象
	 */
	void onSendMessage(int code, GotyeMessage message);

	/**
	 * 收到通知
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param notify 通知对象
	 */
	void onReceiveNotify(int code,GotyeNotify notify);

	/**
	 * 删除好友通知
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param user 被删除人
	 */
	void onRemoveFriend(int code, GotyeUser user);

	/**
	 * 添加好友通知
	 * @param code 状态码 参见 {@link GotyeStatusCode}
	 * @param user 被添加好友
	 */
	void onAddFriend(int code, GotyeUser user);
	
	/**
	 * 通知变态变化通知，多用户更新界面
	 */
	void onNotifyStateChanged();
}
