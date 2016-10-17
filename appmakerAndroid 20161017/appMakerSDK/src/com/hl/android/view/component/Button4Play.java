package com.hl.android.view.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
/**
 * 自定义音频播放按钮
 * @author hl
 * @version 1.0
 * @createed 2013-12-23
 */
public class Button4Play extends View {

	private ActionListener mListener;
	private Context mContext;
	public static final int BTN_STATE_PLAY_NOMAL = 0x1001;
	public static final int BTN_STATE_PLAY_TOUCH = 0x1002;
	public static final int BTN_STATE_STOP_NOMAL = 0x1003;
	public static final int BTN_STATE_STOP_TOUCH = 0x1004;
	public static final int BTN_NOMAL_COLOR =Color.rgb(218, 218, 218);
	public static final int BTN_TOUCH_COLOR =Color.GRAY;
	private int currentState = BTN_STATE_PLAY_NOMAL;
	private PaintFlagsDrawFilter drawFilter=new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
	
	private Paint mPaint;
	private Path path;

	public Button4Play(Context context) {
		super(context);
		mContext = context;
		mPaint=new Paint();
		mPaint.setStrokeWidth(4);
		mPaint.setStyle(Style.FILL);
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!touchInTheRect(event, 0, 0, getLayoutParams().width, getLayoutParams().height)){
					if(currentState==BTN_STATE_PLAY_TOUCH){
						currentState=BTN_STATE_PLAY_NOMAL;
					}else if(currentState==BTN_STATE_STOP_TOUCH){
						currentState=BTN_STATE_STOP_NOMAL;
					}
					postInvalidate();
					return true;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					if(currentState==BTN_STATE_PLAY_NOMAL){
						currentState=BTN_STATE_PLAY_TOUCH;
					}else if(currentState==BTN_STATE_STOP_NOMAL){
						currentState=BTN_STATE_STOP_TOUCH;
					}else{
						break;
					}
					postInvalidate();
					break;
				case MotionEvent.ACTION_UP:
					if(currentState==BTN_STATE_PLAY_TOUCH){
						doPlayAction();
					}else if(currentState==BTN_STATE_STOP_TOUCH){
						doStopAction();
					}
					postInvalidate();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	
	
	private boolean touchInTheRect(MotionEvent event, float x, float y, float width,float height) {
			float tx=event.getX();
			float ty=event.getY();
			if (tx>x) {
				if (tx < x+width) {
					if (ty > y) {
						if (ty < y + height) {
							return true;
						}
					}
				}
			}
			return false;
	}
	
	public ActionListener getActionListener() {
		return mListener;
	}

	public void setActionListener(ActionListener mListener) {
		this.mListener = mListener;
	}

	interface ActionListener {
		
		public void onDoPlay();

		public void onDoStop();
	}

	public void change2ShowStop(){
		currentState=BTN_STATE_STOP_NOMAL;
		postInvalidate();
	}
	
	public void change2ShowPlay(){
		currentState=BTN_STATE_PLAY_NOMAL;
		postInvalidate();
	}
	
	
	public void doPlayAction(){
		currentState=BTN_STATE_STOP_NOMAL;
		if(mListener!=null){
			mListener.onDoPlay();
		}
		postInvalidate();
	}
	
	public void doStopAction(){
		currentState=BTN_STATE_PLAY_NOMAL;
		if(mListener!=null){
			mListener.onDoStop();
		}
		postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(drawFilter);  
		if(path==null){
			path=new Path();
			path.moveTo(getLayoutParams().width/3.0f, getLayoutParams().height/3.0f);
			path.lineTo(getLayoutParams().width*2/3.0f, getLayoutParams().height/2.0f);
			path.lineTo(getLayoutParams().width/3.0f, getLayoutParams().height*2/3.0f);
		}
		switch (currentState) {
		case BTN_STATE_PLAY_NOMAL:
			mPaint.setColor(BTN_NOMAL_COLOR);
			canvas.drawPath(path, mPaint);
			break;
		case BTN_STATE_PLAY_TOUCH:
			mPaint.setColor(BTN_TOUCH_COLOR);
			canvas.drawPath(path, mPaint);
			break;
		case BTN_STATE_STOP_NOMAL:
			mPaint.setColor(BTN_NOMAL_COLOR);
			canvas.drawLine(getLayoutParams().width/3.0f-2, getLayoutParams().height/3.0f, getLayoutParams().width/3.0f-2, getLayoutParams().height*2/3.0f, mPaint);
			canvas.drawLine(getLayoutParams().width*2/3.0f-2, getLayoutParams().height/3.0f, getLayoutParams().width*2/3.0f-2, getLayoutParams().height*2/3.0f, mPaint);
			break;
		case BTN_STATE_STOP_TOUCH:
			mPaint.setColor(BTN_TOUCH_COLOR);
			canvas.drawLine(getLayoutParams().width/3.0f-2, getLayoutParams().height/3.0f, getLayoutParams().width/3.0f-2, getLayoutParams().height*2/3.0f, mPaint);
			canvas.drawLine(getLayoutParams().width*2/3.0f-2, getLayoutParams().height/3.0f, getLayoutParams().width*2/3.0f-2, getLayoutParams().height*2/3.0f, mPaint);
			break;
		default:
			break;
		}
	}
}
