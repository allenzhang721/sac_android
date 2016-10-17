package com.hl.android.book;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.util.Log;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.ContainerEntity;
import com.hl.android.book.entity.CounterEntity;
import com.hl.android.book.entity.GifComponentEntity;
import com.hl.android.book.entity.HTMLComponentEntity;
import com.hl.android.book.entity.PDFComponentEntity;
import com.hl.android.book.entity.PageEntity;
import com.hl.android.book.entity.SWFFileEntity;
import com.hl.android.book.entity.SliderEffectComponentEntity;
import com.hl.android.book.entity.TextComponentEntity;
import com.hl.android.book.entity.TimerEntity;
import com.hl.android.book.entity.VideoComponentEntity;
import com.hl.android.book.entity.moudle.MoudleComponentEntity;

/**
 * PageEntity的工厂类 将xml信息解析创建PageEntity
 * 
 * @author zhaoq
 * @version 1.0
 * @createed 2013-11-12
 */
public class PageFactory {
	/**
	 * 创建解析生成PageEntity
	 * 
	 * @param xmlStream
	 *            字节流xml信息
	 * @return
	 */
	public static PageEntity getPage(InputStream xmlStream) {
		PageEntity page = new PageEntity();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlStream);
			// 获取根节点
			Element root = document.getDocumentElement();
			setPage(root,page);
		} catch (Exception e) {
			Log.e("hl", "解析page错误", e);
		}

		return page;
	}

	/**
	 * 解析复制给pageentity
	 * 
	 * @param pageE
	 * @param page
	 */
	private static void setPage(Element pageE, PageEntity page) {
		Node node = pageE.getFirstChild();
		while(node != null){
			String tagName = node.getNodeName();
			//page的属性
			if(!setProperFromDom(page,node)){
				//containers
				if(tagName.equals("Containers")){
					Node containerNode = node.getFirstChild();
					while(containerNode != null){
						page.getContainers().add(getContainer(containerNode));
						containerNode = containerNode.getNextSibling();
					}
				}
			}
			node = node.getNextSibling();
		}
	}
	
	/**
	 * 解析xml节点中的属性到指定的对象
	 * @param instance
	 * @param node
	 * @return  如果返回false说明没有被消耗掉，要求深入解析
	 */
	private static boolean setProperFromDom(Object instance,Node node){
		String nodeName = node.getNodeName();
		String nodeValue = node.getTextContent();
		//反射设置指定对象和节点的属性
		try {
			for (Field field :instance.getClass().getDeclaredFields()) {
				if (field.getName().toLowerCase(Locale.getDefault())
						.equals(nodeName.toLowerCase(Locale.getDefault()))) {
					field.setAccessible(true);
					if (field.getType() == int.class) {
						field.setInt(instance, Integer.parseInt(nodeValue));
					}else if (field.getType() == String.class) {
						field.set(instance,nodeValue);
					}else if (field.getType() == boolean.class) {
						field.setBoolean(instance, Boolean.parseBoolean(nodeValue));
					}else if (field.getType() == float.class) {
						field.setFloat(instance, Float.parseFloat(nodeValue));
					}
					return false;
				}
			}
		} catch (Exception e) {
			Log.e("hl", "解析设置属性出现问题，解析属性的name和value分别是" + nodeName + "," + nodeValue,e);
		}
		return false;
	}
	
	/**
	 * 解析container
	 * @param node
	 * @return
	 */
	private static ContainerEntity getContainer(Node nodeContainer){
		ContainerEntity container = new ContainerEntity();
		Node node = nodeContainer.getFirstChild();
		while(node != null){
			String tagName = node.getNodeName();
			//container的属性
			if(!setProperFromDom(nodeContainer,node)){
				//解析Component
				
				//TODO  解析Behaviors
				//TODO  解析Animations
			}
			node = node.getNextSibling();
		}
		return container;
	}
	
	private static ComponentEntity getComponent(Element nodeComponent){
		ComponentEntity component = null;
		//获得classname来确定使用哪个component
		Node classNode = (Element) nodeComponent.getElementsByTagName("ClassName").item(0);
		String className = classNode.getTextContent();
		if (className.indexOf("HLGIFComponent") > 0) {
			component = new GifComponentEntity(null);
		}else if (className.indexOf("HLSWFComponent") > 0) {
			component = new GifComponentEntity(null);
		}else if (className.indexOf("HLRollingTextComponent") > 0 || className.indexOf("hlEnglishRollingText") > 0) {
			component = new TextComponentEntity(null);
		}else if (className.indexOf("HLLocalPDFComponent") > 0) {
			component = new PDFComponentEntity();
		}else if (className.indexOf("HLTemplateComponent")>0){
			component = new MoudleComponentEntity(null);
		}else if (className.endsWith("HLLocalVideoComponent")){
			component = new VideoComponentEntity();
		}else if (className.endsWith("HLHtml5Component")){
			component = new HTMLComponentEntity();
		} else if (className.indexOf("HLCounterComponent") > 0) {
			component = new CounterEntity();
		}else if (className.indexOf("HLTimerComponent") > 0) {
			component = new TimerEntity();
		}else if (className.indexOf("HLSWFFileComponent") > 0) {
			component = new SWFFileEntity();
		}else if (className.indexOf("HLSliderEffectComponent") > 0) {
			component = new SliderEffectComponentEntity(null);
		}else{
			Log.e("hl","我们遇到了不认识的组件 ,name is " + className);
			return null;
		}
		Node node = nodeComponent.getFirstChild();
		while(node != null){
			String tagName = node.getNodeName();
			//component的属性
			if(!setProperFromDom(component,node)){
				//TODO  具体组件的属性设置
			}
			node = node.getNextSibling();
		}
		 
		return component;
	}
	
	
}
