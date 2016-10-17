package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.TimerComponent;
/**
 *  触发viewcell的play事件
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class PlayVideoAudioAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		ViewCell viewCell=getViewCell(entity);
		if (viewCell != null) {
			if(viewCell.getComponent() instanceof TimerComponent){
				((TimerComponent)viewCell.getComponent()).playTimer();
			}else{
				viewCell.play();
			}
		}
	}
}
