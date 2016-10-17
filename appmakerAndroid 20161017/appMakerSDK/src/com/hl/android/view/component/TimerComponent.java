package com.hl.android.view.component;

import java.io.InputStream;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.TimerEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.component.bean.TimerShowBean;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;

@SuppressLint({ "HandlerLeak", "ViewConstructor" })
public class TimerComponent extends TextView implements Component,ComponentListener{
	// 全局性的升序计时器
	public TimerShowBean showBean;
	TimerEntity entity;
	private OnComponentCallbackListener onComponentCallbackListener;
	public TimerComponent(Context context, ComponentEntity entity) {
		super(context);
		this.entity = (TimerEntity) entity;
		// 如果是全局计时器，那么就需要判断与全局计时器就行沟通
		if (this.entity.isStaticType) {
			dsyncStatic();
		} else {
			showBean = new TimerShowBean();
		}
		if (showBean.showValue == -1) {
			if (this.entity.isPlayOrderbyDesc) {
				setShowValue(this.entity.getMaxTimer() * 1000);
			} else {
				setShowValue(0);
			}
		} else {
			setShowValue(showBean.showValue);
		}

		if (entity.isHideAtBegining) {
			this.setVisibility(View.INVISIBLE);
		}
		setGravity(Gravity.LEFT);
	}

	private void dsyncStatic() {
		if (entity.isPlayOrderbyDesc) {
			showBean = BookController.getInstance().descShow;
		} else {
			showBean = BookController.getInstance().ascShow;
		}
	}

	@Override
	public ComponentEntity getEntity() {
		// TODO Auto-generated method stub
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (TimerEntity) entity;

	}

