package com.hl.android.book.entity;

import java.util.ArrayList;

public class SliderEffectComponentEntity extends ComponentEntity{
	public SliderEffectComponentEntity(ComponentEntity component){
		subItems=new ArrayList<SubImageItem>();
		if(component!=null){
			this.animationRepeat = component.animationRepeat;
			this.alpha=component.alpha;
		}
	}
	public boolean isUseSlide;
	public boolean isPageTweenSlide;
	public boolean isPageInnerSlide;
	public float   slideBindingX;
	public float   slideBindingY;
	public float   slideBindingWidth;
	public float   slideBindingHeight;
	public float   slideBindingAlpha;
	public String   switchType;
	public int repeat=-1;
	public boolean isLoop;
	public boolean isEndToStart;
	public ArrayList<SubImageItem> subItems;
}
