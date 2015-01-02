package com.gotye.api;
/**
 * 消息对媒体对应的类型
 * @author gotye
 *
 */
public enum GotyeMediaType {
	/**
	 * 文本类型
	 */
	GotyeMediaTypeTEXT, // /<text
	/**
	 * 图片类型
	 */
	GotyeMediaTypeImage, // /<image
	/**
	 * 语音类型
	 */
	GotyeMediaTypeAudio, // /<audio
	/**
	 * 自定义数据类型(文件)
	 */
	GotyeMediaTypeUserData, // /< user data
	/**
	 * 额外数据类型
	 */
	GotyeMediaTypeUserEXTRA; // /< extra
}
