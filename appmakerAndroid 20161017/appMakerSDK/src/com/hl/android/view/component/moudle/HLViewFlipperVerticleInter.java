package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
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
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;

public class HLViewFlipperVerticleInter extends RelativeLayout implements Component,ComponentPost{

	private MoudleComponentEntity mEntity;
	private Context mContext;
	private ArrayList<Bitmap> bitmaps;
	private int mImageWidth, mImageHeight;
	private MotionEvent oldEvent = null;
	private float dx = 0;
	private float dy = 0;
	private float totalAbsDx = 0;
	private float totalAbsDy = 0;
	private int curShowIndex;
	private View myImageView;
	private RelativeLayout.LayoutParams imageLayoutParams;
	private RelativeLayout.LayoutParams naviViewLayoutParams;
	private NaviView naviView;
	
	
	public HLViewFlipperVerticleInter(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		mEntity = (MoudleComponentEntity) entity;
		setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.mEntity = (MoudleComponentEntity) entity;
	}

	@Override
	public void load() {
		ArrayList<String> sourceIDS = mEntity.getSourceIDList();
		bitmaps=new ArrayList<Bitmap>();
		for (String curSourceID : sourceIDS) {
			Bitmap bitmap=BitmapManager.getBitmapFromCache(curSourceID);
			if(bitmap==null){
				bitmap=BitmapUtils.getBitMap(curSourceID, mContext);
				BitmapManager.putBitmapCache(curSourceID, bitmap);
			}
			bitmaps.add(bitmap);
		}
		curShowIndex=0;
		mImageHeight=getLayoutParams().height;
		mImageWidth=getLayoutParams().width;
		if(mEntity.isShowNavi){
			mImageWidth-=60;
			naviView=new NaviView(mContext);
		}
		
		myImageView=new MyImageView(mContext,bitmaps);
		imageLayoutParams=new RelativeLayout.LayoutParams(mImageWidth,mImageHeight*bitmaps.size());
		imageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		addView(myImageView,imageLayoutParams);
		if(mEntity.isShowNavi){
			naviViewLayoutParams=new RelativeLayout.LayoutParams(60,mImageHeight);
			naviViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			addView(naviView,naviViewLayoutParams);
		}
		
	}
	
	@Override
	public void load(InputStream is) {

	}
	
	public void doClickCircle(int i) {
		if(myImageView.getAnimation()!=null&&!myImageView.getAnimation().hasEnded()){
			return;
		}
		playChangeImageAnim(myImageView,i,300);
	}

	private void doClickAction(int index) {
			for(BehaviorEntity behavior:mEntity.behaviors){
				if(behavior.EventName.equals("BEHAVIOR_ON_TEMPLATE_ITEM_CLICK")){
					BehaviorHelper.doBeheavorForList(behavior, index,
							mEntity.componentId);
				}
		}
	}

