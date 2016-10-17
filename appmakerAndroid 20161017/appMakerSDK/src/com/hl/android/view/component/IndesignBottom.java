package com.hl.android.view.component;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.hl.android.R;

public class IndesignBottom extends RelativeLayout{
	
	private Context mContext;
	private BottomNavListenner mListenner=null;
	private Animation anim4dismiss,anim4show;
	private ImageButton mMoveBtn;
	private MotionEvent oldEvent;
	public boolean tagggg=false;
	
	
	public IndesignBottom(Context context) {
		super(context);
		mContext=context;
		init();
	}
	private void init() {
		setBackgroundResource(R.drawable.indesign_bottomnav_bg);
		mMoveBtn=new ImageButton(mContext);
		mMoveBtn.setBackgroundResource(R.drawable.btn_movebtn_selector);
		addView(mMoveBtn);
		anim4dismiss=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
		anim4dismiss.setDuration(200);
		anim4dismiss.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(View.INVISIBLE);
			}
		});
		
		anim4show=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		anim4show.setDuration(200);
		
		mMoveBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if(mListenner!=null){
						mListenner.onSliderTouchDown();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					float dx=event.getRawX()-oldEvent.getRawX();
					float resultX=mMoveBtn.getX()+dx;
					if(resultX<=13){
						resultX=13;
					}else if(resultX>=getWidth()-13-mMoveBtn.getWidth()){
						resultX=getWidth()-13-mMoveBtn.getWidth();
					}
					if(resultX!=mMoveBtn.getX()){
						mMoveBtn.setX(resultX);
						if(mListenner!=null){
							mListenner.onSliderPositionChanged(resultX-13, getTotalSlideLength());
						}
					}
					break;

				default:
					break;
				}
				oldEvent=MotionEvent.obtain(event);
				return false;
			}
		});
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	public void setBottomNavListenner(BottomNavListenner listenner){
		mListenner=listenner;
	}
	
	public boolean isShowing(){
		return getVisibility()==View.VISIBLE;
	}
	
	public void show(){
		if(!isShowing()){
			setVisibility(View.VISIBLE);
			doAnim4Show();
		}
	}
	
	public void dismiss(){
		bringToFront();
		if(isShowing()){
			doAnim4Dismiss();
		}
	}
	
	private void doAnim4Dismiss() {
		if(mListenner!=null){
			mListenner.onDismiss();
		}
		startAnimation(anim4dismiss);
	}

	private void doAnim4Show() {
		if(mListenner!=null){
			mListenner.onShow();
		}
		startAnimation(anim4show);
	}
	
	public float getTotalSlideLength(){
		return getWidth()-26-mMoveBtn.getWidth();
	}
	
	public void seekTo(float percentX){
		float resultX=13+getTotalSlideLength()*percentX;
		mMoveBtn.setX(resultX);
	}

	public interface BottomNavListenner{
		public void onShow();
		public void onDismiss();
		public void onSliderPositionChanged(float newPosition,float totalLength);
		public void onSliderTouchDown();
	}

}
