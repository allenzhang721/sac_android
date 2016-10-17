package com.hl.android.view.pageflip;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.hl.android.controller.BookController;

public abstract class AbstractPageFlipView extends View {

	//
	protected Bitmap mCurPageBitmap;
	protected Bitmap mNextPageBitmap = null;
	ViewGroup bookLayout;
	boolean _preload = false;

	public void setPreLoad(boolean preload) {
		_preload = preload;
	}

	public AbstractPageFlipView(Context context) {
		super(context);
	}

	// ImageView tmpnextpageimg;

	/**
	 * 翻页操作
	 * 
	 * @param currentindex
	 *            -1 向后翻 1 向前翻
	 */
	public abstract void play(int pageIndex, int newPageindex,
			ActionOnEnd action);

	public void show() {
		// Drawable db = new BitmapDrawable(this.mCurPageBitmap);
		// this.setBackgroundDrawable(db);
		this.setVisibility(View.VISIBLE);
		this.bringToFront();
		// 保证公共页在翻页视图的上面

		BookController.getInstance().hlActivity.commonLayout.bringToFront();
	}

	/**
	 * 设置当前页的bitmap
	 * 
	 * @param curBitmap
	 */
	public void setBitmap(Bitmap curBitmap) {
		this.mCurPageBitmap = curBitmap;
	}

	/**
	 * 设置下一页的bitmap
	 * 
	 * @param newBitmap
	 */
	public void setNewBitmap(Bitmap newBitmap) {
		this.mNextPageBitmap = newBitmap;
	}

	public void setViewPage(ViewGroup viewPage) {
		this.bookLayout = viewPage;
	}

	public abstract void hide();

	public void recycleBitmap() {
		try {
			hide();
			if (mCurPageBitmap != null && !mCurPageBitmap.isRecycled()) {
				mCurPageBitmap.recycle();
				mCurPageBitmap = null;
			}
			if (mNextPageBitmap != null && !mNextPageBitmap.isRecycled()) {
				mNextPageBitmap.recycle();
				mNextPageBitmap = null;
			}
		} catch (Exception e) {
		}
	}
}
