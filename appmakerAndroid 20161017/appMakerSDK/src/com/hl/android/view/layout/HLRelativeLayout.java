package com.hl.android.view.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.GestureDetector.OnGestureListener;

public class HLRelativeLayout extends RelativeLayout implements OnGestureListener{
	private Bitmap currentscreen;
	//private GestureDetector detector;
	public HLRelativeLayout(Context context) {
		super(context);
		//detector = new GestureDetector(context, this);
		this.setBackgroundColor(Color.WHITE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if (BookController.getInstance().getViewPage().getEntity().isEnableNavigation() == false){
//			return super.dispatchTouchEvent(ev);
//		}
//		if (detector.onTouchEvent(ev)){
//			return true;
//		}
//		
		boolean result = false;
		try{
			result = super.dispatchTouchEvent(ev);
		}catch(Exception e){
			Log.e("hl", "touch error",e);
		}
		return result;
	}

	/**
	 * 得到当前页面的截屏
	 * 
	 * @return
	 */
	public Bitmap getCurrentScreen() {
		try {
			if (null == currentscreen
					|| currentscreen.getWidth() != this.getMeasuredWidth()) {
				if (currentscreen != null) {
					currentscreen.recycle();
				}
				
				currentscreen = null;
				
				try {
					this.currentscreen = Bitmap.createBitmap(
							this.getMeasuredWidth(), this.getMeasuredHeight(),
							Bitmap.Config.RGB_565);
				} catch (OutOfMemoryError e) {
					this.currentscreen = Bitmap.createBitmap(
							100, 100,
							Bitmap.Config.ALPHA_8);
				}
			}

			draw(new Canvas(this.currentscreen));

		} catch (Exception ex) {
			//ex.printStackTrace();
			return null;
		}
		return this.currentscreen;
	}
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (e1.getPointerCount() >= 2) {
				return true;
			}

			if (e1 == null || e2 == null) {
				return false;
			}
			if (e1.getX() - e2.getX() > HLSetting.FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > HLSetting.FLING_MIN_VELOCIT) {
				BookController.getInstance().flipPage(1);
			}
			if (e1.getX() - e2.getX() < -HLSetting.FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > HLSetting.FLING_MIN_VELOCIT) {
				BookController.getInstance().flipPage(-1);
			}

			// 针对杂志等可以竖向浏览子页 事件改为viewpage中
			if (BookSetting.ISSUBPAGE_ENABLE == true) {
				if (e1.getY() - e2.getY() > HLSetting.FLING_MIN_DISTANCE
						&& Math.abs(velocityX) > HLSetting.FLING_SUB_MIN_VELOCIT) {
					BookController.getInstance().flipSubPage(1);
				}
				if (e1.getY() - e2.getY() < -HLSetting.FLING_MIN_DISTANCE
						&& Math.abs(velocityX) > HLSetting.FLING_SUB_MIN_VELOCIT) {
					BookController.getInstance().flipSubPage(-1);
				}
			}

		} catch (Exception e) {
			Log.e("hl", "load error",e);
			return false;
		}
		return true;
	}
}
