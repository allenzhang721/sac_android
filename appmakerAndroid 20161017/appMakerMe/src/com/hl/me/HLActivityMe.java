package com.hl.me;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.android.HLActivity;
import com.hl.android.common.BookSetting;

/**
 * 基础版本 包含的功能包括 1试用logo添加 2多盟的广告平台
 * 
 * @author zhaoq
 * 
 */
public class HLActivityMe extends HLActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected View getAdView() {
		return null;
	}

	/**
	 * 设置版本试用的logo
	 */
	private void setTryVersionLogo() {
		if (BookSetting.IS_TRY) {
			TextView tryTextView = new TextView(this);
			tryTextView.setText("appMaker试用版");
			RelativeLayout.LayoutParams layoutParamsTry = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParamsTry.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			coverLayout.addView(tryTextView, layoutParamsTry);
			tryTextView.setTextColor(Color.BLACK);
			tryTextView.setTextSize(30f);
			tryTextView.setGravity(Gravity.CENTER);
			tryTextView.setBackgroundColor(Color.BLUE);
			tryTextView.bringToFront();
		}
	}
}
