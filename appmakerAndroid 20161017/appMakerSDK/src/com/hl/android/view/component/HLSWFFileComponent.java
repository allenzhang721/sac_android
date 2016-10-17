package com.hl.android.view.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.SWFFileEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

@SuppressLint({ "NewApi", "ViewConstructor" })
public class HLSWFFileComponent extends LinearLayout implements Component,ComponentListener{

	private SWFFileEntity entity;
	private Context mContext;
	private WebView mWeb;
	private String filePath = "";
	private String strFile;
	private boolean hasStopPlay=false;
	private Handler handler;
	private static int PLAY=0x10010;
	private static int PAUSE=0x10011;
	private static int HAS_PLAYEND=0x10012;
	private OnComponentCallbackListener onComponentCallbackListener;
	public HLSWFFileComponent(Context context, ComponentEntity entity) {
		super(context);
		mContext = context;
		this.entity = (SWFFileEntity) entity;
	}

	@Override
	public ComponentEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (SWFFileEntity) entity;
	}
	@SuppressLint({ "HandlerLeak", "SetJavaScriptEnabled" })
	@Override
	public void load() {
		try {
			InputStream is;
			is = mContext.getAssets().open("index.html");
			strFile = readTextFile(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (HLSetting.IsResourceSD) {
			filePath = "file://" + BookSetting.BOOK_PATH + entity.localSourceId;
		} else {
			filePath = "file:///android_asset/book/" + entity.localSourceId;
		}
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0x10010:
					mWeb.loadUrl("javascript:Play()");
					break;
				case 0x10011:
					mWeb.loadUrl("javascript:Pause()");
					break;
				case 0x10012:
					onComponentCallbackListener.setPlayComplete();
					break;
				default:
					break;
				}
			}
		};
		if (mWeb == null) {
			mWeb = new WebView(mContext);
			mWeb.getSettings().setJavaScriptEnabled(true);
			mWeb.getSettings().setPluginState(PluginState.ON);
			mWeb.getSettings().setPluginsEnabled(true);
			mWeb.getSettings().setAllowFileAccess(true);
			mWeb.addJavascriptInterface(new CallJava(), "CallJava");
			mWeb.setWebChromeClient(new WebChromeClient());
			LinearLayout.LayoutParams videoLayoutLP = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			videoLayoutLP.gravity = Gravity.CENTER;
			addView(mWeb, videoLayoutLP);
			mWeb.setBackgroundColor(Color.TRANSPARENT);
		} else {
			mWeb.resumeTimers();
			mWeb.refreshDrawableState();

		}
		mWeb.loadDataWithBaseURL(null, strFile.replace("flash.swf", filePath),
				"text/html", "UTF-8", null);
		if (entity.isHideAtBegining) {
			this.setVisibility(View.INVISIBLE);
		}
		
		
	}

	private String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toString();
	}

	public final class CallJava {
		public void consoleFlashProgress(float progressSize) {
			if (progressSize >= 100) {
				if(entity.isLoop){
					handler.sendEmptyMessage(PLAY);
					hasStopPlay=false;
				}else{
					handler.sendEmptyMessage(PAUSE);
					handler.sendEmptyMessage(HAS_PLAYEND);
				}
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
			}
		}

		public void FlashLoaded() {
			if(hasStopPlay){
				handler.sendEmptyMessage(PAUSE);
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
			}else{
				if(entity.isPlayVideoOrAudioAtBegining){
					handler.sendEmptyMessage(PLAY);
					hasStopPlay=false;
				}else{
					handler.sendEmptyMessage(PAUSE);
					hasStopPlay=true;
				}
			}
		}
	}


	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void play() {
		handler.sendEmptyMessage(PLAY);
		hasStopPlay=false;
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
	}


	@Override
	public void stop() {
		mWeb.loadDataWithBaseURL(null, strFile.replace("flash.swf", filePath),
				"text/html", "UTF-8", null);
		hasStopPlay=true;
	}

	@Override
	public void hide() {
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		handler.sendEmptyMessage(PLAY);
	}

	@Override
	public void pause() {
		handler.sendEmptyMessage(PAUSE);
	}
	

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {
		onComponentCallbackListener = callbackListner;
	}

	@Override
	public void callBackListener() {
		onComponentCallbackListener.setPlayComplete();
	}

}
