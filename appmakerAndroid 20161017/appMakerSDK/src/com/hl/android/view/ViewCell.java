package com.hl.android.view;

import java.util.ArrayList;
import java.util.Iterator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.ContainerEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.controller.BookController;
import com.hl.android.controller.EventDispatcher;
import com.hl.android.core.helper.AnimationHelper;
import com.hl.android.core.helper.animation.HLAnimatorUpdateListener;
import com.hl.android.core.utils.AppUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.WebUtils;
import com.hl.android.view.component.HLSWFFileComponent;
import com.hl.android.view.component.HorizontalImageComponent;
import com.hl.android.view.component.PDFDocumentViewComponentMU;
import com.hl.android.view.component.ScrollTextViewComponent;
import com.hl.android.view.component.VerticalImageComponent;
import com.hl.android.view.component.VideoComponent;
import com.hl.android.view.component.WebComponent;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.helper.ComponentHelper;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.moudle.HLMediaController;
import com.hl.android.view.component.moudle.HLPaintingUIComponent;
import com.hl.android.view.component.moudle.HLPuzzleGameUIComponent;

/**
 * 视图单元类 viewpage中加载的最小视图对象 在加载page的时候被加载调用load方法 在page中play的时候调用play方法
 * 在需要回收的时候调用stop方法 在cell中要包括如下的属性 1视图组件 就是原来版本中的component 2数据实体bean
 * 3动画事件监听器（可能不止一个，因为要支持属性动画和tween动画），用来记录状态的 4一些标志位，来标识cell状态的
 * 在cell中还需要提供如下的几个方法 1load加载，这个时候资源被分配 2play这个时候开始播放， 3getComponent 返回内部显示的view
 * 4getEntity获得数据实体类 5show和hide方法 6stop方法回收资源 7onTouch事件处理
 * 
 * @author zhaoq
 * @version 1.0
 * @createed 2013-8-28
 */
public class ViewCell extends LinearLayout {

	private float oldDistance;

	private PointF mid = new PointF();

	private boolean isZoomInner;

	private MotionEvent oldEvent;

	private HLAnimatorUpdateListener animationUpdateListener = new HLAnimatorUpdateListener();

	private ViewRecord curRecord = new ViewRecord();

	public Animator mAnimator;
	/**
	 * 组件的业务实体类 从数据文件解析而成 这个实体类要保持不变 不允许修改，用来查看业务组件的最初始状态
	 */
	private ComponentEntity mEntity = null;
	/**
	 * 组件视图
	 */
	private Component mComponent = null;
	/**
	 * 视图单元所在的page
	 */
	private ViewPage mViewPage = null;

	/************* 为了移动事件定义的一些变量 start ******************/
	float lastX, lastY, lastY4viewcell;
	boolean isOutExcute = false;
	boolean isInExcute = false;
	// 点击按下的时候当前视图的view，为了做移出事件特殊定义的变量
	Rect downRect = new Rect();

	private SensorEventListener mSensorListener;
	private SensorManager mSensorMgr;
	private Sensor mSensor;
	private Context mContext;
	protected boolean isPlayingRightGoback;
	protected boolean isPlayingLeftGoback;
	protected int tSpeed;
	protected int curSpeed;
	protected CountDownTimer countdownTimer;
	private boolean canChangeState;
	// 指定轨迹的目标位置
	private PointF targetPonit;

	public int cellID = 0;

	/************* 为了移动定义的一些变量 end ******************/

