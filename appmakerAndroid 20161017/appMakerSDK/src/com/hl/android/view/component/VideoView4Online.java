package com.hl.android.view.component;

import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hl.android.HLActivity;
import com.hl.android.R;
import com.hl.android.VideoActivity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.BitmapUtils;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.moudle.HLMediaController;

public class VideoView4Online extends RelativeLayout implements Component, OnPreparedListener, OnCompletionListener, OnErrorListener{
	private Context mContext;
	private VideoView mVideoView;
	private VideoComponentEntity mEntity;
	private Uri uri;
	private boolean hasPlayAtBegin=false;
	private HLMediaController controllerWindow;
	public static int CONTROLERHEIGHT = 50;
	private boolean cShow = false;
	private boolean isStopped = false;
	private boolean isHide = true;
	private boolean isPlaying = false;
	private boolean isPause = false;
	private RelativeLayout coverLayout;
	private Bitmap bitmap;
	public VideoView4Online(Context context) {
		super(context);
	}
	
	public VideoView4Online(Context context,ComponentEntity entity) {
		super(context);
		mContext=context;
		mEntity=(VideoComponentEntity)entity;
		mVideoView=new VideoView(context);
	}

	@Override
	public ComponentEntity getEntity() {
		return mEntity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		mEntity=(VideoComponentEntity) entity;
	}

	@Override
	public void load() {
		if(HLSetting.isNewActivityForVideo)return;
		controllerWindow=new HLMediaController(mContext, getLayoutParams().width, CONTROLERHEIGHT);
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(mEntity.isHideAtBegining){
			setVisibility(View.GONE);
		}
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnErrorListener(this);
		uri=Uri.parse(mEntity.getLocalSourceId());
		mVideoView.setVideoURI(uri);
		RelativeLayout.LayoutParams layoutParams1;
		if (mEntity.isVideoControlBarIsShow()) {
			layoutParams1=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, getLayoutParams().height-ScreenUtils.dip2px(mContext, CONTROLERHEIGHT));
			layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			addView(mVideoView,layoutParams1);
		}else{
			addView(mVideoView,layoutParams);
		}
		String cover = mEntity.getCoverSourceID();
		if(!StringUtils.isEmpty(cover) && bitmap == null ) bitmap = BitmapUtils.getBitMap(cover, mContext);
		if(bitmap!=null){
			coverLayout=new RelativeLayout(mContext);
			coverLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
			RelativeLayout.LayoutParams layoutParams11=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 400);
			ImageView imageView=new ImageView(mContext);
			imageView.setImageResource(R.drawable.audio_play);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
			coverLayout.addView(imageView,layoutParams2);
			addView(coverLayout,layoutParams11);
			coverLayout.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction()==MotionEvent.ACTION_UP){
						play();
					}
					return true;
				}
			});
			
		}
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mEntity.isVideoControlBarIsShow()) {
						if (getVisibility() == View.INVISIBLE) {
							dismissControll();
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
	}
	
	private void startPlay() {
		//viewCell不可见touch事件失效
		((ViewCell)getParent()).setVisibility(View.VISIBLE);
		mVideoView.start();
		controllerWindow.setTotalDuration(mVideoView.getDuration());
		initPlaying();
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
		if (controllerWindow != null && controllerWindow.isShowing()) {
			controllerWindow.updatePausePlay();
		}
	}
	
	private void initPlaying() {
		isStopped = false;
		isPause = false;
		this.isPlaying = true;
		
	}
	
	private void showControll() {
		controllerWindow.setVideoView(mVideoView);
		controllerWindow.show();
		RelativeLayout.LayoutParams rl=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(controllerWindow,rl);
	}

	private void dismissControll() {
		if (controllerWindow != null && controllerWindow.isShowing()) {
			controllerWindow.dismiss();
			removeView(controllerWindow);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if(mEntity.isPlayVideoOrAudioAtBegining&&!hasPlayAtBegin){
			startPlay();
			hasPlayAtBegin=true;
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return false;
	}
	
	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void play() {
		if(coverLayout!=null){
			removeView(coverLayout);
		}
		if(HLSetting.isNewActivityForVideo){
			Intent videoIntent = new Intent(mContext,VideoActivity.class);
			VideoActivity.resourceID = getEntity().localSourceId;
	        mContext.startActivity(videoIntent); 
			return;
		}
		if (isPlaying) {
			return;
		}
		if (isHide) {
			getEntity().isPlayVideoOrAudioAtBegining = true;
			this.setVisibility(View.VISIBLE);
			mVideoView.setZOrderOnTop(true);
			return;
		} else {
			if (isPause) {
				initPlaying();
				mVideoView.start();
				BookController.getInstance().runBehavior(mEntity,
						Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
				if (controllerWindow != null && controllerWindow.isShowing()) {
					controllerWindow.updatePausePlay();
				}
			} else {
				if(mEntity.isOnlineSource()){
					uri=Uri.parse(mEntity.getLocalSourceId());
					mVideoView.setVideoURI(uri);
				}
			}
		}
	}

	@Override
	public void stop() {
		if(HLSetting.isNewActivityForVideo)return;
		if (!isStopped) {
			try {
				mVideoView.stopPlayback();
				isPlaying = false;
				isStopped = true;
				isPause = false;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			return;
		}
	}

	@Override
	public void hide() {
		if(HLSetting.isNewActivityForVideo)return;
		if (isHide)
			return;
		this.setVisibility(View.INVISIBLE);
		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_HIDE);
	}

	@Override
	public void show() {
		if(HLSetting.isNewActivityForVideo)return;
//		if (getVisibility() == View.VISIBLE) {
//			return;
//		}
		this.setVisibility(View.VISIBLE);
		mVideoView.setZOrderOnTop(true);
		bringToFront();
		requestFocus();
		BookController.getInstance().getViewPage().bringChildToFront(this);

		BookController.getInstance().runBehavior(mEntity,
				Behavior.BEHAVIOR_ON_SHOW);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		if(HLSetting.isNewActivityForVideo)return;
		Log.d("hl", "pause");
		if (null != mVideoView && this.mVideoView.isPlaying()) {
			mVideoView.pause();
			isPause = true;
			isPlaying = false;
			// popupWindow.dismiss();
			if (controllerWindow != null && controllerWindow.isShowing()) {
				controllerWindow.updatePausePlay();
			}
		}
		int x = this.mEntity.x;
		int y = this.mEntity.y;
		int w = this.getLayoutParams().width;
		int h = this.getLayoutParams().height;
		((HLActivity) mContext).setVideoCover(x, y, w, h);
	}
	
}
