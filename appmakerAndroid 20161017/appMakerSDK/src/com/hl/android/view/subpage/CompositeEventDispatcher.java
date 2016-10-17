package com.hl.android.view.subpage;

import java.util.ArrayList;

import android.view.GestureDetector;

import com.hl.android.view.component.inter.Component;

public class CompositeEventDispatcher {

	private static CompositeEventDispatcher eventDispatcher;
	private ArrayList<Component> componentList;
	public GestureDetector mGestureDetector = null;

	public CompositeEventDispatcher() {

	}

	public static CompositeEventDispatcher getInstance() {
		if (null == eventDispatcher) {
			eventDispatcher = new CompositeEventDispatcher();
		}
		return eventDispatcher;
	}

	public void init() {
		if (null != componentList) {
			this.componentList.clear();
		}
	}

	public void init(GestureDetector gestureDetector) {
		mGestureDetector = gestureDetector;
	}
}
