package com.hl.android.core.helper.animation;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;

import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.SeniorAnimationEntity;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.core.utils.StringUtils;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.bean.ViewRecord;

/**
 * this is animation factory it create animation or animators from
 * AnimationEntity
 * 
 * @author zhaoq
 * @version 1.0
 * @createed 2013-8-19
 */
@SuppressLint("NewApi")
public class AnimationFactory {
	
	/**
	 * 增加动画元素
	 * @param v
	 * @param entity
	 * @param record 初始化视图记录，需要根据动画bean来判断起始点是什么
	 * @return
	 */
	public static ObjectAnimator getAnimator(ViewCell v, AnimationEntity entity,
			ViewRecord record) {
		String animationType = entity.AnimationType;
		int index = v.getEntity().getAnims().indexOf(entity);
		AnimatorListener4Item listener = new AnimatorListener4Item(v, index);
		listener.setRecord(record);
		//擦出动画使用的标志位
		boolean isout = "OUT_FLAG".equals(entity.AnimationEnterOrQuit);
		boolean iskeep = Boolean.parseBoolean(entity.IsKeep);

		if (StringUtils.isEmpty(animationType)) {
			animationType = "";
		}
		int cnt = Integer.parseInt(entity.Repeat);
		cnt--;
		long delay = Long.parseLong(entity.Delay);
		long duration = Long.parseLong(entity.Duration);
		ObjectAnimator animator = null;
		
		if (animationType.equals("ANIMATION_FADEOUT")) {
			animator = ObjectAnimator.ofFloat(v, "alpha", v.getEntity().alpha, 0.0f);
//			if(iskeep)record.mAlpha = 0.0f;
		} else if (animationType.equals("ANIMATION_FADEIN")) {
			animator = ObjectAnimator.ofFloat(v, "alpha", 0.0f, v.getEntity().alpha);
		} else if (animationType.equals("MOVE_UP")) {
			float dis = Float.valueOf(entity.CustomProperties);
			animator = ObjectAnimator.ofFloat(v, "y", record.mY, record.mY
					- ScreenUtils.getVerScreenValue(dis));
			if(iskeep)record.mY = record.mY - ScreenUtils.getVerScreenValue(dis);
		} else if (animationType.equals("MOVE_DOWN")) {
			float dis = Float.valueOf(entity.CustomProperties);
			animator = ObjectAnimator.ofFloat(v, "y", record.mY, record.mY
					+ ScreenUtils.getVerScreenValue(dis));
			if(iskeep)record.mY = record.mY + ScreenUtils.getVerScreenValue(dis);
		} else if (animationType.equals("MOVE_LEFT")) {
			float dis = Float.valueOf(entity.CustomProperties);
			animator = ObjectAnimator.ofFloat(v, "x", record.mX, record.mX
					- ScreenUtils.getHorScreenValue(dis));
			if(iskeep)record.mX = record.mX - ScreenUtils.getHorScreenValue(dis);
		} else if (animationType.equals("MOVE_RIGHT")) {
			float dis = Float.valueOf(entity.CustomProperties);
			animator = ObjectAnimator.ofFloat(v, "x", record.mX, record.mX
					+ ScreenUtils.getHorScreenValue(dis));
			if(iskeep)record.mX = record.mX + ScreenUtils.getHorScreenValue(dis);
		} else if (animationType.equals("ANIMATION_ZOOMOUT")) {
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"scaleX", 0.1f, record.mScaleX);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"scaleY", 0.1f, record.mScaleY);

			PropertyValuesHolder scaleAlpha = PropertyValuesHolder.ofFloat(
					"alpha",0f, v.getEntity().alpha);
			animator = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY,scaleAlpha);
		} else if (animationType.equals("ANIMATION_ZOOMIN")) {
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"scaleX", record.mScaleX, 0f);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"scaleY", record.mScaleY, 0f);

			PropertyValuesHolder scaleAlpha = PropertyValuesHolder.ofFloat(
					"alpha",v.getEntity().alpha, 0f);
