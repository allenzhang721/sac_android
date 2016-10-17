package com.hl.android.view.component;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RelativeLayout;

import com.hl.android.HLActivity;
import com.hl.android.R;
import com.hl.android.VideoActivity;
import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;
import com.hl.android.view.component.moudle.HLMediaController;
import com.hl.callback.Action;

/**
 * 视频播放组件
 * 
 * @author webcat
 * 
 */
public class VideoComponent extends RelativeLayout implements Component,
		OnCompletionListener, SurfaceHolder.Callback, ComponentListener,
		ComponentPost, MediaPlayerControl, OnPreparedListener {
	public VideoComponentEntity entity;
	public MediaPlayer mediaPlayer;
	private Context context;
	private boolean isStopped = false;
	private boolean isHide = true;
	private boolean isPlaying = false;
	private boolean isPause = false;
	ArrayList<AnimationEntity> anims;
	private OnComponentCallbackListener onComponentCallbackListener;
	private HLMediaController controllerWindow;
	private HLActivity activity;
	public static int CONTROLERHEIGHT = 50;
	public static int CONTROLERWIDTH = 200;
	private SurfaceView surfaceView;
	private RelativeLayout coverLayout;
	//标记是否在surfaceView创建完成之前执行了播放动作
	private boolean hasRequestPlayBeforeSurfaceViewCreated=false;
	private boolean surfaceViewHasCreated=false;
	private boolean isResume=false;
	
	public VideoComponent(Context context) {
		super(context);
		this.context = context;
		activity = (HLActivity) context;
		if(entity.isHideAtBegining){
			setVisibility(View.GONE);
		}
	}

	public VideoComponent(Context context, ComponentEntity entity) {
		super(context);
		this.context = context;
		this.entity = (VideoComponentEntity) entity;
		activity = (HLActivity) context;
		if(HLSetting.isNewActivityForVideo)return;
	}

	public VideoComponent(SurfaceView surfaceView, String xmlID, Context context) {
		super(context);
	}

	@Override
	public void pause() {
		if(HLSetting.isNewActivityForVideo)return;
		Log.d("hl", "pause");
		if (null != mediaPlayer && this.mediaPlayer.isPlaying()) {
			if(controlenable){
				mediaPlayer.pause();
				isPause = true;
				isPlaying = false;
				if (controllerWindow != null && controllerWindow.isShowing()) {
					controllerWindow.updatePausePlay();
				}
			}
		}
		int x = this.entity.x;
		int y = this.entity.y;
		int w = this.getLayoutParams().width;
		int h = this.getLayoutParams().height;
		activity.setVideoCover(x, y, w, h);
	}

	public void continuePlay() {
		if (isPause) {
			initPlaying();
			mediaPlayer.start();
			BookController.getInstance().runBehavior(entity,
					Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
			if (controllerWindow != null && controllerWindow.isShowing()) {
				controllerWindow.updatePausePlay();
			}
		}
	}
	public static String getMimeType(String url) {
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);

	    if (extension != null) {
	        MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(extension);

	        if (StringUtils.isEmpty(type))
	            type = "video/*"; // No MIME type found, so use the video wildcard
	    }

	    return type;
	}
	/**
	 * 不同状态下的play需要做不同流程的处理
	 */
	public void play() {
		if(coverLayout!=null){
			removeView(coverLayout);
		}
		if(HLSetting.isNewActivityForVideo){
			Intent videoIntent = new Intent(context,VideoActivity.class);
			VideoActivity.resourceID = getEntity().localSourceId;
	        context.startActivity(videoIntent); 
			return;
		}
		if (isPlaying) {
			return;
		}
		if (isHide) {
			getEntity().isPlayVideoOrAudioAtBegining = true;
			this.setVisibility(View.VISIBLE);
			surfaceView.setZOrderOnTop(true);
			if(surfaceViewHasCreated){
				prepareAndLoadVideo();
			}else{
				hasRequestPlayBeforeSurfaceViewCreated=true;
			}
			return;
		} else {
			if (isPause) {
				if(!controlenable){
					return;
				}
				initPlaying();
				mediaPlayer.start();
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
				if (controllerWindow != null && controllerWindow.isShowing()) {
					controllerWindow.updatePausePlay();
				}
			} else {
				prepareAndLoadVideo();
			}
		}
	}

	public HLMediaController getControllerWindow() {
		return controllerWindow;
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if(surfaceView!=null){
			if(visibility==View.INVISIBLE||visibility==View.GONE){
				surfaceView.setLayoutParams(new LayoutParams(0, 0));
				isHide=true;
				if (entity.isVideoControlBarIsShow()) {
					cShow=false;
					try {
						dismissControll();
					} catch (Exception e) {
					}
				}
			}else{
				isHide=false;
			}
		}
	}
	
	private void initPlaying() {
		isStopped = false;
		isPause = false;
		this.isPlaying = true;
		d("initPlaying");
	}

	/**
	 * 开始播放（注意与继续播放的区别，多了事件的触发）
	 */
	private void startPlay() {
		//viewCell不可见touch事件失效
		if(mediaPlayer==null)return;
		((ViewCell)getParent()).setVisibility(View.VISIBLE);
		try{
			mediaPlayer.start();
			if (controllerWindow != null){
				controllerWindow.setTotalDuration(mediaPlayer.getDuration());
			}
			initPlaying();
			if(!isResume){
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
			}else{
				isResume=false;
				if (controllerWindow != null){
					controllerWindow.updatePlay();
					
				}
			}
			if (controllerWindow != null && controllerWindow.isShowing()) {
				controllerWindow.updatePausePlay();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void prepareAndLoadVideo() {
		if(entity.isOnlineSource()){
			loadUrl();
		}else{
			loadStream();
		}
		try {
			mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadUrl() {
		String url=entity.getLocalSourceId();
		try {
			mediaPlayer.setDataSource(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载播放内容
	 */
	private void loadStream() {
		AssetFileDescriptor ass = null;
		FileInputStream fis = null;
		try {
			if (HLSetting.IsResourceSD) {
				String filePath = FileUtils.getInstance().getFilePath(
						entity.localSourceId);
				String privatePath = getContext().getFilesDir()
						.getAbsolutePath();
				if (filePath.contains(privatePath)) {
					FileDescriptor fd = null;
					fis = new FileInputStream(new File(filePath));
					fd = fis.getFD();
					this.mediaPlayer.setDataSource(fd);
				} else {
					this.mediaPlayer.setDataSource(filePath);
				}
			} else {
				ass = FileUtils.getInstance().getFileFD(getContext(),
						entity.localSourceId.trim());
				mediaPlayer.setDataSource(ass.getFileDescriptor(),
						ass.getStartOffset(), ass.getLength());

			}
		} catch (Exception ex) {
			Log.e("hl", "load error",ex);
			ex.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (ass != null)
					ass.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	Handler handler=new Handler(){
		public void dispatchMessage(Message msg) {
			if(msg.what==PLAY_VEDIO){
				startPlay();
			}
		};
	};
	
	@Override
	public void load() {
		int width = getLayoutParams().width;
		if(width<ScreenUtils.dip2px(context, CONTROLERWIDTH))width=ScreenUtils.dip2px(context, CONTROLERWIDTH);
		if(entity.isVideoControlBarIsShow()){
			controllerWindow = new HLMediaController(context,
					width, ScreenUtils.dip2px(context, CONTROLERHEIGHT));
		}
		surfaceView=new SurfaceView(context);
		surfaceView.getHolder().addCallback(this);
		surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		surfaceView.setBackgroundColor(Color.BLACK);
		RelativeLayout.LayoutParams layoutParams;
//		if (entity.isVideoControlBarIsShow()) {
//			setLayoutParams(new LayoutParams(getLayoutParams().width, getLayoutParams().height+ScreenUtils.dip2px(context, CONTROLERHEIGHT)));
//			layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, getLayoutParams().height-ScreenUtils.dip2px(context, CONTROLERHEIGHT));
//			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//			addView(surfaceView,layoutParams);
//		}else{
			addView(surfaceView);
//		}
	}
	
	public SurfaceView getSurfaceView(){
		return surfaceView;
	}

	public void stop() {
		if(HLSetting.isNewActivityForVideo)return;
		d("stop");
		if (!isStopped) {
			try {
				if(controlenable){
					
					mediaPlayer.stop();
					mediaPlayer.reset();
					isPlaying = false;
					isStopped = true;
					isPause = false;
				}
			} catch (Exception ex) {
//				ex.printStackTrace();
			}
		} else {
			return;
		}
	}

	@Override
	public ComponentEntity getEntity() {
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (VideoComponentEntity) entity;
	}

	@Override
	public void load(InputStream is) {
	}

	@Override
	public void resume() {
		requestFocus();
		bringToFront();
		isResume=true;
		if(controllerWindow!=null){
			controllerWindow.dismiss();
//			removeView(controllerWindow);
			cShow=false;
		}
		if(HLSetting.isNewActivityForVideo)return;
//		play();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		onComponentCallbackListener.setPlayComplete();
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
		if (controllerWindow != null) {
			controllerWindow.completion(mediaPlayer);
		}

		if (doCompletAction != null) {
			doCompletAction.doAction();
			doCompletAction = null;
		}
		if (cShow) {
			dismissControll();
			cShow=false;
		}
//		mediaPlayer.pause();
		isPlaying = false;
//		isStopped = true;
		isPause = true;
		
		if(entity.autoLoop){
//			mediaPlayer.stop();
			play();
		}
	}

	public Action doCompletAction;

 

	@Override
	public void hide() {
		if(HLSetting.isNewActivityForVideo)return;
		if (isHide)
			return;
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		if(HLSetting.isNewActivityForVideo)return;
//		if (getVisibility() == View.VISIBLE) {
//			return;
//		}
		this.setVisibility(View.VISIBLE);
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		surfaceView.setZOrderOnTop(true);
		bringToFront();
		requestFocus();
		BookController.getInstance().getViewPage().bringChildToFront(this);

		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	boolean cShow = false;
	private int PLAY_VEDIO=0x1001;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceViewHasCreated=true;
		isHide = false;
		if(HLSetting.isNewActivityForVideo)return;

		String cover = entity.getCoverSourceID();
		if(!StringUtils.isEmpty(cover) && bitmap == null ) bitmap = BitmapUtils.getBitMap(cover, context);
		if(bitmap==null){
			bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.hlvidiodefault);
		}
		if(bitmap!=null){
			coverLayout=new RelativeLayout(context);
			surfaceView.setBackgroundColor(Color.WHITE);
			coverLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			surfaceView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, surfaceView.getHeight());
			if (entity.isVideoControlBarIsShow()) {
				ImageView imageView=new ImageView(context);
				imageView.setImageResource(R.drawable.audio_play);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
				coverLayout.addView(imageView,layoutParams2);
				imageView.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(!controlenable){
							return true;
						}
						if(event.getAction()==MotionEvent.ACTION_UP){
							play();
						}
						return true;
					}
				});
			}
			addView(coverLayout,layoutParams);
		}
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!controlenable){
					return false;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (entity.isVideoControlBarIsShow()) {
						if (getVisibility() == View.INVISIBLE) {
							dismissControll();
							cShow=false;
							return false;
						}
						// if (popupWindow == null || !popupWindow.isShowing())
						// {
						if (cShow) {
							dismissControll();
						} else {
							showControll();
						}
						cShow = !cShow;
					}
				}
				 /*} else { popupWindow.dismiss(); }*/
				 
				return false;
			}
		});
		// setMediaController(controller);
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				// 发生错误时恢复到空闲状态
				mediaPlayer.reset();
				return false;
			}
		});
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setDisplay(surfaceView.getHolder());
		// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		if (hasRequestPlayBeforeSurfaceViewCreated) {
			prepareAndLoadVideo();
		}else{
			if(isResume){
				play();
			}
		}
	}

	private void showControll() {
		controllerWindow.setVideoView(this);
		controllerWindow.show();
//		RelativeLayout.LayoutParams rl=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		addView(controllerWindow,rl);
	}

	private void dismissControll() {
		if (controllerWindow != null && controllerWindow.isShowing()) {
			controllerWindow.dismiss();
//			removeView(controllerWindow);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		try {
			stop();
			controllerWindow.dismiss();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {
		onComponentCallbackListener = callbackListner;
	}

	@Override
	public void callBackListener() {
	}

	@Override
	public void recyle() {
		if (null != this.mediaPlayer) {
			try {
				 this.mediaPlayer.release();
				 if(bitmap != null )BitmapUtils.recycleBitmap(bitmap);
				 //this.mediaPlayer.setDataSource("");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return this.getBufferPercentage();
	}

	@Override
	public int getCurrentPosition() {
		try{
			return this.mediaPlayer.getCurrentPosition();
		}catch(Exception e){
			return -999;
		}
	}

	@Override
	public int getDuration() {
		return this.mediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		if(HLSetting.isNewActivityForVideo)return false;
		if(this.mediaPlayer!=null)
		return this.mediaPlayer.isPlaying();
		return false;
	}

	@Override
	public void seekTo(int pos) {
		this.mediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		if(HLSetting.isNewActivityForVideo)return;
		d("start");
		this.play();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		initPlaying();
		handler.sendEmptyMessageDelayed(PLAY_VEDIO, (long)(entity.delay*1000));
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
	private void d(String message) {
		boolean flag = false;
		if (flag) {
			Log.d("hl", message + "    || id is " + this);
		}
	}
	private Bitmap bitmap = null;
	 Rect src = null;
	private boolean controlenable=true;

	public void setControlUnable(){
		this.controlenable=false;
	}
}
