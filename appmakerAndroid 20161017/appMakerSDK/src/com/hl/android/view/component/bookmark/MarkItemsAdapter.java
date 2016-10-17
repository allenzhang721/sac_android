package com.hl.android.view.component.bookmark;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hl.android.HLLayoutActivity;

public class MarkItemsAdapter extends BaseAdapter {
	HLLayoutActivity mActivity;
	boolean showDelete;// 是否显示书签删除按钮，第一次点编辑时显示，再次点编辑时隐藏
	
	public MarkItemsAdapter(HLLayoutActivity activity) {
		mActivity = activity;
	}

	@Override
	public int getCount() {
		return BookMarkManager.getMarkList(mActivity).size();
	}

	@Override
	public Object getItem(int position) {
		return BookMarkManager.getMarkList(mActivity).get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new BookMarkView(mActivity,position);
		}else{
			((BookMarkView)(convertView)).setImageBitmap(position);
		}
		return convertView;
	}
}
