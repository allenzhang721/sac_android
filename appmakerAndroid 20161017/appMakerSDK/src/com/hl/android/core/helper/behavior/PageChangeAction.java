package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.controller.BookController;
/**
 * 切换到指定页
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class PageChangeAction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
//		测试切换效果
		PageEntity pageEntity=BookController.getInstance().getViewPage().getEntity();
		String effectType=pageEntity.getPageChangeEffectType();
		String effectDir=pageEntity.getPageChangeEffectDir();
		long duration=pageEntity.getPageChangeEffectDuration();
		if(effectType!=null&&!effectType.equals("")){
			BookController.getInstance().changePageWithEffect(effectType, effectDir, duration, 0, entity.Value);
		}else{
			BookController.getInstance().changePageById(entity.Value);
		}
	}
}
