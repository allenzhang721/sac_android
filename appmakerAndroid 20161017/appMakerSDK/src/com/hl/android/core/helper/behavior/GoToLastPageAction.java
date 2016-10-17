package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
/**
 * 翻到back页的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class GoToLastPageAction extends BehaviorAction {
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		if (BookController.lastPageID != null && BookController.lastPageID.trim().length() != 0) {
			BookController.getInstance().playPageById(BookController.lastPageID);
		}
	}
}
