package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.HLCounterComponent;
/**
 * 计数器增加值的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class CounterPlusAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		ViewCell viewCell=getViewCell(entity);
		if (viewCell != null) {
			((HLCounterComponent) viewCell.getComponent()).plus(Integer.valueOf(entity.Value));
		}
	}
}
