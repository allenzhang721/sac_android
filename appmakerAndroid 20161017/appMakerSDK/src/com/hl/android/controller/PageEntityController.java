package com.hl.android.controller;

import java.util.HashMap;

import android.content.Context;

import com.hl.android.book.BookDecoder;
import com.hl.android.book.entity.PageEntity;
/**
 * 页面实体管理类
 * 对于页面实体类进行管理
 * 提供如下几个接口
 * 1加载首页
 * 2获得指定页面的实体类
 * 3回收页面
 * @author zhaoq
 *
 */
public class PageEntityController {
	
	private static PageEntityController _instance;
	//实体类存储容器
	private HashMap<String, PageEntity> pageEntityMap = new HashMap<String, PageEntity>();
	public static PageEntityController getInstance() {
		if (null == _instance) {
			_instance = new PageEntityController();
		}

		return _instance;
	}
	
	public void clear(){
		pageEntityMap.clear();
	}
	public void recyle(){
		pageEntityMap.clear();
		_instance = null;
	}

	/**
	 * 从缓存中得到pageentity
	 * 
	 * @param pageID
	 * @return
	 */
	public PageEntity getPageEntityByPageId(final Context context,String pageID) {
		if(pageEntityMap.containsKey(pageID))return pageEntityMap.get(pageID);
		return loadPageEntity(context,pageID);
	}

	private PageEntity loadPageEntity(Context context,String pageId) {
		PageEntity pageEntity = BookDecoder.getInstance().decodePageEntity(context,pageId);
		if(pageEntity==null)return null;
		pageEntityMap.put(pageId, pageEntity);
		return pageEntity;
	}
}