//			if(iskeep)record.mAlpha = 0f;
			animator = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY,scaleAlpha);
		} else if (animationType.equals("ANIMATION_SPIN")) {
			float rotate = Float.valueOf(entity.CustomProperties);
			animator = ObjectAnimator.ofFloat(v, "rotation", 0, rotate);
			if(iskeep)record.mRotation = rotate%360;
		}else if (animationType.equals("WIPEOUT_LEFT")) {
			if(isout)animator = ObjectAnimator.ofInt(v, "leftPadding", 0, record.mWidth);
			else animator = ObjectAnimator.ofInt(v, "leftPadding", record.mWidth, 0);
		}else if (animationType.equals("WIPEOUT_UP")) {
			if(isout) animator = ObjectAnimator.ofInt(v, "topPadding", 0,record.mHeight);
			else animator = ObjectAnimator.ofInt(v, "topPadding", record.mHeight, 0);
		}else if (animationType.equals("WIPEOUT_DOWN")) {
			if(isout)animator = ObjectAnimator.ofInt(v, "bottomPadding", 0, record.mHeight);
			else animator = ObjectAnimator.ofInt(v, "bottomPadding",record.mHeight, 0);
		}else if (animationType.equals("WIPEOUT_RIGHT")) {
			if(isout)animator = ObjectAnimator.ofInt(v, "rightPadding", 0, record.mWidth);
			else animator = ObjectAnimator.ofInt(v, "rightPadding", record.mWidth, 0);
		}else if (animationType.startsWith("FLOATIN")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					0f, v.getEntity().alpha);
			float value = Float.parseFloat(entity.CustomProperties);
			PropertyValuesHolder pos = null;
			if (animationType.contains("UP")) {
				pos = PropertyValuesHolder.ofFloat("y",
				record.mY + ScreenUtils.getVerScreenValue(value), record.mY);
			} else if (animationType.contains("DOWN")) {
				pos = PropertyValuesHolder.ofFloat("y",
				record.mY - ScreenUtils.getVerScreenValue(value), record.mY);
			} else if (animationType.contains("LEFT")) {
				pos = PropertyValuesHolder.ofFloat("x",
				record.mX + ScreenUtils.getHorScreenValue(value), record.mX);
			} else if (animationType.contains("RIGHT")) {
				pos = PropertyValuesHolder.ofFloat("x",
				record.mX - ScreenUtils.getHorScreenValue(value), record.mX);
			}
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha, pos);
		} else if (animationType.startsWith("FLOATOUT")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					v.getEntity().alpha, 0f);
			float value = Float.parseFloat(entity.CustomProperties);
			PropertyValuesHolder pos = null;
			if (animationType.contains("UP")) {
				pos = PropertyValuesHolder.ofFloat("y", record.mY, record.mY
						- ScreenUtils.getVerScreenValue(value));
				if(iskeep)record.mY = record.mY - ScreenUtils.getVerScreenValue(value);
			} else if (animationType.contains("DOWN")) {
				pos = PropertyValuesHolder.ofFloat("y", record.mY, record.mY
						+ ScreenUtils.getVerScreenValue(value));
				if(iskeep)record.mY = record.mY + ScreenUtils.getVerScreenValue(value);
			} else if (animationType.contains("RIGHT")) {
				pos = PropertyValuesHolder.ofFloat("x", record.mX, record.mX
						+ ScreenUtils.getHorScreenValue(value));
				if(iskeep)record.mX = record.mX + ScreenUtils.getHorScreenValue(value);
			} else if (animationType.contains("LEFT")) {
				pos = PropertyValuesHolder.ofFloat("x", record.mX, record.mX
						- ScreenUtils.getHorScreenValue(value));
				if(iskeep)record.mX = record.mX - ScreenUtils.getHorScreenValue(value);
			}
//			if(iskeep)record.mAlpha=0f;
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha, pos);
		} else if (animationType.equals("ANIMATION_ROTATEIN")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					0f, v.getEntity().alpha);
			PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat(
					"myRotationY", -180f, 360f);
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
					rotationY);
		} else if (animationType.equals("ANIMATION_ROTATEOUT")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					v.getEntity().alpha, 0f);
			PropertyValuesHolder rotationY = PropertyValuesHolder.ofFloat(
					"myRotationY", 360f, -180f);
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
					rotationY);
