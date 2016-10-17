package com.hl.android.view.component.moudle;

import java.util.ArrayList;
import java.util.LinkedList;

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
public class SubCatalogVScrollUIComponent extends View {
	private Paint mPaint;
	private LinkedList<MyRect> rects;
	private int lastPosition;
	private int AGroupRectsLengthY;
	private float curFlingSpeed;
	public boolean mIsMoveAuto;
	public boolean startBeginAnim;
	private int hasStartCount;
	private ArrayList<Bitmap> mBitmaps;
	private ArrayList<Integer> mIndexs;
	private float mWidth;
	private float mHeight;
	
	public SubCatalogVScrollUIComponent(Context context,ArrayList<Bitmap> bitmaps,ArrayList<Integer> indexs,float width,float height) {
		super(context);
		mPaint = new Paint();
		mBitmaps=bitmaps;
		mIndexs=indexs;
		mWidth=width;
		mHeight=height;
		loadRects();
	}

	private void loadRects() {
		rects = new LinkedList<SubCatalogVScrollUIComponent.MyRect>();
		boolean hasLoadRects = false;
		while (!hasLoadRects) {
			loadAGroupRects();
			if (lastPosition >= (mHeight + 4 * AGroupRectsLengthY)) {
				hasLoadRects = true;
			}
		}
	}

	private void loadAGroupRects() {
		for (int i = 0; i < mBitmaps.size(); i++) {
			MyRect myRect=null;
			if(i==0){
				myRect= new MyRect(mBitmaps.get(0),mIndexs.get(0), lastPosition);
			}else{
				myRect=new MyRect(mBitmaps.get(i),mIndexs.get(i), rects.get(rects.size()-1).mPositionY + rects.get(rects.size()-1).mInHeight);
			}
			rects.add(myRect);
		}
		if (lastPosition == 0) {
			lastPosition = rects.get(rects.size() - 1).mPositionY+ rects.get(rects.size() - 1).mInHeight;
			AGroupRectsLengthY = lastPosition;
		} else {
			lastPosition = rects.get(rects.size() - 1).mPositionY+ rects.get(rects.size() - 1).mInHeight;
		}
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
		while(rects.getFirst().mPositionY <=-AGroupRectsLengthY){
			rects.getFirst().mPositionY = rects.getLast().mPositionY+ rects.getLast().mInHeight;
			rects.addLast(rects.getFirst());
			rects.removeFirst();
		}
		while (rects.getLast().mPositionY + rects.getLast().mInHeight >= mHeight + 2*AGroupRectsLengthY) {
			rects.getLast().mPositionY = rects.getFirst().mPositionY- rects.getLast().mInHeight;
			rects.addFirst(rects.getLast());
			rects.removeLast();
		}
		if(startBeginAnim){
			for (int i = 0; i < rects.size(); i++) {
				rects.get(i).mPositionY+=AGroupRectsLengthY/30;
				if(hasStartCount<AGroupRectsLengthY%30){
					rects.get(i).mPositionY+=1;
				}
			}
			hasStartCount++;
			if(hasStartCount>=30){
				startBeginAnim=false;
				hasStartCount=0;
			}
		}
		if(mIsMoveAuto){
			if(curFlingSpeed>0){
				int speed=(int) curFlingSpeed;
				for (int i = 0; i < rects.size(); i++) {
					rects.get(i).mPositionY+=speed;
				}
				curFlingSpeed*=0.97f;
				if(speed<=1.0f){
				   mIsMoveAuto=false;
				}
			}else if(curFlingSpeed<0){
				int speed=(int) curFlingSpeed;
				for (int i = 0; i < rects.size(); i++) {
					rects.get(i).mPositionY+=speed;
				}
				curFlingSpeed*=0.97f;
				if(speed>=-1.0f){
				   mIsMoveAuto=false;
				}
			}
		}
	}

	public LinkedList<MyRect> getRects(){
		return rects;
	}
	
	public void moveAutoWidthSpeed(float flingSpeed){
		curFlingSpeed=flingSpeed;
		mIsMoveAuto=true;
	}
	class MyRect {
		public Bitmap mDrawBitmap;
		public int mPositionY = 0;
		public int mInHeight;
		public float mInWidth;
		private Rect srcRect;
		private RectF dstRect;
		public int mIndex;

		public MyRect(Bitmap bitmap4draw, int index,int positionY) {
			mDrawBitmap = bitmap4draw;
			mIndex=index;
			mPositionY = positionY;
			mInWidth = mWidth;
			mInHeight = (int) (mWidth*1.0f / mDrawBitmap.getWidth() * mDrawBitmap.getHeight());
		}

		private void drawMe(Canvas canvas) {
			srcRect = new Rect(0, 0, mDrawBitmap.getWidth(),mDrawBitmap.getHeight());
			dstRect = new RectF(getPaddingLeft(), getPaddingTop()+mPositionY, getPaddingLeft()+mInWidth, getPaddingTop()+mPositionY + mInHeight);
			canvas.drawBitmap(mDrawBitmap, srcRect, dstRect, mPaint);
		}
	}
	public void doBeginAnim() {
		startBeginAnim=true;
	}
}
