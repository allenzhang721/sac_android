package com.hl.android.core.helper;

import java.util.ArrayList;
import java.util.HashMap;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.os.CountDownTimer;

import com.hl.android.book.entity.AnimationEntity;
import com.hl.android.common.BookSetting;
import com.hl.android.core.helper.animation.AnimationFactory;
import com.hl.android.core.helper.animation.AnimationKey;
import com.hl.android.view.ViewCell;
import com.hl.android.view.component.ComponentListener;
import com.hl.android.view.component.bean.ViewRecord;


public class AnimationHelper {
	//用来标识使用新动画还是老的动画，过渡时期，如果是false则执行旧的动画
	public static HashMap<AnimationKey,Animator> animatiorMap = new HashMap<AnimationKey,Animator>();
	
	
	/**
	 * 执行指定位置的动画
	 * @param component  要被执行动画的组件视图
	 * @param index 动画的顺序
	 */
	public static void playAnimationAt(ViewCell v,int index){
		if(BookSetting.IS_CLOSED){
			animatiorMap.clear();
			return;
		}
		
		if (index >= v.getEntity().getAnims().size() || index < 0)
			return ;
		
		v.getAnimatorUpdateListener().mStop = false;
		
		AnimationKey animationKey = new AnimationKey(v,index);
		Animator animator = animatiorMap.get(animationKey);
		//先判断暂停，如果已经暂停，并且暂停的动画就是自己，则继续播放并返回
		if(v.getAnimatorUpdateListener()!= null){
			if(v.getAnimatorUpdateListener().isPause()){
				if(animator == v.mAnimator){
					v.getAnimatorUpdateListener().play();
				}
				return;
			}
		};
		//将正在播放的动画cancel
		if(v.mAnimator != null && v.mAnimator.isRunning()){
			v.mAnimator.cancel();
		}
		
		AnimationEntity animationEntity = v.getEntity().getAnims().get(index);
		
		//播放时是需要重置
//		if(){
//			v.resetViewCell();
//		}
		if (animatiorMap.containsKey(animationKey) && (!animationEntity.isKeepEndStatus || !Boolean.parseBoolean(animationEntity.IsKeep))) {
			animator = animatiorMap.get(animationKey);
		} else {
			
			animator =  AnimationFactory.getAnimator(v, v.getEntity().getAnims().get(index), v.getViewRecord());
			if (animator == null)return;
			animatiorMap.put(animationKey, animator);
		}
		v.mAnimator = animator;
		animator.start(); 
	}
 
	/**
	 * 停止播放动画，目前仅支持属性动画
	 * @param viewCell
	 */
	public static void stopAnimation(ViewCell viewCell){
		if(viewCell.getAnimatorUpdateListener()!=null){
			viewCell.getAnimatorUpdateListener().play();
		}
		
		Animator animator = viewCell.mAnimator;
		if(animator !=null){
//			animatiorMap.remove(component);
			animator.cancel();
			if(viewCell.getAnimatorUpdateListener()!=null){
				viewCell.getAnimatorUpdateListener().mStop = true;
			}
		}
		viewCell.resetViewCell();
	}
	
	/**
	 * 暂停动画，目前仅支持图片组件的属性动画
	 * @param component
	 */
	public static void pauseAnimation(ViewCell component){
		Animator animator = component.mAnimator;
		if(animator==null)return;
		if(!animator.isRunning())return;
		if(component.getAnimatorUpdateListener()!= null){
			component.getAnimatorUpdateListener().pause();
		}
	}
	
	 
	/**
	 * 播放动画
	 * @param component
	 */
	public static void playAnimation(ViewCell viewCell) {
		if(BookSetting.IS_CLOSED){
			animatiorMap.clear();
			return;
		}
//		if(viewCell.getVisibility() != View.VISIBLE){
//			return;
//		}
		if(viewCell.getAnimatorUpdateListener() == null)return;
		
		viewCell.getAnimatorUpdateListener().mStop = false;
		
		AnimationKey animationKey = new AnimationKey(viewCell);
		Animator animator = animatiorMap.get(animationKey);
		//先判断暂停，如果已经暂停，并且暂停的动画就是整体动画，则继续播放并返回
		if(viewCell.getAnimatorUpdateListener()!= null){
			if(viewCell.getAnimatorUpdateListener().isPause() && animator == viewCell.mAnimator){
				viewCell.getAnimatorUpdateListener().play();
				return;
			}
		};
		//其他状态下，都需要将view的状态进行重置，并重新播放动画
		if(animator != null){
//			stopAnimation(viewCell);
			if(viewCell.getAnimatorUpdateListener()!=null){
				viewCell.getAnimatorUpdateListener().play();
			}
			Animator animator1 = viewCell.mAnimator;
			if(animator1 !=null){
//				animatiorMap.remove(component);
				animator1.cancel();
				viewCell.getAnimatorUpdateListener().mStop = true;
			}
			viewCell.resetViewCell();
		}
		
		if (animatiorMap.containsKey(animationKey)) {
			animator = animatiorMap.get(animationKey);
		} else {
			// 1计算各个节点的数据属性
			AnimatorSet animatorSet = new AnimatorSet();
			ArrayList<Animator> animatorList = new ArrayList<Animator>();
			// 获得组件播放前的状态，由于动画的各个属性是事前预制好的，所以需要记录每个动画开始和结束的动画状态
			for (int i=0;i<viewCell.getEntity().getAnims().size();i++) {
				AnimationEntity entity =viewCell.getEntity().getAnims().get(i);
				Animator aniator = AnimationFactory.getAnimator(
						 viewCell, entity, viewCell.getViewRecord());
				if (aniator == null)
					continue;
				if (animatorList != null)
					animatorList.add(aniator);
				AnimationKey animationKeyat = new AnimationKey(viewCell,i);
				if (!animatiorMap.containsKey(animationKeyat)) {
					animatiorMap.put(animationKeyat, aniator);
				}
				
			}
			animatorSet.addListener(new AnimatorSetListener(viewCell));
			animatorSet.playSequentially(animatorList);
			animatiorMap.put(animationKey, animatorSet);
			animator = animatorSet;
		}
		viewCell.mAnimator = animator;
		animator.start();
	}
 
 
	 
	public static class AnimatorSetListener implements AnimatorListener{
		public ViewCell mComponent;
		boolean iscancel = false;
		public AnimatorSetListener(ViewCell component){
			mComponent = component;
			mComponent.getEntity().currentRepeat = mComponent
					.getEntity().animationRepeat;
		}
		@Override
		public void onAnimationStart(Animator animation) {
			iscancel =false;
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (iscancel){
				animatiorMap.remove(mComponent);
				return;
			}
			if(mComponent.getComponent() instanceof ComponentListener){
				ComponentListener c = (ComponentListener) mComponent.getComponent();
				c.callBackListener();
			}

			// 结束时需要将整个组件对象的播放次数进行控制，因为组件对象也有自己的播放次数
			if (mComponent.getEntity().currentRepeat != 1) {
				if (mComponent.getEntity().currentRepeat != 0)
					mComponent.getEntity().currentRepeat--;
				
				new CountDownTimer(100,100) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onFinish() {
						playAnimation(mComponent);
					}
				}.start();
				
			} else {
//				animatiorMap.remove(new AnimationKey(mComponent));
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {
			iscancel = true;
		}
		
	}
}
