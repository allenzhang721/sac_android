package com.hl.android.view.component.moudle.slide;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

@SuppressLint({ "ViewConstructor", "DrawAllocation" })
public class HorizontalSlide extends View implements Component{
	private Context mContext;
	private Paint paint;
	private ArrayList<HRect> rects;
	private float mImageWith, mImageHeight;
	private float mImageSpace;
	private MotionEvent oldEvent = null;
	private float dx = 0;
	private float dy = 0;
	private float totalAbsDx = 0;
	private float totalAbsDy = 0;
	private float totalDx = 0;
	private boolean canmove = false;
	private ComponentEntity mEntity = null;
	private int CLICKSIZELIMIT = 5;

	public  long sleepTime = 5;
	public float totalMoveLength = 0;
	private float hasMoveLength = 0;

	public int mMoveState;
	public static final int MOVE_TO_RIGHT = 1000123;
	public static final int MOVE_TO_LEFT = 1000124;
	public static final int NO_MOVE = 1000125;
	
	private long lastTime = 0;
	private int nextStep;
	private boolean start;
	
	public HorizontalSlide(Context context, ComponentEntity entity) {
		super(context);
		this.mContext = context;
		this.mEntity = entity;
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		setBackgroundColor(Color.TRANSPARENT);
	}

	public float getImageWith() {
		return mImageWith;
	}

	public void setImageWith(float mImageWith) {
		this.mImageWith = mImageWith;
	}

	public float getImageHeight() {
		return mImageHeight;
	}

	public void setImageHeight(float mImageHeight) {
		this.mImageHeight = mImageHeight;
	}

	public void setImageSpace(float mImageSpace) {
		this.mImageSpace = mImageSpace;
	}

	public float getImageSpace() {
		return mImageSpace;
	}

