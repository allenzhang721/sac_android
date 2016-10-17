package com.hl.android.view.component.moudle.enbedpage;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.hl.android.view.ViewPage;

public class AdapterViewPage extends AdapterView<ViewPageAdapter> {
	private ViewPage currentViewPage;

	public AdapterViewPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ViewPageAdapter getAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdapter(ViewPageAdapter adapter) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getSelectedView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelection(int position) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (currentViewPage != null) {
			this.currentViewPage.layout(0, 0,
					this.currentViewPage.getLayoutParams().width,
					this.currentViewPage.getLayoutParams().height);
			currentViewPage.startPlay();
		}
	}

}
