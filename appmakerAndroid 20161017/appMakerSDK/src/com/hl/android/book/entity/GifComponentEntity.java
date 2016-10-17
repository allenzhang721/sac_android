package com.hl.android.book.entity;

import java.util.ArrayList;

public class GifComponentEntity extends ComponentEntity{
	private boolean IsPlayOnetime;
	private double gifDuration;
	private ArrayList<String> frameList = new ArrayList<String>();
	
	public GifComponentEntity(ComponentEntity component){
		if(component!=null){
			this.animationRepeat = component.animationRepeat;
			this.alpha=component.alpha;
		}
	}
	
	public boolean isIsPlayOnetime() {
		return IsPlayOnetime;
	}
	public void setIsPlayOnetime(boolean isPlayOnetime) {
		IsPlayOnetime = isPlayOnetime;
	}
	public double getGifDuration() {
		return gifDuration;
	}
	public void setGifDuration(double gifDuration) {
		this.gifDuration = gifDuration;
	}
	public ArrayList<String> getFrameList() {
		return frameList;
	}
	public void setFrameList(ArrayList<String> frameList) {
		this.frameList = frameList;
	}
	
}
