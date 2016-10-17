package com.hl.android.view.component.moudle.enbedpage;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hl.android.controller.BookController;
import com.hl.android.view.ViewPage;

public class ViewPageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> mpageIDS;

	public ViewPageAdapter(Context c, ArrayList<String> pageIDS) {
		mContext = c;
		mpageIDS = pageIDS;
	}

	@Override
	public int getCount() {
		return mpageIDS.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewPage pageView;
		if (convertView == null) {
			pageView = new ViewPage(mContext, null, null);
			pageView.load(BookController.getInstance().getPageEntityByID(
					mpageIDS.get(position)));
		} else {
			pageView = (ViewPage) convertView;
		}
		return pageView;
	}

}
