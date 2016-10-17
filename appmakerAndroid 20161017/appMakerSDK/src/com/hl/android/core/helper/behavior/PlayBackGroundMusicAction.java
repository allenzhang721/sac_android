package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
/**
 *播放背景音乐的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class PlayBackGroundMusicAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		BookController.getInstance().playBackgroundMusic();
	}
}
