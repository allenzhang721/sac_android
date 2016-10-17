package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
/**
 * 暂停背景音乐事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class PauseBackGroundMusicAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		if(BookController.getInstance().getBackgroundMusic()!=null){
			BookController.getInstance().getBackgroundMusic().pause();
		}
	}
}
