package com.hl.android.view;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hl.android.HLActivity;
import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.Book;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.ContainerEntity;
import com.hl.android.book.entity.GifComponentEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.book.entity.SectionEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.EventDispatcher;
import com.hl.android.core.helper.AnimationHelper;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.component.AudioComponent;
import com.hl.android.view.component.ImageGifComponent;
import com.hl.android.view.component.PDFDocumentViewComponentMU;
import com.hl.android.view.component.ScrollTextViewComponentEN;
import com.hl.android.view.component.TimerComponent;
import com.hl.android.view.component.VideoComponent;
import com.hl.android.view.component.WebComponent;
import com.hl.android.view.component.helper.ComponentHelper;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;
import com.hl.android.view.component.moudle.HLCameraUIComponent;
import com.hl.android.view.component.moudle.HLHorRightUIComponent;
import com.hl.android.view.component.moudle.HLVerBottomUIComponent;
import com.hl.android.view.component.moudle.HLViewFlipper;
import com.hl.android.view.component.moudle.slide.HorizontalSlide;
import com.hl.android.view.component.moudle.slide.VerticleSlide;
import com.hl.callback.Action;

/**
 * 书籍显示页面，显示书籍的当前页面
 * 
 * @author webcat
 * 
 */
public class ViewPage extends ViewGroup implements OnComponentCallbackListener {
	public static String PAGE_TYPE_HOR = "PAGE_TYPE_HOR";
	public static String PAGE_TYPE_VER = "PAGE_TYPE_VER";
	public static String PAGE_TYPE_NONE = "PAGE_TYPE_NONE";
	private Book book;
	private PageEntity entity;
	public int pageWidth;
	private int pageY;
	public int pageHeight;
	private boolean issequence;
	private int groupindex;
	private CountDownTimer mc;
	private ArrayList<VideoComponent> videoComponnetList;
	private ViewPageState viewPageState;

	public int autoPlayCount = 0; // 自动播放计数器
	private int autoPlayFinishCount = 0;// 自动播放完成计数器
	private HLActivity activity;
	private int shouldStopIndex = -1001;
	private long currentWaitTime;
	private boolean hasLoadPreAndNextPage = false;
	private ViewPage mAniEndPage;
	public String endPage;
	private GestureDetector detector = null;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 50;
	// 判断我是否是公共页，如果我是公共页，那么就需要将事件向内层传递
	public boolean isCommonPage = false;
	private float tempRatio = BookSetting.PAGE_RATIO;//
	private float ratio = 1.0f;
	private float tempRatioX = BookSetting.PAGE_RATIOX;//
	private float tempRatioY = BookSetting.PAGE_RATIOY;//
	private float ratiox = 1.0f;
	private float ratioy = 1.0f;

	public ViewPage(Context context) {
		super(context);
	}

	public ViewPage(Context context, PageEntity entity, Book book) {
		super(context);
		this.entity = entity;
		this.book = book;
		this.setDrawingCacheEnabled(true);
		this.viewPageState = new ViewPageState();
		EventDispatcher.getInstance().init();

		activity = (HLActivity) context;
	}

	public PageEntity getEntity() {
		if (entity == null) {
			Log.d("wdy", "为啥会是空的");
		}
		return entity;
	}

	public void setEntity(PageEntity entity) {
		if (entity == null) {
			Log.d("wdy", "为啥会是空的");
		}
		this.entity = entity;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeigth);

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			int widthSpec = 0;
			int heightSpec = 0;

