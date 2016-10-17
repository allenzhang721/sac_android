package com.hl.android.view.pageflip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.hl.android.HLActivity;
import com.hl.android.R;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;

public class PageWidgetNew extends AbstractPageFlipView {
	private int mCornerX = 0;
	private int mCornerY = 0;
	private Path mPath0;
	private Path mPath1;
	PointF mTouch = new PointF(); //
	PointF mBezierStart1 = new PointF();
	PointF mBezierControl1 = new PointF();
	PointF mBeziervertex1 = new PointF();
	PointF mBezierEnd1 = new PointF();

	PointF mBezierStart2 = new PointF();
	PointF mBezierControl2 = new PointF();
	PointF mBeziervertex2 = new PointF();
	PointF mBezierEnd2 = new PointF();

	float mMiddleX;
	float mMiddleY;
	float mDegrees;
	float mTouchToCornerDis;
	int[] mBackShadowColors;
	int[] mFrontShadowColors;
	GradientDrawable mBackShadowDrawableLR;
	GradientDrawable mFolderShadowDrawableLR;
	GradientDrawable mFrontShadowDrawableHBT;
	GradientDrawable mFrontShadowDrawableHTB;
	GradientDrawable mFrontShadowDrawableVRL;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mBitmapPaint;
	Paint paint;
	private int index = 1000;
	private int step = 1;

	MediaPlayer media;
	public PageWidgetNew(Context context) {
		super(context);
		media = MediaPlayer.create(context, R.raw.flip);
		try {
			media.prepare();
		} catch (Exception e) {
		}
	}
	public void playNext() {
		mCornerX = getWidth();
		mCornerY = getHeight();
		
		mTouch.x = (float) (mCornerX * 0.9);
		mTouch.y = (float) (mCornerY * 0.6);
		index = 5;
		step = 10;
		this.postInvalidate();
	}
	public void playPrev() {
		mCornerX = getWidth();
		mCornerY = getHeight();
		if(getWidth()<getHeight()){
			mTouch.x = -getWidth();
			mTouch.y = (float) (mCornerY * 0.8);
		}else{
			mTouch.x = (float) (mCornerX * 0.9);
			mTouch.y = -getHeight();
		}
		index = 0;
		step = -10;
		this.postInvalidate();
	}
	public void show(){
		index = 0;
		mTouch.y = getHeight();
		mTouch.x = getWidth();
		bringToFront();
		setVisibility(View.VISIBLE);
		//保证公共页在翻页视图的上面
		BookController.getInstance().hlActivity.commonLayout.bringToFront();
	}
	private void initDraw() {
		if(mPath0==null)mPath0 = new Path();
		if(mPath1==null)mPath1 = new Path();
		if(mFolderShadowDrawableLR==null)createDrawable();
		if(mBitmap==null){
//			mBitmap.recycle();
//			mBitmap = null;
			try{
				mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
						Bitmap.Config.ARGB_8888);
			}catch(OutOfMemoryError e){
				try{
					BitmapUtils.recycleBitmap(mBitmap);
					mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
							Bitmap.Config.ARGB_4444);
				}catch(OutOfMemoryError ex){
					BitmapUtils.recycleBitmap(mBitmap);
					mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
							Bitmap.Config.ALPHA_8);
				}
			}
		}
			
		if(mBitmapPaint==null)mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		if(mCanvas==null)mCanvas = new Canvas(mBitmap); 
		 
	}

	private PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	private void calcPoints() {
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;

		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
		mBezierControl1.y = mCornerY;
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;
		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		try{
			mPath0.reset();
			mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
			mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
					mBezierEnd1.y);
			mPath0.lineTo(mTouch.x, mTouch.y);
			mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
			mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
					mBezierStart2.y);
			mPath0.lineTo(mCornerX, mCornerY);
			mPath0.close();
			canvas.save();
			canvas.clipPath(path, Region.Op.XOR);
			canvas.drawBitmap(bitmap, 0, 0, null); 
			canvas.restore();	
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();
		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if(bitmap!=null&&!bitmap.isRecycled())canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		canvas.restore();
	}
 

	@Override
	protected void onDraw(Canvas canvas) {
//		if (isinit) {
//			initDraw();
//			isinit = false;
//		}
		initDraw();
		mCanvas.drawColor(0xFFAAAAAA);
		canvas.drawColor(0xFFAAAAAA);
		calcPoints();
		drawCurrentPageArea(mCanvas, mCurPageBitmap, mPath0);
		
		if(mNextPageBitmap!=null&&!mNextPageBitmap.isRecycled())drawNextPageAreaAndShadow(mCanvas, mNextPageBitmap);
		if(mBitmap!=null&&!mBitmap.isRecycled())canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if(index==1000)return;
		if (index < 100 && mTouch.y > -getWidth() && mTouch.x > -getHeight()) {
			
			if (getWidth() > getHeight()) {
				mTouch.y = (float) (mTouch.y - index * step);
			} else {
				mTouch.x = (float) (mTouch.x - index * step);
			}
			
//			if(index==0){
//				postInvalidate();
//			}else{
//				postInvalidate();
//			}
//			postInvalidateDelayed(1000);//();
			postInvalidateDelayed(50);
			index = index + Math.abs(step);
		}else{
			Log.d("hl","pageflip onDraw");
			mAction.doAction();
			BookController.getInstance().revokeCommonPage();
			Log.d("hl","pageflip doAction");
			BookController.getInstance().startPlay();
			Log.d("hl","pageflip startPlay");
			hide();
		}
	}

	private void createDrawable() {
		int[] color = { 0x333333, 0xB0333333 };
		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xFF111111, 0x111111 };

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowColors = new int[] { 0x80888888, 0x888888 };
		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}
	ActionOnEnd mAction;
	@Override
	public void play(int pageIndex, int newPageindex, ActionOnEnd action) {
		try{
			media.seekTo(0);
			media.start();
		}catch(Exception e){
		}
		mAction = action;
		if(pageIndex<newPageindex){
			new CountDownTimer(1,1) {
				
				@Override
				public void onTick(long millisUntilFinished) {
				}
				
				@Override
				public void onFinish() {
					BitmapUtils.recycleBitmap(mNextPageBitmap);
					mNextPageBitmap = BookController.getInstance().getCurrentBookSnap();
					playNext();
				}
			}.start();
		}else{
			new CountDownTimer(1,1) {
				@Override
				public void onTick(long millisUntilFinished) {
				}
				
				@Override
				public void onFinish() {

					BitmapUtils.recycleBitmap(mNextPageBitmap);
					mNextPageBitmap = BookController.getInstance().getCurrentBookSnap();
					Bitmap tmp = mCurPageBitmap;
					mCurPageBitmap = mNextPageBitmap;
					mNextPageBitmap = tmp;
					playPrev();
				}
			}.start();
		}
	}

	@Override
	public void hide() {
		this.setVisibility(View.GONE);
		setBackgroundColor(Color.TRANSPARENT);
		this.setEnabled(true);
		this.setClickable(true);
		BitmapUtils.recycleBitmap(mNextPageBitmap);
		BitmapUtils.recycleBitmap(mCurPageBitmap);
		HLActivity a = (HLActivity) getContext();
//		a.commonLayout.setBackgroundColor(Color.RED);
//		a.contentLayout.setBackgroundColor(Color.RED);
//		setBackgroundColor(Color.RED)
//		BitmapUtils.recycleBitmap(mBitmap);
	}
}
