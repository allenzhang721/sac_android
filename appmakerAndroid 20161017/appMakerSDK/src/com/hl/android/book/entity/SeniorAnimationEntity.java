package com.hl.android.book.entity;


public class SeniorAnimationEntity {
	public float mX;
	public float mY;
	public float mWidth;
	public float mHeight;
	public float mDegree=0;
	public float mDuration;
	public float mAlpha =1.0f;
	public float mEndTime;
	public float delay;
	
	@Override
	public String toString() {
		return "mX:"+mX+"mY:"+mY+",mWidth:"+mWidth+",mHeight:"+mHeight+",mDegree:"+mDegree+",mDuration:"+mDuration+",mAlpha:"+mAlpha;
	}
	
}
