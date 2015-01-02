package com.gotye.api;

public enum GotyeMessageType {
	/**
	 * 文本消息类型
	 */
	GotyeMessageTypeText, // /< text message
	/**
	 * 图片消息类型
	 */
	GotyeMessageTypeImage, // /< image message
	/**
	 * 语音消息类型
	 */
	GotyeMessageTypeAudio, // /< audio message
	/**
	 * 自定义数据消息类型
	 */
	GotyeMessageTypeUserData, // /< user data
	/**
	 * 群邀请消息类型
	 * @deprecated
	 */
	GotyeMessageTypeInviteGroup
}
