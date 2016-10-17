package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

public class HLVerSlideImageSelectUIComponent extends RelativeLayout implements Component{
	
	private Context mContext;
	private ComponentEntity mEntity;
	private Paint mPaint;
	private ArrayList<Bitmap> defBimaps;
	private ArrayList<Bitmap> selectBitmaps;
	public ArrayList<PositionAndHeight> positionAndHeights;
	private float totalHeight=0;
	private MyImagView myImageView;
	private int curSelectIndex=0;
	private int curWaitToSelectIndex=-1;
	private int longPressIndex=-1;
	private float mTopOffset=0;
	private RectF dst;
	protected boolean playMoveAni;
	private RectF longPressRectf;
	
 	public HLVerSlideImageSelectUIComponent(Context context,ComponentEntity entity) {
		super(context);
		mContext=context;
		mEntity=entity;
		mPaint=new Paint();
		defBimaps=new ArrayList<Bitmap>();
		selectBitmaps=new ArrayList<Bitmap>();
		positionAndHeights=new ArrayList<PositionAndHeight>();
	}

	private void loadBitmaps() {
		ArrayList<String> sourceIDS = ((MoudleComponentEntity)mEntity).getSourceIDList();
		ArrayList<String> selectSourceIDS = ((MoudleComponentEntity)mEntity).getSelectSourceIDList();
		if(sourceIDS!=null&&selectSourceIDS!=null){
			for (int i = 0; i < sourceIDS.size(); i++) {
				Bitmap defBimap=BitmapUtils.getBitMap(sourceIDS.get(i), mContext);
				Bitmap selectBitmap=BitmapUtils.getBitMap(selectSourceIDS.get(i), mContext);
				defBimaps.add(defBimap);
				selectBitmaps.add(selectBitmap);
				float scaleW=getLayoutParams().width*1.0f/defBimap.getWidth();
				float curHeight=defBimap.getHeight()*scaleW;
				positionAndHeights.add(new PositionAndHeight(totalHeight, curHeight));
				totalHeight+=curHeight;
			}
		}
		mTopOffset=0;
	}

	
	private boolean touchInTheRect(MotionEvent event, float x, float y, float width,
			float height) {
			if (event.getX()>x) {
				if (event.getX() < x+width) {
					if (event.getY() > y) {
						if (event.getY() < y + height) {
							return true;
						}
					}
				}
			}
			return false;
	}
	
	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity=entity;
	}

	
	@Override
	public void load() {
		loadBitmaps();
		myImageView=new MyImagView(mContext);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(getLayoutParams().width, (int) totalHeight);
		addView(myImageView, params);
	}
	class PositionAndHeight{
		float mPosition;
		float mHeight;
		public PositionAndHeight(float position,float height){
			mPosition=position;
			mHeight=height;
		}
	}
	

	class MyImagView extends View{

		public MyImagView(Context context) {
			super(context);
			longPressRectf=new RectF(-1, -1, -1, -1);
			setClickable(true);
			SimpleOnGestureListener listener=new  SimpleOnGestureListener(){
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					if (!playMoveAni) {
						for (int i = 0; i < defBimaps.size(); i++) {
							if (touchInTheRect(e, 0,
									mTopOffset+positionAndHeights.get(i).mPosition,
									getLayoutParams().width,
									positionAndHeights.get(i).mHeight)) {
								curSelectIndex = i;
								playMoveAnim(- positionAndHeights.get(i).mPosition,200);
								doClickItemEvent(i);
								break;
							}
						}
					}
					return false;
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, final float velocityY) {
					return false;
				}
				
				@Override
				public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2,float arg3) {
						if(!playMoveAni){
							if(curWaitToSelectIndex==-1){
								mTopOffset-=arg3;
								invalidate();
							}
						}
						return false;
				}
				
				@Override
				public void onLongPress(MotionEvent event) {
					super.onLongPress(event);
					if(!playMoveAni){
						for (int i = 0; i < defBimaps.size(); i++) {
							if(touchInTheRect(event, 0,mTopOffset+positionAndHeights.get(i).mPosition, getLayoutParams().width,positionAndHeights.get(i).mHeight)){
								curWaitToSelectIndex=i;
								longPressRectf.left=0;
								longPressRectf.top=mTopOffset+positionAndHeights.get(i).mPosition;
								longPressRectf.right=getLayoutParams().width;
								longPressRectf.bottom=mTopOffset+positionAndHeights.get(i).mPosition+positionAndHeights.get(i).mHeight;
								invalidate();
								break;
							}
						}
					}
				}
			};
			final GestureDetector detector=new GestureDetector(mContext, listener);
			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(playMoveAni){
						return true;
					}
					detector.onTouchEvent(event);
					if(event.getAction()==MotionEvent.ACTION_UP){
							if(mTopOffset>0){
								playMoveAnim(0, 200);
							}
							if(mTopOffset+totalHeight<positionAndHeights.get(positionAndHeights.size()-1).mHeight){
								if(playMoveAni){
									getAnimation().cancel();
								}
								playMoveAnim(positionAndHeights.get(positionAndHeights.size()-1).mHeight-totalHeight, 200);
							}
							if (curWaitToSelectIndex != -1) {
								if (longPressRectf.contains(event.getX(),event.getY()))
								{
									curSelectIndex = curWaitToSelectIndex;
									curWaitToSelectIndex = -1;
									playMoveAnim(mTopOffset - longPressRectf.top, 200);
									doClickItemEvent(curSelectIndex);
								}
							}
							longPressRectf.left=-1;
							longPressRectf.right=-1;
							longPressRectf.top=-1;
							longPressRectf.bottom=-1;
					}else{
						if(curWaitToSelectIndex!=-1){
									if (!longPressRectf.contains(event.getX(),event.getY()))
									{
										longPressIndex=curWaitToSelectIndex;
										curWaitToSelectIndex=-1;
										invalidate();
									}
						}else {
								if (longPressRectf.contains(event.getX(),event.getY()))
								{
									curWaitToSelectIndex=longPressIndex;
									invalidate();
								}
						}
					}
					return true;
				}
			});
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
			for (int i = 0; i < defBimaps.size(); i++) {
				Bitmap drawBitmap=defBimaps.get(i);
				if(curSelectIndex==i||curWaitToSelectIndex==i){
					drawBitmap=selectBitmaps.get(i);
				}
				dst=new RectF(0,mTopOffset+positionAndHeights.get(i).mPosition, getLayoutParams().width, mTopOffset+positionAndHeights.get(i).mPosition+positionAndHeights.get(i).mHeight);
				canvas.drawBitmap(drawBitmap, null, dst, mPaint);
			}
		}
		
		private void playMoveAnim(float endPosition,long duration) {
			ObjectAnimator animator=ObjectAnimator.ofFloat(this, "mTopOffset", endPosition);
			animator.setDuration(duration);
			animator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					playMoveAni=true;
					myImageView.postInvalidate();
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					playMoveAni=false;
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			animator.start();
		}
		
		public void setMTopOffset(float topOffset){
			mTopOffset=topOffset;
			invalidate();
		}
		
		public float getMTopOffset() {
			return mTopOffset;
		}
		
	}
	
	
	public void doSelectItemEvent(int index){
		myImageView.playMoveAnim(-positionAndHeights.get(index).mPosition,300);
		curSelectIndex=index;
		curWaitToSelectIndex=-1;
	}
//
	private void doClickItemEvent(int i) {
			for (BehaviorEntity behavior : mEntity.behaviors) {
				if(behavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_ITEM_CLICK")){
					BehaviorHelper.doBeheavorForList(behavior, i,mEntity.componentId);
				}
			}
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
		BitmapUtils.recycleBitmaps(defBimaps);
		BitmapUtils.recycleBitmaps(selectBitmaps);
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
