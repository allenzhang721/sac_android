package com.hl.realtest.view;

import com.hl.realtest.ScreenAdapter;
import com.hl.realtest.shelves.ShelvesActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.view.View;

public class TasksCompletedView extends View {

	private Paint mRingPaint;
	private Paint mTextPaint;
	private float mRadius;
	private float mRingRadius;
	private float mStrokeWidth;
	private int mXCenter;
	private int mYCenter;
	private float mTxtWidth;
	private float mTxtHeight;
	private float mTotalProgress = 100;
	private int mProgress;
	private Paint mRingPaint1;
	private float mStrokeWidth1;

	public TasksCompletedView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(Color.WHITE);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint1 = new Paint(mRingPaint);
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mProgress=0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		mStrokeWidth=getWidth()/30.0f;
		mRingPaint.setStrokeWidth(mStrokeWidth);
		mStrokeWidth1=mStrokeWidth/2.0f;
		mRingPaint1.setStrokeWidth(mStrokeWidth1);
		mTextPaint.setTextSize(ScreenAdapter.calcWidth(40*ShelvesActivity.myRatio));
		mRadius=getWidth()/5.0f;
		mRingRadius = mRadius + mStrokeWidth/2.0f;
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		if (mProgress >= 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2.0f + oval.left;
			oval.bottom = mRingRadius * 2.0f + oval.top;
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
			canvas.drawCircle(mXCenter, mYCenter, mRadius+mStrokeWidth+mStrokeWidth1/2.0f-1, mRingPaint1);
			String txt = mProgress + "%";
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2.0f, mYCenter + mTxtHeight / 4.0f, mTextPaint);
		}
	}
	
	public void setProgress(int progress) {
		mProgress = progress;
		postInvalidate();
	}

	public float getMax() {
		return mTotalProgress;
	}

}