	@Override
	public void load() {
		setGravity(Gravity.CENTER);
		setSingleLine(true);
		setPadding(0, 0, 0, 0);
		try {
			String textColor = entity.fontColor;
			textColor = URLDecoder.decode(textColor);
			int color = Color.BLACK;

			if (!StringUtils.isEmpty(textColor)) {
				if (textColor.startsWith("0x")) {
					if (textColor.length() == 8) {
						int r = Integer.parseInt(textColor.substring(2, 4), 16);
						int g = Integer.parseInt(textColor.substring(4, 6), 16);
						int b = Integer.parseInt(textColor.substring(6, 8), 16);
						color = Color.rgb(r, g, b);
					}
				} else {
					String[] a = textColor.split(";");
					color = Color.rgb(Integer.valueOf(a[0]),
							Integer.valueOf(a[1]), Integer.valueOf(a[2]));
				}
			}
			setTextColor(color);

		} catch (Exception ex) {
			setTextColor(Color.BLACK);
		}

		float fontSize = Float.parseFloat(entity.fontSize);

		fontSize =ScreenUtils.getVerScreenValue(fontSize);

		setTextSize(fontSize / 2);

		// if (entity.isStaticType) {
		// startTime = BookController.getInstance().startTime;
		// pauseTime = BookController.getInstance().pauseTime;
		// }
		// if (entity.isPlayOrderbyDesc) {
		// setShowValue(entity.getMaxTimer() * 1000);
		// }else{
		// setShowValue(0);
		// }

		if (this.entity.isPlayVideoOrAudioAtBegining) {
		}else{
			if (!showBean.isStop && !showBean.isPause && !showBean.isEnd
					&& showBean.startTime != -1) {
				playTimer();
			}else if(showBean.isEnd){
				if (entity.isPlayOrderbyDesc) {
					setShowValue(entity.getMaxTimer() * 1000);
				} else {
					setShowValue(0);
				}
			}
		}
	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}
 
	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}

	private void setShowValue(long showValue) {
		String showInfor = Long.toString(showValue / 1000);// String.format("%02d",
															// showValue /1000);
		if (entity.isPlayMillisecond) {
			showInfor = showInfor + ":"
					+ String.format("%02d", showValue % 100);
		}
		setText(showInfor);
		showBean.showValue = showValue;
	}

	/**
	 * 只处理播放的变化，如果停止或者暂停则不予操作
	 */
	private Handler timeMsg = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long showValue = System.currentTimeMillis() - showBean.startTime;
			// 如果已经停止，将计数器需要显示起始状态// 如果暂停，那么什么都不做
			if (showBean.isStop || showBean.isEnd || showBean.isPause) {
			} else {// 正常播放的话
				if (showValue < 0 || showValue > entity.getMaxTimer() * 1000) {
					// 播放已经结束，需要设置状态
					//
					onComponentCallbackListener.setPlayComplete();
					if (entity.isPlayOrderbyDesc) {
						setShowValue(0);
					} else {
						setShowValue(entity.getMaxTimer() * 1000);
					}
					showBean.isEnd = true;
					showBean.startTime = -1;
					showBean.pauseTime = 0;
					for (BehaviorEntity behavior : entity.behaviors) {
						if (behavior.EventName
								.equals(Behavior.BEHAVIOR_ON_AUDIO_VIDEO_END)) {
							BookController.getInstance().runBehavior(behavior);
						}
					}
				} else {
					// 如果是倒序
					if (entity.isPlayOrderbyDesc) {
						showValue = entity.getMaxTimer() * 1000 - showValue;
					}
					setShowValue(showValue);

					if (entity.isPlayMillisecond) {
						timeMsg.sendEmptyMessageDelayed(1, 12);
					} else {
						timeMsg.sendEmptyMessageDelayed(1, 1000);
					}
				}

			}
		}
	};

	public void playTimer() {
		long showValue = System.currentTimeMillis() - showBean.startTime;
		if (showValue < 0 || showValue > entity.getMaxTimer() * 1000) {
			showBean.isEnd = true;
		}
		
		if (showBean.isEnd) {
				showBean.isStop = false;
				showBean.isPause = false;
				showBean.isEnd = false;
				showBean.startTime = -1;
				showBean.pauseTime = 0;
		}
		showBean.isStop = false;
		showBean.isPause = false;
		long curPlayTime = System.currentTimeMillis();
		if (showBean.startTime == -1) {
			showBean.startTime = curPlayTime;

			for (BehaviorEntity behavior : entity.behaviors) {
				if (behavior.EventName
						.equals(Behavior.BEHAVIRO_ON_AUDIO_VIDEO_PLAY)) {
					BookController.getInstance().runBehavior(behavior);
				}
			}
		}
		if (showBean.pauseTime > 0) {
			showBean.startTime = curPlayTime + showBean.startTime
					- showBean.pauseTime;
			showBean.pauseTime = 0;
		}

		timeMsg.sendEmptyMessage(1);

	}

	@Override
	public void play() {

	}

	@Override
	public void stop() {
		timeMsg.removeMessages(1);
	}

	@Override
	public void hide() {
		timeMsg.removeMessages(1);
		this.setVisibility(View.INVISIBLE);

	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);

	}

	@Override
	public void resume() {

	}

	public void pauseTimer() {
		if (showBean.isStop || showBean.isEnd)
			return;
		showBean.isPause = true;
		showBean.pauseTime = System.currentTimeMillis();
	}

	@Override
	public void pause() {
		// timeMsg.removeMessages(1);
		// pauseTimer();
	}

	public void stopTimer() {
		showBean.isStop = true;
		showBean.isPause = false;
		showBean.isEnd = false;
		showBean.startTime = -1;
		showBean.pauseTime = 0;

		timeMsg.removeMessages(1);
		if (entity.isPlayOrderbyDesc) {
			setShowValue(entity.getMaxTimer() * 1000);
		} else {
			setShowValue(0);
		}
	}


	public void resumeTimer() {
		showBean.isStop = false;
		long curPlayTime = System.currentTimeMillis();
		showBean.startTime = curPlayTime + showBean.startTime
				- showBean.pauseTime;
		showBean.pauseTime = 0;
		timeMsg.sendEmptyMessage(1);
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
