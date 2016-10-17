package com.hl.android.book.entity;

import java.util.ArrayList;

public class ContainerEntity {
	public String name;
	public String ID;
	public float rotation;
	public float x;
	public float y;
	public float width;
	public float height;
	public boolean isPlayAnimationAtBegining;
	public boolean isPlayVideoOrAudioAtBegining;
	public boolean isHideAtBegining;
	public boolean autoLoop;
	public ComponentEntity component;
	public ArrayList<AnimationEntity> animations;
	public ArrayList<BehaviorEntity> behaviors;
	
	public boolean IsStroyTelling;
	public boolean isPushBack = false;
	public int minValue;
	public int maxValue;
	//移动时是否会放大
	public Boolean isMoveScale;
	public ContainerEntity(){
		this.component=new ComponentEntity();
		this.animations=new ArrayList<AnimationEntity>();
		this.behaviors=new ArrayList<BehaviorEntity>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public float getRotation() {
		return rotation;
	}
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public boolean isPlayAnimationAtBegining() {
		return isPlayAnimationAtBegining;
	}
	public void setPlayAnimationAtBegining(boolean isPlayAnimationAtBegining) {
		this.isPlayAnimationAtBegining = isPlayAnimationAtBegining;
	}
	public boolean isPlayVideoOrAudioAtBegining() {
		return isPlayVideoOrAudioAtBegining;
	}
	public void setPlayVideoOrAudioAtBegining(boolean isPlayVideoOrAudioAtBegining) {
		this.isPlayVideoOrAudioAtBegining = isPlayVideoOrAudioAtBegining;
	}
	public boolean isHideAtBegining() {
		return isHideAtBegining;
	}
	public void setHideAtBegining(boolean isHideAtBegining) {
		this.isHideAtBegining = isHideAtBegining;
	}
	public ComponentEntity getComponent() {
		return component;
	}
	public void setComponent(ComponentEntity component) {
		this.component = component;
	}
	public ArrayList<AnimationEntity> getAnimations() {
		return animations;
	}
	public void setAnimations(ArrayList<AnimationEntity> animations) {
		this.animations = animations;
	}
	public ArrayList<BehaviorEntity> getBehaviors() {
		return behaviors;
	}
	public void setBehaviors(ArrayList<BehaviorEntity> behaviors) {
		this.behaviors = behaviors;
	}
	
}
