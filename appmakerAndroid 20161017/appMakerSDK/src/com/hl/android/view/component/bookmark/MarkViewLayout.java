package com.hl.android.view.component.bookmark;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hl.android.HLLayoutActivity;
import com.hl.android.R;
import com.hl.android.common.BookSetting;

@SuppressLint("ViewConstructor")
public class MarkViewLayout extends RelativeLayout {
	private HLLayoutActivity mActivity;
	private MarkItemsAdapter markItemsAdapter;
	private static final int MARK_VIEW_ID_TOP = 5000000;
	private static final int MARK_VIEW_ID_BOTTOM = 5000001;
	
	public MarkViewLayout(HLLayoutActivity activity) {
		super(activity);
		mActivity = activity;
		markItemsAdapter = new MarkItemsAdapter(activity);		
		// 顶部标题及关闭按钮区域
		RelativeLayout markTop = new RelativeLayout(mActivity);
		markTop.setPadding(0, 0, 0, -5);
//		markTop.setId(ID_MARK_TOP);

		ImageButton titleBtn = new ImageButton(mActivity);
		titleBtn.setBackgroundResource(R.drawable.mark_title);
		RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 52);
		
		ImageButton closeBtn = new ImageButton(mActivity);
		closeBtn.setBackgroundResource(R.drawable.mark_close_btn);
		RelativeLayout.LayoutParams closeLp = new RelativeLayout.LayoutParams(
				70, 52);
		closeLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TranslateAnimation animation = new TranslateAnimation(0, 0, 0,
						BookSetting.SCREEN_HEIGHT);
				animation.setDuration(300);
				animation.setRepeatCount(0);
				setAnimation(animation);
				animation.startNow();
				setVisibility(View.GONE);
			}
		});

		RelativeLayout.LayoutParams markTopParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 47);
		markTopParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		markTop.setId(MARK_VIEW_ID_TOP);
		markTop.addView(titleBtn, titleLp);
		markTop.addView(closeBtn, closeLp);
		

		// 底部添加、编辑按钮区域
		LinearLayout markBottom = new LinearLayout(mActivity);
		markBottom.setPadding(0, -6, 0, 0);
		markBottom.setOrientation(LinearLayout.HORIZONTAL);

		markBottom.setId(MARK_VIEW_ID_BOTTOM);
		ImageButton addBtn = new ImageButton(mActivity);
		addBtn.setBackgroundResource(R.drawable.mark_add_btn);
		addBtn.setScaleType(ScaleType.FIT_XY);
		LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(130, 37);
		// addLp.weight = 1;
		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BookMarkManager.addCurPage(mActivity);
			}
		});
		markBottom.addView(addBtn, addLp);

		ImageButton editBtn = new ImageButton(mActivity);
		editBtn.setBackgroundResource(R.drawable.mark_edit_btn);
		editBtn.setScaleType(ScaleType.FIT_XY);
		LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(130,
				37);
		// editLp.weight = 1;
		editBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BookMarkView.showDelete = !BookMarkView.showDelete;
				markItemsAdapter.notifyDataSetChanged();
			}
		});
		markBottom.addView(editBtn, editLp);

		RelativeLayout.LayoutParams markBottomParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		markBottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		

		// 中间书签列表视图区域
		ListView markItems = new ListView(mActivity);
		markItems.setBackgroundColor(0xFFEEEEEE);
		markItems.setCacheColorHint(Color.TRANSPARENT);
		markItems.setSelector(android.R.color.transparent);
		RelativeLayout.LayoutParams markItemsParams = new RelativeLayout.LayoutParams(
				259, LayoutParams.MATCH_PARENT);
		markItemsParams.addRule(RelativeLayout.BELOW, markTop.getId());
		markItemsParams.addRule(RelativeLayout.ABOVE, markBottom.getId());
		markItemsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		addView(markTop, markTopParams);
		addView(markBottom, markBottomParams);
		addView(markItems, markItemsParams);
		markItems.setAdapter(markItemsAdapter);
	}
	
	public void refresh(){
		markItemsAdapter.notifyDataSetChanged();
	}

}