	// ScaleGestureDetector mScaleGestureDetector;
	public ViewCell(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            your activity
	 * @param viewPage
	 *            the page this cell in
	 */
	public ViewCell(Context context, ViewPage viewPage) {
		super(context);
		mContext = context;
		mViewPage = viewPage;
	}

	public ViewRecord getViewRecord() {
		return curRecord;
	}

	public void resetViewRecord() {
		curRecord.mX = getEntity().x;
		curRecord.mY = getEntity().y;
		curRecord.mRotation = mEntity.rotation;
		// curRecord.mAlpha = mEntity.alpha;
	}

	/**
	 * construct and load
	 * 
	 * @param context
	 *            your activity
	 * @param viewPage
	 *            the page this cell in
	 * @param containerEntity
	 *            the entiry that will load
	 */
	public ViewCell(Context context, ViewPage viewPage,
			ContainerEntity containerEntity) {
		super(context);
		mContext = context;
		mViewPage = viewPage;
		
		cellID = Integer.parseInt(containerEntity.getID().substring(8));
		setId(cellID);
		//孙永乐 20150306 try catch中是废代码，不知道是否有用，但在201501的版本中在软件中启动页设有背景则会崩。
		try {
			//ArrayList<String> pageIds = BookController.getInstance().getViewPage().getEntity().getNavePageIds();
			ArrayList<String> pageIds=mViewPage.getEntity().getNavePageIds();
			if (pageIds != null && pageIds.size() >0) {
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// setId(Long.parseLong(string)containerEntity.getID())
		load(containerEntity);
	}

	/**
	 * 动画事件监听器
	 * 
	 * @return
	 */
	public HLAnimatorUpdateListener getAnimatorUpdateListener() {
		return animationUpdateListener;
	};

	public ComponentEntity getEntity() {
		return mEntity;
	}

	public Component getComponent() {
		return mComponent;
	}

	public void setMyRotationY(float rotationY) {
		((View) mComponent).setRotationY(rotationY);
	}

	@Override
	public void setRotation(float rotation) {
		super.setRotation(rotation + getComponent().getEntity().getRotation());
	}

	public void setSuperRotation(float rotation) {
		super.setRotation(rotation);
	}

	/**
	 * 在这里将视图初始化， 并load内容
	 */
	public void load(ContainerEntity containerEntity) {
		mComponent = ComponentHelper.getComponent(containerEntity, mViewPage);
		mEntity = mComponent.getEntity();
		if (mComponent instanceof VerticalImageComponent
				|| mComponent instanceof HorizontalImageComponent) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		// 如果是拼图或者绘图模板的话，应该全屏幕设置，坐标为0,0，旋转角度不设置
		if (mComponent instanceof HLPaintingUIComponent
				|| mComponent instanceof HLPuzzleGameUIComponent) {
			mEntity.x = 0;
			mEntity.y = 0;
			mEntity.rotation = 0;
			((View) mComponent).getLayoutParams().width = BookController
					.getInstance().getViewPage().pageWidth;
			((View) mComponent).getLayoutParams().height = BookController
					.getInstance().getViewPage().pageHeight;
		}

		if (mComponent instanceof HLSWFFileComponent) {
			if (!AppUtils.detectPackage(getContext(), "com.adobe.flashplayer")) {
				TextView textView = new TextView(getContext());
				textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				textView.setGravity(Gravity.CENTER);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				textView.setText(com.hl.android.R.string.swfcomponentnotexists);
				textView.setTextColor(Color.WHITE);
				textView.setBackgroundColor(Color.rgb(210, 210, 210));
				addView(textView, lp);
			}
		}
		if (mEntity.ptList.size() > 0) {
			targetPonit = new PointF();
			targetPonit.x = mEntity.ptList.get(0).x
					- (containerEntity.getWidth() / 2);
			targetPonit.y = mEntity.ptList.get(0).y
					- (containerEntity.getHeight() / 2);
		}

		if (mEntity.isHideAtBegining) {
			setVisibility(View.GONE);
		}
		// 如果是浏览器的话，需要判断是否联网，如果无联网
		if (mComponent instanceof WebComponent) {
			if (!WebUtils.isConnectingToInternet((Activity) getContext())) {
				this.setVisibility(View.GONE);
				return;
			}
		}
		addView((View) mComponent);
		if (mComponent instanceof VideoComponent) {
			((VideoComponent) mComponent).getSurfaceView().setVisibility(
					View.VISIBLE);
			((VideoComponent) mComponent).getSurfaceView().setZOrderOnTop(true);
			((VideoComponent) mComponent).getSurfaceView().bringToFront();
			((VideoComponent) mComponent).bringToFront();
			this.bringToFront();

		}

		int width = ((View) mComponent).getLayoutParams().width;
		int height = ((View) mComponent).getLayoutParams().height;
		setLayoutParams(new MarginLayoutParams(width, height));
		if (mEntity.rotation != 0) {
			super.setRotation(mEntity.rotation);
		}
		if (mEntity.alpha != 0) {
			this.setAlpha(mEntity.alpha);
		}

		curRecord.mWidth = getLayoutParams().width;
		curRecord.mHeight = getLayoutParams().height;
		// 坐标需要注意的是优先获取当前的坐标，如果在没有获得正常值得时候，就使用初始的坐标
		float x = getX();
		if (x == 0) {
			x = getEntity().x;
		}
		float y = getY();
		if (y == 0) {
			y = getEntity().y;
		}
		curRecord.mX = x;
		curRecord.mY = y;
		curRecord.mRotation = mEntity.rotation;
		// curRecord.mAlpha = mEntity.alpha;

		if (mEntity.isAllowUserZoom) {// judge zoom
			if ("zoom_inner".equals(mEntity.zoomType)) {
				isZoomInner = true;
			}
			// mScaleGestureDetector = new
			// ScaleGestureDetector(getContext(),this);
			// maxScale =
			// Math.min((float)BookSetting.BOOK_WIDTH/(float)getLayoutParams().width,
			// (float)BookSetting.BOOK_HEIGHT/(float)getLayoutParams().height);

		}
		if (mEntity.IsEnableGyroHor) {
			mSensorMgr = (SensorManager) mContext
					.getSystemService(Service.SENSOR_SERVICE);
			mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

			mSensorListener = new SensorEventListener() {
				int uiRot = ((Activity) mContext).getWindowManager()
						.getDefaultDisplay().getRotation();
				private boolean isPhone = checkIsPhone(uiRot);

				public void onSensorChanged(SensorEvent e) {
					int x = (int) e.values[SensorManager.DATA_X] * 2;
					int y = (int) e.values[SensorManager.DATA_Y] * 2;
					int curUIRot = ((Activity) mContext).getWindowManager()
							.getDefaultDisplay().getRotation();
					int curChangeValue = 0;
					if (isPhone) {
						if (BookSetting.IS_HOR) {
							curChangeValue = -y;
						} else {
							curChangeValue = x;
						}
					} else {
						if (BookSetting.IS_HOR) {
							if (curUIRot == 0) {
								curChangeValue = x;
							} else if (curUIRot == 2) {
								curChangeValue = -x;
							}
						} else {
							if (curUIRot == 1) {
								curChangeValue = -y;
							} else if (curUIRot == 3) {
								curChangeValue = y;
							}
						}
					}
					if (canChangeState) {
						if (isPlayingLeftGoback && curChangeValue < 0) {
							countdownTimer = null;
							isPlayingLeftGoback = false;
						}
						if (isPlayingRightGoback && curChangeValue > 0) {
							countdownTimer = null;
							isPlayingRightGoback = false;
						}
					}
					if (isPlayingLeftGoback) {
						if (countdownTimer == null) {
							canChangeState = false;
							countdownTimer = new CountDownTimer(
									20 * (2 * tSpeed + 2), 20) {
								@Override
								public void onTick(long arg0) {
									setX(getX() - curSpeed);
									requestLayout();
									curSpeed--;
								}

								@Override
								public void onFinish() {
									canChangeState = true;
									if (getX() < 0) {
										setX(0);
										requestLayout();
									} else if (getX() + getLayoutParams().width > BookSetting.BOOK_WIDTH) {
										setX(BookSetting.BOOK_WIDTH
												- getLayoutParams().width);
										requestLayout();
									}
								}
							};
							countdownTimer.start();
						}

					} else if (isPlayingRightGoback) {
						if (countdownTimer == null) {
							canChangeState = false;
							countdownTimer = new CountDownTimer(
									20 * (2 * (-tSpeed) + 2), 20) {
								@Override
								public void onTick(long arg0) {
									setX(getX() - curSpeed);
									requestLayout();
									curSpeed++;
								}

								@Override
								public void onFinish() {
									canChangeState = true;
									if (getX() < 0) {
										setX(0);
										requestLayout();
									} else if (getX() + getLayoutParams().width > BookSetting.BOOK_WIDTH) {
										setX(BookSetting.BOOK_WIDTH
												- getLayoutParams().width);
										requestLayout();
									}
								}
							};
							countdownTimer.start();
						}
					} else {
						setX(getX() - curChangeValue);
						requestLayout();
						if (getX() < 0) {
							isPlayingLeftGoback = true;
							tSpeed = curChangeValue;
							curSpeed = tSpeed;
							setX(0);
							requestLayout();
						} else if (getX() + getLayoutParams().width > BookSetting.BOOK_WIDTH) {
							isPlayingRightGoback = true;
							tSpeed = curChangeValue;
							curSpeed = tSpeed;
							setX(BookSetting.BOOK_WIDTH
									- getLayoutParams().width);
							requestLayout();
						}
					}
				}

				private boolean checkIsPhone(int uiRot2) {
					if ((uiRot2 == 0 || uiRot2 == 2) && (!BookSetting.IS_HOR)) {
						return true;
					} else if ((uiRot2 == 1 || uiRot2 == 3)
							&& (BookSetting.IS_HOR)) {
						return true;
					}
					return false;
				}

				public void onAccuracyChanged(Sensor s, int accuracy) {
				}
			};
			mSensorMgr.registerListener(mSensorListener, mSensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
		initWidth = getLayoutParams().width;
		initHeight = getLayoutParams().height;
		mCurWidth = (int) initWidth;
		setRelativeObjectList();

	}

	/**
	 * TODO 开始播放 包括播放视频、音频，或者序列帧
	 */
	public void play() {
		mComponent.play();
	}

	/**
	 * TODO 暂停
	 */
	public void pause() {
		mComponent.pause();
	}

	/**
	 * TODO 停止
	 */
	public void stop() {
		mComponent.stop();

	}

	public void resume() {
		mComponent.resume();
	}

	/**
	 * TODO 回收资源 回收资源内存 停止动画 清理变量
	 */
	public void recyle() {
		AnimationHelper.stopAnimation(this);
		AnimationHelper.animatiorMap.remove(this);
		mComponent.stop();
		if (animationUpdateListener != null) {
			animationUpdateListener.mStop = true;
			animationUpdateListener = null;
		}
		if (mComponent instanceof ComponentPost) {
			((ComponentPost) mComponent).recyle();
		}
	}

	public void show() {
		if (getVisibility() != View.VISIBLE) {
			setVisibility(View.VISIBLE);
			// if(mComponent instanceof VideoComponent &&
			// !mEntity.isHideAtBegining){
			// ((VideoComponent)mComponent).setVisibility(View.VISIBLE);
			// ((VideoComponent)mComponent).getSurfaceView().setZOrderOnTop(true);
			// ((VideoComponent)mComponent).getSurfaceView().bringToFront();
			// this.bringToFront();
			// }
			mComponent.show();
		}
	}

	public void hide() {
		if (getVisibility() == View.VISIBLE) {
			setVisibility(View.GONE);
			mComponent.hide();
		}
	}

	/**
	 * 设置字体大小，专门为ScrollTextViewComponent 提供
	 * 
	 * @param fontSize
	 */
	public void setFontSize(String fontSize) {
		if (mComponent instanceof ScrollTextViewComponent) {
			ScrollTextViewComponent textcomponent = (ScrollTextViewComponent) mComponent;
			textcomponent.setFontSize(fontSize);
		}
	}

	@Override
	public void setAlpha(float alpha) {
		if (alpha <= 0)
			alpha = 0.0f;
		else if (alpha >= 1)
			alpha = 1.0f;
		super.setAlpha(alpha);
	}

	public void setLeftPadding(int padding) {
		setPadding(padding, 0, 0, 0);
		requestLayout();
		postInvalidate();
	}

	public void setTopPadding(int padding) {
		setPadding(0, padding, 0, 0);
		requestLayout();
		postInvalidate();
	}

	public void setBottomPadding(int padding) {
		setPadding(0, 0, 0, padding);
		requestLayout();
		postInvalidate();
	}

	public void setRightPadding(int padding) {
		setPadding(0, 0, padding, 0);
		requestLayout();
		postInvalidate();
	}

	@Override
	public void setY(float y) {
		super.setY(y + mViewPage.getOffSetY());
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (isZoomInner) {
			super.onLayout(changed, l, t, r, b);
		} else {
			View v = getChildAt(0);
			if (v == null)
				return;
			v.setPadding(-this.getPaddingLeft(), -this.getPaddingTop(),
					-this.getPaddingRight(), -this.getPaddingBottom());
			v.layout(this.getPaddingLeft(), this.getPaddingTop(),
					r - l - this.getPaddingRight(),
					b - t - this.getPaddingBottom());
		}
		if (mComponent instanceof VideoComponent) {
			HLMediaController controllerWindow = ((VideoComponent) mComponent)
					.getControllerWindow();
			if (controllerWindow != null) {
				try {
					controllerWindow.upDateWindowPosition();
				} catch (Exception exception) {

				}
			}
		}
	}

	private float downY = 0;
	private boolean isslided = false;
	private int mCurWidth;

	float initWidth;
	float initHeight;

	private boolean isTwoPointDown;

	private int touchW;

	private float touchY;

	private float touchX;

	private int endPositionx;

	private int endPositiony;

	private float postionXBeforeScale;

	private int widthBeforeScale;

	private float postionYBeforeScale;

	private float posionXBeforeMove;

	private float posionYBeforeMove;

	private float posionCenterWidth;

	private float posionCenterHeitht;

	private static int flipdistance = 20;
  
	/**
	 * 1需要注意的是在热区判断的时候，需要判断原来在不在热区，如果action_down的时候已经在热区里了，
	 * 在action_up的时候就不用触发移入热区的事件了
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果是隐藏组件，就不需要触发事件了
		if (getVisibility() != View.VISIBLE)
			return false;
		if (mComponent instanceof PDFDocumentViewComponentMU)
			return false;
		// scale event
		if (mEntity.isAllowUserZoom) {
			doSomeThing(event);
			// mScaleGestureDetector.onTouchEvent(event);
		}
		// 下面处理组件的点击事件，同时也要注意处理可移动组件的随手指移动，以及热区的处理
		if (event.getPointerCount() == 1) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				lastY4viewcell = (int) event.getRawY();
				isslided = false;
				downY = event.getRawY();
				if (getEntity().isStroyTelling) {
					// 如果是可以随手指移动的就需要记住当前的坐标位置，并将size变大点
					setStartPosition();
					// setRelativeStartPosition();
					if (getEntity().isMoveScale && !isZoomInner) {
						storeLayoutBeforeScale();
						scaleMe(1.2f * widthBeforeScale / initWidth);
					}
					lastX = event.getRawX();
					lastY = event.getRawY();
					getGlobalVisibleRect(downRect);
				}
				// 处理点击事件
				BookController.getInstance().runBehavior(getEntity(),
						"BEHAVIOR_ON_CLICK");
				break;
			case MotionEvent.ACTION_MOVE:
				// 如果是可以随手指移动的，那就移动
				float dy = event.getRawY() - downY;
				if (getEntity().isStroyTelling) {
					if (isZoomInner) {
						if (initWidth == mCurWidth) {
							if (!isTwoPointDown) {
								float moveX = event.getRawX() - lastX;
								float moveY = event.getRawY() - lastY;
								moveMe(moveX, moveY, true);
								lastX = event.getRawX();
								lastY = event.getRawY();
							}
						}
					} else {
						if (!isTwoPointDown) {
							float moveX = event.getRawX() - lastX;
							float moveY = event.getRawY() - lastY;
							moveMe(moveX, moveY, true);
							lastX = event.getRawX();
							lastY = event.getRawY();
						}
					}
				} else {
					if (isZoomInner) {
						if (initWidth == mCurWidth) {
							if (BookSetting.BOOK_HEIGHT < BookController
									.getInstance().getViewPage().pageHeight) {
								float dy4viewcell = event.getRawY()
										- lastY4viewcell;
								BookController.getInstance().getViewPage()
										.moveDy(dy4viewcell);
								lastY4viewcell = event.getRawY();
							}
						}
					} else {
						if (!isTwoPointDown) {
							if (BookSetting.BOOK_HEIGHT < BookController
									.getInstance().getViewPage().pageHeight) {
								float dy4viewcell = event.getRawY()
										- lastY4viewcell;
								BookController.getInstance().getViewPage()
										.moveDy(dy4viewcell);
								lastY4viewcell = event.getRawY();
							}
						}
					}
				}
				// 下滑
				if (dy > flipdistance && !isslided) {
					// isslided = true;
					BookController.getInstance().runBehavior(getEntity(),
							"BEHAVIOR_ON_SLIDER_DOWN");
					// 上滑
				} else if (dy < -flipdistance && !isslided) {
					isslided = true;
					BookController.getInstance().runBehavior(getEntity(),
							"BEHAVIOR_ON_SLIDER_UP");
				} 
				break;
			case MotionEvent.ACTION_UP:
			
				if (getEntity().isStroyTelling) {
					// 如果是因为按下移动导致的放大需要回到初始大小
					// 恢复组件的大小
					if (getEntity().isMoveScale) {
						storeLayoutBeforeScale();
						scaleMe(1.0f / 1.2f * widthBeforeScale / initWidth);
					}
					// 并判断热区范围
					hotRectBeheavor(event);
				}
				// 处理BEHAVIOR_ON_MOUSE_UP事件
				// 当页面x绝对位置大于5时，视为在做页间滑动，不响应点击抬起事件
				// 如此处理是因为页间滑动抬起时如果设置了点击抬起切换页面等事件，将出现冲突
				if (Math.abs(mViewPage.getX()) <= 5) {
					BookController.getInstance().runBehavior(getEntity(),
							"BEHAVIOR_ON_MOUSE_UP");
				}
				break;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (isTwoPointDown) {
				if (isZoomInner) {
					if (endPositionx > 0) {
						endPositionx = 0;
					} else if (endPositionx + getCurWidth() < getLayoutParams().width) {
						endPositionx = (int) (getLayoutParams().width - getCurWidth());
					}
					if (endPositiony > 0) {
						endPositiony = 0;
					} else if (endPositiony + getCurWidth()
							* getLayoutParams().height
							/ getLayoutParams().width < getLayoutParams().height) {
						endPositiony = (int) (getLayoutParams().height - getCurWidth()
								* getLayoutParams().height
								/ getLayoutParams().width);
					}
					getChildAt(0).setX(endPositionx);
					getChildAt(0).setY(endPositiony);
				}
			}
			isTwoPointDown = false;
		}
		// 传递给子对象
		((View) mComponent).onTouchEvent(event);
		PageEntity page = BookController.getInstance().mainViewPage.getEntity();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			downEvent = MotionEvent.obtain(event);
			mViewPage.isHorMove = false;
			if (page.enablePageTurnByHand && page.isEnableNavigation()) {
				EventDispatcher.getInstance().onTouch(downEvent);
			}
			mViewPage.getDetector().onTouchEvent(downEvent);
			if (BookSetting.FLIP_CHANGE_PAGE && BookSetting.FLIPCODE == 1) {
				mViewPage.doTouchAction4MovePageBetweenPage(downEvent);
			}
		} else {
			if (event.getAction() == MotionEvent.ACTION_MOVE
					&& downEvent != null) {
				float firstDx = event.getRawX() - downEvent.getRawX();
				float firstDy = event.getRawY() - downEvent.getRawY();
				mViewPage.isHorMove = Math.abs(firstDx) > Math.abs(firstDy);
				downEvent = null;
			}
			Log.d("zhaoq", "is hor move" + mViewPage.isHorMove + " action is "
					+ event.getRawX());
			// 页间滑动与上下滑动是互斥的
			mViewPage.getDetector().onTouchEvent(event);
			if(!mViewPage.isCommonPage)
			{
				if (mViewPage.isHorMove && BookSetting.FLIPCODE == 1) {
					if (BookSetting.FLIP_CHANGE_PAGE) {
						mViewPage.doTouchAction4MovePageBetweenPage(event);
					} else {
						if (page.enablePageTurnByHand && page.isEnableNavigation()) {
							EventDispatcher.getInstance().onTouch(event);
						}
					}
				} else {
					// 传递给翻页事件
					if (page.enablePageTurnByHand && page.isEnableNavigation()) {
						EventDispatcher.getInstance().onTouch(event);
					}
				}
			}
		}

		oldEvent = MotionEvent.obtain(event);
		return true;
	}

	MotionEvent downEvent;

	private void setStartPosition() {
		posionXBeforeMove = getX();
		posionYBeforeMove = getY();
		posionCenterWidth = getX() + getLayoutParams().width / 2.0f;
		posionCenterHeitht = getY() + getLayoutParams().height / 2.0f;
		setRelativeStartPosition();
	}

	private void setRelativeStartPosition() {
		setRelativeObjectList();
		for (ViewCell cell : relatives) {
			cell.setStartPosition();
		}
	}

	private void storeLayoutBeforeScale() {
		postionXBeforeScale = getX();
		postionYBeforeScale = getY() - mViewPage.getOffSetY();
		widthBeforeScale = getLayoutParams().width;
		setRelativeObjectList();
		for (ViewCell cell : relatives) {
			cell.storeLayoutBeforeScale();
		}
	}

	public void doSomeThing(MotionEvent event) {
		if (event.getPointerCount() == 1) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (isZoomInner) {
					if (isTwoPointDown) {
						return;
					}
					int translateToPositionX = (int) (getChildAt(0).getX()
							+ event.getRawX() - oldEvent.getRawX());
					int translateToPositionY = (int) (getChildAt(0).getY()
							+ event.getRawY() - oldEvent.getRawY());
					if (translateToPositionX > 0) {
						translateToPositionX = 0;
					} else if (translateToPositionX + getCurWidth() < getLayoutParams().width) {
						translateToPositionX = (int) (getLayoutParams().width - getCurWidth());
					}
					if (translateToPositionY > 0) {
						translateToPositionY = 0;
					} else if (translateToPositionY + getCurWidth()
							* getLayoutParams().height
							/ getLayoutParams().width < getLayoutParams().height) {
						translateToPositionY = (int) (getLayoutParams().height - getCurWidth()
								* getLayoutParams().height
								/ getLayoutParams().width);
					}
					getChildAt(0).setX(translateToPositionX);
					getChildAt(0).setY(translateToPositionY);
				}
			}
		} else if (event.getPointerCount() == 2) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				isTwoPointDown = true;
				oldDistance = (float) Math.sqrt((event.getX(0) - event.getX(1))
						* (event.getX(0) - event.getX(1))
						+ (event.getY(0) - event.getY(1))
						* (event.getY(0) - event.getY(1)));
				mid.set((event.getX(0) + event.getX(1)) / 2,
						(event.getY(0) + event.getY(1)) / 2);
				storeLayoutBeforeScale();
				touchX = getChildAt(0).getX();
				touchY = getChildAt(0).getY() - mViewPage.getOffSetY();
				touchW = getChildAt(0).getLayoutParams().width;
				break;
			case MotionEvent.ACTION_MOVE:
				float newDistance;
				newDistance = (float) Math.sqrt((event.getX(0) - event.getX(1))
						* (event.getX(0) - event.getX(1))
						+ (event.getY(0) - event.getY(1))
						* (event.getY(0) - event.getY(1)));
				mCurWidth = (int) (getCurWidth() * newDistance / oldDistance);
				if (mCurWidth >= 4 * initWidth) {
					mCurWidth = (int) (4 * initWidth);
				} else if (mCurWidth <= initWidth) {
					mCurWidth = (int) initWidth;
				}
				if ("zoom_inner".equals(mEntity.zoomType)) {
					getChildAt(0).setLayoutParams(
							new LayoutParams(mCurWidth, (int) (mCurWidth
									* initHeight / initWidth)));
					endPositionx = (int) (mid.x - (mid.x - touchX) * mCurWidth
							/ touchW);
					endPositiony = (int) (mid.y - (mid.y - touchY) * mCurWidth
							/ touchW);
					getChildAt(0).setX(endPositionx);
					getChildAt(0).setY(endPositiony);
				} else {
					scaleMe(mCurWidth / initWidth);
					// setRelativeScale(mCurWidth/initWidth);
				}
				oldDistance = newDistance;
				break;
			}
		}
	}

	/**
	 * 
	 * @param curScale2Init
	 *            相对于加载时宽高的缩放率
	 */
	private void setRelativeScale(float curScale2Init) {
		setRelativeObjectList();
		for (ViewCell cell : relatives) {
			cell.scaleMe(1.0f * curScale2Init * cell.mCurWidth / cell.initWidth);
		}
	}

	/**
	 * 
	 * @param curScale2Init
	 *            相对于加载时宽高的缩放率
	 */
	private void scaleMe(float curScale2Init) {
		if (curScale2Init < 1) {
			curScale2Init = 1.0f;
		}
		LayoutParams layoutParams = new LayoutParams(
				(int) (initWidth * curScale2Init),
				(int) (initHeight * curScale2Init));
		setLayoutParams(layoutParams);
		setX(postionXBeforeScale + widthBeforeScale / 2 - initWidth
				* curScale2Init / 2);
		setY(postionYBeforeScale + widthBeforeScale * (initHeight / initWidth)
				/ 2 - initWidth * curScale2Init * (initHeight / initWidth) / 2);
		setRelativeScale(curScale2Init);
	}

	private float getCurWidth() {
		return mCurWidth;
	}

	/**
	 * 移动我
	 * 
	 * @param dx
	 * @param dy
	 */
	public void moveMe(float dx, float dy, boolean aa) {
		float resultX = getX() + getLayoutParams().width / 2.0f + dx;
		float resultY = getY() + getLayoutParams().height / 2.0f + dy;
		// 先检验坐标合法性
		float[] resultPoint = { resultX, resultY };
		if (mEntity.ptList.size() != 0 && aa) {
			calcStroyTellPt(resultPoint);
		}
		// else{//此处注释是为了与ios和软件效果一致，不再做边界限制
		// if(resultPoint[0] < 0)
		// resultPoint[0] = 0;
		// if(resultPoint[1] < mViewPage.getOffSetY())
		// resultPoint[1] = mViewPage.getOffSetY();
		// if(resultPoint[0] > BookSetting.BOOK_WIDTH-getWidth())
		// resultPoint[0] = BookSetting.BOOK_WIDTH-getWidth();
		// if(resultPoint[1] >
		// mViewPage.getOffSetY()+mViewPage.pageHeight-getHeight())
		// resultPoint[1] =
		// mViewPage.getOffSetY()+mViewPage.pageHeight-getHeight();
		// }
		float relativeDx = resultPoint[0] - getX() - getLayoutParams().width
				/ 2.0f;
		float relativeDy = resultPoint[1] - getY() - getLayoutParams().height
				/ 2.0f;
		setX(resultPoint[0] - getLayoutParams().width / 2.0f);
		super.setY(resultPoint[1] - getLayoutParams().height / 2.0f);
		relativeMove(relativeDx, relativeDy);
	}

	private void relativeMove(float dx, float dy) {
		setRelativeObjectList();
		for (ViewCell cell : relatives) {
			float rate = cell.getEntity().getLinkPageObj().rate;
			float resulX = dx * rate;
			float resulY = dy * rate;
			cell.moveMe(resulX, resulY, false);
			// cell.setX(cell.getX() + resulX);
			// cell.setY(cell.getY() + resulY);
		}
	}

	// @Override
	// public void setX(float x) {
	// setRelativeObjectList();
	// for(ViewCell cell:relatives){
	// float rate = cell.getEntity().getLinkPageObj().rate;
	// float dx = x - getX();
	// float resulX = dx*rate;
	// cell.setX(cell.getX() + resulX);
	// }
	// super.setX(x);
	// }
	// @Override
	// public void setY(float y) {
	// setRelativeObjectList();
	// for(ViewCell cell:relatives){
	// float rate = cell.getEntity().getLinkPageObj().rate;
	// float dy = y - getY();
	// float resultY = dy*rate;
	// cell.setY(cell.getY() + resultY);
	// }
	// super.setY(y);
	// }
	/**
	 * 按中心点算
	 * 
	 * @param pos
	 */
	private void calcStroyTellPt(float[] pos) {
		float targetDx = ScreenUtils.getHorScreenValue(targetPonit.x)
				- mEntity.x;
		float targetDy = ScreenUtils.getVerScreenValue(targetPonit.y)
				- mEntity.y;
		float k = targetDy / targetDx;

		float actualDx = pos[0] - getX() - getLayoutParams().width / 2.0f;
		float actualDy = pos[1] - getY() - getLayoutParams().height / 2.0f;
		// 根据谁的偏移量大，按照x还是y来计算
		if (Math.abs(targetDx) >= Math.abs(targetDy)) {
			pos[1] = getY() + getLayoutParams().height / 2.0f + actualDx * k;
		} else {
			pos[0] = getX() + getLayoutParams().width / 2.0f + actualDy / k;
		}
		if (pos[0] < Math
				.min(mEntity.x + initWidth / 2.0f,
						ScreenUtils.getHorScreenValue(targetPonit.x)
								+ initWidth / 2.0f))
			pos[0] = Math.min(mEntity.x + initWidth / 2.0f,
					ScreenUtils.getHorScreenValue(targetPonit.x) + initWidth
							/ 2.0f);
		if (pos[1] < Math.min(mEntity.y + mViewPage.getOffSetY() + initHeight
				/ 2.0f, mViewPage.getOffSetY() + initHeight / 2.0f
				+ ScreenUtils.getVerScreenValue(targetPonit.y)))
			pos[1] = Math.min(mEntity.y + mViewPage.getOffSetY() + initHeight
					/ 2.0f, mViewPage.getOffSetY() + initHeight / 2.0f
					+ ScreenUtils.getVerScreenValue(targetPonit.y));
		if (pos[0] > Math
				.max(mEntity.x + initWidth / 2.0f,
						ScreenUtils.getHorScreenValue(targetPonit.x)
								+ initWidth / 2.0f))
			pos[0] = Math.max(mEntity.x + initWidth / 2.0f,
					ScreenUtils.getHorScreenValue(targetPonit.x) + initWidth
							/ 2.0f);
		if (pos[1] > Math.max(mEntity.y + mViewPage.getOffSetY() + initHeight
				/ 2.0f, mViewPage.getOffSetY() + initHeight / 2.0f
				+ ScreenUtils.getVerScreenValue(targetPonit.y)))
			pos[1] = Math.max(mEntity.y + mViewPage.getOffSetY() + initHeight
					/ 2.0f, mViewPage.getOffSetY() + initHeight / 2.0f
					+ ScreenUtils.getVerScreenValue(targetPonit.y));
	}

	private ArrayList<ViewCell> relatives = new ArrayList<ViewCell>();

	/**
	 * 初始化关联对象的数据
	 */
	private void setRelativeObjectList() {
		// if(relatives.size() == 0){
		if (BookController.getInstance().getViewPage() != null) {
			for (int i = 0; i < BookController.getInstance().getViewPage()
					.getChildCount(); i++) {
				ViewCell view = (ViewCell) BookController.getInstance()
						.getViewPage().getChildAt(i);
				if (mEntity.componentId.equals(view.getEntity()
						.getLinkPageObj().entityID)) {
					if (!relatives.contains(view))
						relatives.add(view);
				}
			}
		}
		// }
	}

	/**
	 * 重写放大的方法，加上关联对象的放大和缩小
	 */
	// @Override
	// public void setScaleY(float scaleY) {
	// super.setScaleY(scaleY);
	// setRelativeObjectList();
	// for(ViewCell cell:relatives){
	// float myScale = scaleY * cell.getEntity().getLinkPageObj().rate;
	// cell.setScaleY(myScale);
	// }
	// }
	// @Override
	// public void setScaleX(float scaleX) {
	// super.setScaleX(scaleX);
	// setRelativeObjectList();
	// for(ViewCell cell:relatives){
	// float myScale = scaleX * cell.getEntity().getLinkPageObj().rate;
	// cell.setScaleX(myScale);
	// }
	// }
	// /**
	// * 设置我的方法是比例
	// * @param scale
	// */
	// public void setCellScale(float scale){
	// if("zoom_inner".equals(mEntity.zoomType)){
	// ((View)mComponent).setScaleX(scale);
	// ((View)mComponent).setScaleY(scale);
	// }else{
	// setScaleX(scale);
	// setScaleY(scale);
	// }
	// }

	/**
	 * 处理移入热区和移出热区的事件
	 * 
	 * @param event
	 */
	private void hotRectBeheavor(MotionEvent event) {
		boolean needBack = true;

		Rect myRect = new Rect();
		getGlobalVisibleRect(myRect);
		curRecord.mX = getX();
		curRecord.mY = getY();
		if (mEntity.behaviors != null && mEntity.behaviors.size() != 0) {
			Iterator<BehaviorEntity> it = mEntity.behaviors.iterator();
			while (it.hasNext()) {
				BehaviorEntity e = it.next();
				if (e.EventName.equals("BEHAVIOR_ON_OUT_SPOT")
						|| e.EventName.equals("BEHAVIOR_ON_ENTER_SPOT")) {
					ViewCell viewCell = BookController.getInstance()
							.getViewPage().getCellByID(e.EventValue);
					if (viewCell == null
							|| viewCell.getVisibility() != View.VISIBLE)
						continue;
					Rect hotRect = new Rect();
					viewCell.getGlobalVisibleRect(hotRect);
					// down的时候不在热区内部
					if (!hotRect.contains(downRect) && hotRect.contains(myRect)
							&& e.EventName.equals("BEHAVIOR_ON_ENTER_SPOT")) {
						BookController.getInstance().runBehavior(e);
						needBack = false;
					}
					// 松手的时候不在热区内部
					if (!hotRect.contains(myRect) && hotRect.contains(downRect)
							&& e.EventName.equals("BEHAVIOR_ON_OUT_SPOT")) {
						BookController.getInstance().runBehavior(e);
						needBack = false;
					}
				}
			}
		}
		// 如果没有触发热点并且需要返回
		if (mEntity.isPushBack && needBack) {
			setRelativeObjectList();
			doBack();

		}
	}

	//
	// private float myScale = 1.0f;
	// private static final float MIN_SCALE = 1.0f;
	// private static final float MAX_SCALE = 5.0f;
	// @Override
	// public boolean onScale(ScaleGestureDetector detector) {
	// // TODO Auto-generated method stub
	// myScale = Math.min(Math.max(myScale*detector.getScaleFactor(),
	// MIN_SCALE), MAX_SCALE);
	// // if(System.currentTimeMillis()-curTime < 50)return true;
	// // float curDis = arg0.getCurrentSpan();
	// //// if(Math.abs(curDis - prevDis) < 50)return true;
	// // float scale = 1.0f;
	// // if(prevDis == 0f){
	// // scale = 1.0f;
	// // }else{
	// // scale = curDis/prevDis;
	// // }
	// //// if(scale<0.8){
	// //// return true;
	// //// }
	// // myScale = myScale *scale;
	// // if(myScale>maxScale)myScale = maxScale;
	// // if(myScale<0.5)myScale = 0.5f;
	// setCellScale(myScale);
	// postInvalidate();
	// return true;
	// }
	// @Override
	// public boolean onScaleBegin(ScaleGestureDetector arg0) {
	// // TODO Auto-generated method stub
	// return mEntity.isAllowUserZoom;
	// }
	// @Override
	// public void onScaleEnd(ScaleGestureDetector arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	private void doBack() {
		PropertyValuesHolder xp = PropertyValuesHolder.ofFloat("posionCenterX",
				posionCenterWidth);
		PropertyValuesHolder yp = PropertyValuesHolder.ofFloat("posionCenterY",
				posionCenterHeitht - mViewPage.getOffSetY());
		ObjectAnimator backAnimator = ObjectAnimator.ofPropertyValuesHolder(
				this, xp, yp);
		backAnimator.setDuration(600);
		backAnimator.start();
		curRecord.mX = posionXBeforeMove;
		curRecord.mY = posionYBeforeMove;
		for (int i = 0; i < relatives.size(); i++) {
			ViewCell cell = relatives.get(i);
			cell.doBack();
		}
	}

	public float getPosionCenterX() {
		return getX() + getLayoutParams().width / 2.0f;
	}

	public float getPosionCenterY() {
		return getY() + getLayoutParams().height / 2.0f
				- mViewPage.getOffSetY();
	}

	public void setPosionCenterX(float centerx) {
		setX(centerx - getLayoutParams().width / 2.0f);
	}

	public void setPosionCenterY(float centery) {
		setY(centery - getLayoutParams().height / 2.0f);
	}

	/**
	 * 初始化viewcell 播放动画以后重新播放的时候使用
	 */
	public void resetViewCell() {
		setX(mEntity.x);
		setY(mEntity.y);
		setAlpha(mEntity.alpha);
		setScaleX(1.0f);
		setScaleY(1.0f);
		super.setRotation(mEntity.getRotation());
		setPadding(0, 0, 0, 0);
		resetViewRecord();
		postInvalidate();
	}

	public int moveX = 0;
	public int moveY = 0;

	/**
	 * 滑动相关的变化
	 * 
	 * @param rate
	 *            变化的比率
	 */
	public void doSlideAction(float dy) {
		// //透明度
		// float durAlpha = mEntity.slideBindingAlha - mEntity.alpha;
		// float targetAlpha = mEntity.alpha + (durAlpha*rate);
		// setAlpha(targetAlpha);
		// //TODO 坐标
		// int durX = mEntity.slideBindingX - mEntity.x;
		// moveX = (int) (durX*rate);
		// // setX(targetX);
		//
		// int durY = mEntity.slideBindingY - mEntity.y;
		// moveY = (int) (durY*rate);
		// // setX(targetX);
		// //size
		// float durWidth = mEntity.slideBindingWidth - getLayoutParams().width;
		// float targetWidth = getLayoutParams().width + (durWidth*rate);
		// float scaleX = targetWidth/(float)getLayoutParams().width;
		// setScaleX(scaleX);
		// float durHeight = mEntity.slideBindingHeight -
		// getLayoutParams().height;
		// float targetHeight = getLayoutParams().height + (durHeight*rate);
		// float scaleY = targetHeight/(float)getLayoutParams().height;
		// setScaleY(scaleY);
		moveX = (int) (dy * mEntity.sliderHorRate);
		moveY = (int) (dy * mEntity.sliderVerRate);
	}

	/**
	 * 滑动相关的变化
	 * 
	 * @param rate
	 *            变化的比率
	 */
	public void doSlideAction4TweenPage(float rate) {
		// //透明度
		float durAlpha = mEntity.slideBindingAlha - mEntity.alpha;
		float targetAlpha = mEntity.alpha + (durAlpha * Math.abs(rate));
		setAlpha(targetAlpha);
		// TODO 坐标

		float durWidth = mEntity.slideBindingWidth - getLayoutParams().width;
		float targetWidth = getLayoutParams().width
				+ (durWidth * Math.abs(rate));
		float scaleX = targetWidth / (float) getLayoutParams().width;
		setScaleX(scaleX);
		float durHeight = mEntity.slideBindingHeight - getLayoutParams().height;
		float targetHeight = getLayoutParams().height
				+ (durHeight * Math.abs(rate));
		float scaleY = targetHeight / (float) getLayoutParams().height;
		setScaleY(scaleY);
		float durX = ScreenUtils.getHorScreenValue(mEntity.slideBindingX)
				- mEntity.x;
		setX(mEntity.x - durX * rate - getLayoutParams().width
				* (1.0f - scaleX) / 2.0f);

		float durY = ScreenUtils.getVerScreenValue(mEntity.slideBindingY)
				- mEntity.y;
		setY(mEntity.y - durY * rate - getLayoutParams().height
				* (1.0f - scaleY) / 2.0f);
	}
}