//			if(iskeep)record.mAlpha=0f;
		} else if (animationType.equals("ANIMATION_TURNIN")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					0f, v.getEntity().alpha);
			PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(
					"rotation", 90f, 0f);
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"scaleX", 0.1f, record.mScaleX);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"scaleY", 0.1f, record.mScaleY);
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
					rotation, scaleX, scaleY);
		} else if (animationType.equals("ANIMATION_TURNOUT")) {
			PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha",
					v.getEntity().alpha, 0f);
			PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(
					"rotation", 0f, 90f);
			PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(
					"scaleX", record.mScaleX, 0.1f);
			PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(
					"scaleY", record.mScaleY, 0.1f);
			animator = ObjectAnimator.ofPropertyValuesHolder(v, alpha,
					rotation, scaleX, scaleY);
//			if(iskeep)record.mAlpha=0f;
		} else if (animationType.equals("ANIMATION_SEESAW")) {
			animator = ObjectAnimator.ofFloat(v, "rotation", 0f, 15f, 0f, -15f,
					0f, 15f, 0f, -15f, 0);
		} else if (animationType.startsWith("SCALE")) {
			float scale = 1.0f;
			String properties = entity.CustomProperties;
			if (properties.equals("Min")) {
				scale = 0.3f;
			} else if (properties.equals("MinMax")) {
				scale = 0.16f;
			} else if (properties.equals("Max")) {
				scale = 3.0f;
			} else if (properties.equals("MaxMax")) {
				scale = 6.0f;
			} else {
				scale = Float.valueOf(properties);
			}

			PropertyValuesHolder scaleValueX = null;// PropertyValuesHolder.ofFloat("scaleX",
													// 1f,scale);
			PropertyValuesHolder scaleValueY = null;
			if (animationType.endsWith("ALL")) {
				scaleValueX = PropertyValuesHolder.ofFloat("scaleX",
						record.mScaleX, record.mScaleX * scale);
				scaleValueY = PropertyValuesHolder.ofFloat("scaleY",
						record.mScaleY, record.mScaleY * scale);
				if(iskeep)record.mScaleX = record.mScaleX * scale;
				if(iskeep)record.mScaleY = record.mScaleY * scale;

				animator = ObjectAnimator.ofPropertyValuesHolder(v,
						scaleValueX, scaleValueY);
			} else if (animationType.endsWith("HORZ")) {
				scaleValueX = PropertyValuesHolder.ofFloat("scaleX",
						record.mScaleX, record.mScaleX * scale);
				
				animator = ObjectAnimator
						.ofPropertyValuesHolder(v, scaleValueX);
				if(iskeep)record.mScaleX = record.mScaleX * scale;
			} else {
				scaleValueY = PropertyValuesHolder.ofFloat("scaleY",
						record.mScaleY, record.mScaleY * scale);

				animator = ObjectAnimator
						.ofPropertyValuesHolder(v, scaleValueY);
				if(iskeep)record.mScaleY = record.mScaleY * scale;
			}
		}else if(animationType.equals("ANIMATION_SENIOR")){
			animator = getSeniorAnimator(v, entity, record);
		 } else {
			return null;
		}
		animator.setRepeatCount(cnt);
		animator.setDuration(duration);
		animator.setStartDelay(delay);
