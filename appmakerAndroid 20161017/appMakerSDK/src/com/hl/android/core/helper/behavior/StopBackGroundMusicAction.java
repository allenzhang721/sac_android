package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
/**
 * 停止背景音乐的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class StopBackGroundMusicAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		if(BookController.getInstance().getBackgroundMusic()!=null){
			BookController.getInstance().getBackgroundMusic().stop();
		}
		//此处不能销毁mediaplayer，如果销毁，再触发播放事件时就不能播放了
		//		BookController.getInstance().getBackgroundMusic().recyle();
	}
}
