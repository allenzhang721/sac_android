package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;

public class HLViewFlipperVerticle extends FrameLayout implements Component,
		ComponentPost {
	ComponentEntity entity;
	ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

	public HLViewFlipperVerticle(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HLViewFlipperVerticle(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;
		this.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;

	}

	
	@Override
	public void load() {
		ViewGroup.LayoutParams lp = this.getLayoutParams();

		ArrayList<String> sourceIDS = ((MoudleComponentEntity) this.entity)
				.getSourceIDList();

		if (null != sourceIDS && sourceIDS.size() > 0) {
			for (int i = 0; i < sourceIDS.size(); i++) {
				ImageView imageView = new ImageView(this.getContext());
				imageView.measure(MeasureSpec.makeMeasureSpec(lp.width,
						MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						lp.height, MeasureSpec.EXACTLY));

				imageView.setImageBitmap(getBitMap(sourceIDS.get(i)));
				this.addView(imageView);
			}
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		this.showOnly(0);
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}

	 

	@Override
	public void play() {

	}

	public Bitmap getBitMap(String localSourceID) {
		return BitmapUtils.getBitMap(localSourceID, getContext(), getLayoutParams().width, getLayoutParams().height);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	private float oldTouchValue;

	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			oldTouchValue = touchevent.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {

			float currentX = touchevent.getY();
			if ((oldTouchValue - currentX) < -5) {
				this.setInAnimation(inFromLeftAnimation());
				this.setOutAnimation(outToRightAnimation());
				this.showPrevious();

			} else if ((oldTouchValue - currentX) > 5) {
				this.setInAnimation(inFromRightAnimation());
				this.setOutAnimation(outToLeftAnimation());
				this.showNext();
			} else {
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_CLICK);
			}

			break;
		}
		}
		return true;

	}

	public static Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(350);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	public static Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT,-1.0f);
		outtoLeft.setDuration(350);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	// for the next movement
	public static Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(350);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	public static Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f);
		outtoRight.setDuration(350);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	int mWhichChild = 0;
	boolean mFirstTime = true;
	boolean mAnimateFirstTime = true;

	Animation mInAnimation;
	Animation mOutAnimation;

	/**
	 * Sets which child view will be displayed.
	 * 
	 * @param whichChild
	 *            the index of the child view to display
	 */
	public void setDisplayedChild(int whichChild) {
		mWhichChild = whichChild;
		if (whichChild >= getChildCount()) {
			mWhichChild = 0;
		} else if (whichChild < 0) {
			mWhichChild = getChildCount() - 1;
		}
		boolean hasFocus = getFocusedChild() != null;
		// This will clear old focus if we had it
		showOnly(mWhichChild);
		if (hasFocus) {
			// Try to retake focus if we had it
			requestFocus(FOCUS_FORWARD);
		}
	}

	/**
	 * Returns the index of the currently displayed child view.
	 */
	public int getDisplayedChild() {
		return mWhichChild;
	}

	/**
	 * Manually shows the next child.
	 */
	public void showNext() {
		setDisplayedChild(mWhichChild + 1);
	}

	/**
	 * Manually shows the previous child.
	 */
	public void showPrevious() {
		setDisplayedChild(mWhichChild - 1);
	}

	/**
	 * Shows only the specified child. The other displays Views exit the screen
	 * with the {@link #getOutAnimation() out animation} and the specified child
	 * enters the screen with the {@link #getInAnimation() in animation}.
	 * 
	 * @param childIndex
	 *            The index of the child to be shown.
	 */
	void showOnly(int childIndex) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			final boolean checkForFirst = (!mFirstTime || mAnimateFirstTime);
			if (i == childIndex) {
				if (checkForFirst && mInAnimation != null) {
					child.startAnimation(mInAnimation);
				}
				child.setVisibility(View.VISIBLE);
				mFirstTime = false;
			} else {

				if (checkForFirst && mOutAnimation != null
						&& child.getVisibility() == View.VISIBLE) {
					child.startAnimation(mOutAnimation);
				} else if (child.getAnimation() == mInAnimation)
					child.clearAnimation();
				child.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		// if (getChildCount() == 1) {
		// child.setVisibility(View.VISIBLE);
		// } else {
		// child.setVisibility(View.GONE);
		// }

		child.setVisibility(View.VISIBLE);
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		mWhichChild = 0;
		mFirstTime = true;
	}

	@Override
	public void removeView(View view) {
		final int index = indexOfChild(view);
		if (index >= 0) {
			removeViewAt(index);
		}
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		final int childCount = getChildCount();
		if (childCount == 0) {
			mWhichChild = 0;
			mFirstTime = true;
		} else if (mWhichChild >= childCount) {
			// Displayed is above child count, so float down to top of stack
			setDisplayedChild(childCount - 1);
		} else if (mWhichChild == index) {
			// Displayed was removed, so show the new child living in its place
			setDisplayedChild(mWhichChild);
		}
	}

	public void removeViewInLayout(View view) {
		removeView(view);
	}

	public void removeViews(int start, int count) {
		super.removeViews(start, count);
		if (getChildCount() == 0) {
			mWhichChild = 0;
			mFirstTime = true;
		} else if (mWhichChild >= start && mWhichChild < start + count) {
			// Try showing new displayed child, wrapping if needed
			setDisplayedChild(mWhichChild);
		}
	}

	public void removeViewsInLayout(int start, int count) {
		removeViews(start, count);
	}

	/**
	 * Returns the View corresponding to the currently displayed child.
	 * 
	 * @return The View currently displayed.
	 * 
	 * @see #getDisplayedChild()
	 */
	public View getCurrentView() {
		return getChildAt(mWhichChild);
	}

	/**
	 * Returns the current animation used to animate a View that enters the
	 * screen.
	 * 
	 * @return An Animation or null if none is set.
	 * 
	 * @see #setInAnimation(android.view.animation.Animation)
	 * @see #setInAnimation(android.content.Context, int)
	 */
	public Animation getInAnimation() {
		return mInAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that enters the screen.
	 * 
	 * @param inAnimation
	 *            The animation started when a View enters the screen.
	 * 
	 * @see #getInAnimation()
	 * @see #setInAnimation(android.content.Context, int)
	 */
	public void setInAnimation(Animation inAnimation) {
		mInAnimation = inAnimation;
	}

	/**
	 * Returns the current animation used to animate a View that exits the
	 * screen.
	 * 
	 * @return An Animation or null if none is set.
	 * 
	 * @see #setOutAnimation(android.view.animation.Animation)
	 * @see #setOutAnimation(android.content.Context, int)
	 */
	public Animation getOutAnimation() {
		return mOutAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that exit the screen.
	 * 
	 * @param outAnimation
	 *            The animation started when a View exit the screen.
	 * 
	 * @see #getOutAnimation()
	 * @see #setOutAnimation(android.content.Context, int)
	 */
	public void setOutAnimation(Animation outAnimation) {
		mOutAnimation = outAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that enters the screen.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param resourceID
	 *            The resource id of the animation.
	 * 
	 * @see #getInAnimation()
	 * @see #setInAnimation(android.view.animation.Animation)
	 */
	public void setInAnimation(Context context, int resourceID) {
		setInAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	/**
	 * Specifies the animation used to animate a View that exit the screen.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param resourceID
	 *            The resource id of the animation.
	 * 
	 * @see #getOutAnimation()
	 * @see #setOutAnimation(android.view.animation.Animation)
	 */
	public void setOutAnimation(Context context, int resourceID) {
		setOutAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	/**
	 * Indicates whether the current View should be animated the first time the
	 * ViewAnimation is displayed.
	 * 
	 * @param animate
	 *            True to animate the current View the first time it is
	 *            displayed, false otherwise.
	 */
	public void setAnimateFirstView(boolean animate) {
		mAnimateFirstTime = animate;
	}

	@Override
	public int getBaseline() {
		return (getCurrentView() != null) ? getCurrentView().getBaseline()
				: super.getBaseline();
	}

	private static final boolean LOGD = false;

	private static final int DEFAULT_INTERVAL = 3000;

	private int mFlipInterval = DEFAULT_INTERVAL;
	private boolean mAutoStart = false;

	private boolean mRunning = false;
	private boolean mStarted = false;
	private boolean mVisible = false;
	private boolean mUserPresent = true;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mUserPresent = false;
				updateRunning();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				mUserPresent = true;
				updateRunning();
			}
		}
	};

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Listen for broadcasts related to user-presence
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		getContext().registerReceiver(mReceiver, filter);

		if (mAutoStart) {
			// Automatically start when requested
			startFlipping();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVisible = false;

		getContext().unregisterReceiver(mReceiver);
		updateRunning();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		mVisible = visibility == VISIBLE;
		updateRunning();
	}

	/**
	 * How long to wait before flipping to the next view
	 * 
	 * @param milliseconds
	 *            time in milliseconds
	 */

	public void setFlipInterval(int milliseconds) {
		mFlipInterval = milliseconds;
	}

	/**
	 * Start a timer to cycle through child views
	 */
	public void startFlipping() {
		mStarted = true;
		updateRunning();
	}

	/**
	 * No more flips
	 */
	public void stopFlipping() {
		mStarted = false;
		updateRunning();
	}

	/**
	 * Internal method to start or stop dispatching flip {@link Message} based
	 * on {@link #mRunning} and {@link #mVisible} state.
	 */
	private void updateRunning() {
		boolean running = mVisible && mStarted && mUserPresent;
		if (running != mRunning) {
			if (running) {
				showOnly(mWhichChild);
				Message msg = mHandler.obtainMessage(FLIP_MSG);
				mHandler.sendMessageDelayed(msg, mFlipInterval);
			} else {
				mHandler.removeMessages(FLIP_MSG);
			}
			mRunning = running;
		}
		if (LOGD) {
			Log.d("", "updateRunning() mVisible=" + mVisible + ", mStarted="
					+ mStarted + ", mUserPresent=" + mUserPresent
					+ ", mRunning=" + mRunning);
		}
	}

	/**
	 * Returns true if the child views are flipping.
	 */
	public boolean isFlipping() {
		return mStarted;
	}

	/**
	 * Set if this view automatically calls {@link #startFlipping()} when it
	 * becomes attached to a window.
	 */
	public void setAutoStart(boolean autoStart) {
		mAutoStart = autoStart;
	}

	/**
	 * Returns true if this view automatically calls {@link #startFlipping()}
	 * when it becomes attached to a window.
	 */
	public boolean isAutoStart() {
		return mAutoStart;
	}

	private final int FLIP_MSG = 1;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FLIP_MSG) {
				if (mRunning) {
					showNext();
					msg = obtainMessage(FLIP_MSG);
					sendMessageDelayed(msg, mFlipInterval);
				}
			}
		}
	};

	@Override
	public void recyle() {		
		BitmapUtils.recycleBitmaps(bitmapList);
		for (int i = 0; i < this.getChildCount(); i++) {
			((ImageView)this.getChildAt(i)).setImageBitmap(null);
		}

	}

}
