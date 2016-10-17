package com.hl.android.view.component;

import com.hl.android.view.component.listener.OnComponentCallbackListener;

public interface ComponentListener {
	public void registerCallbackListener(OnComponentCallbackListener callbackListner);
	public void callBackListener();
}
