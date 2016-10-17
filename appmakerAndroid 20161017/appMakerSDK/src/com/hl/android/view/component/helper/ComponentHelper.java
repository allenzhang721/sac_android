package com.hl.android.view.component.helper;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.ContainerEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;
import com.hl.android.core.utils.ReflectHelp;
import com.hl.android.core.utils.ScreenUtils;
import com.hl.android.view.component.ComponentListener;
import com.hl.android.view.component.inter.Component;
import com.hl.android.view.component.listener.OnComponentCallbackListener;
import com.hl.android.view.component.moudle.HLCameraUIComponent;

/**
 * 构建显示组件类，用于viewPage显示
 * 
 * @author webcat
 * 
 */
public class ComponentHelper {
	public static final String IMAGE_TYPE_HOR = "text_hor_image";
	public static final String IMAGE_TYPE_VER = "text_ver_image";
	public static final String IMAGE_TYPE_NORMAL = "normal_image";
	
	private static HashMap<String, String> componentMap = new HashMap<String, String>();
	static {
		componentMap
				.put("com.hl.flex.components.objects.hlImage::HLLocalImageComponent",
						"com.hl.android.view.component.ImageComponent");
		componentMap.put("com.hl.flex.components.objects.hlButton::HLLocalButtonComponent",
								"com.hl.android.view.component.HLLocalButtonComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlVideo::HLLocalVideoComponent",
						"com.hl.android.view.component.VideoComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlAudio::HLMp3Component",
						"com.hl.android.view.component.AudioComponent");
		componentMap.put(
				"com.hl.flex.components.objects.html::HLHtmlComponent",
				"com.hl.android.view.component.WebComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlImage::HLGIFComponent",
						"com.hl.android.view.component.ImageGifComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlText.hlRollingText::HLRollingTextComponent",
						"com.hl.android.view.component.ScrollTextViewComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlSwf::HLLocalPDFComponent",
						"com.hl.android.view.component.PDFDocumentViewComponentMU");
		componentMap
				.put("com.hl.flex.components.objects.template::HLTemplateComponent",
						"com.hl.flex.components.objects.template::HLTemplateComponent");
		componentMap
				.put("com.hl.flex.components.objects.hlText.hlEnglishRollingText::HLEnglishRollingTextComponent",
						"com.hl.android.view.component.ScrollTextViewComponentEN");
		componentMap.put(
				"com.hl.flex.components.objects.swf::HLSWFComponent",
				"com.hl.android.view.component.ImageGifComponent");

		componentMap.put(
				"com.hl.flex.components.objects.html::HLHtml5Component",
				"com.hl.android.view.component.HTMLComponent");

		componentMap
				.put("com.hl.flex.components.objects.counter::HLCounterComponent",
						"com.hl.android.view.component.HLCounterComponent");
		
		componentMap
		.put("com.hl.flex.components.objects.hltimer::HLTimerComponent",
				"com.hl.android.view.component.TimerComponent");
		componentMap
		.put("com.hl.flex.components.objects.swf::HLSWFFileComponent",
				"com.hl.android.view.component.HLSWFFileComponent");
		componentMap
		.put("com.hl.flex.components.objects.effect::HLSliderEffectComponent",
				"com.hl.android.view.component.HLSliderEffectComponent");
		
	}

	public static String getComponentClassName(String key) {
		return componentMap.get(key);
	}

