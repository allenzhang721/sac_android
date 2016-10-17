package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build.VERSION;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.SliderEffectComponentEntity;
import com.hl.android.book.entity.SubImageItem;
import com.hl.android.controller.BookController;
import com.hl.android.core.helper.BehaviorHelper;
import com.hl.android.core.helper.animation.MyAnimation4CubeEffect;
import com.hl.android.core.helper.animation.MyAnimation4FlipEffect;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.inter.Component;

/**
 * 幻灯片类
 * 
 * @author wangdayong
 * @version 1.0
 * @createed 2013-11-14
 */
@SuppressLint({ "ViewConstructor", "DrawAllocation" })
public class HLSliderEffectComponent extends RelativeLayout implements
		Component {
	private SliderEffectComponentEntity mEntity;
	private Context mContext;
	private Paint mPaint;// 绘制当前bitmap的paint
	private Paint paint;// 绘制下一bitmap的paint
	private int curShowIndex = 0;// 当前绘制bitmap的索引
	private RectF dst;// 默认的rect
	private boolean hasAutoPlay;// 已经开始自动切换幻灯片
	private int currentAlpha = 255;// 当前透明度
	private boolean autoPlay = false;// 记录自动播放幻灯片
	private int hasPlayCount = 1;// 记录播放次数
	private View view;// 绘制默认bitmap和切换bitmap
	private MotionEvent oldEvent;
	private float dx;
	private float dy;
	private float totalDx;
	private float totalDy;
	private boolean hasMovePlay;// 标识已经构成滑动切换的条件
	private boolean isStop;
	private static final float CLICKSIZELIMIT = 30;// 水平或竖直移动距离，超出此距离算滑动，否则算点击

	public HLSliderEffectComponent(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		mEntity = (SliderEffectComponentEntity) entity;
		mPaint = new Paint(Paint.DITHER_FLAG);
		mPaint.setStyle(Style.STROKE);
		mPaint.setAntiAlias(true);

		paint = new Paint(Paint.DITHER_FLAG);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = (SliderEffectComponentEntity) entity;
	}

	@Override
	public void load() {
		dst = new RectF(0, 0, getLayoutParams().width, getLayoutParams().height);
		for (int i=0;i< mEntity.subItems.size();i++) {
			SubImageItem curSubImageItem=mEntity.subItems.get(i);
			curSubImageItem.changeSourceID2Bitmap(mContext);
			curSubImageItem.mIndex=i;
		}
		view = new MyView(mContext);
		addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 如果开始时播放幻灯片，autoPlay=true；开始播放
	}

	@Override
	public void load(InputStream is) {

	}

	/**
	 * 翻转切换
	 * 
	 * @param isNext
	 *            传入true，nextBitmap索引是curShowIndex+1（超出做取余处理）否则请传false
	 */
	private void doPlayWithTypeTransitionFlipEffect(final boolean isNext,
			String direction) {
		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(isNext);
		final ImageView imageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		imageView.setImageBitmap(curBitmap1);
		imageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(imageView, params);
		Animation curImageAnim = new MyAnimation4FlipEffect(0, 180, imageView,
				nextBitmap, direction);
		curImageAnim.setDuration(curSubItem.duration);
		curImageAnim.setAnimationListener(new AnimationListener() {
			float scalex;
			float scaley;
			ViewCell cell;
			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);
				cell=((ViewCell)(getParent()));
				scalex=cell.getScaleX();
				scaley=cell.getScaleY();
				cell.setScaleX(scalex*2);
				cell.setScaleY(scaley*2);
				imageView.setScaleX(0.5f);
				imageView.setScaleY(0.5f);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				cell.setScaleX(scalex);
				cell.setScaleY(scaley);
				if(VERSION.SDK_INT>15){
					imageView.setScaleX(1f);
					imageView.setScaleY(1f);
				}
				removeAllViews();
				doChangeEndAction(isNext);
				addView(view);
			}
		});
		imageView.startAnimation(curImageAnim);
	}

	/**
	 * 做立方体切换
	 * 
	 * @param isNext
	 *            传入true，nextBitmap索引是curShowIndex+1（超出做取余处理）否则传false
	 */
	
	private void doPlayWithTypeTransitionCubeEffect(final boolean isNext,
			String direction) {
		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(isNext);
		ImageView curShowImageView = new ImageView(mContext);
		ImageView nextShowImageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		curShowImageView.setImageBitmap(curBitmap1);
		curShowImageView.setScaleType(ScaleType.FIT_XY);
		nextShowImageView.setImageBitmap(nextBitmap);
		nextShowImageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(curShowImageView, params);
		addView(nextShowImageView, params);
		Animation curImageAnim = null;
		Animation nextImageAnim = null;
		if (direction.equals("left") || direction.equals("up")) {
			curImageAnim = new MyAnimation4CubeEffect(0, -90, direction);
			nextImageAnim = new MyAnimation4CubeEffect(90, 0, direction);
		} else if (direction.equals("right") || direction.equals("down")) {
			curImageAnim = new MyAnimation4CubeEffect(0, 90, direction);
			nextImageAnim = new MyAnimation4CubeEffect(-90, 0, direction);
		}
		curImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				removeAllViews();
				doChangeEndAction(isNext);
				addView(view);
			}
		});
		curShowImageView.startAnimation(curImageAnim);
		nextShowImageView.startAnimation(nextImageAnim);
	}

	/**
	 * 做覆盖切换
	 * 
	 * @param isNext
	 *            传入true，nextBitmap索引是curShowIndex+1（超出做取余处理）否则传false，direction
	 *            切换方向
	 */
	private void doPlayWithTypeTransitionNomal(String type,
			final boolean isNext, String direction) {
		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(isNext);
		ImageView curShowImageView = new ImageView(mContext);
		ImageView nextShowImageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		curShowImageView.setImageBitmap(curBitmap1);
		curShowImageView.setScaleType(ScaleType.FIT_XY);
		nextShowImageView.setImageBitmap(nextBitmap);
		nextShowImageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(curShowImageView, params);
		addView(nextShowImageView, params);
		AnimationSet curImageAnim = null;
		AnimationSet nextImageAnim = null;
		if (type.equals("fade")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
			curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
			nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		} else if (type.equals("moveIn")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0.5f, 1));
			TranslateAnimation transAni = null;
			if (direction.equals("left")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						1, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						-1, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("down")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0);
			}
			nextImageAnim.addAnimation(transAni);
		} else if (type.equals("reveal")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0f, 1));
			TranslateAnimation transAni = null;
			if (direction.equals("left")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1);
			} else if (direction.equals("down")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1);
			}
			curImageAnim.addAnimation(transAni);
			curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
			nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		} else if (type.equals("push")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			AlphaAnimation alphaAnimation=new AlphaAnimation(0.5f, 1);
			nextImageAnim.addAnimation(alphaAnimation);
			TranslateAnimation transAni4curImage = null;
			TranslateAnimation transAni4nextImage = null;
			if (direction.equals("left")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("down")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0);
			}
			curImageAnim.addAnimation(transAni4curImage);
			nextImageAnim.addAnimation(transAni4nextImage);
		}
		curImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				removeAllViews();
				doChangeEndAction(isNext);
				addView(view);
			}
		});
		curShowImageView.startAnimation(curImageAnim);
		nextShowImageView.startAnimation(nextImageAnim);
	}

	/**
	 * 根据传入boolean值为下一子项赋值
	 * 
	 * @param isNext
	 *            传入true，nextBitmap索引是curShowIndex+1（超出做取余处理）否则请传false
	 * @return
	 */
	private SubImageItem getNextSubItem(boolean isNext) {
		SubImageItem nextSubItem = mEntity.subItems.get((curShowIndex + 1)
				% mEntity.subItems.size());
		if (!isNext) {
			if (curShowIndex == 0) {
				if (mEntity.isEndToStart) {
					nextSubItem = mEntity.subItems
							.get(mEntity.subItems.size() - 1);
				}
			} else {
				nextSubItem = mEntity.subItems.get(curShowIndex - 1);
			}
		}
		return nextSubItem;
	}

	/**
	 * 根据传入boolean值判断下一项显示，做切换完成后的处理
	 * 
	 * @param isNext
	 */
	private void doChangeEndAction(boolean isNext) {
		hasAutoPlay = false;
		if (isNext) {
			curShowIndex++;
		} else {
			curShowIndex--;
		}
		if (mEntity.isLoop && hasPlayCount != mEntity.repeat) {
			if (curShowIndex >= mEntity.subItems.size()) {
				if (hasPlayCount < mEntity.repeat) {
					hasPlayCount++;
				}
				curShowIndex = 0;
			}else if (curShowIndex >= mEntity.subItems.size() - 1) {
				BookController.getInstance().runBehavior(mEntity,Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
			}
		} else {
			if (curShowIndex >= mEntity.subItems.size()) {
				curShowIndex=0;
			}else if (curShowIndex >= mEntity.subItems.size() - 1) {
				BookController.getInstance().runBehavior(mEntity,Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
				curShowIndex %= mEntity.subItems.size();
				autoPlay = false;
			} else if (curShowIndex < 0) {
				curShowIndex = mEntity.subItems.size() - 1;
				autoPlay = false;
			}
		}
		doChangeEnd(curShowIndex);
	}
	
	private void doChangeEndAction(int position) {
		hasAutoPlay = false;
		curShowIndex=position;
		doChangeEnd(curShowIndex);
	}

	/**
	 * 绘制当前item的bitmap
	 * 
	 * @param canvas
	 */
	private void drawCurrentImage(Canvas canvas) {
		currentAlpha = 255;
		mPaint.setAlpha(currentAlpha);
		paint.setAlpha(currentAlpha);
		if(isStop){
			curShowIndex=0;
		}
		Bitmap curBitmap = mEntity.subItems.get(
				curShowIndex % mEntity.subItems.size()).getBitmap(mContext);
		canvas.drawBitmap(curBitmap, null, dst, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (autoPlay) {// 开始自动播放touch无效
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_CLICK");
			break;
		case MotionEvent.ACTION_MOVE:
			if (hasMovePlay) {// 如果已经触发滑动事件，又没有up会执行此处
				break;
			}
			dx = event.getX() - oldEvent.getX();
			dy = event.getY() - oldEvent.getY();
			totalDx += dx;
			totalDy += dy;
			if (Math.abs(totalDx) >= CLICKSIZELIMIT
					|| Math.abs(totalDy) >= CLICKSIZELIMIT) {
				hasMovePlay = true;
				if (mEntity.switchType.equals("click")) {// 如果设置是点击切换幻灯片，则不执行滑动切换，跳出
					break;
				}
				// 否则向上滑动或向左滑动执行切换下一张幻灯片，向下或向右滑动执行切换上一张幻灯片
				if (Math.abs(totalDx) >= Math.abs(totalDy)) {
					if (totalDx > 0) {
						changePre("right");
					} else {
						changeNext("left");
					}
				} else {
					if (totalDy > 0) {
						changePre("down");
					} else {
						changeNext("up");
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			BookController.getInstance().runBehavior(getEntity(),"BEHAVIOR_ON_MOUSE_UP");
			if (!hasMovePlay) {
				if (mEntity.switchType.equals("click")
						|| mEntity.switchType.equals("clickAndSlide")) {
					SubImageItem curSubItem = mEntity.subItems
							.get((curShowIndex) % mEntity.subItems.size());
					changeNext(curSubItem.aniProperty);
				}
			}
			dx = 0;
			dy = 0;
			totalDx = 0;
			totalDy = 0;
			hasMovePlay = false;
			break;
		default:
			break;
		}
		oldEvent = MotionEvent.obtain(event);
		return true;// 不允许翻页、移动viewpage，保证滑动切换
	}

	/**
	 * 切换到上一张幻灯片
	 */
	private void changePre(String direction) {
		isStop=false;
		String aniType = mEntity.subItems.get(curShowIndex).aniType;
		if (curShowIndex == 0) {
			if (!mEntity.isEndToStart) {
				return;
			}
		}
		if (!hasAutoPlay) {
			hasAutoPlay = true;
			if (aniType.equals("cubeEffect")) {
				doPlayWithTypeTransitionCubeEffect(false, direction);
			} else if (aniType.equals("flipEffect")) {
				doPlayWithTypeTransitionFlipEffect(false, direction);
			} else if (aniType.equals("transitionFade")) {
				doPlayWithTypeTransitionNomal("fade", false, direction);
			} else if (aniType.equals("transitionMoveIn")) {
				doPlayWithTypeTransitionNomal("moveIn", false, direction);
			} else if (aniType.equals("transitionPush")) {
				doPlayWithTypeTransitionNomal("push", false, direction);
			} else if (aniType.equals("transitionReveal")) {
				doPlayWithTypeTransitionNomal("reveal", false, direction);
			} else {
				view.postInvalidate();
			}
		}

	}
	
	private void changeTo(int position) {
		isStop=false;
		String aniType = mEntity.subItems.get(curShowIndex).aniType;
		String direction = mEntity.subItems.get(curShowIndex).aniProperty;
		if (!hasAutoPlay) {
			hasAutoPlay = true;
			if (aniType.equals("cubeEffect")) {
				doPlayWithTypeTransitionCubeEffect(position, direction);
			} else if (aniType.equals("flipEffect")) {
				doPlayWithTypeTransitionFlipEffect(position, direction);
			} else if (aniType.equals("transitionFade")) {
				doPlayWithTypeTransitionNomal("fade", position, direction);
			} else if (aniType.equals("transitionMoveIn")) {
				doPlayWithTypeTransitionNomal("moveIn", position, direction);
			} else if (aniType.equals("transitionPush")) {
				doPlayWithTypeTransitionNomal("push", position, direction);
			} else if (aniType.equals("transitionReveal")) {
				doPlayWithTypeTransitionNomal("reveal", position, direction);
			} else {
				view.postInvalidate();
			}
		}

	}

	private void doPlayWithTypeTransitionNomal(String type, final int position,
			String direction) {

		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(position);
		ImageView curShowImageView = new ImageView(mContext);
		ImageView nextShowImageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		curShowImageView.setImageBitmap(curBitmap1);
		curShowImageView.setScaleType(ScaleType.FIT_XY);
		nextShowImageView.setImageBitmap(nextBitmap);
		nextShowImageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(curShowImageView, params);
		addView(nextShowImageView, params);
		AnimationSet curImageAnim = null;
		AnimationSet nextImageAnim = null;
		if (type.equals("fade")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0, 1));
			curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
			nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		} else if (type.equals("moveIn")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0.5f, 1));
			TranslateAnimation transAni = null;
			if (direction.equals("left")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						1, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						-1, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("down")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0);
			}
			nextImageAnim.addAnimation(transAni);
		} else if (type.equals("reveal")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			nextImageAnim.addAnimation(new AlphaAnimation(0f, 1));
			TranslateAnimation transAni = null;
			if (direction.equals("left")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1);
			} else if (direction.equals("down")) {
				transAni = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
						0, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1);
			}
			curImageAnim.addAnimation(transAni);
			curImageAnim.setZAdjustment(Animation.ZORDER_TOP);
			nextImageAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		} else if (type.equals("push")) {
			curImageAnim = new AnimationSet(true);
			curImageAnim.addAnimation(new AlphaAnimation(1.0f, 0));
			nextImageAnim = new AnimationSet(true);
			AlphaAnimation alphaAnimation=new AlphaAnimation(0.5f, 1);
			nextImageAnim.addAnimation(alphaAnimation);
			TranslateAnimation transAni4curImage = null;
			TranslateAnimation transAni4nextImage = null;
			if (direction.equals("left")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("right")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("up")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0);
			} else if (direction.equals("down")) {
				transAni4curImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1);
				transAni4nextImage = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -1,
						Animation.RELATIVE_TO_SELF, 0);
			}
			curImageAnim.addAnimation(transAni4curImage);
			nextImageAnim.addAnimation(transAni4nextImage);
		}
		curImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				removeAllViews();
				doChangeEndAction(position);
				addView(view);
			}
		});
		curShowImageView.startAnimation(curImageAnim);
		nextShowImageView.startAnimation(nextImageAnim);
	
	}

	private void doPlayWithTypeTransitionFlipEffect(final int position,String direction) {

		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(position);
		final ImageView imageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		imageView.setImageBitmap(curBitmap1);
		imageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(imageView, params);
		Animation curImageAnim = new MyAnimation4FlipEffect(0, 180, imageView,
				nextBitmap, direction);
		curImageAnim.setDuration(curSubItem.duration);
		curImageAnim.setAnimationListener(new AnimationListener() {
			float scalex;
			float scaley;
			ViewCell cell;
			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);
				cell=((ViewCell)(getParent()));
				scalex=cell.getScaleX();
				scaley=cell.getScaleY();
				cell.setScaleX(scalex*2);
				cell.setScaleY(scaley*2);
				imageView.setScaleX(0.5f);
				imageView.setScaleY(0.5f);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				cell.setScaleX(scalex);
				cell.setScaleY(scaley);
				if(VERSION.SDK_INT>15){
					imageView.setScaleX(1f);
					imageView.setScaleY(1f);
				}
				removeAllViews();
				doChangeEndAction(position);
				addView(view);
			}
		});
		imageView.startAnimation(curImageAnim);
	
	}

	private void doPlayWithTypeTransitionCubeEffect(final int position,
			String direction) {
		removeAllViews();
		SubImageItem curSubItem = mEntity.subItems.get((curShowIndex)
				% mEntity.subItems.size());
		final SubImageItem nextSubItem = getNextSubItem(position);
		ImageView curShowImageView = new ImageView(mContext);
		ImageView nextShowImageView = new ImageView(mContext);
		Bitmap curBitmap1 = curSubItem.getBitmap(mContext);
		Bitmap nextBitmap = nextSubItem.getBitmap(mContext);
		curShowImageView.setImageBitmap(curBitmap1);
		curShowImageView.setScaleType(ScaleType.FIT_XY);
		nextShowImageView.setImageBitmap(nextBitmap);
		nextShowImageView.setScaleType(ScaleType.FIT_XY);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(curShowImageView, params);
		addView(nextShowImageView, params);
		Animation curImageAnim = null;
		Animation nextImageAnim = null;
		if (direction.equals("left") || direction.equals("up")) {
			curImageAnim = new MyAnimation4CubeEffect(0, -90, direction);
			nextImageAnim = new MyAnimation4CubeEffect(90, 0, direction);
		} else if (direction.equals("right") || direction.equals("down")) {
			curImageAnim = new MyAnimation4CubeEffect(0, 90, direction);
			nextImageAnim = new MyAnimation4CubeEffect(-90, 0, direction);
		}
		curImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setDuration(curSubItem.duration);
		nextImageAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				doChangeStart(nextSubItem.mIndex);

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				removeAllViews();
				doChangeEndAction(position);
				addView(view);
			}
		});
		curShowImageView.startAnimation(curImageAnim);
		nextShowImageView.startAnimation(nextImageAnim);
	
	}

	private SubImageItem getNextSubItem(int position) {
		return mEntity.subItems.get(position);
	}

	/**
	 * 切换到下一张幻灯片
	 */
	private void changeNext(String direction) {
		isStop=false;
		String aniType = mEntity.subItems.get(curShowIndex).aniType;
		if (curShowIndex == mEntity.subItems.size() - 1) {
			if (!mEntity.isEndToStart) {
				return;
			}
		}
		if (!hasAutoPlay) {
			hasAutoPlay = true;
			if (aniType.equals("cubeEffect")) {
				doPlayWithTypeTransitionCubeEffect(true, direction);
			} else if (aniType.equals("flipEffect")) {
				doPlayWithTypeTransitionFlipEffect(true, direction);
			} else if (aniType.equals("transitionFade")) {
				doPlayWithTypeTransitionNomal("fade", true, direction);
			} else if (aniType.equals("transitionMoveIn")) {
				doPlayWithTypeTransitionNomal("moveIn", true, direction);
			} else if (aniType.equals("transitionPush")) {
				doPlayWithTypeTransitionNomal("push", true, direction);
			} else if (aniType.equals("transitionReveal")) {
				doPlayWithTypeTransitionNomal("reveal", true, direction);
			} else {
				view.postInvalidate();
			}
		}
	}

	@Override
	public void play() {
		if(autoPlay){
			return;
		}
		isStop=false;
		final SubImageItem curImageitem = mEntity.subItems.get(curShowIndex
				% mEntity.subItems.size());
		hasMovePlay = false;
		autoPlay = true;
		new CountDownTimer(curImageitem.delay, curImageitem.delay) {

			@Override
			public void onTick(long arg0) {

			}

			@Override
			public void onFinish() {
				BookController.getInstance().runBehavior(mEntity,Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
				hasAutoPlay = true;
				if (curImageitem.aniType.equals("cubeEffect")) {
					doPlayWithTypeTransitionCubeEffect(true,
							curImageitem.aniProperty);
				} else if (curImageitem.aniType.equals("flipEffect")) {
					doPlayWithTypeTransitionFlipEffect(true,
							curImageitem.aniProperty);
				} else if (curImageitem.aniType.equals("transitionFade")) {
					doPlayWithTypeTransitionNomal("fade", true,
							curImageitem.aniProperty);
				} else if (curImageitem.aniType.equals("transitionMoveIn")) {
					doPlayWithTypeTransitionNomal("moveIn", true,
							curImageitem.aniProperty);
				} else if (curImageitem.aniType.equals("transitionPush")) {
					doPlayWithTypeTransitionNomal("push", true,
							curImageitem.aniProperty);
				} else if (curImageitem.aniType.equals("transitionReveal")) {
					doPlayWithTypeTransitionNomal("reveal", true,
							curImageitem.aniProperty);
				} else {
					view.postInvalidate();
				}
			}
		}.start();
	
	}

	@Override
	public void stop() {
		for (int i = 0; i < getChildCount(); i++) {
			View subView=getChildAt(i);
			try{
				if(!subView.getAnimation().hasEnded()){
					subView.getAnimation().cancel();
					subView.clearAnimation();
				}
			}catch(Exception e){
				
			}
			
		}
		isStop=true;
		hasAutoPlay=false;
		curShowIndex=0;
		autoPlay=false;
		removeAllViews();
		addView(view);
		view.postInvalidate();
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

	@Override
	public void hide() {
		setVisibility(View.GONE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}
	
	

	@Override
	public void pause() {
		if(autoPlay){
			autoPlay=false;
		}
	}

	/**
	 * 绘制默认的当前bitmap和通过绘制切换的bitmap
	 * 
	 * @author wangdayong
	 * @version 1.0
	 * @createed 2013-11-14
	 */
	public class MyView extends View {

		public MyView(Context context) {
			super(context);
		}

		private int index = 0;

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// 设置画布抗锯齿
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			drawCurrentImage(canvas);
			if (hasAutoPlay) {
				String aniType = mEntity.subItems.get(curShowIndex).aniType;
				String direction = mEntity.subItems.get(curShowIndex).aniProperty;
				if (aniType.equals("transitionFade")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionNomal("fade", true, direction);
				} else if (aniType.equals("cubeEffect")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionCubeEffect(true, direction);
				} else if (aniType.equals("flipEffect")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionFlipEffect(true, direction);

				} else if (aniType.equals("transitionMoveIn")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionNomal("moveIn", true, direction);
				} else if (aniType.equals("transitionPush")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionNomal("push", true, direction);
				} else if (aniType.equals("transitionReveal")) {
					drawCurrentImage(canvas);
					doPlayWithTypeTransitionNomal("reveal", true, direction);
				} else {// 翻页，波纹，吸入。。。先使用淡入效果
				// doPlayWithTypeTransitionNomal("fade",true,direction);

					canvas.save();
					canvas.translate(10, 10);
					drawScene(canvas);
					canvas.restore(); 
			            
					canvas.save();
					canvas.translate(160, 10);
					canvas.clipRect(10, 10, 90, 90);// 第一次
					canvas.clipRect(30, 30, 70, 70, Region.Op.DIFFERENCE);// 第二次
					drawScene(canvas);
					canvas.restore();
					// postInvalidateDelayed(mEntity.subItems.get(curShowIndex).delay);
				}

			} else {
				if (autoPlay) {
					hasAutoPlay = true;
					postInvalidateDelayed(mEntity.subItems.get(curShowIndex).delay);
				}
			}
		}

		private void drawScene(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			Paint mPaint = new Paint();
			mPaint.setColor(Color.RED);
			canvas.drawLine(0, 0, getWidth(), getHeight(), mPaint);

			mPaint.setColor(Color.GREEN);
			canvas.drawCircle(50, 50, 50, mPaint);

			mPaint.setColor(Color.BLACK);
			canvas.drawText("Clipping", 100, 30, mPaint);
		}
	}

	public void doChangeToAction(int position) {
		changeTo(position);
	}
}
