package com.hl.android.view.gallary;

import android.content.Context;

import com.hl.android.common.BookSetting;
import com.hl.android.core.utils.ReflectHelp;
import com.hl.android.view.gallary.base.AbstractGalley;

public class GalleyHelper {
	@SuppressWarnings("rawtypes")
	public static AbstractGalley getGalley(Context context){
		Class[] argsType = new Class[] { Context.class};
		Object[] argsValue = new Object[] { context};
		
		try {
			if (BookSetting.GALLEYCODE == 0){
				return (AbstractGalley) ReflectHelp.newInstance("com.hl.android.view.gallary.GalleyCommon", argsType, argsValue);
			} else if (BookSetting.GALLEYCODE == 1){
				return (AbstractGalley) ReflectHelp.newInstance("com.hl.android.view.gallary.Galley3D4ShowSnaps", argsType, argsValue);
			}else{
				return (AbstractGalley) ReflectHelp.newInstance("com.hl.android.view.gallary.GalleyCommon", argsType, argsValue);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
