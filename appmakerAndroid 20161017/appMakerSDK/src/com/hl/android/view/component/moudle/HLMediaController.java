package com.hl.android.view.component.moudle;

import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hl.android.R;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.VideoComponent;

public class HLMediaController extends LinearLayout {
	private MediaPlayerControl mPlayer;
	private SeekBar mSeekBar;
	private ImageButton btnAction;
	private TextView textLeft;
	private TextView textRight;
	private LinearLayout layout;
	
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    
	

    private boolean mDragging;
    private boolean mShowing;
    private static final int    SHOW_PROGRESS = 2;
    private Context mContext;
	private long mDuration = 0;
	public void setTotalDuration(long duration){
		mDuration = duration;
	  if (textLeft != null)
        	textLeft.setText(stringForTime(mDuration));
        if (textRight != null)
        	textRight.setText("-"+stringForTime(0));

        
	}
	public HLMediaController(Context context){
		this(context,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	}
	public HLMediaController(Context context,int width,int height) {
		super(context);
		mContext = context;
		LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(width, height);
		addView(drawView(),layoutParams);
		mWindowManager= ((Activity)context).getWindowManager();
		 wmParams = new WindowManager.LayoutParams();  
		 wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;  
		 wmParams.format = PixelFormat.RGBA_8888;;  
		 wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
		 wmParams.gravity = Gravity.LEFT | Gravity.TOP;  
	}
	
	private View drawView(){
		LinearLayout rootLay = new LinearLayout(mContext);
		layout = new LinearLayout(mContext);
		layout.setBackgroundResource(R.drawable.media_control_bg);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams layLp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		int padding = ScreenUtils.dip2px(mContext, 20);
		layout.setPadding(padding, 0, padding, 0);
		layout.setLayoutParams(layLp);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		
		LinearLayout.LayoutParams leftLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		btnAction = new ImageButton(mContext);
		LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(ScreenUtils.dip2px(mContext, VideoComponent.CONTROLERHEIGHT/2),ScreenUtils.dip2px(mContext,  VideoComponent.CONTROLERHEIGHT/2));
		btnAction.setScaleType(ScaleType.FIT_XY);
		btnAction.setBackgroundResource(R.drawable.audio_play);
		layout.addView(btnAction,btnLp);
		
		textLeft = new TextView(mContext);
		textLeft.setTextColor(Color.WHITE);
		textLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
		textLeft.setText("     ");
		layout.addView(textLeft,leftLp);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setMax(100);
		mSeekBar.setMinimumHeight(ScreenUtils.dip2px(mContext, VideoComponent.CONTROLERHEIGHT/3));
//		mSeekBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.media_player_seekbar_selector));
//		mSeekBar.setThumb(mContext.getResources().getDrawable(R.drawable.player_seekbar_thumbnail));
//		mSeekBar.setThumbOffset(0);
		LinearLayout.LayoutParams seekLp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT,1);
		layout.addView(mSeekBar,seekLp);
		
		textRight = new TextView(mContext);
		textRight.setTextColor(Color.WHITE);
		textRight.setText("     ");
		textRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
		layout.addView(textRight,leftLp);
		
		layout.setLayoutParams(layLp);
		
		
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        mSeekBar.setOnSeekBarChangeListener(mSeekListener);
        btnAction.setOnClickListener(mPauseListener);
        rootLay.addView(layout);
        rootLay.setBackgroundColor(Color.TRANSPARENT);
		return rootLay;
	}
	
	public void show() {
		try{
			int[] location=new int[2];
			((View)mPlayer).getLocationInWindow(location);
			wmParams.width = ((View)mPlayer).getLayoutParams().width;  
			wmParams.height = ScreenUtils.dip2px(mContext, VideoComponent.CONTROLERHEIGHT);  
			wmParams.x =location[0];
			wmParams.y = location[1]+((View)mPlayer).getLayoutParams().height-wmParams.height;  
			mWindowManager.addView(this, wmParams);
			updatePausePlay();
			if(mPlayer.isPlaying()){
				setProgress();
			}
			mShowing = true;
			if (mPlayer.isPlaying()) {
				mHandler.sendEmptyMessage(SHOW_PROGRESS);
			}
		}catch(Exception exception){
			
		}
	}
	
