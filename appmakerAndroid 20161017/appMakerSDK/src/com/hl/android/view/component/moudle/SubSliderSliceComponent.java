package com.hl.android.view.component.moudle;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

@SuppressLint("ViewConstructor")
public class SubSliderSliceComponent  extends View{
	private  ArrayList<Bitmap> mBitmaps;
	private  float mPosition;
	private  Paint mPaint;
	private  float mSingleWidth;
	private  float mSingleHeight;
	public ArrayList<MyRect> rects;
	public MyRect myRect1;
	public MyRect myRect2;
	public MyRect myRect3;
	public boolean isMoveToUp;
	public boolean isMoveToDown;
	private float mDistanceTomove;
	private float halfDistanceTomove;
	private boolean mShouldInitNext;
	private float moveSpeed=1;
	private boolean mIsRight;
	
	public SubSliderSliceComponent(Context context,ArrayList<Bitmap> bitmaps,float width,float height,boolean isRight) {
		super(context);
		mBitmaps=bitmaps;
		mSingleWidth=width;
		mSingleHeight=height;
		mPosition=-mSingleHeight;
		mIsRight=isRight;
		rects=new ArrayList<SubSliderSliceComponent.MyRect>();
		if(isRight){
			myRect1=new MyRect(1, 0);
			myRect2=new MyRect(0, 1);
			myRect3=new MyRect(bitmaps.size()-1, 2);
		}else{
			myRect1=new MyRect(bitmaps.size()-1, 0);
			myRect2=new MyRect(0, 1);
			myRect3=new MyRect(1, 2);
		}
		rects.add(myRect1);
		rects.add(myRect2);
		rects.add(myRect3);
		mPaint=new Paint();
		
	}

	public void setposition(float position){
		mPosition=position;
	}
	
	public float getPosition() {
		return mPosition;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		for (int i = 0; i < rects.size(); i++) {
			rects.get(i).drawMe(canvas);
		}
		logic();
		postInvalidate();
	}
	
	private void logic() {
		if(isMoveToUp){
			if(mDistanceTomove<=0||Math.abs(mDistanceTomove-moveSpeed)<moveSpeed){
				moveSpeed=30;
				isMoveToUp=false;
				mDistanceTomove=0;
				setposition(-mSingleHeight);
				if(mShouldInitNext){
					initBitmap(true);
				}
			}else{
				mPosition-=moveSpeed;
				mDistanceTomove-=moveSpeed;
				if(mDistanceTomove>=halfDistanceTomove){
					moveSpeed+=2;
				}else{
					moveSpeed-=1;
				}
			}
		}else if(isMoveToDown){
			if(mDistanceTomove<=0||Math.abs(mDistanceTomove-moveSpeed)<moveSpeed){
				moveSpeed=30;
				isMoveToDown=false;
				mDistanceTomove=0;
				setposition(-mSingleHeight);
				if(mShouldInitNext){
					initBitmap(false);
				}
			}else{
				mPosition+=moveSpeed;
				mDistanceTomove-=moveSpeed;
				if(mDistanceTomove>=halfDistanceTomove){
					moveSpeed+=2;
				}else{
					moveSpeed-=1;
				}
			}
		}
	}

	private void initBitmap(boolean isMoveToUp) {
		if(isMoveToUp){
			myRect1.mIndexOfBitmaps=myRect2.mIndexOfBitmaps;
			myRect2.mIndexOfBitmaps=myRect3.mIndexOfBitmaps;
			if(!mIsRight){
				myRect3.mIndexOfBitmaps++;
				if(myRect3.mIndexOfBitmaps>=mBitmaps.size()){
					myRect3.mIndexOfBitmaps=0;
				}
			}else{
				myRect3.mIndexOfBitmaps--;
				if(myRect3.mIndexOfBitmaps<0){
					myRect3.mIndexOfBitmaps=mBitmaps.size()-1;
				}
			}
		}else{
			myRect3.mIndexOfBitmaps=myRect2.mIndexOfBitmaps;
			myRect2.mIndexOfBitmaps=myRect1.mIndexOfBitmaps;
			if(mIsRight){
				myRect1.mIndexOfBitmaps++;
				if(myRect1.mIndexOfBitmaps>=mBitmaps.size()){
					myRect1.mIndexOfBitmaps=0;
				}
			}else{
				myRect1.mIndexOfBitmaps--;
				if(myRect1.mIndexOfBitmaps<0){
					myRect1.mIndexOfBitmaps=mBitmaps.size()-1;
				}
			}
		}
	}

	class MyRect{
		public int mIndexOfBitmaps;
		public int mIntdexOfPositon;
		public Bitmap mDrawBitmap;
		private Rect srcRect;
		private RectF dstRect;
		public MyRect(int indexOfBitmaps, int intdexOfPositon) {
			mIndexOfBitmaps= indexOfBitmaps;
			mIntdexOfPositon = intdexOfPositon;
		}
		private void drawMe(Canvas canvas){
			mDrawBitmap=mBitmaps.get(mIndexOfBitmaps);
			srcRect=new Rect(0, 0,mDrawBitmap.getWidth(), mDrawBitmap.getHeight());
			dstRect=new RectF(getPaddingLeft(),getPaddingTop()+mPosition+mSingleHeight*mIntdexOfPositon, getPaddingLeft()+mSingleWidth,getPaddingTop()+mPosition+mSingleHeight*mIntdexOfPositon+mSingleHeight);
			canvas.drawBitmap(mDrawBitmap, srcRect, dstRect, mPaint);
		}
	}

	public void MoveToUp(float distance,boolean shouldInitNext) {
		mDistanceTomove=distance;
		halfDistanceTomove=mDistanceTomove/2.0f;
		mShouldInitNext=shouldInitNext;
		isMoveToUp=true;
	}

	public void MoveToDown(float distance,boolean shouldInitNext) {
		mDistanceTomove=distance;
		halfDistanceTomove=mDistanceTomove/2.0f;
		mShouldInitNext=shouldInitNext;
		isMoveToDown=true;
	}
}
