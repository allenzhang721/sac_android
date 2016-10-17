package com.hl.android.view.component.moudle.bookshelf;

import android.content.Context;

import com.hl.android.controller.BookController;

/**
 * 书架模板的数据实体bean
 * @author carter
 *
 */
public class ShelvesBook {
	Context context;
	public String mBookID;
	public String mCoverUrl;
	public String mBookUrl;
	public String version;
	public String mLocalPath;
	public String mCoverPath;
	//public boolean isDowning;
	//boolean isError;
	public int viewID;
	public int mState = 0;
	public static int curIndex = 233232423;

	public ShelvesBook() {
		this.context = BookController.getInstance().hlActivity;
		viewID = curIndex++;
	}
}