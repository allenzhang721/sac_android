package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

/**
 * 横向文本图片组件类
 * 
 * @author raozhicheng
 * 
 */
public class HorizontalImageComponent extends HorizontalScrollView implements Component, ComponentListener{
	private ComponentEntity mEntity;
	private OnComponentCallbackListener onComponentCallbackListener;
	private Bitmap mBitmap;
	private boolean isSendAutoPage = false;
	private Context mContext;
	private RectF rectF;
	private float leftOffset=0;
	private int targetWidth;
	private Paint mPaint;
	private MotionEvent oldEvent;
	public HorizontalImageComponent(Context context) {
		super(context);
	}

	public HorizontalImageComponent(Context context, ComponentEntity entity) {
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
		targetWidth=mBitmap.getWidth()*getLayoutParams().height/mBitmap.getHeight();
	}

	@Override
	public void load(InputStream is) {
		

	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG)); 
		rectF=new RectF(leftOffset+getPaddingLeft(), getPaddingTop(), leftOffset+targetWidth+getPaddingLeft(),getLayoutParams().height+getPaddingTop());
		if(rectF.left>getPaddingLeft()){
			rectF.left=getPaddingLeft();
		}else if(rectF.left<getLayoutParams().width-targetWidth+getPaddingLeft()){
			rectF.left=getLayoutParams().width-targetWidth+getPaddingLeft();
		}
		leftOffset=rectF.left-getPaddingLeft();
		rectF.right=rectF.left+targetWidth;
		canvas.drawBitmap(mBitmap, null, rectF, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_MOVE){
			int dx=(int) (event.getX()-oldEvent.getX());
			leftOffset+=dx;
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
}
