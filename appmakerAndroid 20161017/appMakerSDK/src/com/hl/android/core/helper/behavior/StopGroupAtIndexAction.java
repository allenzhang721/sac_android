package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
/**
 * 播放到图片序列的某一序列时，停止序列播放的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class StopGroupAtIndexAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		BookController.getInstance().getViewPage().stopGroupAtSomeWhere(entity.Value);
	}
}
