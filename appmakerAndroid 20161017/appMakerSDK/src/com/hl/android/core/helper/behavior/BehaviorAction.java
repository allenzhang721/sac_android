package com.hl.android.core.helper.behavior;

import java.util.ArrayList;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;

/**
 * 动作执行类基础类
 * 所有的动作都要继承本类
 * @author zhaoq
 * @version 1.0
 * @createed 2013-11-5
 */
public class BehaviorAction {
	/**
	 * 动作实体类，需要被设置
	 */
	protected BehaviorEntity mEntity;
	/**
	 * 
	 * @param behavior
	 * @return 动作执行者viewcell
	 */
	public ViewCell getViewCell(BehaviorEntity behavior){
		ViewCell viewCell = BookController.getInstance().getViewPage().getCellByID(behavior.FunctionObjectID);
		if (viewCell == null&& StringUtils.isEmpty(behavior.FunctionObjectID)) {
			behavior.FunctionObjectID = behavior.triggerComponentID;
			viewCell = BookController.getInstance().getViewPage().getCellByID(behavior.FunctionObjectID);
		}
		return viewCell;
	}
	
	/**
	 * 执行动作
	 * @param entity
	 */
	public void doAction(BehaviorEntity entity){
		mEntity = entity;
	}
	
	
}
