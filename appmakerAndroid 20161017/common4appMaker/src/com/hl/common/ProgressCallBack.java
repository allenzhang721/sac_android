package com.hl.common;

/**
 * 下载的回调函数
 * @author zhaoq
 *
 */
public interface ProgressCallBack {
	/**
	 * 下载开始的回调
	 * @param totalSize总大小
	 * @return
	 */
	public boolean startDown(int totalSize);
	/**
	 * 回调方法
	 * @param totalSize 总大小  
	 * @param curSize  当前大小
	 * return 是否继续下载，如果返回false，那么我们就不下载在了，并停止
	 */
	public boolean doProgressAction(int totalSize,int curSize);
	/**
	 * 下载结束的回调
	 * @param totalSize
	 * @return
	 */
	public boolean downOver(int totalSize);
	
}
