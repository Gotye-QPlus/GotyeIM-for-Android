package com.open_demo.util;

public interface OnEventListener {
	/**
	 * 开始倾听按钮事件回调
	 * 
	 * @return true成功启动。
	 */
	public boolean onStartListening();

	/**
	 * 结束倾听按钮事件回调
	 * 
	 * @return true 成功结束
	 */
	public boolean onStopListening();

	/**
	 * 取消按钮事件
	 * 
	 * @return true 成功取消
	 */
	public boolean onCancel();
}