	public void dismiss(){
		try{
			mShowing = false;
			mWindowManager.removeView(this);
		}catch(Exception exception){
			
		}
	}
	
	
	
	public void setVideoView(MediaPlayerControl player){
		mPlayer = player;
	}
	
	private boolean isComplete = false;
	private MediaPlayer mediaPlayer;
	public void completion(MediaPlayer player){
		mediaPlayer = player;
		isComplete = true;
		//mHandler.removeMessages(SHOW_PROGRESS);
    	btnAction.setBackgroundResource(R.drawable.audio_play);
	}
    public void updatePausePlay() {
        if (layout == null || btnAction == null)
            return;
        updatePlay();
    }
    
    public void updatePlay() {
        if (mPlayer.isPlaying()) {
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        	btnAction.setBackgroundResource(R.drawable.audio_stop);
        } else {
            mHandler.removeMessages(SHOW_PROGRESS);
        	btnAction.setBackgroundResource(R.drawable.audio_play);
        }
       
    }
    
   public void upDateWindowPosition(){
    	try{
    		int[] location=new int[2];
    		((View)mPlayer).getLocationInWindow(location);
    		wmParams.width = ((View)mPlayer).getLayoutParams().width;  
    		wmParams.height = ScreenUtils.dip2px(mContext, VideoComponent.CONTROLERHEIGHT);  
    		wmParams.x =location[0];
    		wmParams.y = location[1]+((View)mPlayer).getLayoutParams().height-wmParams.height;
    		mWindowManager.updateViewLayout(this, wmParams);
    	}catch(Exception e){
    		
    	}
    }
    
    
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }
            if(mDuration==0){
            	setTotalDuration(mPlayer.getDuration());
            }
            long left = (mDuration * progress) / 100L;
            long right = mDuration - left;
            if(textLeft != null && textRight != null ){
            	textLeft.setText(stringForTime((int) left));
            	textRight.setText("-"+ stringForTime((int) right));
            }
            if(mPlayer.isPlaying()){
            	mPlayer.seekTo( (int) left);
            }
            
        }

        public void onStopTrackingTouch(SeekBar bar) {
        	 if(mPlayer.isPlaying()){
        		  mDragging = false;
                  setProgress();
                  updatePausePlay();
             }
          
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing && mPlayer.isPlaying() && pos>0) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 100 - (pos % 100));
                    }
                    break;
            }
        }
    };
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };
    
    public void doPauseResume() {
    	if(isComplete){
    		long left = (mDuration * mSeekBar.getProgress()) / 100L;
            long right = mDuration - left;
            if(left == 0 || right == 0){
         	   mediaPlayer.seekTo(0);
            }else{
         	   mediaPlayer.seekTo((int) left);
            }
            mediaPlayer.start();
            mPlayer.start();
        	isComplete = false;
        }else if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
        	long left = (mDuration * mSeekBar.getProgress()) / 100L;
            long right = mDuration - left;
            if(left == 0 || right == 0){
            	if(mediaPlayer!=null)mediaPlayer.seekTo(0);
            }else{
         	   if(mediaPlayer!=null)mediaPlayer.seekTo((int) left);
            }
            mPlayer.start();
        }
    	updatePausePlay();
    }
    
    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        if(position < 0){
        	return position;
        }
//        if(mDuration==0){
//        	setTotalDuration(mPlayer.getDuration());
//        }
        long right = mDuration - position;
        if (mSeekBar != null) {
            if (mDuration > 0) {
                // use long to avoid overflow
                long pos = 100L * position / mDuration;
                mSeekBar.setProgress( (int) pos);
            }
        }
        
        if (textLeft != null)
        	textLeft.setText(stringForTime(position));
        if (textRight != null)
        	textRight.setText("-"+stringForTime(right));

        return position;
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
	public boolean isShowing() {
		return mShowing;
	}
	
}
