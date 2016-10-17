package com.hl.android.book.entity;

import java.util.ArrayList;

import android.graphics.PointF;
import android.view.animation.Animation;

public class ComponentEntity {
	public Boolean isMoveScale = false;
	public String imageType;
	public double imageScale;
	public String localSourceId;
	public String downSourceID;
	public boolean isSynchronized;
	public String className;
	public boolean autoLoop;
	public String multiMediaUrl;
	public boolean isAllowUserZoom;
	public String htmlUrl;
	public double delay;
	public float rotation;
	public ArrayList<AnimationEntity> anims;
	public boolean isPlayAnimationAtBegining;
	public boolean isPlayVideoOrAudioAtBegining;
	public boolean isHideAtBegining;
	public ArrayList<BehaviorEntity> behaviors;
	public boolean IsEnableGyroHor=false;
	private boolean isOnlineSource=false;
	public int x;
	public int y;
	
	public int xOffset = 0;
	public int yOffset = 0;
	
	public String componentId;
	public float alpha=1.0f;
	
	public float oldWidth;
	public float oldHeight;
	
//	public ArrayList<Point> pointList; 
	public boolean isStroyTelling;
	public boolean isPushBack;
	
	public boolean showProgress = false;
	
	public int animationRepeat=1;
	public int currentPlayTime = 0;
	public int currentRepeat = 0;
	public Animation currentPlayingAnim;
	public int currentPlayingIndex;
	public boolean isPause;
	
	public String zoomType = "zoom_out";

	public ArrayList<PointF> ptList = new ArrayList<PointF>();
	/**
	 * 关联对象 指定的对象发生size和location发生变化的时候，本对象也要发生变化的数据对象
	 */
	private LinkageObj linkageObj;

	/************页面关联滑动的属性 start by zhaoq**********/
	public boolean isPageInnerSlide = false;//是否是页面内部滑动
	public boolean isPageTweenSlide = false;//是否是页面间滑动
	public int slideBindingX = 0;//滑动目标x坐标
	public int slideBindingY = 0;//滑动目标Y坐标
	public int slideBindingWidth = 0;//滑动目标宽度
	public int slideBindingHeight = 0;//滑动目标高度
	public float slideBindingAlha = 1.0f;//滑动目标透明度
	

	public float sliderHorRate = 0.0f;//滑动目标高度
	public float sliderVerRate = 1.0f;//滑动目标透明度
	/************页面关联滑动的属性 end by zhaoq**********/
	
	public LinkageObj getLinkPageObj() {
		if (linkageObj == null)
			linkageObj = new LinkageObj();
		return linkageObj;
	}
	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public double getImageScale() {
		return imageScale;
	}

	public void setImageScale(double imageScale) {
		this.imageScale = imageScale;
	}
	
	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public void setOnlineSource(boolean isOnlineSource) {
		this.isOnlineSource = isOnlineSource;
	}
	
	public boolean isOnlineSource() {
		return isOnlineSource;
	}
/*	public float getMaxAnimDuration(){
		if(anims == null || anims.size() == 0)return 0;
		float result = 0;
		for(AnimationEntity e:anims){
			float sumDuration = e.getSumDuration();
			if(result<sumDuration)result = sumDuration;
		}
		
		return result;
	}
	*/
	public ArrayList<AnimationEntity> getAnims() {
		return anims;
	}

	public void setAnims(ArrayList<AnimationEntity> anims) {
		this.anims = anims;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public ComponentEntity() {
		this.isAllowUserZoom = false;
	}

	public String getLocalSourceId() {
		return localSourceId;
	}

	public void setLocalSourceId(String localSourceId) {
		this.localSourceId = localSourceId;
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isAutoLoop() {
		return autoLoop;
	}

	public void setAutoLoop(boolean autoLoop) {
		this.autoLoop = autoLoop;
	}

	public String getMultiMediaUrl() {
		return multiMediaUrl;
	}

	public void setMultiMediaUrl(String multiMediaUrl) {
		this.multiMediaUrl = multiMediaUrl;
	}

	public boolean isAllowUserZoom() {
		return isAllowUserZoom;
	}

	public void setAllowUserZoom(boolean isAllowUserZoom) {
		this.isAllowUserZoom = isAllowUserZoom;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	
	
//	public ArrayList<Point> getPointList() {
//		return pointList;
//	}
//
//	public void setPointList(ArrayList<Point> pointList) {
//		this.pointList = pointList;
//	}

//	public boolean isVideo(){
//		if (this.getClassName().equals(ComponentHelper.COMPONENT_VIDEO_CLASS)){
//			return true;
//		}else{
//			return false;
//		}
//	}
}
