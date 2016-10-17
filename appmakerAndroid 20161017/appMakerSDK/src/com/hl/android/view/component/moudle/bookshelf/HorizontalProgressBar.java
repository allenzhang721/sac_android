package com.hl.android.view.component.moudle.bookshelf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

@SuppressLint("DrawAllocation")
public class HorizontalProgressBar extends View {
	private float max = 1000;
	private float progress = 0;
	private Paint bgPaint = new Paint();
	private Paint fgPaint = new Paint();

	public HorizontalProgressBar(Context context) {
		super(context);
		bgPaint.setColor(Color.GRAY);
		bgPaint.setStyle(Style.FILL);
		fgPaint.setColor(Color.YELLOW);
		fgPaint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		RectF rect = new RectF(0, 0, getWidth(), getHeight());
		canvas.drawRoundRect(rect, 6, 6, bgPaint);
		canvas.clipRect(new RectF(0, 0, getWidth() * (progress/max), getHeight()));
		canvas.drawRoundRect(rect, 6, 6, fgPaint);
		super.onDraw(canvas);
	}

	@Override
	public void setBackgroundColor(int color) {
		bgPaint.setColor(color);
	}
	
	public void setProgressColor(int color) {
		fgPaint.setColor(color);
	}

	public int getMax() {
		return (int) max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getProgress() {
		return (int) progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}
	
}
