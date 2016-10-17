package com.hl.android.view.component.bookmark;

import java.util.ArrayList;

import com.hl.android.HLLayoutActivity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.DataUtils;

public class BookMarkManager {

	// 书签列表对象的Key
	private static final String MARKLISTKEY = "com.hl.appbook.marklist";
	// 书签列表
	private static ArrayList<String> markList;
	
	public static ArrayList<String> getMarkList(HLLayoutActivity activity){
		String markKey = MARKLISTKEY + BookController.getInstance().getBook().getBookInfo().getId();
		markList = DataUtils.getSerializable(activity, markKey);
		if (markList == null) {
			markList = new ArrayList<String>();
		}
		return markList;
	}
	public static void deleteMark(HLLayoutActivity activity,int pos){
		markList = getMarkList(activity);
		markList.remove(pos);
		String markKey = MARKLISTKEY + BookController.getInstance().getBook().getBookInfo().getId();
		DataUtils.saveSerializable(activity, markKey, markList);
		activity.refreshMark();
	}
	
	/**
	 *添加当前页的书签 并更新书签视图
	 */
	public static void addCurPage(HLLayoutActivity activity) {
		String markKey = MARKLISTKEY + BookController.getInstance().getBook().getBookInfo().getId();
		String curPageId = BookController.getInstance().getViewPage()
				.getEntity().getID();
		if (!markList.contains(curPageId)) {
			markList.add(curPageId);
			DataUtils.saveSerializable(activity, markKey, markList);
			activity.refreshMark();
		}
	}

}
