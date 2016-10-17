package com.hl.android.controller;

import android.util.Log;

/**
 * 书籍状态机
 * 
 * @author webcat
 * 
 */
public class BookState {
	private static BookState bookState;

	public static BookState getInstance() {
		if (null == bookState) {
			bookState = new BookState();
		}
		return bookState;
	}

	// 翻页状态
	public boolean isFliping = false;
	//public boolean galleryDiaplay = false;
//	public boolean isPlayingViewPage = false;
	public boolean isChangeTo = true;

	/**
	 * 设置翻页状态
	 * 
	 * @return
	 */
	public boolean setFlipState() {
		if (!isFliping) {
			isFliping = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 恢复翻页状态
	 * 
	 * @return
	 */
	public void restoreFlipState() {
		this.isFliping = false;
	}

	/**
	 * 设置正在显示页面
	 * 
	 * @return
	 */
	public boolean setPlayViewPage() {
		isFliping = false;
		//galleryDiaplay = false;
//		isPlayingViewPage = true;
		isChangeTo = false;
		return true;
	}

	/**
	 * 回收资源
	 */
	public void recyle() {
		bookState=null;
	}

//	/**
//	 * 设置显示Gallery
//	 * 
//	 * @return
//	 */
//	public boolean setGalleryDisplay() {
//		galleryDiaplay = true;
//		return true;
//	}
//
//	public boolean isPlayingViewPage() {
//		return isPlayingViewPage;
//	}
//
//	public void setPlayingViewPage(boolean isPlayingViewPage) {
//		this.isPlayingViewPage = isPlayingViewPage;
//	}

}
