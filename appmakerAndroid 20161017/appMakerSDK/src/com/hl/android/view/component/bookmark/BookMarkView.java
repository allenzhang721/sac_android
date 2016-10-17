package com.hl.android.view.component.bookmark;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLLayoutActivity;
import com.hl.android.R;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.controller.BookController;

@SuppressLint("ViewConstructor")
public class BookMarkView extends RelativeLayout {
	public BookMarkView(HLLayoutActivity activity, int position) {
		super(activity);
		this.mActivity = activity;
		this.mPosition = position;
		initViews();
		setImageBitmap(position);
	}
	private static final int ID_MARK_IMAGE = 100903;
	private static final int ID_MARK_DELETEIMAGE = 100904;
	private static final int ID_MARK_TEXT = 100905;
	public static boolean showDelete = false;
	
	private int mPosition;
	private HLLayoutActivity mActivity;
	private ImageView img;
	private TextView text;
	private ImageView deleteView;


	private Bitmap getImageBitmap() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
		if (bitmapDrawable != null) {
			return bitmapDrawable.getBitmap();
		} else {
			return null;
		}
	}

	private void setShowDelete() {
		if (showDelete) {
			deleteView.setVisibility(View.VISIBLE);
		} else {
			deleteView.setVisibility(View.INVISIBLE);
		}
	}

	void setImageBitmap(int position) {
		PageEntity page = BookController.getInstance().getPageEntityByID(
				BookMarkManager.getMarkList(mActivity).get(position));
		Bitmap oldBitmap = getImageBitmap();
		Bitmap bitmap = BookController.getInstance().getSmallSnapShotCashImage(
				page);
		img.setImageBitmap(bitmap);
		text.setText(page.getTitle());
		mPosition = position;
		setShowDelete();
		if (oldBitmap != null && !oldBitmap.isRecycled()) {
			oldBitmap.recycle();
		}
	}

	private void initViews() {
		// 缩略图
		img = new ImageView(mActivity);
		img.setId(ID_MARK_IMAGE);
		RelativeLayout.LayoutParams imgLp = new RelativeLayout.LayoutParams(
				100, 80);
		img.setPadding(10, 0, 0, 0);
		imgLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		addView(img, imgLp);
		// 书签删除按钮
		deleteView = new ImageView(mActivity);
		deleteView.setTag(true);
		deleteView.setImageResource(R.drawable.mark_delete_btn);
		deleteView.setId(ID_MARK_DELETEIMAGE);
		RelativeLayout.LayoutParams checkLp = new RelativeLayout.LayoutParams(
				45, 45);
		checkLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		checkLp.addRule(RelativeLayout.CENTER_VERTICAL);
		deleteView.setPadding(0, 0, 15, 0);
		addView(deleteView, checkLp);
		//
		// 书签描述信息
		text = new TextView(mActivity);
		text.setClickable(true);
		text.setTextColor(Color.GRAY);
		text.setLines(3);
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setId(ID_MARK_TEXT);
		RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		text.setPadding(15, 0, 15, 0);
		titleLp.addRule(RelativeLayout.RIGHT_OF, img.getId());
		titleLp.addRule(RelativeLayout.LEFT_OF, deleteView.getId());
		titleLp.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(text, titleLp);
		deleteView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BookMarkManager.deleteMark(mActivity, mPosition);
				mActivity.refreshMark();
			}
		});
		setShowDelete();
		img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!showDelete) {
					BookController.getInstance().playPageById(
							BookMarkManager.getMarkList(mActivity).get(mPosition));
				}
			}
		});
	}
}
