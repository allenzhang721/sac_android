package com.hl.android.view.component;

import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

@SuppressLint("SetJavaScriptEnabled")
public class WebComponent extends WebView implements Component {

	private ComponentEntity entity;
	ArrayList<AnimationEntity> anims;
	public boolean loadingFinished = true;
	boolean redirect = false;
	boolean isShow = true;
	LoadView loadView; 
	Context mContext;
	private boolean isChangeUrl=false;
	ViewGroup.LayoutParams alp;
	
	public WebComponent(Context context) {
		super(context);
		mContext=context;
	}

	@SuppressWarnings("deprecation")
	public WebComponent(Context context, ComponentEntity entity) {
		super(context);
		this.mContext=context;
		this.entity = entity;
		loadView = new LoadView(context);
		alp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		addView(loadView,alp);
		
		//if(entity.isHideAtBegining)this.setVisibility(View.INVISIBLE);
		this.getSettings().setJavaScriptEnabled(true);
		WebChromeClient wcc = new WebChromeClient();
		this.setWebChromeClient(wcc);
		this.getSettings().setBuiltInZoomControls(true);
		this.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		isShow = !entity.isHideAtBegining;
		//this.getSettings().setJavaScriptEnabled(true); 
		setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (!loadingFinished) {
					redirect = true;
				}
				loadingFinished = false;
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				loadingFinished = false;
				if(isChangeUrl){
					addView(loadView,alp);
				}
				//hide();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if(isChangeUrl){
					removeView(loadView);
					isChangeUrl=false;
				}
				if (!redirect) {
					loadingFinished = true;
				}

				if (loadingFinished && !redirect) {
					// HIDE LOADING IT HAS FINISHED
					if(isShow)show();
				} else {
					redirect = false;
				}

			}
		});
	}

	@Override
	public ComponentEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if ((keyCode == KeyEvent.KEYCODE_BACK) && this.canGoBack()
				&& event.getAction() == KeyEvent.ACTION_UP) {
			this.goBack();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void load() {
		//this.loadUrl(this.entity.htmlUrl);
	}

	@Override
	public void load(InputStream is) {
		this.loadUrl(this.entity.htmlUrl);

	}

	public void play() {
		this.loadUrl(this.entity.htmlUrl);
	}
 
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		isShow = false;
		this.setVisibility(View.GONE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		removeView(loadView);
		isShow = true;
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (canGoBack()) {
				goBack();
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	public void changeUrl(String newUrl) {
		isChangeUrl=true;
		this.loadUrl(newUrl);
	}
	/***************************下面都是属性动画使用相关代码*******************************/
	public ViewRecord initRecord;
	@SuppressLint("NewApi")
	public ViewRecord getCurrentRecord(){
		ViewRecord curRecord = new ViewRecord();
		curRecord.mHeight = getLayoutParams().width;
		curRecord.mWidth = getLayoutParams().height;
		
		curRecord.mX = getX();
		curRecord.mY = getY();
		curRecord.mRotation = getRotation();
//		curRecord.mAlpha = getAlpha();
		return curRecord;
	}
}
