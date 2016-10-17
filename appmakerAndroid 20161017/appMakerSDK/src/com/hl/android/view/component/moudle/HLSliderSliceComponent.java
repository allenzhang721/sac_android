package com.hl.android.view.component.moudle;

import java.io.InputStream;
import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapManager;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.inter.Component;

public class HLSliderSliceComponent extends LinearLayout implements Component {

	private SubSliderSliceComponent lSingleTouchView;
	private SubSliderSliceComponent rSingleTouchView;
	private ArrayList<Bitmap> lBitmaps;
	private ArrayList<Bitmap> rBitmaps;
	private Bitmap bitmap;
	private boolean hasMoveAuto;
	private float totalDy;
	private Context mContext;
	private boolean isFling;
	private int mLayoutWidth;
	private int mLayoutHeight;
	private ComponentEntity mEntity;

	public HLSliderSliceComponent(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		mEntity = entity;
		setClickable(true);
	}

	// private void loadBitmaps() {
	// lBitmaps = new ArrayList<Bitmap>();
	// rBitmaps = new ArrayList<Bitmap>();
	// ArrayList<String> sourceIDS =
	// ((MoudleComponentEntity)mEntity).getSourceIDList();
	// for (int i = 0; i < sourceIDS.size(); i++) {
	// bitmap= BitmapUtils.getBitMap(sourceIDS.get(i), mContext);
	// lBitmaps.add(BitmapUtils.getBitmap(bitmap, 0, 0, bitmap.getWidth()/2,
	// bitmap.getHeight()));
	// rBitmaps.add(BitmapUtils.getBitmap(bitmap, bitmap.getWidth()/2, 0,
	// bitmap.getWidth()/2, bitmap.getHeight()));
	// BitmapUtils.recycleBitmap(bitmap);
	// bitmap=null;
	// }
	// }
	private void loadBitmaps() {
		lBitmaps = new ArrayList<Bitmap>();
		rBitmaps = new ArrayList<Bitmap>();
		ArrayList<String> sourceIDS = ((MoudleComponentEntity) mEntity)
				.getSourceIDList();
		for (int i = 0; i < sourceIDS.size(); i++) {
			bitmap = BitmapManager.getBitmapFromCache(sourceIDS.get(i));
			if (bitmap == null || bitmap.isRecycled()) {
				bitmap = BitmapUtils.getBitMap(sourceIDS.get(i), mContext);
				BitmapManager.putBitmapCache(sourceIDS.get(i), bitmap);
			}
			Bitmap bitmapL = BitmapManager.getBitmapFromCache(sourceIDS.get(i)
					+ "L");
			if (bitmapL == null) {
				bitmapL = BitmapUtils.getBitmap(bitmap, 0, 0,
						bitmap.getWidth() / 2, bitmap.getHeight());
				BitmapManager.putBitmapCache(sourceIDS.get(i) + "L", bitmapL);
			}
			if (bitmapL != null) {
				lBitmaps.add(bitmapL);
			}
			Bitmap bitmapR = BitmapManager.getBitmapFromCache(sourceIDS.get(i)
					+ "R");
			if (bitmapR == null) {
				bitmapR = BitmapUtils.getBitmap(bitmap, bitmap.getWidth() / 2,
						0, bitmap.getWidth() / 2, bitmap.getHeight());
				BitmapManager.putBitmapCache(sourceIDS.get(i) + "R", bitmapR);
			}
			if (bitmapR != null) {
				rBitmaps.add(bitmapR);
			}
			BitmapUtils.recycleBitmap(bitmap);
			bitmap = null;
		}
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity = entity;
	}