	public static Component getComponent(ContainerEntity entity,
			View currentViewPage) {
		
		entity.getComponent().isHideAtBegining = entity.isHideAtBegining;
		// 实例化component
		@SuppressWarnings("rawtypes")
		Class[] argsType = new Class[] { Context.class, ComponentEntity.class };
		Object[] argsValue = new Object[] { currentViewPage.getContext(),
				entity.component };
		entity.getComponent().setRotation(entity.getRotation());
		Component component = null;
		
		if (null == ComponentHelper.getComponentClassName(entity.getComponent()
				.getClassName())) {
			Log.d("wdy", "没有控件："+entity.getComponent().getClassName()+",请添加！！！");
			return null;
		}
		
		
		try {
			// 如果是zoom构造zoom component
//			if (entity.getComponent().isAllowUserZoom
//					&& entity.getComponent().getClassName()
//							.indexOf("HLLocalImageComponent") > 0) {
//				component = (Component) ReflectHelp.newInstance(
//						"com.hl.android.view.component.ImageZoomComponent",
//						argsType, argsValue);
//			}

			// 初始化模板
			if (entity.getComponent().getClassName()
					.indexOf("HLTemplateComponent") > 0) {
				component = ComponentMoudleHelper.getComponent(
						((MoudleComponentEntity) entity.component)
								.getModuleID(), argsType, argsValue);
				// 如果添加了模板但是最后却没有，那么我们就直接返回吧
				if (component == null) {
					return null;
				}

			}
			// 横向文本或竖向文本图片组件
			if (null == component && entity.getComponent().getClassName()
					.indexOf("HLLocalImageComponent") > 0) {
				entity.component.autoLoop = entity.autoLoop;
				String imgType = entity.getComponent().getImageType();
				if (imgType != null) {
					// 横向文本
					if (imgType.contains(IMAGE_TYPE_HOR)) {
						
						component = (Component) ReflectHelp.newInstance(
								"com.hl.android.view.component.VerticalImageComponent",
								argsType, argsValue);
						
					}
					// 竖向文本
					else if (imgType.contains(IMAGE_TYPE_VER)) {
						component = (Component) ReflectHelp.newInstance(
								"com.hl.android.view.component.HorizontalImageComponent",
								argsType, argsValue);
					}
				}
			}
//			else if (null == component && entity.getComponent().getClassName()
//					.indexOf("HLLocalVideoComponent") > 0) {
//				entity.component.autoLoop = entity.autoLoop;
//				boolean isOnlineSource = entity.component.isOnlineSource();
//				if (isOnlineSource) {
//						component = (Component) ReflectHelp.newInstance(
//								"com.hl.android.view.component.VideoView4Online",
//								argsType, argsValue);
//				}
//			}
			// 如果没有初始化成功，调用一般初始化方法
			if (null == component) {
				entity.component.autoLoop = entity.autoLoop;
				component = (Component) ReflectHelp.newInstance(
						ComponentHelper.getComponentClassName(entity
								.getComponent().getClassName().trim()),
						argsType, argsValue);
			}

			int x = 0, y = 0, width = 0, height = 0;
			component.getEntity().oldHeight = entity.getHeight();
			component.getEntity().oldWidth = entity.getWidth();
			
			if (entity.getRotation() != 0) {
					float rotation=entity.getRotation();
					float ratioMHeight=ScreenUtils.getVerScreenValue(entity.getHeight());
					float ratioMWidth=ScreenUtils.getHorScreenValue(entity.getWidth());
					float ratioMX=ScreenUtils.getHorScreenValue(entity.getX());
					float ratioMY=ScreenUtils.getVerScreenValue(entity.getY());
					x= (int) (ratioMX - ratioMHeight / 2//计算没有旋转角度的x点坐标
							* Math.sin(rotation * Math.PI / 180) 
							- ratioMWidth / 2 
							+ ratioMWidth / 2
							* Math.cos(rotation * Math.PI / 180));
					y = (int) (ratioMY + ratioMWidth / 2//计算没有旋转角度的y点坐标
							* Math.sin(rotation * Math.PI / 180) 
							- ratioMHeight / 2 
							+ ratioMHeight / 2
							* Math.cos(rotation * Math.PI / 180));
					
					
			} else {
				x = (int) ScreenUtils.getHorScreenValue(entity.getX());
				y = (int) ScreenUtils.getVerScreenValue(entity.getY());
			}
			width = (int) ScreenUtils.getHorScreenValue(entity.getWidth());
			height = (int) ScreenUtils.getVerScreenValue(entity.getHeight());
			//增加slide相关的计算
			if(component.getEntity().isPageInnerSlide){
				component.getEntity().slideBindingWidth =  (int) ScreenUtils.getHorScreenValue(component.getEntity().slideBindingWidth);
				component.getEntity().slideBindingHeight =  (int) ScreenUtils.getVerScreenValue(component.getEntity().slideBindingHeight);
				component.getEntity().slideBindingX =  (int) ScreenUtils.getHorScreenValue(component.getEntity().slideBindingX);
				component.getEntity().slideBindingY =  (int) ScreenUtils.getVerScreenValue(component.getEntity().slideBindingY);
			}
			
			LayoutParams layoutParams = new LayoutParams(width, height);
			((View) component).setLayoutParams(layoutParams);
//			component.setRotation(entity.getRotation());
			component.getEntity().setAnims(entity.getAnimations());
			component.getEntity().isPlayAnimationAtBegining = entity.isPlayAnimationAtBegining;
			component.getEntity().isPlayVideoOrAudioAtBegining = entity
					.isPlayVideoOrAudioAtBegining();
			component.getEntity().isHideAtBegining = entity.isHideAtBegining;

			component.getEntity().x = x;
			component.getEntity().y = y;
			component.getEntity().isStroyTelling = entity.IsStroyTelling;
			component.getEntity().isMoveScale = entity.isMoveScale;
			component.getEntity().isPushBack = entity.isPushBack;

			component.getEntity().autoLoop = entity.autoLoop;
			component.getEntity().setComponentId(entity.getID());
			component.getEntity().behaviors = entity.behaviors;
			component.load();
			
//			Log.d("hl","get component.load() time is " + (System.currentTimeMillis()-l));
			if (component instanceof ComponentListener) {
				((ComponentListener) component)
						.registerCallbackListener((OnComponentCallbackListener) currentViewPage);
			}
			
		} catch (Exception e) {
			Log.e("hl", "加载组件"+entity.getComponent().getClassName()+"出错", e);
		}

		return component;
	}
}
