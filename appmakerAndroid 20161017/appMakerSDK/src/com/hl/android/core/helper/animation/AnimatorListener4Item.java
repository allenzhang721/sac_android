package com.hl.android.core.helper.animation;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.view.View;

import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.controller.BookController;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.Behavior;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.moudle.slide.HorizontalSlide;

/**
 * 属性动画中单个动画播放的事件监听器
 * 在这个事件监听类中需要做如下事情
 * 本类只在AnimationHelper中的播放高级动画的时候被调用
 * 1在动画开始时触发单个动画的开始事件
 * 2动画结束时触发单个动画的结束事件
 * 3如果是图片组件对象，那就是需要在动画被cancel的时候将当前的动画顺序设置给图片组件
 * @author zhaoq
 * @version 1.0 create at 
 */
@SuppressLint("NewApi")
public class AnimatorListener4Item implements Animator.AnimatorListener {
	private ViewCell mView;
	private int mIndex = 0;
	private ComponentEntity entity;
	private AnimationEntity animationEntity;
	private ViewRecord mRecord;
	private boolean isCancel = false;
	
	private AnimationKey animationKey;
	
	public void setRecord(ViewRecord record){
		mRecord = record.getClone();
	}
	//记录动画开始播放的时间
	/**
	 * 可以传入的任何组件对象
	 * @param v  组件对象作为一个view来传递进来
	 * @param index 当前动画所在的整个动画集合中的位置，这个参数是为了确定触发单个动画事件的位置所以要穿进进来
	 */
	public AnimatorListener4Item(ViewCell v, int index) {
		mView = v;
		mIndex = index;
		entity = mView.getEntity();
		animationEntity = entity.getAnims().get(mIndex);
		animationKey = new AnimationKey(v,index); 
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		isCancel = true;
//		//目前只支持图片组件，如果需要支持更多就需要大范围的修改整个组件对象，留给follower来做吧
//		if(mComponent instanceof ImageComponent){
//			((ImageComponent)mComponent).animationIndex = mIndex;
//			//整个动画结束以后，需要将组建对象的当前动画个数重置，并将动画集合对象从容器中移出
//		}
	}
	
	/**
	 * it will trigger follow behaviors
	 * BEHAVIOR_ON_ANIMATION_END
	 * BEHAVIOR_ON_ANIMATION_END_AT
	 */
	@Override
	public void onAnimationEnd(Animator animation) {
		//防止沿Y轴旋转时被viewcell遮挡，动画结束时回到原缩放比例
//		if(animationEntity.AnimationType.equals("ANIMATION_ROTATEIN")||animationEntity.AnimationType.equals("ANIMATION_ROTATEOUT")){
//			mView.setScaleX(mRecord.mScaleX);
//			mView.setScaleY(mRecord.mScaleY);
//			((View)(mView.getComponent())).setScaleX(1.0f);
//			((View)(mView.getComponent())).setScaleY(1.0f);
//		}
//		AnimationHelper.animatiorMap.remove(mView);
		if(isCancel){
			isCancel = false;
			return;
		}
		//it will not used in current version,but for previous version
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_ANIMATION_END);

//		if(AnimationHelper.animatiorMap.containsKey(animationKey)){
//			if(AnimationHelper.animatiorMap.get(animationKey) == animation){
//				AnimationHelper.animatiorMap.remove(animationKey);
//			}
//		}
		BookController.getInstance().runBehavior(entity,Behavior.BEHAVIOR_ON_ANIMATION_END_AT, Integer.toString(mIndex));
		//如果最终的alpha状态0的话就将visible设置成隐藏，并且将状态重置
		if(animationEntity.AnimationType.contains("WIPEOUT")){
			mView.setPadding(0, 0, 0, 0);
			if(Boolean.parseBoolean(animationEntity.IsKeep)&&"OUT_FLAG".equals(animationEntity.AnimationEnterOrQuit)){
				mView.setVisibility(View.INVISIBLE);
			}
			return;
		}
		//如果不保持最终状态，就需要恢复到record的状态
		if(!Boolean.parseBoolean(animationEntity.IsKeep)){
			mView.setPadding(0, 0, 0, 0);
			mView.setX(mRecord.mX);
			mView.setY(mRecord.mY);
			mView.setAlpha(mView.getEntity().alpha);
			mView.setSuperRotation(mRecord.mRotation);
			mView.setScaleX(mRecord.mScaleX);
			mView.setScaleY(mRecord.mScaleY);
		}
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}
	/**
	 * it will trigger follow behaviors
	 * BEHAVIOR_ON_ANIMATION_PLAY
	 * BEHAVIOR_ON_ANIMATION_PLAY_AT
	 */
	@Override
	public void onAnimationStart(Animator animation) {
		if(mView.getVisibility() != View.VISIBLE){
			mView.show();
		}
		//防止在沿Y轴旋转时被viewcell遮挡
		if(animationEntity.AnimationType.equals("ANIMATION_ROTATEIN")||animationEntity.AnimationType.equals("ANIMATION_ROTATEOUT")){
			mView.setScaleX(mRecord.mScaleX*1.1f);
			mView.setScaleY(mRecord.mScaleY*1.1f);
			((View)(mView.getComponent())).setScaleX(0.909f);
			((View)(mView.getComponent())).setScaleY(0.909f);
		}
		//之所以在单个动画事件中设置显示，除了考虑播放整个动画和单个动画之外，还是因为在整体动画中设置显示，会比单个动画的开始延时一部分
		//会造成图片先显示，后播放动画的不正常现象
		if(((View)mView.getComponent()).getVisibility()!=View.VISIBLE){//此处处理是解决死循环问题（否则由show时触发的动画会出现死循环）
			mView.show();
		}
		//it will not used in current version,but for previous version
		BookController.getInstance().runBehavior(entity,
				Behavior.BEHAVIOR_ON_ANIMATION_PLAY);
		BookController.getInstance().runBehavior(entity,Behavior.BEHAVIOR_ON_ANIMATION_PLAY_AT, Integer.toString(mIndex));
	}
}
