package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.common.BookSetting;
/**
 *  自动播放书籍的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class AutoPlayBookAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		BookSetting.IS_AUTOPAGE = true;
	}
}