	private ArrayList<HRect> getDataSource(ArrayList<String> sourceIDS,
			LayoutParams lp) {
		setImageSpace(0);
		ArrayList<HRect> souce = new ArrayList<HRect>();
		float endPosition=0;
		if (null != sourceIDS && sourceIDS.size() > 0) {
			for (int i = 0; i < sourceIDS.size(); i++) {
				Bitmap bitmap =BitmapManager.getBitmapFromCache(sourceIDS.get(i));
				if(bitmap==null){
					bitmap=BitmapUtils.getBitMap(sourceIDS.get(i), mContext);
					BitmapManager.putBitmapCache(sourceIDS.get(i), bitmap);
				}
				float ratio=1.0f*lp.height/bitmap.getHeight();
				HRect rect = new HRect(endPosition, 0,
						bitmap.getWidth()*ratio, lp.height,
						bitmap, i);
				endPosition+=bitmap.getWidth()*ratio;
				souce.add(rect);
			}
			totalMoveLength = endPosition - lp.width;
			if (endPosition > lp.width) {
				mMoveState = MOVE_TO_LEFT;
				canmove = true;
			} else {
				mMoveState = NO_MOVE;
				canmove = false;
			}
		}
		return souce;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_CLICK");
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			dx = event.getX() - oldEvent.getX();
			dy = event.getY() - oldEvent.getY();
			totalDx += dx;
			totalAbsDx += Math.abs(dx);
			totalAbsDy += Math.abs(dy);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_SLIDER_UP");
			if (totalAbsDx <= CLICKSIZELIMIT && totalAbsDy <= CLICKSIZELIMIT) {
				for (int i = 0; i < rects.size(); i++) {
					HRect mCurrentHRect = rects.get(i);
					if (hasTouchedRect(event.getX(), event.getY(),
							mCurrentHRect)) {
						doClickAction(mCurrentHRect);
						break;
					}
				}
				
			} else if (canmove && Math.abs(totalDx) > CLICKSIZELIMIT) {
				{
					if (totalDx > CLICKSIZELIMIT) {
						mMoveState = MOVE_TO_RIGHT;
					} else {
						mMoveState = MOVE_TO_LEFT;
					}
				}
			}
			totalAbsDx = 0;
			totalAbsDy = 0;
			totalDx = 0;
		}
		oldEvent = MotionEvent.obtain(event);
		return true;
	}

	private void doClickAction(HRect currentTouchRect) {
		if (currentTouchRect != null) {
			for (BehaviorEntity behavior : mEntity.behaviors) {
				if(behavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_ITEM_CLICK")){
					// 此处整理了分号分割的问题 ,为什么这么搞就是因为要兼容老版本 by zhaoq
					BehaviorHelper.doBeheavorForList(behavior, currentTouchRect.mIndex,
							mEntity.componentId);
				}
			}
		}
	}

	private boolean hasTouchedRect(float touchX, float touchY, HRect touchedRect) {
		if (touchX > touchedRect.mX) {
			if (touchX < touchedRect.mX + touchedRect.mWidth) {
				if (touchY > touchedRect.mY) {
					if (touchY < touchedRect.mY + touchedRect.mHeight) {
						return true;
					}
				}
			}
		}
		return false;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
		nextStep = getNextStep();
		hasMoveLength = hasMoveLength - nextStep;
		for (int i = 0; i < rects.size(); i++) {
			HRect mCurrentRect = rects.get(i);
			if(start){
				mCurrentRect.mX += nextStep;
			}
			mCurrentRect.drawMe(canvas, paint, i);
		}
		if (mMoveState != NO_MOVE) {
			if (hasMoveLength >= totalMoveLength) {
				mMoveState = MOVE_TO_RIGHT;
			} else if (hasMoveLength <= 0) {
				mMoveState = MOVE_TO_LEFT;
			}
		}
		postInvalidate();
	}

	@Override
	public ComponentEntity getEntity() {
		return this.mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = entity;
	}

	@Override
	public void load() {
		ViewGroup.LayoutParams lp = this.getLayoutParams();
		ArrayList<String> sourceIDS = ((MoudleComponentEntity) this.mEntity)
				.getSourceIDList();
		rects = getDataSource(sourceIDS, lp);
		sleepTime =((MoudleComponentEntity) this.mEntity).getTimerDelay();
//		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	

	@Override
	public void load(InputStream is) {

	}
 
	@Override
	public void play() {
		start=true;
	}

	@Override
	public void stop() {
		start=false;
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
	
	/**
	 * 获得我下次移动的距离
	 * @return
	 */
	private int getNextStep(){
		int result = 0;
		//如果是第一次计算，那么不需要移动
		if(lastTime==0){
			lastTime = System.currentTimeMillis();
			return 0;
		}
		if(mMoveState == NO_MOVE){
			return 0;
		}
		//如果间隔时间小于要求的时间间隔，那么就不需要移动
		long duration = System.currentTimeMillis() - lastTime;
		if(duration<sleepTime){
			return 0;
		}
		//下面开始计算移动的距离
		result = (int) (duration/sleepTime);
		//如果是向右侧移动，需要设置成负值
		if (mMoveState == MOVE_TO_LEFT) {
			result = -result;
		}
		lastTime = System.currentTimeMillis();
		return result;
	}
	
	class HRect {
		public float mX = 0;
		public float mY = 0;
		public float mWidth = 0;
		public float mHeight = 0;
		public int mIndex;
		public Bitmap mImageBitmap;
		public HRect(float x, float y, float width, float height,
				Bitmap bitmap, int index) {
			mX = x;
			mY = y;
			mWidth = width;
			mHeight = height;
			mImageBitmap = bitmap;
			mIndex = index;
		}

		public void drawMe(Canvas canvas, Paint paint, int columnIndex) {
			if (mImageBitmap != null) {
				RectF rect = new RectF(mX+getPaddingLeft(),mY+getPaddingTop(),mX+mWidth+getPaddingLeft(),mY+mHeight+getPaddingTop());
//				if(rect.left>getWidth()||rect.right<0)return;
				canvas.drawBitmap(mImageBitmap, null,rect, paint);
			}
		}
	}
}

