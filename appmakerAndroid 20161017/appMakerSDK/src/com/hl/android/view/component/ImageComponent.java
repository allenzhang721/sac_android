package com.hl.android.view.component;

import java.io.InputStream;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

/**
 * 一般图片组件类
 * 
 * @author webcat
 * 
 */
@SuppressLint("NewApi")
public class ImageComponent extends ImageView implements Component, ComponentListener, ComponentPost {

	public ComponentEntity entity = null;
	private OnComponentCallbackListener onComponentCallbackListener;
	Bitmap bitmap = null;
	private boolean isSendAutoPage = false;
	Bitmap[][] bitmaps;
	int initWidth = 0;
	int initHeight = 0;
	int initX = 0;
	int initY = 0;

	

	public ImageComponent(Context context) {
		super(context);
	}

	public ImageComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
	}

	@Override
	public void load() {
		initX = getEntity().x;
		initY = getEntity().y;
		loadBitmap();
		this.setScaleType(ScaleType.FIT_XY);
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
		
		bitmap=BitmapManager.getBitmapFromCache(getEntity().localSourceId);
		if(bitmap==null||bitmap.isRecycled()){
			
			bitmap = BitmapUtils.getBitMap(getEntity().localSourceId, getContext(),size);
			BitmapManager.putBitmapCache(getEntity().localSourceId, bitmap);
		}
		this.setImageBitmap(bitmap);
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
	}

	@Override
	public void hide() {
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		if (getVisibility() == View.VISIBLE)
			;
		else
			setVisibility(View.VISIBLE);
		// 不能将这个放到最前面
//		 BookController.getInstance().getViewPage().bringChildToFront(this);
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
