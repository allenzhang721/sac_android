package com.hl.android.core.helper.behavior;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.HLSliderEffectComponent;
import com.hl.android.view.component.moudle.HLVerSlideImageSelectUIComponent;
import com.hl.android.view.component.moudle.HLViewFlipperInter;
import com.hl.android.view.component.moudle.HLViewFlipperVerticleInter;

public class TempalteChangetoaction extends BehaviorAction{
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		ViewCell viewCell=getViewCell(entity);
		if (viewCell != null) {
			if(viewCell.getComponent() instanceof HLVerSlideImageSelectUIComponent){
				((HLVerSlideImageSelectUIComponent)viewCell.getComponent()).doSelectItemEvent(Integer.parseInt(entity.Value));
			}else if(viewCell.getComponent() instanceof HLViewFlipperVerticleInter){
				((HLViewFlipperVerticleInter)viewCell.getComponent()).doClickCircle(Integer.parseInt(entity.Value));
			}else if(viewCell.getComponent() instanceof HLViewFlipperInter){
				((HLViewFlipperInter)viewCell.getComponent()).doClickCircle(Integer.parseInt(entity.Value));
			}else if(viewCell.getComponent() instanceof HLSliderEffectComponent){
				((HLSliderEffectComponent)viewCell.getComponent()).doChangeToAction(Integer.parseInt(entity.Value));
			}
		}
	}
}
