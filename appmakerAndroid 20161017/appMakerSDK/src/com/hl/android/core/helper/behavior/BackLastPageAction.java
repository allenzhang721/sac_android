package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.controller.BookController;
/**
 *  切换到back页的事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class BackLastPageAction extends BehaviorAction {
	@Override
	public void doAction(BehaviorEntity entity) {
		
		super.doAction(entity);
		if (BookController.lastPageID != null && BookController.lastPageID.trim().length() != 0) {
//			测试切换效果
			PageEntity pageEntity=BookController.getInstance().getViewPage().getEntity();
			String effectType=pageEntity.getPageChangeEffectType();
			String effectDir=pageEntity.getPageChangeEffectDir();
			long duration=pageEntity.getPageChangeEffectDuration();
			if(effectType!=null&&!effectType.equals("")){
				BookController.getInstance().changePageWithEffect(effectType, effectDir, duration, 0, BookController.lastPageID);
			}else{
				BookController.getInstance().changePageById(BookController.lastPageID);
			}
		}
	}
}
