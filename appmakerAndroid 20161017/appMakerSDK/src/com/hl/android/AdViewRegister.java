package com.hl.android;

import android.app.Activity;
import android.view.View;

public interface AdViewRegister {
	/**
	 * 返回广告视图
	 * @param activity
	 * @return
	 */
	View getView(Activity activity);
	/**
	 * 回调函数，广告被销毁的代码写在这里
	 * @param activity
	 */
	void recyle(Activity activity);
}