	private void playChangeImageAnim(View v,final int endIndex,long duration) {
		ObjectAnimator animator=ObjectAnimator.ofFloat(v, "topDrawPositon", -endIndex*mImageHeight);
		animator.setDuration(duration);
		animator.addListener(new AnimatorListener() {
			private int triggerIndex = 0;
			@Override
			public void onAnimationStart(Animator arg0) {
				triggerIndex = endIndex;
				doChangeStart(triggerIndex);
				curShowIndex=endIndex;
				if(naviView!=null){
					naviView.postInvalidate();
				}
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				doChangeEnd(triggerIndex);
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		animator.start();
	}
	
	@Override
	public void recyle() {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
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
	
	class MyImageView extends View{
		private ArrayList<Bitmap> mBitmaps;
		private RectF rectF;
		private float topDrawPositon;
		private boolean hasFlingAni;
		public MyImageView(Context context,ArrayList<Bitmap> bitmaps) {
			super(context);
			mBitmaps=bitmaps;
			rectF=new RectF(0, topDrawPositon, mImageWidth,topDrawPositon+mImageHeight);
			SimpleOnGestureListener listener=new  SimpleOnGestureListener(){
				
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
					if(velocityY>0){
						if(curShowIndex-1>=0){
							doClickCircle(curShowIndex-1);
							hasFlingAni=true;
						}
					}else{
						if(curShowIndex+1<=mBitmaps.size()-1){
							doClickCircle(curShowIndex+1);
							hasFlingAni=true;
						}
					}
					return false;
				}
				
				@Override
				public boolean onScroll(MotionEvent event1, MotionEvent event2, float arg2,float arg3) {
					return false;
				}
				
				@Override
				public void onLongPress(MotionEvent event) {
					
				}
			};
			final GestureDetector detector=new GestureDetector(mContext, listener);
			setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					detector.onTouchEvent(event);

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
					} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if(hasFlingAni){
							return true;
						}
						dx = event.getX() - oldEvent.getX();
						dy = event.getY() - oldEvent.getY();
						topDrawPositon+=dy;
						if(topDrawPositon>0){
							topDrawPositon=0;
						}else if(topDrawPositon+mBitmaps.size()*mImageHeight<mImageHeight){
							topDrawPositon=mImageHeight-mBitmaps.size()*mImageHeight;
						}
						totalAbsDx += Math.abs(dx);
						totalAbsDy += Math.abs(dy);
						invalidate();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						if (totalAbsDx < 2 && totalAbsDy < 2) {
							doClickAction(curShowIndex);
						} else	if (mBitmaps.size() >= 1) {
							if(hasFlingAni){
								hasFlingAni=false;
							}else{
								if(topDrawPositon+curShowIndex*mImageHeight>=mImageHeight/2){
									doClickCircle(curShowIndex-1);
								}else if(topDrawPositon+curShowIndex*mImageHeight<=-mImageHeight/2){
									doClickCircle(curShowIndex+1);
								}else{
									doClickCircle(curShowIndex);
								}
							}
						}
						totalAbsDx = 0;
						totalAbsDy = 0;
					}
					oldEvent=MotionEvent.obtain(event);
					return true;
				}
			});
		}
		
		public void setTopDrawPositon(float topDrawPositon) {
			this.topDrawPositon = topDrawPositon;
			invalidate();
		}
		
		public float getTopDrawPositon() {
			return topDrawPositon;
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
			for (int i = 0; i < mBitmaps.size(); i++) {
				rectF.top=i*mImageHeight+topDrawPositon;
				rectF.bottom=rectF.top+mImageHeight;
				if(mBitmaps.get(i)!=null&&!mBitmaps.get(i).isRecycled()){
					canvas.drawBitmap(mBitmaps.get(i), null, rectF, null);
				}
			}
		}
		
	}
	
	class NaviView extends View{
		private Paint paint;
		public NaviView(Context context) {
			super(context);
			paint = new Paint(Paint.DITHER_FLAG);
			paint.setStyle(Style.STROKE);
			paint.setTextSize(15);
			paint.setAntiAlias(true);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
			String des=mEntity.renderDes.get(curShowIndex);
			for (int i = 0; i <des.length(); i++) {
				canvas.drawText(des.charAt(i)+"",5,mImageHeight*1.0f/2+7-des.length()*paint.getTextSize()/2.0f+i*paint.getTextSize(), paint);
			}
			for (int i = 0; i < bitmaps.size(); i++) {
				paint.setStyle(Style.STROKE);
				canvas.drawCircle(40, mImageHeight*1.0f/2-15*(bitmaps.size()-1)+30*i, 10, paint);
				if(i==curShowIndex){
					paint.setStyle(Style.FILL);
					canvas.drawCircle(40, mImageHeight*1.0f/2-15*(bitmaps.size()-1)+30*i, 6, paint);
				}
			}
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				for (int i = 0; i < bitmaps.size(); i++) {
					if (event.getX() > 25) {
						if (event.getY() > mImageHeight * 1.0f / 2 - 15
								* (bitmaps.size() - 1) + 30 * i - 15) {
							if (event.getX() < 55) {
								if (event.getY() < mImageHeight * 1.0f / 2 - 15
										* (bitmaps.size() - 1) + 30 * i + 15) {
									if (curShowIndex != i) {
										doClickCircle(i);
									}
									break;
								}
							}
						}
					}
				}
			}
			return true;
		}
	}
	
	private void doChangeStart(int index) {
		for (BehaviorEntity behavior : mEntity.behaviors) {
			if (Behavior.BEHAVIOR_ON_TEMPLATE_ITEM_CHANGE_BEGIN
					.equals(behavior.EventName)) {
				BehaviorHelper.doBeheavorForList(behavior,
						index, mEntity.componentId);
			}

		}
	}
	
	private void doChangeEnd(int index) {
		for (BehaviorEntity behavior : mEntity.behaviors) {
			if (Behavior.BEHAVIOR_ON_TEMPLATE_ITEM_CHANGE_COMPLETE
					.equals(behavior.EventName)) {
				BehaviorHelper.doBeheavorForList(behavior,
						index, mEntity.componentId);
			}

		}
	}

}
