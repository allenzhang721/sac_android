package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

/**
 * 竖向文本图片组件类
 * 
 * @author raozhicheng
 * 
 */
public class VerticalImageComponent extends View implements Component,ComponentListener{
	private ComponentEntity mEntity;
	private OnComponentCallbackListener onComponentCallbackListener;
	private Bitmap mBitmap;
	private boolean isSendAutoPage = false;
	private Context mContext;
	private RectF rectF;
	private float topOffset=0;
	private int targetHeight;
	private Paint mPaint;
	private MotionEvent oldEvent;
	public VerticalImageComponent(Context context) {
		super(context);
	}

	public VerticalImageComponent(Context context, ComponentEntity entity) {
		super(context);
		mContext=context;
		this.setEntity(entity);
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
	}
	
	@Override
	public void load() {
		mBitmap=BitmapManager.getBitmapFromCache(mEntity.getLocalSourceId());
		if(mBitmap==null){
			mBitmap=BitmapUtils.getBitMap(mEntity.getLocalSourceId(), mContext);
			BitmapManager.putBitmapCache(mEntity.getLocalSourceId(), mBitmap);
		}
		targetHeight=mBitmap.getHeight()*getLayoutParams().width/mBitmap.getWidth();
	}

	@Override
	public void load(InputStream is) {
		

	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		rectF=new RectF(getPaddingLeft(), topOffset+getPaddingTop(), getLayoutParams().width+getPaddingLeft(),topOffset+targetHeight+getPaddingTop());
		if(rectF.top>getPaddingTop()){
			rectF.top=getPaddingTop();
		}else if(rectF.top<getLayoutParams().height-targetHeight+getPaddingTop()){
			rectF.top=getLayoutParams().height-targetHeight+getPaddingTop();
		}
		topOffset=rectF.top-getPaddingTop();
		rectF.bottom=rectF.top+targetHeight;
		if (mBitmap != null) {
			Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_MOVE){
			int dy=(int) (event.getY()-oldEvent.getY());
			topOffset+=dy;
			invalidate();
		}
		oldEvent=MotionEvent.obtain(event);
		return true;
	}
	
	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = entity;
	}
  
	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
	}

	@Override
	public void hide() {
		this.clearAnimation();
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		if(getVisibility() == View.VISIBLE)return;
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(mEntity,
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
	public void callBackListener() {
		if (isSendAutoPage == false) {
			onComponentCallbackListener.setPlayComplete();
			isSendAutoPage = true;
		}

	}
	/***************************下面都是属性动画使用相关代码*******************************/
	public ViewRecord initRecord;
	@SuppressLint("NewApi")
	public ViewRecord getCurrentRecord(){
		ViewRecord curRecord = new ViewRecord();
		curRecord.mHeight = getLayoutParams().width;
		curRecord.mWidth = getLayoutParams().height;
		
		curRecord.mX = getX();
		curRecord.mY = getY();
		curRecord.mRotation = getRotation();
//		curRecord.mAlpha = getAlpha();
		return curRecord;
	}
 
}
