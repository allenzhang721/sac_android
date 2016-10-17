package com.hl.android.core.helper.animation;

import android.view.animation.Interpolator;
/**
 * 加速器
 * 软件端提供easetype，来决定使用哪个算法
 * @author wangdayong
 * @version 1.0
 * @createed 2013-8-20
 */
public class HLInterpolator implements Interpolator ,Cloneable{
	/**
	 * 加速类型
	 */
	private String mEaseType="";
	public HLInterpolator(String easeType) {
		if(easeType!=null){
			mEaseType=easeType;
		}
	}

	@Override
	public float getInterpolation(float input) {
		float result=input;
		if(mEaseType.equals("AnimationEaseType_EaseInQuad")){
			result = (float) Math.pow(input, 2);
		}else if(mEaseType.equals("AnimationEaseType_EaseOutQuad")){
			result = 2*input - (float) Math.pow(input, 2);
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInOutQuad")){
			if(input<=0.5){
				result= 2*(float) Math.pow(input, 2);
			}else{
				result= (float) (-1/2.0*((2*input-1)*(2*input-3)-1));
			}
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInCubic")){
			result= (float) Math.pow(input, 3);
		}else if(mEaseType.equals("AnimationEaseType_EaseOutCubic")){
			result=  (float) Math.pow(input-1, 3)+1;
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInOutCubic")){
			if(input<=0.5){
				result= (float) (4*Math.pow(input, 3));
			}else{
				result= (float) (1/2.0*(Math.pow(2*input-2, 3)+2));
			}
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInQuart")){
			result=  (float) Math.pow(input, 4);
		}else if(mEaseType.equals("AnimationEaseType_EaseOutQuart")){
			result=  (float) -(Math.pow(input-1, 4)-1);
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutQuart")){
			if(input<=0.5){
				result= (float) (8*Math.pow(input, 4));
			}else{
				result= (float) (-1/2.0*(Math.pow(2*input-2, 4)-2));
			}
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInQuint")){
			result=  (float) Math.pow(input, 5);
		}else if(mEaseType.equals("AnimationEaseType_EaseOutQuint")){
			result=  (float) Math.pow(input-1, 5)+1;
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutQuint")){
			if(input<=0.5){
				result= (float) (16*Math.pow(input, 5));
			}else{
				result= (float) (1/2.0*(Math.pow(2*input-2, 5)+2));
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInSine")){
			result= (float) (1-Math.cos(input*Math.PI/2.0));
		}else if(mEaseType.equals("AnimationEaseType_EaseOutSine")){
			result= (float) Math.sin(input*Math.PI/2.0);
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutSine")){
			result= (float) (1/2.0-1/2.0*Math.cos(input*Math.PI));
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInExpo")){
			result= (float) ((input==0) ? 0 : Math.pow(2, 10*(input-1)));
		}else if(mEaseType.equals("AnimationEaseType_EaseOutExpo")){
			result= (float) ((input==1) ? 1 : -Math.pow(2, -10 * input) + 1);
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutExpo")){
			if (input==0){
				result= 0;
			}else if (input<=0.5){
				result= (float) (1/2.0 * Math.pow(2, 10 * (2*input - 1)));
			}else  if (input<1){
				result= (float) (1/2.0 * (-Math.pow(2, -10 * (2*input-1)) + 2));
			}else{
				result= 1;
			}
		}
		else if(mEaseType.equals("AnimationEaseType_EaseInCirc")){
			result= (float) -(Math.sqrt(1 - (float) Math.pow(input, 2)) - 1);
		}else if(mEaseType.equals("AnimationEaseType_EaseOutCirc")){
			result=  (float) Math.sqrt(1 - Math.pow(input-1, 2));
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutCirc")){
			if (input<=0.5){
				result= (float) (-1/2.0 * (Math.sqrt(1 - 4*(float) Math.pow(input, 2)) - 1));
			}else{
				result= (float) (1/2.0 * (Math.sqrt(1 - Math.pow(2*input-2, 2)) + 1));
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInElastic")){
			double s = 1.70158; double p=0.3; double a=1;
			if (input==0){
				result= 0;  
			}else if (input==1){
				result= 1; 
			}else{
				if (a < 1) {
					a=1;
					s=p/4; 
				}else {
					s = p/(2*Math.PI) * Math.asin (1/a);
				}
				result= (float) -(a*Math.pow(2,10*(input-1)) * Math.sin( (input-1-s)*(2*Math.PI)/p ));
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseOutElastic")){
			double s = 1.70158; double p=0.3; double a=1;
			if (input==0){
				result= 0;  
			}else if (input==1){
				result= 1; 
			}else{
				if (a < 1) {
					a=1;
					s=p/4; 
				}else {
					s = p/(2*Math.PI) * Math.asin (1/a);
				}
				result=(float) (a*Math.pow(2,-10*input) * Math.sin( (input-s)*(2*Math.PI)/p) + 1);
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutElastic")){
			double s = 1.70158; double p=0.45; double a=1.0;
			if (input==0){
				result= 0;  
			}else if (input==1){
				result= 1; 
			}else{
				if (a < 1) {
					a=1;
					s=p/4; 
				}else {
					s = p/(2*Math.PI) * Math.asin (1/a);
				}
				if (input <= 0.5){
					result=(float) (-0.5*(a*Math.pow(2,10*(2*input-1))) * Math.sin( (2*input-1-s)*(2*Math.PI)/p ));
				}else{
					result=(float) (a*Math.pow(2,-10*(2*input-1)) * Math.sin( (2*input-1-s)*(2*Math.PI)/p )*0.5 + 1);
				}
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInBack")){
			double s = 1.70158;
			result= (float) ((float) Math.pow(input, 2)*((s+1)*input - s));
		}else if(mEaseType.equals("AnimationEaseType_EaseOutBack")){
			double s = 1.70158;
			result=(float) (Math.pow((input-1),2)*((s+1)*(input-1) + s) + 1);
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutBack")){
			double s = 1.70158;
			if (input<=0.5){
				result= (float) (1/2.0*(Math.pow(2*input, 2)*(((s*=(1.525))+1)*2*input - s)));
			}else{
				result= (float) (1/2.0*(Math.pow(2*input-2, 2)*(((s*=(1.525))+1)*(2*input-2) + s) + 2));
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInBounce")){
			if (1-input < (1/2.75)) {
				result= (float)(7.5625*Math.pow(1-input, 2));
			} else if (1-input < (2/2.75)) {
				result= (float) (7.5625*Math.pow(1-input-1.5/2.75, 2) + 0.75);
			} else if (1-input < (2.5/2.75)) {
				result= (float) (7.5625*Math.pow(1-input-2.25/2.75, 2) + 0.9375);
			} else {
				result= (float) (7.5625*Math.pow(1-input-2.625/2.75, 2) + 0.984375);
			}
			result=1-result;
		}else if(mEaseType.equals("AnimationEaseType_EaseOutBounce")){
			if (input < (1/2.75)) {
				result= (float) (7.5625*(float) Math.pow(input, 2));
			} else if (input < (2/2.75)) {
				result= (float) (7.5625*Math.pow(input-1.5/2.75, 2) + 0.75);
			} else if (input < (2.5/2.75)) {
				result= (float) (7.5625*Math.pow(input-2.25/2.75, 2) + 0.9375);
			} else {
				result= (float) (7.5625*Math.pow(input-2.625/2.75, 2) + 0.984375);
			}
		}else if(mEaseType.equals("AnimationEaseType_EaseInOutBounce")){
			if (input <= 0.5){
				if (1-2*input < (1/2.75)) {
					result= (float)(7.5625*Math.pow(1-2*input, 2));
				} else if (1-2*input < (2/2.75)) {
					result= (float) (7.5625*Math.pow(1-2*input-1.5/2.75, 2) + 0.75);
				} else if (1-2*input < (2.5/2.75)) {
					result= (float) (7.5625*Math.pow(1-2*input-2.25/2.75, 2) + 0.9375);
				} else {
					result= (float) (7.5625*Math.pow(1-2*input-2.625/2.75, 2) + 0.984375);
				}
				result=(float) (0.5*(1-result));
			}
			else{
				if (2*input-1 < (1/2.75)) {
					result= (float) (7.5625*Math.pow(2*input-1, 2));
				} else if (2*input-1 < (2/2.75)) {
					result= (float) (7.5625*Math.pow(2*input-1-1.5/2.75, 2) + 0.75);
				} else if (2*input-1 < (2.5/2.75)) {
					result= (float) (7.5625*Math.pow(2*input-1-2.25/2.75, 2) + 0.9375);
				} else {
					result= (float) (7.5625*Math.pow(2*input-1-2.625/2.75, 2) + 0.984375);
				}
				result=(float) (0.5*(result+1));
			}
		}
		return result;
	}
	
	@Override
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
