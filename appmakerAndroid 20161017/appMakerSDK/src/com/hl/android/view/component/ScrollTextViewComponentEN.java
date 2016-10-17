package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.ScrollView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.textview.TextViewComponent;

public class ScrollTextViewComponentEN extends ScrollView implements Component{
	public ComponentEntity entity = null;
	public AnimationSet animationset = null;
	private boolean isAutoScroll = false;

	public ScrollTextViewComponentEN(Context context) {
		super(context);
	}

	public ScrollTextViewComponentEN(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;
		this.setFadingEdgeLength(0);
		this.setVerticalFadingEdgeEnabled(false);
	}

	@Override
	public ComponentEntity getEntity() {
		// TODO Auto-generated method stub
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;

	}
 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	TextViewComponent textView = null;

	@Override
	public void load() {
		setScrollContainer(true);
		setFocusable(true);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);
		textView = new TextViewComponent(this.getContext(), entity);
		LayoutParams layoutParams = new LayoutParams(
				this.getLayoutParams().width, this.getLayoutParams().height);
		textView.setLayoutParams(layoutParams);
		textView.loadText();
		//
		this.addView(textView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (null == textView){
			return;
		}
		int height = textView.getTextHeight();
		if (height > (b - t)) {
			textView.layout(0, 0, r - l, height);
		} else {
			textView.layout(0, 0, r - l, b - t);
		}

	}

	private void scrollText() {
		this.scrollBy(0, 1);
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				scrollText();
				break;
			}
			super.handleMessage(msg);
		}
	};
	MyThread autoThread = null;

	@SuppressWarnings("unused")
	private void autoScroll() {
		autoThread = new MyThread();
		autoThread.start();
	}

	class MyThread extends Thread implements Runnable {
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					Message message = new Message();
					message.what = 1;
					if (isAutoScroll == true) {
						myHandler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {

		this.invalidate();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	@Override
	public void hide() {
		this.clearAnimation();
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
