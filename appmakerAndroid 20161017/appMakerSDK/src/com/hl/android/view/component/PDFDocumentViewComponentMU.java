package com.hl.android.view.component;

import java.io.File;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.MuPDFPageView;
import com.artifex.mupdf.PageView;
import com.artifex.mupdf.ReaderView;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.PDFComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.controller.PdfController;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

public class PDFDocumentViewComponentMU extends LinearLayout implements Component {
	public ComponentEntity entity = null;
	private MuPDFCore core;
	private ReaderView mDocView;

	private enum LinkState {
		DEFAULT, HIGHLIGHT, INHIBIT
	};

	private final int TAP_PAGE_MARGIN = 5;
	private int pageIndex = 0;
	private LinkState mLinkState = LinkState.DEFAULT;
	// pdf 路径
	String pdfFile = "";

	private Context mContext;

	public PDFDocumentViewComponentMU(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PDFDocumentViewComponentMU(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;
		mContext = context;
	}

	@Override
	public ComponentEntity getEntity() {
		// TODO Auto-generated method stub
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;

	}

	@Override
	public void load() {
		String storageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(storageState)) {
			File sdPath = Environment.getExternalStorageDirectory();
			FileUtils.getInstance().copyFileToSDCard(this.getContext(),
					((PDFComponentEntity) entity).getPdfSourceID());
			PdfController.getInstance().openFile(
					sdPath.getPath() + "/"
							+ ((PDFComponentEntity) entity).getPdfSourceID());
			core = PdfController.getInstance().muPDFCore;
		}
		pageIndex = Integer.parseInt(((PDFComponentEntity) entity).getPdfPageIndex());

		// 创建视图
		mDocView = new ReaderView(mContext) {
			private boolean showButtonsDisabled;

			public boolean onSingleTapUp(MotionEvent e) {
				if (e.getX() < super.getWidth() / TAP_PAGE_MARGIN) {
					super.moveToPrevious();
				} else if (e.getX() > super.getWidth() * (TAP_PAGE_MARGIN - 1)
						/ TAP_PAGE_MARGIN) {
					super.moveToNext();
				} else if (!showButtonsDisabled) {
					int linkPage = -1;
					if (mLinkState != LinkState.INHIBIT) {
						MuPDFPageView pageView = (MuPDFPageView) mDocView
								.getDisplayedView();
						if (pageView != null) {
							// XXX linkPage = pageView.hitLinkPage(e.getX(),
							// e.getY());
						}
					}

					if (linkPage != -1) {
						mDocView.setDisplayedViewIndex(linkPage);
					} else {
						// if (!mButtonsVisible) {
						// showButtons();
						// } else {
						// hideButtons();
						// }
					}
				}
				return super.onSingleTapUp(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// if (!showButtonsDisabled)
				// hideButtons();

				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			public boolean onScaleBegin(ScaleGestureDetector d) {
				// Disabled showing the buttons until next touch.
				// Not sure why this is needed, but without it
				// pinch zoom can make the buttons appear
				showButtonsDisabled = true;
				return super.onScaleBegin(d);
			}

			public boolean onTouchEvent(MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
					showButtonsDisabled = false;

				return super.onTouchEvent(event);
			}

			protected void onChildSetup(int i, View v) {
				// if (SearchTaskResult.get() != null &&
				// SearchTaskResult.get().pageNumber == i)
				// ((PageView)v).setSearchBoxes(SearchTaskResult.get().searchBoxes);
				// else
				// ((PageView)v).setSearchBoxes(null);
				//
				// ((PageView)v).setLinkHighlighting(mLinkState ==
				// LinkState.HIGHLIGHT);
			}

			protected void onMoveToChild(int i) {
				// if (core == null)
				// return;
				// if (SearchTaskResult.get() != null &&
				// SearchTaskResult.get().pageNumber != i) {
				// SearchTaskResult.set(null);
				// mDocView.resetupChildren();
				// }
			}

			protected void onSettle(View v) {
				// When the layout has settled ask the page to render
				// in HQ
				((PageView) v).addHq();
			}

			protected void onUnsettle(View v) {
				// When something changes making the previous settled view
				// no longer appropriate, tell the page to remove HQ
				((PageView) v).removeHq();
			}
		};
		mDocView.setAdapter(new MuPDFPageAdapter(mContext, core));
		mDocView.setDisplayedViewIndex(pageIndex-1);
		addView(mDocView);
	}

	// /**
	// * remove init imageview
	// */
	// public void removeImageView(){
	// if (imageView != null){
	// imageView.setVisibility(View.GONE);
	// this.removeView(this.imageView);
	// }
	//
	// }
	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestLayout() {
		super.forceLayout();
		super.requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println("sfsfdsfdsfdsf");
	}

	@Override
	public void play() {

	}

//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		// super.onLayout(changed, left, top, right, bottom);
//		if (null != mDocView) {
//			mDocView.layout(0, 0, this.getLayoutParams().width,
//					this.getLayoutParams().height);
//		}
//	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	/*************************** 下面都是属性动画使用相关代码 *******************************/
	public ViewRecord initRecord;

	@SuppressLint("NewApi")
	public ViewRecord getCurrentRecord() {
		ViewRecord curRecord = new ViewRecord();
		curRecord.mHeight = getLayoutParams().width;
		curRecord.mWidth = getLayoutParams().height;

		curRecord.mX = getX();
		curRecord.mY = getY();
		curRecord.mRotation = getRotation();
//		curRecord.mAlpha = getAlpha();
		return curRecord;
	}
}
