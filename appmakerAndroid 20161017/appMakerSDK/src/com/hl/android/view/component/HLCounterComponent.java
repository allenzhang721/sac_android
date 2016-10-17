package com.hl.android.view.component;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.CounterEntity;
import com.hl.android.controller.BookController;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

@SuppressLint({ "NewApi", "ViewConstructor" })
public class HLCounterComponent extends TextView implements Component{

	CounterEntity entity;
	int countValue = 0;
	String scope = "";

	public HLCounterComponent(Context context, ComponentEntity entity) {
		super(context);
		this.setEntity(entity);
		countValue = ((CounterEntity) entity).minValue;
		scope = ((CounterEntity) entity).scope;
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			if(BookController.getInstance().count<0){
				BookController.getInstance().count = countValue;
			}
			this.setText(Integer.toString(BookController.getInstance().count));
		} else {
			this.setText(Integer.toString(countValue));
		}
		if (entity.isHideAtBegining == true) {
			this.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public CounterEntity getEntity() {
		return entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = (CounterEntity) entity;
		countValue = ((CounterEntity) entity).minValue;
	}
	
	public void reset(){
		countValue = ((CounterEntity) entity).minValue;
		scope = ((CounterEntity) entity).scope;
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			//if(BookController.getInstance().count<0){
				BookController.getInstance().count = countValue;
			//}
			this.setText(Integer.toString(BookController.getInstance().count));
		} else {
			this.setText(Integer.toString(countValue));
		}
		if (entity.isHideAtBegining == true) {
			this.setVisibility(View.INVISIBLE);
		}
	}

	public void minus(int value) {
		ArrayList<BehaviorEntity> behaviors = entity.behaviors;

		int eqvalue;
		
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			eqvalue = BookController.getInstance().count;
			if( eqvalue == ((CounterEntity) entity).minValue) {
				return;
			}
			eqvalue = BookController.getInstance().count- value;
			if (eqvalue < ((CounterEntity) entity).minValue) {
				eqvalue = ((CounterEntity) entity).minValue;
			}
			BookController.getInstance().count = eqvalue;
		} else {
			eqvalue = countValue;
			if (eqvalue == ((CounterEntity) entity).minValue) {
				return;
				//countValue = ((CounterEntity) entity).minValue;
			}
			eqvalue  = eqvalue - value;
			if (eqvalue < ((CounterEntity) entity).minValue) {
				eqvalue = ((CounterEntity) entity).minValue;
			}
			
			countValue = eqvalue;

		}

		this.setText(Integer.toString(eqvalue));
		for (BehaviorEntity behavior : behaviors) {
			if (behavior.EventName.equals("BEHAVIOR_ON_COUNTER_NUMBER")) {
				int targetValue = -1;
				if(StringUtils.isEmpty(behavior.EventValue)){
					targetValue = 0;
				}else{
					targetValue = Integer.valueOf(behavior.EventValue);
				}
				if (eqvalue == targetValue) {
					BookController.getInstance().runBehavior(behavior);
				}
			}
		}
	}

	public void plus(int value) {

		ArrayList<BehaviorEntity> behaviors = entity.behaviors;

		int eqvalue;
		
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			eqvalue = BookController.getInstance().count;
			if( eqvalue == ((CounterEntity) entity).maxValue) {
				return;
			}
			eqvalue = BookController.getInstance().count + value;
			if (eqvalue > ((CounterEntity) entity).maxValue) {
				eqvalue = ((CounterEntity) entity).maxValue;
			}
			BookController.getInstance().count = eqvalue;
		} else {
			eqvalue = countValue;
			if (eqvalue == ((CounterEntity) entity).maxValue) {
				return;
				//countValue = ((CounterEntity) entity).minValue;
			}
			eqvalue  = eqvalue + value;
			if (eqvalue > ((CounterEntity) entity).maxValue) {
				eqvalue = ((CounterEntity) entity).maxValue;
			}
			
			countValue = eqvalue;

		}

		this.setText(Integer.toString(eqvalue));
		for (BehaviorEntity behavior : behaviors) {
			if (behavior.EventName.equals("BEHAVIOR_ON_COUNTER_NUMBER")) {
				if (eqvalue == Integer.valueOf(behavior.EventValue)) {
					BookController.getInstance().runBehavior(behavior);
				}
			}
		}
	}

	public int setCounterText(int value) {
		int eqvalue;
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			eqvalue = BookController.getInstance().count;
			if (eqvalue == ((CounterEntity) entity).maxValue) {
				//return;
			}
			eqvalue =  eqvalue + value;
			if (eqvalue > ((CounterEntity) entity).maxValue) {
				eqvalue = ((CounterEntity) entity).maxValue;
			}
			BookController.getInstance().count =eqvalue;
		} else {
			eqvalue = countValue;
			if (countValue == ((CounterEntity) entity).maxValue) {
				//return;
			}
			eqvalue  = eqvalue + value;
			if (eqvalue > ((CounterEntity) entity).maxValue) {
				eqvalue = ((CounterEntity) entity).maxValue;
			}
			countValue = eqvalue;

		}
		this.setText(Integer.toString(eqvalue));
		return eqvalue;
	}
	
	@Override
	public void load() {
		
		try {
			String textColor = entity.fontColor;
			textColor = URLDecoder.decode(textColor);
			int color = Color.BLACK;

			if (!StringUtils.isEmpty(textColor)) {
				if(textColor.startsWith("0x")){
					if(textColor.length()==8){
						int r = Integer.parseInt(textColor.substring(2, 4),16);
						int g = Integer.parseInt(textColor.substring(4, 6),16);
						int b = Integer.parseInt(textColor.substring(6, 8),16);
						color = Color.rgb(r, g, b);
					}
				}else{
					String[] a = textColor.split(";");
					color = Color.rgb(Integer.valueOf(a[0]), Integer.valueOf(a[1]),
							Integer.valueOf(a[2]));
				}
			} 
			setTextColor(color);
			
		} catch (Exception ex) {
		
			setTextColor(Color.BLACK);
		}
		
		float fontSize = Float.parseFloat(entity.fontSize);
		
		fontSize =ScreenUtils.getVerScreenValue(fontSize);
		
		/*int height = getLayoutParams().height;
		height =  (int) (height * BookSetting.RESIZE_HEIGHT);*/
		
		setSingleLine(true);
		setTextSize(fontSize/2);
		//setPadding(0, -10, 0, 0);
		setGravity(Gravity.CENTER);
		
		/*
		float fontSize = Float.parseFloat(entity.fontSize);
				
		fontSize = (float) (fontSize * BookSetting.RESIZE_WIDTH);
		Context c = getContext();
		Resources r;

		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();
		
		float factSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				fontSize, r.getDisplayMetrics());
		
		setTextSize(TypedValue.COMPLEX_UNIT_SP,factSize);*/
		
		if (!StringUtils.isEmpty(scope) && scope.equals("global")) {
			setText(Integer.toString(BookController.getInstance().count));
		} else {
			setText(Integer.toString(entity.minValue));
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
	public AnimationSet animationset = null;


	MyCount1 count = null;

	public class MyCount1 extends CountDownTimer {
		public MyCount1(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			setVisibility(View.VISIBLE);
			startAnimation(animationset);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		this.setVisibility(View.INVISIBLE);

	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);

	}

	@Override
	public void resume() {
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
