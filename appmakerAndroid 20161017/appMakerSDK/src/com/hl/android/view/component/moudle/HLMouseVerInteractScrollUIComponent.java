package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

@SuppressLint({ "DrawAllocation", "ViewConstructor" })
public class HLMouseVerInteractScrollUIComponent extends View implements Component{
	private Context mContext;
	private ComponentEntity mEntity;
	private ArrayList<MyRect> rects;
	private int mHeight;
//	private ArrayList<MyEventValue> eventValues;
	private int offsetY = 0;
	private int bottomY = 0;
	private Animator animator;
	public HLMouseVerInteractScrollUIComponent(Context context,ComponentEntity entity) {
		super(context);
		mContext=context;
		mEntity=entity;
	}
	
	private void loadRects() {
		rects=new ArrayList<MyRect>();
		ArrayList<String> sourceIDS = ((MoudleComponentEntity)mEntity).getSourceIDList();
		if(sourceIDS==null)return;
		for (int i = 0; i < sourceIDS.size(); i++) {
			//临时使用的变量，等到软件端bug修复以后就不需要替换后缀了
			String localSourceID =  sourceIDS.get(i);
			int size[] = {getLayoutParams().width,0};
			
			Bitmap bitmap4draw=BitmapUtils.getBitMap(localSourceID, mContext,size);
			size[1] = (int) ((float)size[1]*(float)getLayoutParams().width/(float)size[0]);
			size[0] = getLayoutParams().width;
			MyRect myRect=new MyRect(bitmap4draw,size);
			rects.add(myRect);
			
			bottomY += size[1];
		}
	}
//	
	//
	RectF dst = new RectF(getPaddingLeft(),getPaddingTop(),getPaddingLeft(),getPaddingTop());
	//需要回收的图片需要放在这个列表中
	ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
			dst.top = offsetY+getPaddingTop();
			for(MyRect myRect : rects){
				//如果要绘制的图片位置在视窗下边就不绘制
				if(dst.top >= getLayoutParams().height)break;
				dst.bottom = dst.top + myRect.mSize[1];
				//如果绘制的图片位置在视窗上边也不绘制
				if(dst.bottom <= 0 ){
					dst.top = dst.bottom;
					continue;
				}
				if(myRect.mDrawBitmap != null && myRect.mDrawBitmap.isRecycled())continue;
				canvas.drawBitmap(myRect.mDrawBitmap, null, dst, null);
				dst.top = dst.bottom;
			}
			new CountDownTimer(0, 0) {
				
				@Override
				public void onTick(long arg0) {
					
				}
				
				@Override
				public void onFinish() {
					doRectAction();
				}
			}.start();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private float lastoffsetY = 0;
	/**
	 * 执行进出区域所触发的操作
	 */
	private void doRectAction(){
		for(BehaviorEntity beheavior:mEntity.behaviors){
			if(beheavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_SLIDER_IN") || beheavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_SLIDER_OUT")){
				String[] splits = beheavior.EventValue.split(","); 
				float[] positions = {Float.parseFloat(splits[0]),Float.parseFloat(splits[1])};
				int top = (int) Math.min(positions[0], positions[1]);
				int bottom = (int) Math.max(positions[0], positions[1]);
				
				boolean curIsIn = -offsetY  > top && -offsetY < bottom;
				boolean lastIsIn = -lastoffsetY  > top && -lastoffsetY < bottom;
				if(curIsIn ^ lastIsIn){//两者不同则执行事件触发
					if(!curIsIn && beheavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_SLIDER_OUT")){
						BookController.getInstance().runBehavior(beheavior);
					} 
					if(curIsIn && beheavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_SLIDER_IN")){
						BookController.getInstance().runBehavior(beheavior);
					} 
				} 
			}
		}
		lastoffsetY = offsetY;
	}
	
	public void setOffsetY(int p) {
		offsetY = p;
		invalidate();
	}
	public int getOffsetY() {
		return offsetY;
	}
	class MyRect{
		public Bitmap mDrawBitmap;
		public int[] mSize;
		public MyRect(Bitmap bitmap4draw,int size[]) {
			mDrawBitmap=bitmap4draw;
			mSize = size;
		}
	}
	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity=entity;
	}
	SimpleOnGestureListener listener=new  SimpleOnGestureListener(){

		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2,float arg2, float arg3) {
			float aa;
			if(null!=animator&&animator.isRunning()){
				return true;
			}
			if(offsetY>mHeight-bottomY&&offsetY<0){
				aa=arg3;
			}else{
				aa=arg3/2;
				if(offsetY>=0){
					if(arg3>0){
						aa=arg3;
					}
				}else if(offsetY<=mHeight-bottomY){
					if(arg3<0){
						aa=arg3;
					}
				}
			}
			offsetY -= aa;
			invalidate();
			return true;
		}
	};
	GestureDetector detector = null;
	@Override
	public void load() {
		mHeight=getLayoutParams().height;
		dst.right = getLayoutParams().width;
		loadRects();
		setClickable(true);
		detector=new GestureDetector(mContext, listener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		if(event.getAction()==MotionEvent.ACTION_UP){
			if(offsetY>0){
				doGoBackAnimation(0);
			}else if(offsetY<mHeight-bottomY){
				doGoBackAnimation(mHeight-bottomY);
			}
		}
		return true;
	}
	

	private void doGoBackAnimation(int endOffset){
		if(animator!=null&&animator.isRunning())animator.cancel();
		animator = ObjectAnimator.ofInt(this, "offsetY",endOffset);
		animator.setDuration(500);
		animator.start();
	}
	
	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		if(rects==null){
			return;
		}
		for (int i = 0; i < rects.size(); i++) {
			BitmapUtils.recycleBitmap(rects.get(i).mDrawBitmap);
		}
		rects.clear();
		rects = null;
	}

	@Override
	public void hide() {
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(mEntity,
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
	
}
