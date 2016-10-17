package com.hl.android.core.helper.animation;

import android.view.View;

/**
 * 用于动画容器里的key
 * 需要用动画对象和动画顺序全都一致才可以取出同一个动画
 * @author zhaoq
 * @version 1.0
 * @createed 2013-10-28
 */
public class AnimationKey {
	public View mView;
	public int mIndex=9999;//999说明是个动画集，即指定对象的所有动画
	/**
	 * 如果是播放整个动画的时候这个作为key
	 * @param view
	 */
	public AnimationKey(View view){
		mView = view;
	}
	/**
	 * 播放指定动画的key
	 * @param view
	 * @param index
	 */
	public AnimationKey(View view,int index){
		mView = view;
		mIndex = index;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof AnimationKey){
			AnimationKey target = (AnimationKey)o;
			return target.mView == mView && target.mIndex == mIndex;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashCode = mView.hashCode();
		hashCode = hashCode*100 + mIndex;
		return hashCode;
	}
}
