package com.hl.callback;

/**
 * 回调action
 * 对外提供了接口不要随意改变
 * @author zhaoq
 *
 */
public interface Action {
	/**
	 * 如果想要程序继续执行返回true否则返回false
	 * @return
	 */
	public boolean doAction();
}
