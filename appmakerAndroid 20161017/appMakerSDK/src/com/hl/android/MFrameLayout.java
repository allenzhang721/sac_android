package com.hl.android;

import android.content.Context;
import android.widget.FrameLayout;

public class MFrameLayout extends FrameLayout {

	private HLLayoutActivity mContext;

	public MFrameLayout(Context context) {
		super(context);
		mContext=(HLLayoutActivity) context;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		((HLLayoutActivity)mContext).updateCoverPosition();
	}
}