//		animator.setEvaluator();
		
		animator.addListener(listener);
		animator.setInterpolator(new HLInterpolator(entity.EaseType));
		//增加监听事件
		//如果返回为空则说明该控件不支持暂停事件
		if(v.getAnimatorUpdateListener()!=null){
			animator.addUpdateListener(v.getAnimatorUpdateListener());
		}
		
		return animator;
	}
	
 

	/**
	 * 高级动画的生成方法
	 * @param v  组件对象
	 * @param entity  动画entity
	 * @param record  组件对象状态记录类
	 * @param floatEvaleuator 
	 * @return
	 */
	private static ObjectAnimator getSeniorAnimator(ViewCell v,
			AnimationEntity entity, ViewRecord record){
		ViewRecord recordCopy=record.getClone();
		//总区间
		long duration  = Long.parseLong(entity.Duration);
		
		//alpha
		Keyframe[] alphaList = new Keyframe[entity.hEntitys.size()+1];
		//rotate
		Keyframe[] rotateList = alphaList.clone();
		//x
		Keyframe[] xList = alphaList.clone();
		//y
		Keyframe[] yList = alphaList.clone();
		//width
		Keyframe[] widthList = alphaList.clone();
		//height
		Keyframe[] heightList = alphaList.clone();
		//动画的时间点
		float framePoint = 0.0f;
		//初始化第一个点的时间和位置
		Keyframe alphaFrame = Keyframe.ofFloat(framePoint,v.getEntity().alpha);
		alphaList[0] = alphaFrame;

		Keyframe rotateFrame = Keyframe.ofFloat(framePoint,recordCopy.mRotation);
		rotateList[0] = rotateFrame;

		Keyframe xFrame = Keyframe.ofFloat(framePoint,recordCopy.mX);
		xList[0] = xFrame;

		Keyframe yFrame = Keyframe.ofFloat(framePoint,recordCopy.mY);
		yList[0] = yFrame;

		Keyframe widthFrame = Keyframe.ofFloat(framePoint,recordCopy.mScaleX);
		widthList[0] = widthFrame;

		Keyframe heightFrame = Keyframe.ofFloat(framePoint,recordCopy.mScaleY);
		heightList[0] = heightFrame;
		int time = 0;
		
		float lastCenterX  = recordCopy.mX+v.getLayoutParams().width/2;
		float lastCenterY = recordCopy.mY+v.getLayoutParams().height/2;
		
		//增加动画的设置点，同时计算开始时间位置点
		for(int index = 0;index<entity.hEntitys.size();index++){
			SeniorAnimationEntity animEntity = entity.hEntitys.get(index);
			float ratioMX=ScreenUtils.getHorScreenValue(animEntity.mX);
			float ratioMY=ScreenUtils.getVerScreenValue(animEntity.mY);
			float ratioMWidth=ScreenUtils.getHorScreenValue(animEntity.mWidth);
			float ratioMHeight=ScreenUtils.getVerScreenValue(animEntity.mHeight);
			
			time +=animEntity.mDuration;
			framePoint = (float)time/(float)duration;
			
			alphaFrame = Keyframe.ofFloat(framePoint, animEntity.mAlpha);
			alphaList[index+1] = alphaFrame;
//			record.mAlpha = animEntity.mAlpha;

			rotateFrame = Keyframe.ofFloat(framePoint, animEntity.mDegree);
			recordCopy.mRotation = animEntity.mDegree;
			rotateList[index+1] = rotateFrame;
			
			float ratio = ratioMWidth/v.getLayoutParams().width;
			widthFrame = Keyframe.ofFloat(framePoint, ratio);
			widthList[index+1] = widthFrame;
			recordCopy.mScaleX = ratio;

			ratio = ratioMHeight/v.getLayoutParams().height;
			heightFrame = Keyframe.ofFloat(framePoint, ratio);
			heightList[index+1] = heightFrame;
			recordCopy.mScaleY = ratio;
			
			float nX = (float) (ratioMX - ratioMHeight / 2//计算没有旋转角度的x点坐标
					* Math.sin(animEntity.mDegree * Math.PI / 180) - ratioMWidth / 2 + ratioMWidth / 2
					* Math.cos(animEntity.mDegree * Math.PI / 180));
			float nY = (float) (ratioMY + ratioMWidth / 2//计算没有旋转角度的y点坐标
					* Math.sin(animEntity.mDegree * Math.PI / 180) - ratioMHeight / 2 + ratioMHeight / 2
					* Math.cos(animEntity.mDegree * Math.PI / 180));
			
			nX+=ratioMWidth/2;
			nY+=ratioMHeight/2;
			nX-=lastCenterX;
			nY-=lastCenterY;
			
			xFrame = Keyframe.ofFloat(framePoint, recordCopy.mX + nX);
			recordCopy.mX = recordCopy.mX + nX;
			xList[index+1] = xFrame;
			
			yFrame = Keyframe.ofFloat(framePoint, recordCopy.mY + nY);
			recordCopy.mY =  recordCopy.mY + nY;
			yList[index+1] = yFrame;

			lastCenterX += nX;
			lastCenterY += nY;
		}
		
		PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofKeyframe("alpha", alphaList);
		PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe("superRotation", rotateList);
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("x",xList);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("y",yList);
		PropertyValuesHolder pvhWidth = PropertyValuesHolder.ofKeyframe("scaleX", widthList);
		PropertyValuesHolder pvhHeight = PropertyValuesHolder.ofKeyframe("scaleY", heightList);

		ObjectAnimator animator= ObjectAnimator.ofPropertyValuesHolder(v,
				pvhAlpha,pvhRotate,pvhX,pvhY,pvhWidth,pvhHeight);
		return animator;
	}
}
