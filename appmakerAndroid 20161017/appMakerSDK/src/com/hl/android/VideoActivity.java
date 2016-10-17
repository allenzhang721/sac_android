package com.hl.android;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.util.Formatter;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.android.R;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.core.utils.FileUtils;

/**
 * 专门的视频播放activity
 * 
 * @author zhaoq
 * 
 */
@SuppressLint("HandlerLeak")
public class VideoActivity extends Activity implements OnCompletionListener,
		OnErrorListener, OnInfoListener, OnPreparedListener,
		OnSeekCompleteListener, OnVideoSizeChangedListener,
		SurfaceHolder.Callback {
	// 播放的资源id
	public static String resourceID = "";

	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;

	MediaPlayer mediaPlayer;

	int videoWidth = 0, videoHeight = 0;

	boolean readyToPlay = false;

	Display currentDisplay;

	private LinearLayout layout;
	MediaController mediaController = null;

	LinearLayout controllerLay;

	Button btnAction;
	Button btnBack;
	TextView textLeft;
	TextView textRight;
	SeekBar seekBar;
	boolean isShow = false;
	LinearLayout midLay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BookSetting.IS_HOR) {
			// 强制为横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			// 强制为竖屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.rgb(192, 192, 192));
		layout.setOrientation(LinearLayout.VERTICAL);
		isPause = false;

		LinearLayout topLay = new LinearLayout(this);
		topLay.setGravity(Gravity.LEFT);
		LinearLayout.LayoutParams topLp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 60);
		Button btn = new Button(this);
		btn.setText("返回");
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		topLay.setPadding(10, 10, 10, 10);
		topLay.addView(btn);

		midLay = new LinearLayout(this);
		midLay.setBackgroundColor(Color.BLACK);
		midLay.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams midLp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);

		LinearLayout bottomLay = new LinearLayout(this);
		LinearLayout.LayoutParams bottomLp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 100);

		layout.addView(topLay, topLp);
		layout.addView(midLay, midLp);
		layout.addView(bottomLay, bottomLp);

		surfaceView = new SurfaceView(this);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * if (controllerLay.getVisibility() == View.VISIBLE) {
				 * controllerLay.setVisibility(View.INVISIBLE); } else {
				 * controllerLay.setVisibility(View.VISIBLE); }
				 */
			}
		});

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnVideoSizeChangedListener(this);

		currentDisplay = getWindowManager().getDefaultDisplay();

		btnBack = new Button(this);
		btnBack.setText("返回");
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// layout.addView(btnBack,btnLp);
		midLay.addView(surfaceView);

		controllerLay = new LinearLayout(this);
		btnAction = new Button(this);
		textLeft = new TextView(this);
		textRight = new TextView(this);
		btnAction.setBackgroundResource(R.drawable.audio_stop);
		textLeft.setText("00:00");
		textLeft.setTextColor(Color.BLACK);
		textRight.setText("00:00");
		textRight.setTextColor(Color.BLACK);
		seekBar = new SeekBar(this);
		seekBar.setMax(100);
		seekBar.setOnSeekBarChangeListener(mSeekListener);

		btnAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					btnAction.setBackgroundResource(R.drawable.audio_play);
				} else {
					mHandler.sendEmptyMessage(1);
					mediaPlayer.start();
					btnAction.setBackgroundResource(R.drawable.audio_stop);
				}
			}
		});

		controllerLay.addView(btnAction);
		controllerLay.addView(textLeft);
		controllerLay.setGravity(Gravity.CENTER);

		LinearLayout.LayoutParams seekLp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1);
		controllerLay.addView(seekBar, seekLp);
		controllerLay.addView(textRight);

		LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		clp.setMargins(20, 10, 20, 10);
		bottomLay.addView(controllerLay, clp);

		setContentView(layout);
	}

	public static float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		AssetFileDescriptor ass = null;
		FileInputStream fis = null;
		try {
			if (HLSetting.IsResourceSD) {
				String filePath = FileUtils.getInstance()
						.getFilePath(resourceID);
				String privatePath = getFilesDir().getAbsolutePath();
				if (filePath.contains(privatePath)) {
					FileDescriptor fd = null;
					fis = new FileInputStream(new File(filePath));
					fd = fis.getFD();
					this.mediaPlayer.setDataSource(fd);
				} else {
					this.mediaPlayer.setDataSource(filePath);
				}
			} else {
				ass = FileUtils.getInstance().getFileFD(this, resourceID);
				mediaPlayer.setDataSource(ass.getFileDescriptor(),
						ass.getStartOffset(), ass.getLength());

			}
		} catch (Exception ex) {
			Log.e("hl", " video",ex);  
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

		mediaController = new MediaController(this);
		mediaPlayer.setDisplay(holder);
		try {
			mediaPlayer.prepare();
		} catch (Exception e) {
			finish();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		try {
			mediaPlayer.pause();
		} catch (Exception e) {

		}
		finish();
	}
	
	

	/*public void changeSize(boolean flag) {

		int actHeight = BookSetting.INIT_SCREEN_HEIGHT - 160;
		if (flag) {
			videoWidth = (int) (videoWidth * 1.1);
			videoHeight = (int) (videoHeight * 1.1);
			if (videoHeight > actHeight) {
				setFitSize();
			}
			surfaceView.setLayoutParams(new LinearLayout.LayoutParams(
					videoWidth, videoHeight));
		} else {
			if (videoHeight >= (actHeight / 4)
					&& videoHeight >= (BookSetting.INIT_SCREEN_WIDTH / 4)) {

				videoWidth = (int) (videoWidth * 0.9);
				videoHeight = (int) (videoHeight * 0.9);
			}
			// if (videoHeight < (orginHeight/2)) {
			// videoWidth = orginWidth;
			// videoHeight = orginHeight;
			// }
		}

		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,
				videoHeight));
	}
*/
	@Override
	protected void onDestroy() {
		super.onDestroy();

		mediaPlayer.release();
	}

	public void setFitSize() {
		int width = midLay.getMeasuredWidth();
		int height = midLay.getMeasuredHeight();

		float layRatio = (float) width / (float) height;
		float videoRatio = (float) videoWidth / (float) videoHeight;

		if (layRatio > videoRatio) {
			videoHeight = height;
			videoWidth = (int) (videoHeight * videoRatio);
		} else {

			videoWidth = width;
			videoHeight = (int) (videoWidth / videoRatio);
		}
		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,
				videoHeight));
	}

	int orginWidth = 0;
	int orginHeight = 0;

	@Override
	public void onPrepared(MediaPlayer mp) {
		videoWidth = mp.getVideoWidth();
		videoHeight = mp.getVideoHeight();

/*		float ratioH = (float) videoHeight / (float) videoHeight;
		if (videoHeight > BookSetting.INIT_SCREEN_HEIGHT - 160) {
			videoHeight = BookSetting.INIT_SCREEN_HEIGHT - 160;
			videoWidth = (int) (videoHeight * ratioH);
		}*/

		setFitSize();
		/*orginWidth = videoWidth;
		orginHeight = videoHeight;
		surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,
				videoHeight));*/
		mp.start();

		mHandler.sendEmptyMessage(1);
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(this, "视频播放出现问题", Toast.LENGTH_LONG).show();
		finish();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		finish();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setProgress();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				sendEmptyMessageDelayed(1, 100);
			}
		}
	};

	private int setProgress() {
		if (mediaPlayer == null) {
			return 0;
		}
		int position = mediaPlayer.getCurrentPosition();
		int duration = mediaPlayer.getDuration();
		int right = duration - position;
		if (seekBar != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 100L * position / duration;
				seekBar.setProgress((int) pos);
			}
		}

		if (textLeft != null)
			textLeft.setText(stringForTime((int) position));
		if (textRight != null)
			textRight.setText("-" + stringForTime(right));
		if (mediaPlayer.isPlaying()) {
			btnAction.setBackgroundResource(R.drawable.audio_stop);
		} else {
			btnAction.setBackgroundResource(R.drawable.audio_play);
		}

		return position;
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		StringBuilder mFormatBuilder = new StringBuilder();
		@SuppressWarnings("resource")
		Formatter mFormatter = new Formatter(mFormatBuilder,
				Locale.getDefault());

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			mHandler.removeMessages(1);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				return;
			}

			int totalMileSeconds = mediaPlayer.getDuration();
			long left = (totalMileSeconds * progress) / 100L;
			long right = totalMileSeconds - left;
			if (textLeft != null && textRight != null) {
				textLeft.setText(stringForTime((int) left));
				textRight.setText("-" + stringForTime((int) right));
			}
			mediaPlayer.seekTo((int) left);
			mHandler.sendEmptyMessage(1);
		}

		public void onStopTrackingTouch(SeekBar bar) {
			setProgress();
		}
	};
	private boolean isPause = false;

	protected void onPause() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			btnAction.setBackgroundResource(R.drawable.audio_play);
			isPause = true;
		}
		super.onPause();
	};

	@Override
	protected void onResume() {
		Log.d("hl","onresume");
		if (isPause) {
			mHandler.sendEmptyMessage(1);
			mediaPlayer.start();
			btnAction.setBackgroundResource(R.drawable.audio_stop);
			isPause = false;
		}
		super.onResume();
	}
}
