package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.GifComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

/**
 * 播放GIf组件类
 * 加载bitmap部分，我们采用队列的方式来加载
 * 设定队列size是10
 * 启动两个线程，一个加载，一个
 * @author webcat
 * 
 */
@SuppressLint({ "NewApi", "DrawAllocation" })
public class ImageGifComponent extends View implements Component,
		ComponentPost, ComponentListener, AnimationListener {
	//暂停标识
	private boolean mPausing = false;
	private boolean mRunning = false;
	public GifComponentEntity entity = null;
	public AnimationSet animationset = null;
//	private Rect touchDownRect;
	RectF rectf = null;
	//图片队列的默认size
	private int duration = 0;
	private int currentIndex = 0;
	//加载图片顺序
	private OnComponentCallbackListener onComponentCallbackListener;

	Paint paint = null;
	//当前的要绘制的bitmap
//	Bitmap currentBitmap = null;
	float last_x = 0;
	float last_y = 0;
	private long lastChangeTime;

	int width = 0;
	int height = 0;
	
	public ImageGifComponent(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public ImageGifComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
	}

	@SuppressLint("NewApi")
	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (GifComponentEntity) entity;
	}
	@Override
	public void load() {

		//获得当前图片的高度和宽度
		width =  getLayoutParams().width;
		height = getLayoutParams().height;

		//绘制到画布上，但是一定要，设定大小
		rectf = new RectF(0,0,width,height);
		loadGifFrame();
	}

	private void loadGifFrame() {
		duration = (int) entity.getGifDuration();
		if (duration > 0) {
			duration = duration / (entity.getFrameList().size()-1);
//			Log.d("wdy", "duration:"+duration);
		}
	}
	 
	private boolean isEnd = false;
	private Bitmap getBitmap(){
		String bitMapResource = entity.getFrameList().get(currentIndex);
		Bitmap bitmap = BitmapManager.getBitmapFromCache(bitMapResource);
		if( bitmap == null){
			bitmap = BitmapUtils.getBitMap(bitMapResource, getContext(), width, height);	
			BitmapManager.putBitmapCache(bitMapResource, bitmap);
		}
		return bitmap;
	}
	/**
	 * 绘制图片
	 * 根据当前的currentIndex创建bitmap进行绘制
	 * 如果当前的bitmap为空就需要重新创建一个
	 * 绘制结束以后，需要判断是否是最后一针
	 * 如果不是最后一帧，创建下一个bitmap，并触发下一帧的绘制
	 * 如果是最后一帧，如果是循环播放则将currentindex重置，并从头开始
	 * 如果不是循环播放，则停止循环播放，并触发停播事件
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  
//		super.onDraw(canvas);
		//回去当前的bitmap，不确定当前的bimap是否可用，如果不可用就去取一个
//		if(currentBitmap == null||currentBitmap.isRecycled()){
			Bitmap currentBitmap = getBitmap();//bitmapQueue.poll();
//		}
			if (currentBitmap != null) {
			    Rect rect = new Rect(0, 0, currentBitmap.getWidth(), currentBitmap.getHeight());
				canvas.drawBitmap(currentBitmap,rect, rectf, paint);
			}
		if(isEnd)return;
		if(mPausing)return;
		//计算绘制下一帧的时间
		long nextDuration = System.currentTimeMillis() - lastChangeTime;
		nextDuration = duration - nextDuration;
		if(nextDuration>0){
			invalidate();
			return;
		}else{
//			Log.d("wdy", "nextDuration："+(-nextDuration));
		}
		//判断是否有下一帧
		if(currentIndex>=entity.getFrameList().size()-1){
			//如果轮播就重置序号
			if (entity.isIsPlayOnetime()){
				isEnd = true;
				mRunning = false;
				onComponentCallbackListener.setPlayComplete();
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_ANIMATION_END);
				return;
			}else{
				currentIndex = 0;
//				Log.d("wdy", "换图片间隔时间是(回到开始位置)："+(System.currentTimeMillis()-lastChangeTime));
				lastChangeTime = System.currentTimeMillis();
			}
		}else{
			if(mRunning){
				if(lastChangeTime!=0){
					currentIndex++;
				}
//				Log.d("wdy", "换图片间隔时间是："+(System.currentTimeMillis()-lastChangeTime));
				lastChangeTime = System.currentTimeMillis();
			}
		}
		//回收当前
//		if(currentBitmap!=null&&!currentBitmap.isRecycled()){
//          此处不使用recycle（）是应为会出现闪烁的问题，切记不要加recycle（）；
			currentBitmap = null;
//			System.gc();
//		}
		invalidate();
	}
	int lastX, lastY;
	Component component = null;
	boolean isOutExcute = false;
	boolean isInExcute = false;

	@Override
	public void load(InputStream is) {

	}
 
	MyCount1 count = null;
	private int repeatCount = 0;
	private boolean isEndHide = false;
	private float deltaX;
	private float deltaY;
	int repeat = 0;
	


	@Override
	public void play() {
		if(!mRunning){
			isEnd = false;
			currentIndex = 0;
			lastChangeTime=0;
//			BitmapUtils.recycleBitmap(currentBitmap);
//			currentBitmap = null;
//			this.mRunning = entity.isPlayVideoOrAudioAtBegining;
			this.mRunning=true;
			BookController.getInstance().runBehavior(entity,
					Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
			//启动加载图片线程
//			new Thread(loadRunnable).start();
			
		}else{
			if(isEnd)return;
		}
		mPausing = false;
//		currentIndex = 0;
		invalidate();
		
	}


	@Override
	public void stop() {
		mPausing = true;
		mRunning = false;
		currentIndex = 0;
		 
//		System.gc();
		if (null != count) {
			count.cancel();
		}
//		BitmapUtils.recycleBitmap(currentBitmap);
		invalidate();
		
	}

	@Override
	public void hide() {
		this.clearAnimation();
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		this.mPausing = false;
		postInvalidate();
	}
	@Override
	public void pause() {
		this.mPausing = true;
	}

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {
		onComponentCallbackListener = callbackListner;

	}

	@Override
	public void recyle() {
		this.mRunning = false;
//		BitmapUtils.recycleBitmap(currentBitmap);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		int index =  animationset.getAnimations().indexOf(animation);
		BookController.getInstance().runBehavior(entity,Behavior.BEHAVIOR_ON_ANIMATION_PLAY_AT, Integer.toString(index));
		
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_ANIMATION_PLAY);

	}
	@Override
	public void onAnimationEnd(Animation animation) {
		repeatCount++;
		if (repeat == 0) {
			count = new MyCount1(0, 100);
			count.start();
			return;
		} else if (repeat == 1) {
		} else if (repeatCount < repeat) {
			if (repeatCount == (repeat - 1)) {
				onComponentCallbackListener.setPlayComplete();
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_ANIMATION_END);
				return;
			}
			count = new MyCount1(0, 100);
			count.start();
		}
		if (isEndHide == true) {
			this.setVisibility(View.GONE);
		}

		if (deltaX != 0 || deltaY != 0) {
			Log.i("ImageGifComponent onAnimationEnd",
					"Location translate.........");
			TranslateAnimation anim = new TranslateAnimation(0, 0, 0, 0);
			this.setAnimation(anim);
			float width = getLayoutParams().width;
			float height = getLayoutParams().height;
			float left = entity.x + deltaX;
			float top = entity.y + deltaY;
			float right = entity.x + deltaX + width;
			float bottom = entity.y + deltaY + height;
			this.entity.x = (int) left;
			this.entity.y = (int) top;
			this.layout((int) left, (int) top, (int) right, (int) bottom);
		}
	
		onComponentCallbackListener.setPlayComplete();
		
		int index = animationset.getAnimations().indexOf(animation);
		BookController.getInstance().runBehavior(entity,Behavior.BEHAVIOR_ON_ANIMATION_END_AT, Integer.toString(index));
		
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_ANIMATION_END);

	}

	@Override
	public void onAnimationRepeat(Animation animation) {

		// System.out.println("dsfdsfdsfdf");
	}

	public class MyCount1 extends CountDownTimer {
		public MyCount1(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			setVisibility(View.VISIBLE);
			startAnimation(animationset);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
	
	@Override
	public void callBackListener() {
		onComponentCallbackListener.setPlayComplete();

	}
 
	@Override
	public void setAlpha(float alpha){
		if(alpha<0)alpha=0;
		else if(alpha>1)alpha=1;
		super.setAlpha(alpha);
	}
}