	@Override
	public void load() {
		mLayoutWidth = getLayoutParams().width;
		mLayoutHeight = getLayoutParams().height;
		loadBitmaps();
		lSingleTouchView = new SubSliderSliceComponent(mContext, lBitmaps,
				mLayoutWidth / 2, mLayoutHeight, false);
		rSingleTouchView = new SubSliderSliceComponent(mContext, rBitmaps,
				mLayoutWidth / 2 + mLayoutWidth % 2, mLayoutHeight, true);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				mLayoutWidth / 2, mLayoutHeight);
		LinearLayout.LayoutParams layoutParams4right = new LinearLayout.LayoutParams(
				mLayoutWidth / 2 + mLayoutWidth % 2, mLayoutHeight);
		setOrientation(LinearLayout.HORIZONTAL);
		addView(lSingleTouchView, layoutParams);
		addView(rSingleTouchView, layoutParams4right);
		final GestureDetector detector = new GestureDetector(mContext,
				new OnGestureListener() {

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						return false;
					}

					@Override
					public void onShowPress(MotionEvent e) {
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						if (!hasMoveAuto) {
							totalDy += distanceY;
							float curlPosition = lSingleTouchView.getPosition();
							float currPosition = rSingleTouchView.getPosition();
							if (e2.getX() < getLayoutParams().width / 2.0f) {
								lSingleTouchView.setposition(curlPosition
										- distanceY);
								rSingleTouchView.setposition(currPosition
										+ distanceY);
							} else {
								lSingleTouchView.setposition(curlPosition
										+ distanceY);
								rSingleTouchView.setposition(currPosition
										- distanceY);
							}
						}
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (Math.abs(velocityY) > Math.abs(velocityX)) {
							isFling = true;
							hasMoveAuto = true;
							if (e2.getX() > getLayoutParams().width / 2.0f) {
								if (velocityY >= 0) {
									if (totalDy < 0) {
										lSingleTouchView.MoveToUp(mLayoutHeight
												- Math.abs(totalDy), true);
										rSingleTouchView.MoveToDown(
												mLayoutHeight
														- Math.abs(totalDy),
												true);
									} else {
										lSingleTouchView.MoveToUp(
												Math.abs(totalDy), false);
										rSingleTouchView.MoveToDown(
												Math.abs(totalDy), false);
									}
								} else {
									if (totalDy < 0) {
										lSingleTouchView.MoveToDown(
												Math.abs(totalDy), false);
										rSingleTouchView.MoveToUp(
												Math.abs(totalDy), false);
									} else {
										lSingleTouchView.MoveToDown(
												mLayoutHeight
														- Math.abs(totalDy),
												true);
										rSingleTouchView.MoveToUp(mLayoutHeight
												- Math.abs(totalDy), true);
									}
								}
							} else {
								if (velocityY >= 0) {
									if (totalDy < 0) {
										rSingleTouchView.MoveToUp(mLayoutHeight
												- Math.abs(totalDy), true);
										lSingleTouchView.MoveToDown(
												mLayoutHeight
														- Math.abs(totalDy),
												true);
									} else {
										rSingleTouchView.MoveToUp(
												Math.abs(totalDy), false);
										lSingleTouchView.MoveToDown(
												Math.abs(totalDy), false);
									}
								} else {
									if (totalDy < 0) {
										rSingleTouchView.MoveToDown(
												Math.abs(totalDy), false);
										lSingleTouchView.MoveToUp(
												Math.abs(totalDy), false);
									} else {
										rSingleTouchView.MoveToDown(
												mLayoutHeight
														- Math.abs(totalDy),
												true);
										lSingleTouchView.MoveToUp(mLayoutHeight
												- Math.abs(totalDy), true);
									}
								}
							}
						}
						return false;
					}

					@Override
					public boolean onDown(MotionEvent e) {
						if (!lSingleTouchView.isMoveToUp
								&& !lSingleTouchView.isMoveToDown) {
							hasMoveAuto = false;
							totalDy = 0;
							isFling = false;
						}
						return false;
					}
				});
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				detector.onTouchEvent(event);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!isFling && !hasMoveAuto) {
						hasMoveAuto = true;
						if (event.getX() > getLayoutParams().width / 2.0f) {
							if (Math.abs(totalDy) > getLayoutParams().height / 2.0f) {
								if (totalDy <= 0) {
									lSingleTouchView.MoveToUp(mLayoutHeight
											- Math.abs(totalDy), true);
									rSingleTouchView.MoveToDown(mLayoutHeight
											- Math.abs(totalDy), true);
								} else {
									lSingleTouchView.MoveToDown(mLayoutHeight
											- Math.abs(totalDy), true);
									rSingleTouchView.MoveToUp(mLayoutHeight
											- Math.abs(totalDy), true);
								}
							} else {
								if (totalDy <= 0) {
									lSingleTouchView.MoveToDown(
											Math.abs(totalDy), false);
									rSingleTouchView.MoveToUp(
											Math.abs(totalDy), false);
								} else {
									lSingleTouchView.MoveToUp(
											Math.abs(totalDy), false);
									rSingleTouchView.MoveToDown(
											Math.abs(totalDy), false);
								}
							}
						} else {
							if (Math.abs(totalDy) > getLayoutParams().height / 2.0f) {
								if (totalDy <= 0) {
									rSingleTouchView.MoveToUp(mLayoutHeight
											- Math.abs(totalDy), true);
									lSingleTouchView.MoveToDown(mLayoutHeight
											- Math.abs(totalDy), true);
								} else {
									rSingleTouchView.MoveToDown(mLayoutHeight
											- Math.abs(totalDy), true);
									lSingleTouchView.MoveToUp(mLayoutHeight
											- Math.abs(totalDy), true);
								}
							} else {
								if (totalDy <= 0) {
									rSingleTouchView.MoveToDown(
											Math.abs(totalDy), false);
									lSingleTouchView.MoveToUp(
											Math.abs(totalDy), false);
								} else {
									rSingleTouchView.MoveToUp(
											Math.abs(totalDy), false);
									lSingleTouchView.MoveToDown(
											Math.abs(totalDy), false);
								}
							}
						}
					}
				}
				return false;
			}
		});
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
		for (int i = 0; i < lSingleTouchView.rects.size(); i++) {
			BitmapUtils
					.recycleBitmap(lSingleTouchView.rects.get(i).mDrawBitmap);
		}
		for (int i = 0; i < rSingleTouchView.rects.size(); i++) {
			BitmapUtils
					.recycleBitmap(rSingleTouchView.rects.get(i).mDrawBitmap);
		}
		BitmapUtils.recycleBitmaps(lBitmaps);
		BitmapUtils.recycleBitmaps(rBitmaps);
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
	}

	@Override
	public void pause() {
	}
}
