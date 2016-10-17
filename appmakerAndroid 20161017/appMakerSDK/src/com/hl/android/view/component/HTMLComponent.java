package com.hl.android.view.component;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.HTMLComponentEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

@SuppressLint("NewApi")
public class HTMLComponent extends WebView implements Component {

	private HTMLComponentEntity _entity;
	public HTMLComponent(Context context) {
		super(context);
		getSettings().setJavaScriptEnabled(true);
		getSettings().setPluginState(PluginState.ON);
		getSettings().setPluginsEnabled(true);
		getSettings().setDatabaseEnabled(true); 
		String dir = getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath(); 
		//设置数据库路径 
		getSettings().setDatabasePath(dir); 
		getSettings().setDomStorageEnabled(true); 
		getSettings().setGeolocationEnabled(true); 
		setVerticalScrollbarOverlay(true);  
		setWebChromeClient(new WebChromeClient());
	}

	public HTMLComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
	}

	
	@Override
	public ComponentEntity getEntity() {
		return _entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		_entity = (HTMLComponentEntity) entity;
	}
 
	@Override
	public void load() {
		String path = "";
		if (HLSetting.IsResourceSD){
			path = "file:///" +BookSetting.BOOK_PATH + "/" + _entity.getHtmlFolder() + "/"
					+ _entity.getIndexHtml();
		}else{
			path = "file:///android_asset/" + BookSetting.BOOK_RESOURCE_DIR + _entity.getHtmlFolder() + "/" + _entity.getIndexHtml();
		}
		loadUrl(path);
		//测试用loadUrl("file:///sdcard/start.html");
		//"file:///android_asset/a.html"
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {
		setVisibility(View.VISIBLE);
		bringToFront();
		invalidate();
	}

	@Override
	public void stop() {
		try{

			loadUrl("about:blank");
			clearDisappearingChildren();
			clearCache(false);
			pauseTimers();
			destroy();
			clearFormData();
			clearView();
		}catch(Exception e){
			
		}
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

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
