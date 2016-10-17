package com.hl.android.view.component.helper;

import android.os.AsyncTask;
import android.view.View;

import com.hl.android.view.component.inter.Component;

public class ComponentLoadTask extends AsyncTask<Component, Integer, Component> {
	@Override
	protected Component doInBackground(Component... params) {
		params[0].load();
		return params[0];
	}

	@Override
	protected void onPostExecute(Component component) {
		// doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
		((View)component).postInvalidate();
		super.onPostExecute(component);
	}

}
