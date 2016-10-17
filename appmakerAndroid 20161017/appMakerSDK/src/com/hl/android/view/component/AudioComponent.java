package com.hl.android.view.component;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hl.android.R;
import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.Button4Play.ActionListener;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.inter.ComponentPost;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

/**
 * 声音组件
 * 
 * @author webcat
 * 
 */
public class AudioComponent extends RelativeLayout implements Component,
		OnCompletionListener, MediaPlayer.OnPreparedListener,
		ComponentListener, ComponentPost {
	public ComponentEntity entity;
	private MediaPlayer mediaPlayer; 
	ArrayList<AnimationEntity> anims;
	private OnComponentCallbackListener onComponentCallbackListener;
	private ImageView img;
	private MySeekBar mSeekBar;
	private int totalMileSeconds = 0;
	private int PLAY_MUSIC=10010;
	private int [] progressImages;
	public boolean isBackGroundMusic=false;
	public AudioComponent(Context context, ComponentEntity entity) {
		super(context);
		this.entity = entity;

		if (null == mediaPlayer) {
			this.mediaPlayer = new MediaPlayer();
			this.mediaPlayer.setOnCompletionListener(this);
			this.mediaPlayer.setOnPreparedListener(this);
			this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			this.mediaPlayer.setDisplay(null);
			if (entity.autoLoop) {
				this.mediaPlayer.setLooping(true);
			}
			mediaPlayer.setOnErrorListener(new OnErrorListener() {  
	            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {  
	                //发生错误时恢复到空闲状态 
	            	if(mediaPlayer!=null)mediaPlayer.reset();  
	                return false;  
	            }  
	        }); 
		}
	}

	public AudioComponent(Context context) {
		super(context);
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
	public void load() {
		AssetFileDescriptor ass = null;
		FileInputStream fis = null;
		try {
			if(entity.isOnlineSource()){
//				String url="http://zhangmenshiting.baidu.com/data2/music/87718191/12625981387141261320.mp3?xcode=d4b302bc6046566569420c79ed21e379a9facf82742be816";
				String url=entity.getLocalSourceId();
				this.mediaPlayer.setDataSource(url);
			}else{
				if (HLSetting.IsResourceSD) {
					String filePath = FileUtils.getInstance().getFilePath(
							entity.localSourceId);
					String privatePath = getContext().getFilesDir()
							.getAbsolutePath();
					if (filePath.contains(privatePath)) {
						fis = new FileInputStream(new File(filePath));
						FileDescriptor fd = fis.getFD();
						this.mediaPlayer.setDataSource(fd);
					}else{
						this.mediaPlayer.setDataSource(filePath);
					}
					
				} else {				
					ass = FileUtils.getInstance().getFileFD(getContext(),
							entity.localSourceId);
					this.mediaPlayer.setDataSource(ass.getFileDescriptor(),
							ass.getStartOffset(), ass.getLength());
				}
			}

			this.init = true;
				if(isBackGroundMusic){
					return;
				}
				removeAllViews();
				img = new ImageView(getContext());
				mSeekBar = new MySeekBar(getContext(),getLayoutParams().width,LayoutParams.WRAP_CONTENT);
				
				img.setImageResource(R.drawable.audio_play_new);
				mSeekBar.changeToPlayImage();
				RelativeLayout.LayoutParams layoutParams4image=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				layoutParams4image.addRule(RelativeLayout.CENTER_IN_PARENT);
				addView(img,layoutParams4image);
				RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
				addView(mSeekBar,layoutParams);
				
				img.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch(action){
						case MotionEvent.ACTION_DOWN:
							break;
						case MotionEvent.ACTION_UP:
							playOrStop();
							break;
						}
						return true;
					}
				});
				if(entity.isHideAtBegining){
					img.setVisibility(View.INVISIBLE);
					mSeekBar.setVisibility(View.INVISIBLE);
				}

			if(entity.showProgress){
				if(img!=null)img.setVisibility(View.INVISIBLE);
				if(mSeekBar!=null)mSeekBar.setVisibility(View.VISIBLE);
				setBackgroundResource(R.drawable.blackcorner);
			}else{
				progressImages=new int[61];
				for (int i = 0; i < progressImages.length; i++) {
					progressImages[i]=R.drawable.jz_00000+i;
				}
				if(mSeekBar!=null)mSeekBar.setVisibility(View.INVISIBLE);
				if(mSeekBar!=null)img.setVisibility(View.VISIBLE);
				
			}
//			if(entity.isPlayVideoOrAudioAtBegining){
//					play();
//			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if (ass != null) {
					ass.close();

					ass = null;
				}
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	private boolean controlenable=true;
	public void setControlUnable(){
		if(img!=null){
			img.setEnabled(false);
		}
		if(mSeekBar!=null){
			mSeekBar.getBtnAction().setEnabled(false);
			mSeekBar.getSeekbar().setEnabled(false);
		}
		controlenable=false;
	}
	  
    private int setProgress() {
        if (mediaPlayer == null) {
            return 0;
        }
        if(!hasPrePared){
        	return 0;
        }
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        //int right = duration - position;
        if(entity.showProgress){
        	if (mSeekBar != null) {
        		if (duration > 0) {
        			// use long to avoid overflow
        			long pos = 1000L * position / duration;
        			mSeekBar.setProgress((int) pos);
        		}
        	}
        }else{
         	if (img != null) {
        		if (duration > 0) {
        			// use long to avoid overflow
        			if(progressImages== null){
        				progressImages=new int[61];
        				for (int i = 0; i < progressImages.length; i++) {
        					progressImages[i]=R.drawable.jz_00000+i;
        				}
        			}
        			int pos = 61 * position / duration;
        			if(pos>=61){
        				pos=60;
        			}
        			int imgID = progressImages[pos];
        			img.setImageResource(imgID);
        		}
        	}
        }
        
        return position;
    }
    
	  private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		  	private boolean isPlaying=false;
	        public void onStartTrackingTouch(SeekBar bar) {
	        	if(!hasPrePared){
	        		return;
	        	}
				mHandler.removeMessages(1);
				isPlaying = mediaPlayer.isPlaying();
				if(isPlaying){
					mediaPlayer.pause();
				}
	        }

	        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
	        	if(!hasPrePared){
	        		mSeekBar.setProgress(0);
	        		return;
	        	}
	            if (!fromuser) {
	                // We're not interested in programmatically generated changes to
	                // the mSeekBar bar's position.
	                return;
	            }

	            totalMileSeconds = mediaPlayer.getDuration();
	            long left = (totalMileSeconds * progress) / 1000L;
	            mediaPlayer.seekTo( (int) left);
	            
	        }

	        public void onStopTrackingTouch(SeekBar bar) {
	        	if(!hasPrePared){
	        		return;
	        	}
	            setProgress();
				mHandler.sendEmptyMessage(1);
				if(isPlaying){
					mediaPlayer.start();
				}
	        }

	    };

	public void loadStream() {
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==PLAY_MUSIC){
				doPlay();
			}else{
				if(mediaPlayer==null)return;
				if (mediaPlayer.isPlaying()) {
					setProgress();
					sendEmptyMessageDelayed(1, 100);
				}
			}
		}
	};

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		int action = event.getAction();
//		switch(action){
//		case MotionEvent.ACTION_DOWN:
//			break;
//		case MotionEvent.ACTION_UP:
//			this.playOrStop();
//			break;
//		}
//		return super.onTouchEvent(event);
//	}

	private boolean isPaused = false;
	private boolean isStopped = false;
	private boolean isPlaying = false;
	private boolean init = false;
	private boolean isCompleted = false;
	private int mediaDuration;
	private boolean hasPrePared;

	public void play() {
		if(isPlaying)return;
		if(this.isPaused){
			mHandler.sendEmptyMessage(PLAY_MUSIC);
		}else{
			
			mHandler.sendEmptyMessageDelayed(PLAY_MUSIC, (long) (entity.delay*1000));
			Log.d("hl",this + "  played after " + entity.delay*1000);
		}
	}

	protected void doPlay() {
		Log.d("hl",this + "  is doPlay ");
		if(BookSetting.noBackGround){
			mediaPlayer.setVolume(0, 0);
		}
//		if(this.isPlaying){
//			return;
//		}
		if(mediaPlayer == null)return;
		try {
			if (init) {
				this.init = false;
				this.mediaPlayer.prepare();
				return;
			}
			//暂停，并且音乐已经播放完毕，那么暂停状态设置成false，直接返回
			if(this.isPaused && this.isCompleted){
				this.isPaused = false;
				return;
			}
			if (this.isPaused&& !isCompleted) {
				this.mediaPlayer.start();
				initPlaying();
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
				return;
			}
			if (isStopped) {
				this.mediaPlayer.prepare();
				return;
			}
			if (this.isPlaying&& !this.isCompleted) {
				this.mediaPlayer.stop();
				this.mediaPlayer.prepare();
				return;
			}
			if (this.isCompleted == true) {
				initPlaying();
				this.mediaPlayer.start();
				BookController.getInstance().runBehavior(entity,
						Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			initPlaying();
			mediaPlayer.reset();
			try {
				load();
				this.mediaPlayer.prepare();
				this.init = true;
				this.isPaused = true;
				this.isCompleted = false;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void initPlaying() {
		this.isPaused = false;
		this.isCompleted = false;
		this.isStopped = false;
		this.isPlaying = true;
		if(img!=null){
			img.setImageResource(R.drawable.audio_stop_new);
			mHandler.sendEmptyMessage(1);
		}
		if(mSeekBar!=null){
			mSeekBar.changeToPauseImage();
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {	
		hasPrePared=true;
		if (null != this.mediaPlayer) {
			//this.setBackgroundResource(R.drawable.audio_stop);
			try {
				mediaDuration=mp.getDuration();
				this.mediaPlayer.seekTo(0);
				mediaPlayer.start();
				initPlaying();
			} catch (Exception ex) {
				load();
				mediaPlayer.start();
			}
		}
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY);
	}
	
	public void stop() {
		if ((null != this.mediaPlayer && this.mediaPlayer.isPlaying())
				|| this.isPaused == true) {
			try {
				if(controlenable){
					if(img!=null){
						img.setImageResource(R.drawable.audio_play_new);
					}
					if(mSeekBar!=null){
						mSeekBar.setProgress(0);
						mSeekBar.changeToPlayImage();
					}
					mediaPlayer.stop();
					this.isStopped = true;
					this.isPaused = false;
					this.isPlaying = false;
					
					mHandler.removeMessages(1);
				}
			} catch (Exception ex) {
				Log.e("AudioComponent", "stop", ex);
			}

		}

	}

	@Override
	public void pause() {
		if(this.isStopped){
			return;
		}
		try {
			if(controlenable){
				this.isPaused = true;
				this.isPlaying = false;
				if (null != mediaPlayer && this.mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					if(img!=null){
						img.setImageResource(R.drawable.audio_play_new);
					}
					if(mSeekBar!=null){
						mSeekBar.changeToPlayImage();
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void resume() {
		try {
			if (mediaPlayer != null&&isPaused){
				mediaPlayer.start();
				this.initPlaying();
			}
			isPaused = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void playOrStop() {
		if (null != mediaPlayer && this.isPlaying == true) {
			if(!controlenable){
				return;
			}
			if(img!=null){
				img.setImageResource(R.drawable.audio_play_new);
			}
			if(mSeekBar!=null){
				mSeekBar.changeToPlayImage();
			}
			mediaPlayer.pause();
			this.isPlaying = false;
			this.isPaused = true;
			if(img != null){
				mHandler.removeMessages(1);
			}
		} else{
			/*mediaPlayer.start();
			if(img!=null){
				img.setBackgroundResource(R.drawable.audio_stop);
			}
			this.isPlaying = true;
			this.isPaused = false;*/
			doPlay();
		}
	}
 

	@Override
	public void hide() {
		this.setVisibility(View.GONE);
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
	public void onCompletion(MediaPlayer mp) {
		if (null != onComponentCallbackListener) {
			onComponentCallbackListener.setPlayComplete();
		}
		
		this.isCompleted = true;
		//this.isPaused = false;
		this.isPlaying = false;
		if(img!=null){
			img.setImageResource(R.drawable.audio_play_new);
		}
		if(mSeekBar!=null){
			mSeekBar.setProgress(0);
			mSeekBar.changeToPlayImage();
		}
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END);
		
	}

	@Override
	public void registerCallbackListener(
			OnComponentCallbackListener callbackListner) {
		onComponentCallbackListener = callbackListner;

	}

	@Override
	public void recyle() {
		Log.d("hl",this + "  is recyled ");
		mHandler.removeMessages(1);
		mHandler.removeMessages(PLAY_MUSIC);
		try {
			//为什么回收要加上这个isPlaying，暂时去掉 by zhaoq
			//if (null != this.mediaPlayer&&this.mediaPlayer.isPlaying()) {
			if (null != this.mediaPlayer) {
				this.mediaPlayer.stop();
				this.mediaPlayer.release();
				this.mediaPlayer = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally{
			this.mediaPlayer = null;
		}

	}

	@Override
	public void callBackListener() {
		onComponentCallbackListener.setPlayComplete();
	}
	class MySeekBar extends RelativeLayout{
		int mWidth;
		int mHeight;
		Context mContext;
		SeekBar seekbar;
		Button4Play btnAction;
		TextView textRight;
		StringBuilder mFormatBuilder;
		Formatter mFormatter;
		public MySeekBar(Context context,int width,int height) {
			super(context);
			mWidth=width;
			mHeight=height;
			removeAllViews();
			addView(drawView(context),width,height);
		}
	
		public void changeToPlayImage() {
			btnAction.change2ShowPlay();
		}
		
		public void changeToPauseImage() {
			btnAction.change2ShowStop();
		}

		public void setProgress(int pos) {
			seekbar.setProgress(pos);
			String curString;
			if(pos!=0){
				curString=stringForTime(mediaPlayer.getCurrentPosition());
			}else{
				curString="00:00";
			}
			textRight.setText(curString+"/"+stringForTime(mediaDuration));
		}

		 private String stringForTime(long timeMs) {
		    	
		    	long totalSeconds = timeMs /(long) 1000;

		    	long seconds = totalSeconds % 60;
		    	long minutes = (totalSeconds / 60) % 60;
		    	long hours   = totalSeconds / 3600;
		       
		        mFormatBuilder.setLength(0);
		        if (hours > 0) {
		            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		        } else {
		            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		        }
		    }
		
		private LinearLayout drawView(Context context){
			LinearLayout drawView=new LinearLayout(context);
			drawView.setOrientation(LinearLayout.HORIZONTAL);
			drawView.setGravity(Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(30,30);
			btnLp.leftMargin=5;
			btnAction = new Button4Play(context);
			drawView.addView(btnAction,btnLp);
			
			seekbar = new SeekBar(context);
			seekbar.setMax(1000);
			LinearLayout.LayoutParams seekLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,15,1.0f);
			drawView.addView(seekbar,seekLp);
			Drawable progressDrawable = this.getResources().getDrawable(R.drawable.media_player_seekbar_selector);
			progressDrawable.setBounds(seekbar.getProgressDrawable().getBounds());
			seekbar.setProgressDrawable(progressDrawable);
			Drawable thumb = this.getResources().getDrawable(R.drawable.player_seekbar_thumbnail);
			progressDrawable.setBounds(seekbar.getProgressDrawable().getBounds());
			seekbar.setThumb(thumb);
			seekbar.setPadding(10, 0, 10, 0);
			
			 mFormatBuilder = new StringBuilder();
		     mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
			
			textRight = new TextView(context);
			textRight.setText("00:00/00:00");
			textRight.setTextColor(Color.WHITE);
			textRight.setGravity(Gravity.CENTER_VERTICAL);
			textRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15);
			LinearLayout.LayoutParams righttLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			righttLp.rightMargin=10;
			righttLp.leftMargin=5;
			drawView.addView(textRight,righttLp);
			
			
			seekbar.setOnSeekBarChangeListener(mSeekListener);
			btnAction.setActionListener(new ActionListener() {
				
				@Override
				public void onDoStop() {
					playOrStop();
				}
				
				@Override
				public void onDoPlay() {
					playOrStop();
				}
			});
	        
			return drawView;
		}
		
		public Button4Play getBtnAction() {
			return btnAction;
		}
		
		public SeekBar getSeekbar() {
			return seekbar;
		}
		
	}
}
