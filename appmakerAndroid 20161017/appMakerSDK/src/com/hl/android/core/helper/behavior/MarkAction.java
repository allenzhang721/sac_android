package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;

/**
 * 处理书签的动作
 * @author zhaoq
 * @version 1.0
 * @createed 2013-11-5
 */
public class MarkAction extends BehaviorAction {
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		BookController.getInstance().hlActivity.showMark();
	}
}
