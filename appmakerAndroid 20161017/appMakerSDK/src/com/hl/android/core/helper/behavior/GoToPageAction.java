package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;


/**
 * GoToPageAction 翻到指定页
 * @author zhaoq
 * @version 1.0
 * @createed 2013-11-5
 */
public class GoToPageAction extends BehaviorAction {
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		BookController.getInstance().playPageById(entity.Value);
	}
}
