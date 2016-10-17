package com.hl.android.book.entity;

public class TimerEntity extends ComponentEntity{
	private int maxTimer = 1000;
	public int getMaxTimer() {
		if(maxTimer<0){
			maxTimer = 200;
		}
		return maxTimer;
	}

	public void setMaxTimer(int maxTimer) {
		this.maxTimer = maxTimer;
	}

	public boolean isPlayOrderbyDesc = true;
	public boolean isPlayMillisecond = true;;
	public String fontColor="0xcc0000";
	public String fontSize="20";
	
	public boolean isStaticType = false;
}
