package com.hl.android.view.component.moudle.imageseq;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;

public class HL3DViewHorFlipper extends ImageView implements Component,
		ComponentPost {
	ComponentEntity entity;
	private ArrayList<Bitmap> bitMapList;
	private int mCount;
	int mCurrentIndex = 0;

	public HL3DViewHorFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HL3DViewHorFlipper(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;
		this.setBackgroundColor(Color.TRANSPARENT);
		setScaleType(ScaleType.FIT_XY);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;

	}

	ArrayList<String> sourceIDS = null;

	@Override
	public void load() {
		sourceIDS = ((MoudleComponentEntity) this.entity).getSourceIDList();
		mCount = sourceIDS.size();
		mCurrentIndex = 0;
		this.setImageBitmapOnly(0);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	} 

	@Override
	public void play() {

	}
 
	Bitmap bitmap = null;
 

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

	private float oldTouchValue;

	public boolean onTouchEvent(MotionEvent touchevent) {

		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			oldTouchValue = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			float currentX = touchevent.getX();
			if (oldTouchValue < currentX) {
				this.loadPreBitmap();
			} else if (oldTouchValue > currentX) {
				this.loadNextBitmap();
			}
		}
		case MotionEvent.ACTION_UP: {
			break;
		}
		}
		return true;

	}

	public void loadNextBitmap() {
		if (mCurrentIndex == mCount - 1) {
			return;
		} else {
			mCurrentIndex++;
			mHandler.sendEmptyMessage(0);
		}
	}

	public void loadPreBitmap() {
		if (mCurrentIndex == 0) {
			return;
		} else {
			mCurrentIndex--;
			mHandler.sendEmptyMessage(0);
		}
	}

	int FLIP_MSG = 0;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FLIP_MSG) {
				setImageBitmapOnly(mCurrentIndex);
			}
		}
	};

	public void setImageBitmapOnly(int index) {
		if(bitmap!= null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		bitmap =BitmapUtils.getBitMap(sourceIDS.get(index),getContext(),
				 this.getLayoutParams().width,
				this.getLayoutParams().height);
		this.setImageBitmap(bitmap);
	}

	public ArrayList<Bitmap> getBitMapList() {
		return bitMapList;
	}

	public void setBitMapList(ArrayList<Bitmap> bitMapList) {
		this.bitMapList = bitMapList;
	}

	@Override
	public void recyle() {
		this.setImageBitmap(null);
		BitmapUtils.recycleBitmap(bitmap);
	}
}
