package com.hl.android.view.component;

import java.io.InputStream;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

public class HLLocalButtonComponent extends Button implements Component, ComponentListener, ComponentPost  {

	public ComponentEntity entity = null;
	private OnComponentCallbackListener onComponentCallbackListener;
	Bitmap bitmap = null;
	Bitmap selectbitmap = null;
	private boolean isSendAutoPage = false;

	int initWidth = 0;
	int initHeight = 0;
	int initX = 0;
	int initY = 0;
	private Context mContext;
	BitmapDrawable selectD ,normalD;
	public HLLocalButtonComponent(Context context) {
		super(context);
	}

	public HLLocalButtonComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
		mContext = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			this.setBackgroundDrawable(selectD);
			break;
		case MotionEvent.ACTION_UP:
			this.setBackgroundDrawable(normalD);
			break;
		}
		return false;
	}
	@Override
	public void load() {
		initX = getEntity().x;
		initY = getEntity().y;
		loadBitmap();
		if (this.entity.isHideAtBegining) {
			setVisibility(View.GONE);
		}
	}

	public void loadBitmap() {
		int width = getLayoutParams().width;
		int height = getLayoutParams().height;
		initWidth = width;
		initHeight = height;
		int size[] = { width, height };
		bitmap = BitmapUtils.getBitMap(getEntity().localSourceId, getContext(),
				size);
		selectbitmap = BitmapUtils.getBitMap(getEntity().downSourceID, getContext(),
				size);
		
		selectD = new BitmapDrawable(mContext.getResources(),selectbitmap);
		normalD = new BitmapDrawable(mContext.getResources(),bitmap);
//		StateListDrawable sd = new StateListDrawable();
//		sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, selectD);  
//        sd.addState(new int[]{android.R.attr.state_focused}, selectD);  
//        sd.addState(new int[]{android.R.attr.state_pressed}, selectD);  
//        sd.addState(new int[]{}, normalD);  
		this.setBackgroundDrawable(normalD);
	}

	@Override
	public void load(InputStream is) {

	}

	

	@Override
	public ComponentEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;
	}
 
	AnimatorSet animsationSet;
	
	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}

	}

	@Override
	public void hide() {
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
		if (null != this.bitmap) {
//			setImageBitmap(null);
			bitmap.recycle();
			bitmap = null;
		}
	}

	@Override
	public void show() {
		if (null == this.bitmap) {
			this.loadBitmap();
		}

		if (getVisibility() == View.VISIBLE)
			;
		else
			setVisibility(View.VISIBLE);
		// 不能将这个放到最前面
		// BookController.getInstance().getViewPage().bringChildToFront(this);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {
		onComponentCallbackListener = callbackListner;

	}

	@Override
	public void recyle() {
//		this.setImageBitmap(null);
		if (null != bitmap) {
			bitmap.recycle();
			bitmap = null;
		} 
	}

	//
	// public float getFactX(){
	// Math.sin(a)
	// return 0;
	// }

	@Override
	public void callBackListener() {
		if (isSendAutoPage == false) {
			onComponentCallbackListener.setPlayComplete();
			isSendAutoPage = true;
		}
	}
}
