package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.core.helper.AnimationHelper;
import com.hl.android.view.ViewCell;
/**
 * 播放整体动画的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class PlayAnimationAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		ViewCell viewCell=getViewCell(entity);
		if (viewCell != null) {
			AnimationHelper.playAnimation(viewCell);
		}
	}
}
