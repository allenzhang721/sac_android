package com.artifex.mupdf;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * pdf测试
 * 最重要测试通过将pdf代码抽取出来的view
 * @author zhaoq
 *
 */
public class MyActivity extends Activity implements View.OnClickListener{
	private MuPDFCore core;
	private ReaderView mDocView;
	private enum LinkState {DEFAULT, HIGHLIGHT, INHIBIT};
	private final int    TAP_PAGE_MARGIN = 5;
	private int pageIndex = 0;
	private LinkState    mLinkState = LinkState.DEFAULT;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化画布
		LinearLayout lay = new LinearLayout(this);
		lay.setBackgroundColor(Color.rgb(220, 220, 220));
		
		setContentView(lay);
		
		//pdf 路径
		String pdfFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.pdf";
		//初始化pdfcore
		try {
			core = new MuPDFCore(pdfFile);
		} catch (Exception e) {
		}
		if(core == null){
			Toast.makeText(this, "create pdf core error", Toast.LENGTH_LONG).show();
			return;
		}
		//创建视图
		mDocView = new ReaderView(this) {
			private boolean showButtonsDisabled;

			public boolean onSingleTapUp(MotionEvent e) {
				if (e.getX() < super.getWidth()/TAP_PAGE_MARGIN) {
					super.moveToPrevious();
				} else if (e.getX() > super.getWidth()*(TAP_PAGE_MARGIN-1)/TAP_PAGE_MARGIN) {
					super.moveToNext();
				} else if (!showButtonsDisabled) {
					int linkPage = -1;
					if (mLinkState != LinkState.INHIBIT) {
						MuPDFPageView pageView = (MuPDFPageView) mDocView.getDisplayedView();
						if (pageView != null) {
// XXX							linkPage = pageView.hitLinkPage(e.getX(), e.getY());
						}
					}

					if (linkPage != -1) {
						mDocView.setDisplayedViewIndex(linkPage);
					} else {
//						if (!mButtonsVisible) {
//							showButtons();
//						} else {
//							hideButtons();
//						}
					}
				}
				return super.onSingleTapUp(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//				if (!showButtonsDisabled)
//					hideButtons();

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
				if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber == i)
					((PageView)v).setSearchBoxes(SearchTaskResult.get().searchBoxes);
				else
					((PageView)v).setSearchBoxes(null);

				((PageView)v).setLinkHighlighting(mLinkState == LinkState.HIGHLIGHT);
			}

			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber != i) {
					SearchTaskResult.set(null);
					mDocView.resetupChildren();
				}
			}

			protected void onSettle(View v) {
				// When the layout has settled ask the page to render
				// in HQ
				((PageView)v).addHq();
			}

			protected void onUnsettle(View v) {
				// When something changes making the previous settled view
				// no longer appropriate, tell the page to remove HQ
				((PageView)v).removeHq();
			}
		};
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));
		Button btnNext = new Button(this);
		btnNext.setText("下一页");
		btnNext.setOnClickListener(this);
		
		lay.setOrientation(LinearLayout.VERTICAL);
		lay.addView(btnNext);
		lay.addView(mDocView);
	}
	@Override
	public void onClick(View v) {
		pageIndex++;
		mDocView.setDisplayedViewIndex(pageIndex);
	}
}
