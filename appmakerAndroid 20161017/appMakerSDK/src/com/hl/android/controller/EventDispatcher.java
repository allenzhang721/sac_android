package com.hl.android.controller;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.GestureDetector.OnGestureListener;
import com.hl.android.view.component.inter.Component;

public class EventDispatcher implements OnGestureListener {

	private static EventDispatcher eventDispatcher;
	private GestureDetector detector;
	private ArrayList<Component> componentList;
	private boolean canDoDetector=true;
	private float totaldeltaY;
	private MotionEvent oldEvent;
	
	public EventDispatcher() {

	}

	public static EventDispatcher getInstance() {
		if (null == eventDispatcher) {
			eventDispatcher = new EventDispatcher();
		}
		return eventDispatcher;
	}

	public void init() {
		if (null != componentList) {
			this.componentList.clear();
		}
	}

	public void init(Context context) {
		detector = new GestureDetector(context, this);
	}

	float oldTouchValue = 0;
	private boolean isDown;

	public boolean onTouch(MotionEvent touchevent) {
		if(touchevent.getPointerCount()>=2){
			canDoDetector=false;
		}
		if (null == touchevent) {
			return true;
		}
		if (null == this.detector) {
			return true;
		}

		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			isDown=true;
			totaldeltaY=0;
			canDoDetector=true;
			Log.d("ww", "ACTION_DOWN");
			BookController.getInstance().getViewPage().hideMenu();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if(!isDown){
				totaldeltaY=0;
				return true;
			}
			Log.d("ww", "ACTION_MOVE");
			if (BookController.getInstance().getViewPage().getOffSetY()>=0||(BookController.getInstance().getViewPage().getOffSetY()<=BookSetting.BOOK_HEIGHT-BookController.getInstance().getViewPage().pageHeight)) {
				float deltaY=touchevent.getRawY()-oldEvent.getRawY();
				totaldeltaY+=deltaY;
				if(totaldeltaY > 100){
					if (BookState.getInstance().isFliping == false){
						if(BookSetting.FLIPCODE==1){
							BookController.getInstance().flipSubPage(-1);
						}
						totaldeltaY=0;
						deltaY=0;
						isDown=false;
						return true;
					}
				}else if(totaldeltaY < -100){
					if (BookState.getInstance().isFliping == false){
						if(BookSetting.FLIPCODE==1){
							BookController.getInstance().flipSubPage(1);
						}
						totaldeltaY=0;
						deltaY=0;
						isDown=false;
						return true;
					}
				}
				
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			Log.d("ww", "ACTION_UP");
			BookController.getInstance().getViewPage().playSequence();
			break;
		}
		}
//		if(BookSetting.FLIPCODE==1&&BookSetting.FLIP_CHANGE_PAGE){
//			return true;
//		}
		if(canDoDetector){
			this.detector.onTouchEvent(touchevent);
		}
		oldEvent=MotionEvent.obtain(touchevent);
		return true;
	}

	/**
	 * 针对动画移动后不能触发click事件的情况，在这里注册并统一处理
	 * 
	 * @param component
	 */
	public void registComponent(Component component) {
		if (null == componentList) {
			componentList = new ArrayList<Component>();
		}
		componentList.add(component);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(BookSetting.FLIPCODE==1){
			if(BookSetting.FLIP_CHANGE_PAGE){
				return false;
			}
		}
		if (BookController.getInstance().mainViewPage != null
				&& BookController.getInstance().mainViewPage.getEntity() != null) {
			if(!BookController.getInstance().mainViewPage.getEntity().enablePageTurnByHand)return false;
			if(!BookController.getInstance().mainViewPage.getEntity().isEnableNavigation())return false;
			
		}else{
			return true;
		}
		try {
			if (e1 == null || e2 == null) {
				return false;
			}
			if(Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())){
				if (e1.getX() - e2.getX() > HLSetting.FLING_MIN_DISTANCE&&BookController.getInstance().mainViewPage.isHorMove) {
					BookController.getInstance().flipPage(1);
				}
				if (e1.getX() - e2.getX() < -HLSetting.FLING_MIN_DISTANCE&&BookController.getInstance().mainViewPage.isHorMove) {
					BookController.getInstance().flipPage(-1);
				}	
			}
		} catch (Exception e) {
			Log.e("hl", " onFling ",e); 
			return false;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}
}
