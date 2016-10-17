package com.hl.android.core.helper.animation;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;

import com.hl.android.common.BookSetting;


/**
 * 属性动画的刷新监听器
 * 为了做暂停
 * 增加了两个方法
 * pause 暂停
 * play 继续播放
 * @author zhaoq
 * @version 1.0
 * @createed 2013-8-23
 */
@SuppressLint("NewApi")
public class HLAnimatorUpdateListener implements AnimatorUpdateListener {
	/**
	 * 暂停的播放标志位
	 */
	private boolean mPause = false;
	private boolean isElapsed = true;
	private float fraction = 0.0f;
	private long mCurrentPlayTime = 0l;
	
	private HLInterpolator initInterInterpolator;
	
	public boolean mStop = false;
	
	
	public boolean isPause(){
		return mPause;
	}
	public void pause(){
		mPause = true;
	}
	public void play(){
		mPause = false;
	}
	@Override
	public void onAnimationUpdate(final ValueAnimator animation) {
		if(BookSetting.IS_CLOSED){
			return;
		}
//		if(mStop)return;
		if(mPause){
			if(isElapsed){
				if(initInterInterpolator == null){
					initInterInterpolator = (HLInterpolator) ((HLInterpolator) animation.getInterpolator()).clone();
				}
				animation.setInterpolator(pauseTimer);
			}else{
				isElapsed = false;
			}
			
			new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()){

				@Override
				public void onTick(long millisUntilFinished) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onFinish() {
					if(mPause)animation.setCurrentPlayTime(mCurrentPlayTime);
				}
				
			}.start();
		}else{
			
			mCurrentPlayTime = animation.getCurrentPlayTime();
			fraction = animation.getAnimatedFraction();
			if(animation.getInterpolator() == pauseTimer){
				isElapsed = true;
				animation.setInterpolator(initInterInterpolator);
			}
		}
	}
	
	private TimeInterpolator pauseTimer = new TimeInterpolator() {
		
		@Override
		public float getInterpolation(float input) {
			return fraction;
		}
	};

}