			ViewGroup.LayoutParams params = v.getLayoutParams();
			if (params.width > 0) {
				widthSpec = MeasureSpec.makeMeasureSpec(params.width,
						MeasureSpec.EXACTLY);
			} else if (params.width == -1) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.EXACTLY);
			} else if (params.width == -2) {
				widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.AT_MOST);
			}

			if (params.height > 0) {
				heightSpec = MeasureSpec.makeMeasureSpec(params.height,
						MeasureSpec.EXACTLY);
			} else if (params.height == -1) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth,
						MeasureSpec.EXACTLY);
			} else if (params.height == -2) {
				heightSpec = MeasureSpec.makeMeasureSpec(measureWidth,
						MeasureSpec.AT_MOST);
			}
			v.measure(widthSpec, heightSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 计算当前移动位移占总共唯一的比例
		// float rate =
		// (float)offSetY/(float)(BookSetting.BOOK_HEIGHT-pageHeight);
		// 显示其他元素
		try {
			for (int i = 0; i < this.getChildCount(); i++) {
				ViewCell view = (ViewCell) this.getChildAt(i);

				// if(view.getEntity().isPageInnerSlide){
				view.doSlideAction(offSetY);
				// }
				MarginLayoutParams marginLp = (MarginLayoutParams) view
						.getLayoutParams();
				if (BookSetting.FITSCREEN_TENSILE) {
					if (tempRatioX != BookSetting.PAGE_RATIOX) {
						ratiox = BookSetting.PAGE_RATIOX / tempRatioX;
						tempRatioX = BookSetting.PAGE_RATIOX;
					}
					if (tempRatioY != BookSetting.PAGE_RATIOY) {
						ratioy = BookSetting.PAGE_RATIOY / tempRatioY;
						tempRatioY = BookSetting.PAGE_RATIOY;
					}
					view.layout(
							(int) (marginLp.leftMargin
									+ ((ViewCell) view).getEntity().x * ratiox + ((ViewCell) view).moveX),
							(int) (marginLp.topMargin
									+ ((ViewCell) view).getEntity().y * ratioy + ((ViewCell) view).moveY),
							(int) (marginLp.leftMargin
									+ ((ViewCell) view).getEntity().x * ratiox
									+ marginLp.width * ratiox + ((ViewCell) view).moveX),
							(int) (marginLp.topMargin
									+ ((ViewCell) view).getEntity().y * ratioy
									+ marginLp.height * ratioy + ((ViewCell) view).moveY));
				} else {
					if (tempRatio != BookSetting.PAGE_RATIO) {
						ratio = BookSetting.PAGE_RATIO / tempRatio;
						tempRatio = BookSetting.PAGE_RATIO;
					}
					view.layout(
							(int) (marginLp.leftMargin
									+ ((ViewCell) view).getEntity().x * ratio + ((ViewCell) view).moveX),
							(int) (marginLp.topMargin
									+ ((ViewCell) view).getEntity().y * ratio + ((ViewCell) view).moveY),
							(int) (marginLp.leftMargin
									+ ((ViewCell) view).getEntity().x * ratio
									+ marginLp.width * ratio + ((ViewCell) view).moveX),
							(int) (marginLp.topMargin
									+ ((ViewCell) view).getEntity().y * ratio
									+ marginLp.height * ratio + ((ViewCell) view).moveY));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public LayoutParams getCurrentLayoutParams() {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (BookSetting.FITSCREEN_TENSILE) {
			tempRatioX = BookSetting.PAGE_RATIOX;
			tempRatioY = BookSetting.PAGE_RATIOY;
			BookSetting.PAGE_RATIOX = BookSetting.BOOK_WIDTH4CALCULATE
					/ getEntity().getWidth();
			int initBookHeight = BookController.getInstance().getBook()
					.getBookInfo().bookHeight;
			int initBookWidth = BookController.getInstance().getBook()
					.getBookInfo().bookWidth;
			BookSetting.PAGE_RATIOY = BookSetting.BOOK_HEIGHT4CALCULATE
					/ initBookHeight;
			if (getEntity().getWidth() < getEntity().getHeight()) {
				BookSetting.PAGE_RATIOY = BookSetting.BOOK_HEIGHT4CALCULATE
						/ Math.max(initBookHeight, initBookWidth);
			}
			lp.width = (int) (ScreenUtils.getHorScreenValue(getEntity()
					.getWidth()));
			lp.height = (int) (ScreenUtils.getVerScreenValue(getEntity()
					.getHeight()));
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			if (tempRatioX != BookSetting.PAGE_RATIOX
					|| tempRatioY != BookSetting.PAGE_RATIOY) {
				requestLayout();
			}
		} else {
			tempRatio = BookSetting.PAGE_RATIO;
			BookSetting.PAGE_RATIO = BookSetting.BOOK_WIDTH4CALCULATE
					/ getEntity().getWidth();
			lp.width = (int) (ScreenUtils.getHorScreenValue(getEntity()
					.getWidth()));
			lp.height = (int) (ScreenUtils.getVerScreenValue(getEntity()
					.getHeight()));
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			if (tempRatio != BookSetting.PAGE_RATIO) {
				requestLayout();
			}
		}
		return lp;
	}

	/**
	 * load页面
	 * 
	 * @param entity
	 */
	public void load(PageEntity entity) {
		this.entity = entity;
		this.autoPlayCount = 0;
		this.autoPlayFinishCount = 0;
		load();
	}

	/**
	 * TODO 需要继续优化 开始viewpage初始播放，例如动画，声音等，对视频调用的是 playVideo()
	 */
	public void startPlay() {
		try {

			// load的时候需要将动画的容器清空 update by zhaoq 20140212
			// 将动画清空的地方从load转移到play，是因为页间滑动在点击的时候会调用下一页的load而导致动画容器被清空
			AnimationHelper.animatiorMap.clear();
			// 如果是序列则调用序列播放方法
			if (this.issequence) {
				groupindex = 0;
				if (entity.IsGroupPlay || BookSetting.IS_AUTOPAGE) {
					currentWaitTime = entity.getSequence().Delay.get(0);
					mc = new MyCount(currentWaitTime, currentWaitTime);
					mc.start();
					return;
				}
			}
			if (!hasLoadPreAndNextPage) {
				if (BookSetting.FLIP_CHANGE_PAGE && BookSetting.FLIPCODE == 1) {
					if (!isCommonPage) {
						BookController.getInstance()
								.getAndSetPreAndNextViewPage();
						hasLoadPreAndNextPage = true;
						mAniEndPage = BookController.getInstance()
								.getViewPage();
						endPage = "this";
					}
				}
			} else {
			}

			for (int i = 0; i < this.getChildCount(); i++) {
				ViewCell cell = (ViewCell) this.getChildAt(i);
				if (cell.getEntity().isHideAtBegining) {
					cell.setVisibility(View.GONE);
				}
				Component component = cell.getComponent();
				ComponentEntity entity = cell.getEntity();
				if (entity.isPlayVideoOrAudioAtBegining) {
					cell.play();
					// 用来计算自动的播放记数
					if (component instanceof ImageGifComponent) {
						GifComponentEntity g = ((GifComponentEntity) entity);
						if (g.isIsPlayOnetime()) {
							if (!entity.isAutoLoop()) {
								autoPlayCount = autoPlayCount + 1;
							}
						}
					} else {
						if (!entity.isAutoLoop()) {
							autoPlayCount = autoPlayCount + 1;
						}
					}
					if (component instanceof TimerComponent) {
						((TimerComponent) component).playTimer();
					}
					Book book = BookController.getInstance().getBook();
					if (book != null) {
						String pageid = book.getStartPageID();
						if (this.getEntity().getID().equals(pageid)) {
							if (component instanceof AudioComponent) {
								((AudioComponent) component).setControlUnable();
							}
							if (component instanceof VideoComponent) {
								((VideoComponent) component).setControlUnable();
							}
						}
					}

				}
				if (entity.isPlayAnimationAtBegining) {
					if (entity.getAnims() != null
							&& entity.getAnims().size() > 0) {
						AnimationHelper.playAnimation(cell);
						if (component.getEntity().isAutoLoop() == false) {
							autoPlayCount = autoPlayCount + 1;
						}
					}
				}

				if (component instanceof PDFDocumentViewComponentMU
						|| component instanceof WebComponent
						|| component instanceof HLViewFlipper
						|| component instanceof VerticleSlide
						|| component instanceof ScrollTextViewComponentEN
						|| component instanceof HorizontalSlide
				// || component instanceof ImageGifComponent
				) {
					cell.play();
				}
			}
			if (autoPlayCount == 0 && mHLCallBack != null) {
				if (!viewPageState.isStopped()) {
					// 是下一轮刷新
					new CountDownTimer(1, 1) {
						@Override
						public void onFinish() {
							mHLCallBack.doAction();
							mHLCallBack = null;
						}

						@Override
						public void onTick(long arg0) {
						}

					}.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity.finish();
		} catch (Error e) {
			activity.finish();
		}

	}

	Action mHLCallBack = null;
	private MotionEvent oldEvent4moveY = null;
	private MotionEvent oldEvent4moveX = null;
	private float offSetY = 0;// 竖向滑动的偏移量
	protected boolean hasDoFling;

	public void setPageCompletion(Action callBack) {
		if (null != this.videoComponnetList
				&& this.videoComponnetList.size() > 0) {
			((VideoComponent) videoComponnetList.get(0)).doCompletAction = callBack;
		} else {
			mHLCallBack = callBack;
		}
	}

	public GestureDetector getDetector() {
		return detector;
	}

	public void playVideo() {
		if (null != this.videoComponnetList
				&& this.videoComponnetList.size() > 0) {

			// if (this.viewPageState.stopped == true) {
			// ((Component) this.videoComponnetList.get(0)).play();
			// } else {
			if (viewPageState.stopped) {
				for (Component videoCompent : this.videoComponnetList) {
					VideoComponent videoC = (VideoComponent) videoCompent;
					videoC.continuePlay();
				}
			} else {
				for (Component videoCompent : this.videoComponnetList) {
					if (!HLSetting.isNewActivityForVideo) {
						this.addView((View) videoCompent);
						(((VideoComponent) videoCompent).getSurfaceView())
								.setZOrderOnTop(true);
						((View) videoCompent).bringToFront();

						if (videoCompent.getEntity().isHideAtBegining) {
							((View) videoCompent).setVisibility(View.INVISIBLE);
							// this.addView((View) videoCompent);
						} else {
							// this.addView((View) videoCompent);
							((View) videoCompent).setVisibility(View.VISIBLE);
							// ((SurfaceView)videoCompent).setZOrderOnTop(true);
							// ((SurfaceView)videoCompent).bringToFront();
						}
					}

				}
			}

		}
	}

	//
	// /**
	// * 得到当前页面的截屏
	// *
	// * @return
	// */
	// public Bitmap getCurrentScreen() {
	// // return this.currentscreen;
	// Bitmap bitmap = null;
	// if(getEntity().isCashSnapshot()){
	// return BookController.getInstance().getSnapShotCashImage(getEntity());
	// }
	// int width = 0;
	// int height = 0;
	// if(BookSetting.FIX_SIZE){
	// width = BookSetting.INIT_SCREEN_WIDTH;
	// height = BookSetting.INIT_SCREEN_HEIGHT;
	// }else{
	// width = (int) ScreenUtils.getHorScreenValue(getEntity().getWidth());
	// height = (int) ScreenUtils.getVerScreenValue(getEntity().getHeight());
	// }
	// try {
	// // if (BitmapManageUtil.currentscreen == null) {
	// try {
	//
	// bitmap = Bitmap.createBitmap(
	// width,
	// height, Bitmap.Config.RGB_565);
	//
	// } catch (OutOfMemoryError e) {
	// bitmap = Bitmap.createBitmap(
	// width,
	// height, Bitmap.Config.ALPHA_8);
	// }
	// // }
	// this.draw(new Canvas(
	// bitmap));
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// return null;
	// }
	// return bitmap;
	// }

	/**
	 * 暂停当前页面中组件的视频播放，音频播放
	 */
	public void pause() {
		if (viewPageState.stopped)
			return;
		try {
			viewPageState.stopped = true;
			if (this.getChildCount() > 0) {
				for (int i = 0; i < this.getChildCount(); i++) {
					ViewCell cell = (ViewCell) this.getChildAt(i);
					cell.pause();
				}
			}
			if (issequence)
				stopSequence();
		} catch (Exception e) {
			Log.e("hl", "stop ", e);
		}
	}

	public void resume() {
		if (!viewPageState.stopped)
			return;
		viewPageState.stopped = false;
		try {
			if (this.getChildCount() > 0) {
				for (int i = 0; i < this.getChildCount(); i++) {
					ViewCell cell = (ViewCell) this.getChildAt(i);
					cell.resume();
				}
			}

		} catch (Exception e) {

			Log.e("hl", "resume ", e);
		}
	}

	public void stopVideo() {
		for (VideoComponent vc : videoComponnetList) {
			vc.stop();
			vc.setVisibility(View.GONE);
			removeView(vc);
		}

	}

	/**
	 * 停止当前页面中组件的动画，视频播放，音频播放
	 */
	public void stop() {

		try {
			// load的时候需要将动画的容器清空
			AnimationHelper.animatiorMap.clear();
			// BookController.lastPageID = entity.getID();
			if (this.getChildCount() > 0) {
				for (int i = 0; i < this.getChildCount(); i++) {
					ViewCell viewCell = ((ViewCell) this.getChildAt(i));
					AnimationHelper.stopAnimation(viewCell);
					viewCell.stop();
				}
			}
			if (issequence)
				stopSequence();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(this.getClass().toString(), "stop", e);
		}

		this.viewPageState.setStoped();
	}

	/**
	 * 设置viewpage背景
	 */
	private void setBackground() {
		if (StringUtils.isEmpty(entity.getBackground().ID)) {
			return;
		}
		if (StringUtils.isEmpty(entity.getBackground().getComponent()
				.getClassName())) {
			return;
		}
		ViewCell bgCell = new ViewCell(getContext(), this,
				entity.getBackground());
		this.addView(bgCell);
	}

	/**
	 * 装载页面component,对video进行特殊化处理 如果是video不直接放到viewgroup中,放到videoComponentList中
	 * 当掉用playVideo时再放到viewgroup中
	 */
	private void load() {
		detector = new GestureDetector(activity, listener);
		// 此处才传入entity所以在此处设置height
		BookSetting.PAGE_RATIO = BookSetting.BOOK_WIDTH4CALCULATE
				/ entity.getWidth();
		BookSetting.PAGE_RATIOX = BookSetting.BOOK_WIDTH4CALCULATE
				/ entity.getWidth();
		int initBookHeight = BookController.getInstance().getBook()
				.getBookInfo().bookHeight;
		int initBookWidth = BookController.getInstance().getBook()
				.getBookInfo().bookWidth;
		BookSetting.PAGE_RATIOY = BookSetting.BOOK_HEIGHT4CALCULATE
				/ initBookHeight;
		if (getEntity().getWidth() < getEntity().getHeight()) {
			BookSetting.PAGE_RATIOY = BookSetting.BOOK_HEIGHT4CALCULATE
					/ Math.max(initBookHeight, initBookWidth);
		}
		this.pageWidth = BookSetting.BOOK_WIDTH;
		this.pageHeight = (int) (entity.getHeight() * BookSetting.PAGE_RATIO);
		if (BookSetting.FITSCREEN_TENSILE) {
			this.pageHeight = (int) (entity.getHeight() * BookSetting.PAGE_RATIOY);
		}
		videoComponnetList = new ArrayList<VideoComponent>();
		this.setBackground();
		try {
			for (ContainerEntity containerEntity : this.entity.getContainers()) {
				Component component = ComponentHelper.getComponent(
						containerEntity, this);
				if (component == null) {
					continue;
				}
				if (component instanceof HLCameraUIComponent) {
					if (((HLCameraUIComponent) component).camera != null) {
						((HLCameraUIComponent) component).camera.stopPreview();
						((HLCameraUIComponent) component).camera.release();
						((HLCameraUIComponent) component).camera = null;
						((HLCameraUIComponent) component).holder = null;
						((HLCameraUIComponent) component).surface = null;
					}
				}
				ViewCell cell = new ViewCell(getContext(), this,
						containerEntity);
				addView(cell);
			}

			if (entity.getSequence() != null
					&& entity.getSequence().Group.size() > 0) {
				this.issequence = true;
				groupindex = 0;

			} else {
				this.issequence = false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void stopSequence() {
		if (mc != null && (BookSetting.IS_AUTOPAGE || entity.IsGroupPlay)) {
			mc.cancel();
		}
	}

	public void hideMenu() {
		try {
			for (int i = 0; i < this.getChildCount(); i++) {
				if (this.getChildAt(i) instanceof HLVerBottomUIComponent) {
					HLVerBottomUIComponent m = (HLVerBottomUIComponent) getChildAt(i);
					m.hideMenu();
				}
				if (this.getChildAt(i) instanceof HLHorRightUIComponent) {
					HLHorRightUIComponent m = (HLHorRightUIComponent) getChildAt(i);
					m.hideMenu();
				}
			}
		} catch (Exception e) {
			Log.e("hl", "hideMenu ", e);
		}
	}

	/**
	 * 播放序列
	 */
	public void playSequence() {
		try {
			if (issequence) {
				if (groupindex - 1 == shouldStopIndex
						|| groupindex == shouldStopIndex) {
					stopSequence();
					for (String cID : entity.getSequence().Group
							.get(shouldStopIndex).ContainerID) {
						ViewCell c = this.getCellByID(cID);
						if (c != null && c.getEntity().getAnims() != null) {
							AnimationHelper.stopAnimation(c);
						}
					}
					groupindex = entity.getSequence().Group.size();
					shouldStopIndex = -1001;
					return;
				}
				// this.containers=new ArrayList<Container>();
				for (String cID : entity.getSequence().Group.get(groupindex).ContainerID) {
					currentWaitTime = 0;
					ViewCell c = this.getCellByID(cID);
					if (c != null && c.getEntity().getAnims() != null) {
						for (int i = 0; i < c.getEntity().getAnims().size(); i++) {
							AnimationEntity curAnimationEntity = c.getEntity()
									.getAnims().get(i);
							currentWaitTime += Long
									.parseLong(curAnimationEntity.Delay)
									+ Long.parseLong(curAnimationEntity.Duration)
									* Long.parseLong(curAnimationEntity.Repeat);
						}
						AnimationHelper.playAnimation(c);
					}
					if (c != null)
						c.play();
				}
				groupindex++;
				this.invalidate();
			}
		} catch (Exception e) {
			Log.e("hl", "playSequence ", e);
		}
	}

	public ViewCell getCellByID(String id) {
		int viewId = Integer.parseInt(id.substring(8));
		return (ViewCell) BookController.getInstance().hlActivity.mainLayout
				.findViewById(viewId);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
	}

	MotionEvent downEvent;
	public boolean isHorMove = false;

	/**
	 * 处理页面上下滑动和页间滑动
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		long startTime = System.currentTimeMillis();
		// 如果正在切换子页，直接返回
		if (BookController.getInstance().doFlipSubPage) {
			return true;
		}
		if (isCommonPage) {
			BookController.getInstance().getHLBookLayout()
					.dispatchTouchEvent(event);
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			downEvent = MotionEvent.obtain(event);
			isHorMove = false;
			if (entity.enablePageTurnByHand && entity.isEnableNavigation()) {
				if (event.getPointerCount() == 1) {
					EventDispatcher.getInstance().onTouch(downEvent);
				}
			}
			getDetector().onTouchEvent(downEvent);
			if (BookSetting.FLIP_CHANGE_PAGE && BookSetting.FLIPCODE == 1) {
				doTouchAction4MovePageBetweenPage(downEvent);
			}
		} else {
			if (event.getAction() == MotionEvent.ACTION_MOVE
					&& downEvent != null) {
				float firstDx = event.getRawX() - downEvent.getRawX();
				float firstDy = event.getRawY() - downEvent.getRawY();
				isHorMove = Math.abs(firstDx) > Math.abs(firstDy);
				downEvent = null;
			}
			Log.d("zhaoq",
					"is hor move" + isHorMove + " action is " + event.getRawX());
			// 页间滑动与上下滑动是互斥的
			getDetector().onTouchEvent(event);
			if (isHorMove && BookSetting.FLIPCODE == 1) {
				if (BookSetting.FLIP_CHANGE_PAGE) {
					doTouchAction4MovePageBetweenPage(event);
				} else {
					if (entity.enablePageTurnByHand
							&& entity.isEnableNavigation()) {
						if (event.getPointerCount() == 1) {
							EventDispatcher.getInstance().onTouch(event);
						}
					}

				}

			} else {
				// 传递给翻页事件
				if (entity.enablePageTurnByHand && entity.isEnableNavigation()) {
					if (event.getPointerCount() == 1) {
						EventDispatcher.getInstance().onTouch(event);
					}
				}
			}
		}

		// 增加viewPage竖向move事件
		if (BookSetting.BOOK_HEIGHT < pageHeight) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				int dy = (int) (event.getRawY() - oldEvent4moveY.getRawY());
				moveDy(dy);
			}
		}
		oldEvent4moveY = MotionEvent.obtain(event);
		long endTime = System.currentTimeMillis();
		Log.d("wdy", "touch Time:endTime-startTime:" + (endTime - startTime));
		return true;
	}

	public void doTouchAction4MovePageBetweenPage(MotionEvent event) {
		if (BookController.getInstance().isPlayingChangePageAni) {
			return;
		}
		int action = event.getAction();
		if (action == MotionEvent.ACTION_MOVE) {
			// if(!hasLoadPreAndNextPage){
			// Log.d("wdy",
			// "MotionEvent.ACTION_MOVE LoadPreAndNextPage"+getEntity().getID());
			// BookController.getInstance().getAndSetPreAndNextViewPage();
			// hasLoadPreAndNextPage=true;
			// mAniEndPage=BookController.getInstance().getViewPage();
			// endPage="this";
			// return;
			// }
			float dx = 0;
			if (oldEvent4moveX != null) {
				dx = (event.getRawX() - oldEvent4moveX.getRawX());
			}

			if (BookController.getInstance().getNextPageId() == null) {
				if (dx < 0 && getX() <= 0) {
					dx = 0;
				}
			}
			if (BookController.getInstance().getPrePageId() == null) {
				if (dx > 0 && getX() >= 0) {
					dx = 0;
				}
			}
			for (int i = 0; i < BookController.getInstance().getHLBookLayout()
					.getChildCount(); i++) {
				View v = BookController.getInstance().getHLBookLayout()
						.getChildAt(i);
				v.setX(v.getX() + dx);
			}
			// setCommonPageX();

		} else if (action == MotionEvent.ACTION_UP) {
			if (hasDoFling) {
				hasDoFling = false;
				return;
			}
			float moveX = 0;
			if (getX() < -BookSetting.BOOK_WIDTH / 2.0f) {
				moveX = -BookController.getInstance().getViewPage().getX()
						- BookSetting.BOOK_WIDTH;
				if (doMoveAni(moveX)) {
					for (int i = 0; i < BookController.getInstance()
							.getHLBookLayout().getChildCount(); i++) {
						ViewPage page = (ViewPage) BookController.getInstance()
								.getHLBookLayout().getChildAt(i);
						if (page.getEntity()
								.getID()
								.equals(BookController.getInstance()
										.getNextPageId())) {
							mAniEndPage = page;
							endPage = "next";
							break;
						}
					}
				}
			} else if (getX() > BookSetting.BOOK_WIDTH / 2.0f) {
				moveX = -BookController.getInstance().getViewPage().getX()
						+ BookSetting.BOOK_WIDTH;
				if (doMoveAni(moveX)) {
					for (int i = 0; i < BookController.getInstance()
							.getHLBookLayout().getChildCount(); i++) {
						ViewPage page = (ViewPage) BookController.getInstance()
								.getHLBookLayout().getChildAt(i);
						if (page.getEntity()
								.getID()
								.equals(BookController.getInstance()
										.getPrePageId())) {
							mAniEndPage = page;
							endPage = "prev";
							break;
						}
					}
				}
			} else {// 回到初始状态
				moveX = -BookController.getInstance().getViewPage().getX();
				if (doMoveAni(moveX)) {
					mAniEndPage = BookController.getInstance().getViewPage();
					endPage = "this";
					BookController.getInstance().shouldKeepMainPage = true;
				}
			}
			if (mAniEndPage != null) {
				BookController.getInstance().setDefaultView(
						BookController.getInstance().getSectionPagePosition(
								mAniEndPage.getEntity().getID()));
			}
			hasDoFling = false;
		}
		oldEvent4moveX = MotionEvent.obtain(event);
	}

	// private boolean isCommonMove = false;
	SimpleOnGestureListener listener = new SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (BookController.getInstance().getBook().getBookInfo().bookNavType
					.equals("indesign_slider_view")) {
				if (activity.getUPNav().isShowing()) {
					activity.getUPNav().dismiss();
					activity.getBottomNav().dismiss();
				} else {
					activity.getUPNav().show();
					activity.getBottomNav().show();
				}
			}
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (isHorMove && BookSetting.FLIPCODE == 1) {
				if (BookSetting.FLIP_CHANGE_PAGE) {
					if (BookController.getInstance().isPlayingChangePageAni) {
						return false;
					}
					float moveX = 0;
					BookController bookController = BookController
							.getInstance();
					if (e1.getRawX() - e2.getRawX() > FLING_MIN_DISTANCE
							&& Math.abs(velocityX) > FLING_MIN_VELOCITY
							&& e1.getRawX() - e2.getRawX() > e1.getRawY()
									- e2.getRawY()) {
						if (bookController.getNextPageId() == null) {
							hasDoFling = false;
							return false;
						}
						moveX = -bookController.getViewPage().getX()
								- BookSetting.BOOK_WIDTH;
						if (doMoveAni(moveX) ) {
							for (int i = 0; i < bookController
									.getHLBookLayout().getChildCount(); i++) {
								ViewPage page = (ViewPage) bookController
										.getHLBookLayout().getChildAt(i);
								if (page.getEntity().getID()
										.equals(bookController.getNextPageId())) {
									mAniEndPage = page;
									endPage = "next";
									break;
								}
							}
//							bookController.setDefaultView(bookController
//									.getSectionPagePosition(mAniEndPage
//											.getEntity().getID()));
							hasDoFling = true;
						}
					} else if (e2.getRawX() - e1.getRawX() > FLING_MIN_DISTANCE
							&& Math.abs(velocityX) > FLING_MIN_VELOCITY
							&& Math.abs(velocityX) > Math.abs(velocityY)
							&& e2.getRawX() - e1.getRawX() > e2.getRawY()
									- e1.getRawY()) {
						if (bookController.getPrePageId() == null) {
							hasDoFling = false;
							return false;
						}
						moveX = -bookController.getViewPage().getX()
								+ BookSetting.BOOK_WIDTH;
						if (doMoveAni(moveX)) {
							for (int i = 0; i < bookController
									.getHLBookLayout().getChildCount(); i++) {
								ViewPage page = (ViewPage) bookController
										.getHLBookLayout().getChildAt(i);
								if (page.getEntity().getID()
										.equals(bookController.getPrePageId())) {
									mAniEndPage = page;
									endPage = "prev";
									break;
								}
							}
//							bookController.setDefaultView(bookController
//									.getSectionPagePosition(mAniEndPage
//											.getEntity().getID()));
							hasDoFling = true;
						}
					} else {
						hasDoFling = false;
					}
				}
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent event1, MotionEvent event2,
				float arg2, float arg3) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent event) {

		}
	};

	/**
	 * 设置公共页的坐标
	 * 
	 * @param dx
	 */
	public void setCommonPageX() {
		activity.commonLayout.setVisibility(View.VISIBLE);
		// Log.d("zhaoq","common layout view is "
		// +(activity.commonLayout.isShown()));
		float targetX = BookController.getInstance().viewPage.getX();
		// isCommonMove = false;
		ViewPage page = null;
		boolean prev = BookController.getInstance().prevCommonPage != null;
		boolean next = BookController.getInstance().nextCommonPage != null;
		if (prev) {// 前页
			page = BookController.getInstance().prevCommonPage;
			page.setX(targetX - BookSetting.BOOK_WIDTH);
		}
		if (next) {// 后页
			page = BookController.getInstance().nextCommonPage;
			page.setX(targetX + BookSetting.BOOK_WIDTH);
		}
		if (BookController.getInstance().commonPage != null) {// 后页
			page = BookController.getInstance().commonPage;
			// 如果前一页存在公共页，并且与当前的公共页相同，并且是向前滑动
			if (!prev && targetX > 0
					&& !BookController.getInstance().prevCommonPageEmpty) {
				page.setX(0);
			} else if (!next && targetX < 0
					&& !BookController.getInstance().nextCommonPageEmpty) {
				page.setX(0);
			} else {
				page.setX(targetX);
			}
		}
	}

	// 公共页面的动画
	AnimatorListener ans = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			// 如果是子页之间的切换就不需要了
			// if(BookController.getInstance().viewPage!=BookController.getInstance().mainViewPage)return;
			ViewPage page = BookController.getInstance().commonPage;
			if (page == null) {
				return;
			}
			if (page.getX() > 0) {
				ViewPage tmpPage = BookController.getInstance().nextCommonPage;
				if (tmpPage != null)
					tmpPage.clean();
				BookController.getInstance().nextCommonPage = BookController
						.getInstance().commonPage;
				BookController.getInstance().commonPage = BookController
						.getInstance().prevCommonPage;
				BookController.getInstance().prevCommonPage = null;
				if (BookController.getInstance().commonPage.getEntity() != null) {
					BookController.getInstance().commonPageID = BookController
							.getInstance().commonPage.getEntity().getID();
				} else {
					BookController.getInstance().commonPageID = "";
				}
				activity.commonLayout.removeView(tmpPage);
			} else if (page.getX() < 0) {
				ViewPage tmpPage = BookController.getInstance().prevCommonPage;
				if (tmpPage != null)
					tmpPage.clean();
				BookController.getInstance().prevCommonPage = BookController
						.getInstance().commonPage;
				BookController.getInstance().commonPage = BookController
						.getInstance().nextCommonPage;
				if (BookController.getInstance().commonPage.getEntity() != null) {
					BookController.getInstance().commonPageID = BookController
							.getInstance().commonPage.getEntity().getID();
				} else {
					BookController.getInstance().commonPageID = "";
				}
				BookController.getInstance().nextCommonPage = null;
				activity.commonLayout.removeView(tmpPage);
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			// TODO Auto-generated method stub

		}
	};

	// 将所有加载的页面从当前位置沿x移动给定的距离,此方法在当前显示的页面中调用
	public boolean doMoveAni(float moveX) {

		if (BookController.getInstance().isPlayingChangePageAni) {
			return false;
		}
		BookController.getInstance().isPlayingChangePageAni = true;
		boolean hasSetListenner = false;
		for (int i = 0; i < BookController.getInstance().getHLBookLayout()
				.getChildCount(); i++) {
			View page = BookController.getInstance().getHLBookLayout()
					.getChildAt(i);
			ObjectAnimator animator = ObjectAnimator.ofFloat(page, "x",
					page.getX() + moveX);
			animator.setDuration(500);
			// 只为一个viewpage设置监听器
			if (!hasSetListenner) {
				setListenner(animator);
				hasSetListenner = true;
			}
			animator.start();
		}
		// Log.d("wdy", "doMoveAni move Length is:"+moveX);
		return true;
	}

	public void setAniEndPage(ViewPage aniEndPage) {
		this.mAniEndPage = aniEndPage;
	}

	private void setListenner(ObjectAnimator animator) {
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				activity.getUPNav().dismiss();
				activity.getBottomNav().dismiss();
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// 此处延迟50毫秒
				new CountDownTimer(50, 50) {

					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFinish() {
						// Log.d("wdy", "doMoveAni end");
						BookController.getInstance().isPlayingChangePageAni = false;
						// if(!mHasDoBehaviorMouseUP){
						hasLoadPreAndNextPage = false;
						if (!BookController.getInstance().shouldKeepMainPage) {
							BookController.getInstance()
									.showPageWithLoadedPage(mAniEndPage,
											endPage);
						} else {
							hasLoadPreAndNextPage = true;
							BookController.getInstance().shouldKeepMainPage = false;
						}
						if (mAniEndPage != ViewPage.this) {
							BookController.getInstance().startPlay();
						}
						// }else{
						// Log.d("wdy", "有点击抬起事件");
						// mHasDoBehaviorMouseUP=false;
						// }
					}
				}.start();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		// animator.addListener(ans);
	}

	public void moveDy(float dy) {
		offSetY += dy;
		if (offSetY >= 0) {
			offSetY = 0;
		} else if (offSetY < BookSetting.BOOK_HEIGHT - pageHeight) {
			offSetY = BookSetting.BOOK_HEIGHT - pageHeight;
		}
		requestLayout();
	}

	public float getOffSetY() {
		return offSetY;
	}

	public boolean isHasChildPage() {
		if (null != this.entity.getNavePageIds()
				&& this.entity.getNavePageIds().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void clean() {
		try {
			// 调用所有孩子的recycle方法
			for (int i = 0; i < this.getChildCount(); i++) {
				ViewCell cell = (ViewCell) getChildAt(i);
				cell.recyle();
			}
			this.clearAnimation();
			this.groupindex = 0;
			System.gc();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cleanForChangePage() {
		try {
			// 调用所有孩子的recycle方法
			for (int i = 0; i < this.getChildCount(); i++) {
				this.removeView(this.getChildAt(i));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getPageWidth() {
		return pageWidth;
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		// if(!isCommonPage)Log.d("zhaoq", "main page x is " + x +
		// " page id is " + entity.getID());
		if (this == BookController.getInstance().viewPage) {
			setCommonPageX();
		}
		float offsetX = getX();
		float rate = -offsetX / BookSetting.BOOK_WIDTH;
		// 显示其他元素
		try {
			for (int i = 0; i < this.getChildCount(); i++) {
				ViewCell view = (ViewCell) this.getChildAt(i);
				if (view.getEntity().isPageTweenSlide) {
					view.doSlideAction4TweenPage(rate);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}

	public int getPageY() {
		return pageY;
	}

	public void setPageY(int pageY) {
		this.pageY = pageY;
	}

	public int getPageHeight() {
		return pageHeight;
	}

	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public boolean getIsSequence() {
		return this.issequence;
	}

	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			try {
				if (groupindex >= entity.getSequence().Group.size()) {
					stopSequence();
					shouldStopIndex = -1001;
					return;
				}
				playSequence();
				if (currentWaitTime != 0) {
					mc = new MyCount(currentWaitTime, currentWaitTime).start();
				}

			} catch (Exception ex) {
				Log.e("hl", " finish", ex);
			}

		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	public ViewPageState getViewPageState() {
		return viewPageState;
	}

	public void setViewPageState(ViewPageState viewPageState) {
		this.viewPageState = viewPageState;
	}

	AutoPageCountDown autoPageViewCountDown;

	public void setPlayComplete() {
		this.autoPlayFinishCount = this.autoPlayFinishCount + 1;
		// 自动翻页，但是是否会影响到自动翻页

		if (this.autoPlayCount == this.autoPlayFinishCount
				&& (BookSetting.IS_AUTOPAGE && entity.IsGroupPlay)) {
			autoPageViewCountDown = new AutoPageCountDown(2000, 1000);
			autoPageViewCountDown.start();
			return;
		}
		if (this.autoPlayCount == this.autoPlayFinishCount && autoPlayCount > 0
				&& mHLCallBack != null) {
			mHLCallBack.doAction();
			mHLCallBack = null;
		}
	}

	public class AutoPageCountDown extends CountDownTimer {

		public AutoPageCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			BookController.getInstance().flipPage(1);

		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}

	@Override
	public void bringToFront() {
		super.bringToFront();
		// activity.coverLayout.bringToFront();
	}

	public void stopGroupAtSomeWhere(String value) {
		this.shouldStopIndex = Integer.parseInt(value);
	}

}
