package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

public class HL3DViewFlipper extends ImageView implements Component {
	MoudleComponentEntity entity;
	private String rotationType="clockwise";

	public HL3DViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HL3DViewFlipper(Context context, ComponentEntity entity) {
		super(context);
		this.entity = (MoudleComponentEntity) entity;
		this.setBackgroundColor(Color.TRANSPARENT);
		setScaleType(ScaleType.FIT_XY);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (MoudleComponentEntity) entity;

	}

	ArrayList<String> sourceIDS = null;
	@Override
	public void load() {
		 sourceIDS = ((MoudleComponentEntity) this.entity)
				.getSourceIDList();
		this.setImageBitmapOnly(0);
		rotationType=entity.rotationType;
		
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}


	@Override
	public void play() {
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}


	@Override
	public void hide() {
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	public boolean onTouchEvent(MotionEvent touchevent) {
		if(touchevent.getPointerCount()==1){
			dotouchOnePointEvent(touchevent);
		}else if(touchevent.getPointerCount()==2){
			((ViewCell)getParent()).doSomeThing(touchevent);
		}
		return true;//此处返回true是因为左右滑动与翻页有冲突，屏蔽掉翻页
	}
	private float oldTouchValueX;
	private float oldTouchValueY;
	private void dotouchOnePointEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			oldTouchValueX = touchevent.getX();
			oldTouchValueY = touchevent.getY();
			mRunning = false;
			break;
		}
		case MotionEvent.ACTION_UP: {
			mRunning = true;
			loadHandler.sendEmptyMessage(1);
		}
		case MotionEvent.ACTION_MOVE:
			
			float currentX = touchevent.getX();
			float currentY = touchevent.getY();
			float distabcex = oldTouchValueX - currentX;
			float distabcey = oldTouchValueY - currentY;
			if(entity.isHorSlider){
				if(distabcex <0){
					mLeft = false;
					loadNextBitmap(mLeft);
					rotationType="anticlosewise";
				} else if(distabcex > 0){
					mLeft = true;
					loadNextBitmap(mLeft);
					rotationType="clockwise";
				}else{
					break;
				}
			}else{
				if(distabcey <0){
					mUp = false;
					loadNextBitmap(mUp);
					rotationType="anticlosewise";
				} else if(distabcey > 0){
					mUp = true;
					loadNextBitmap(mUp);
					rotationType="clockwise";
				}else{
					break;
				}
			}
			
			break;
		}
	}

	int currentID = 0;
	boolean mRunning = true;
	boolean mLeft = true;
	boolean mUp=true;
	boolean mStart = false;

	private long lastPlayTime = 0l;
	public void loadNextBitmap(boolean currentDirect) {
		if (!currentDirect) {
			if (this.currentID == 0) {
				currentID = sourceIDS.size() - 1;
			} else {
				currentID--;
			}
			doBehaiors(currentID);
			this.setImageBitmapOnly(currentID);

		} else {
			if (currentID < this.sourceIDS.size() - 1) {
				currentID++;
			} else {
				currentID = 0;
			}
			doBehaiors(currentID);
			this.setImageBitmapOnly(currentID);
		}

	}

	public Handler loadHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(rotationType.equalsIgnoreCase("clockwise"))loadNextBitmap(true);
			else loadNextBitmap(false);
		}
	};

	public void setImageBitmapOnly(int index){

		lastPlayTime = System.currentTimeMillis();
		Bitmap bitmap=BitmapManager.getBitmapFromCache(sourceIDS.get(index));
		if(bitmap==null){
			bitmap=BitmapUtils.getBitMap(sourceIDS.get(index),getContext());
			BitmapManager.putBitmapCache(sourceIDS.get(index), bitmap);
		}
		this.setImageBitmap(bitmap);

		this.postInvalidate();
		
//		if(!entity.isAutoRotation)return;
		
		long delay =entity.getTimerDelay() - (System.currentTimeMillis() - lastPlayTime);
		if(delay<0)delay=0;
		if(mRunning && entity.isAutoRotation)loadHandler.sendEmptyMessageDelayed(1, delay);
		
	}
	
	private void doBehaiors(int index){
		for(BehaviorEntity beheavior:entity.behaviors){
			//此处整理了分号分割的问题 ,为什么这么搞就是因为要兼容老版本  by zhaoq
			BehaviorHelper.doBeheavorForList(beheavior, index,beheavior.triggerComponentID);
		}
	}

}
