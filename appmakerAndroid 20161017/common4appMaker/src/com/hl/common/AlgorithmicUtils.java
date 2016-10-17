package com.hl.common;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 算法通用模块  <br>
 * <br>
 * 提供常用的算法解决方案 <br>
 * <br>
 * 所有方法均为静态方法，可以直接使用。<br>
 * <br>
 * @author ThinkPad
 * @LastModified 2011-9-5
 */
public class AlgorithmicUtils {
	
	/**
	 * 判断指定的参数中是否包含空值，如果任何一个参数为空值，则返回true。
	 * @param objects 要测试的参数
	 * @return 是否含有空值
	 * */
	public static boolean hasEmpty(Object ...objects) {
		if(objects==null||objects.length==0) {
			return true;
		}
		for(Object obj:objects) {
			if(isEmpty(obj)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断指定的参数是否为空，包括空值，空字符串，空Collection，空Map，空数组，都会返回true
	 * @param obj 要测试的参数
	 * @return 是否为空
	 * */
	public static boolean isEmpty(Object obj) {
		try {
			if(obj==null) {
				return true;
			}
			if(obj.toString().trim().length()==0) {
				return true;
			}
			if(obj instanceof Collection<?>) {
				if(((Collection<?>)obj).size()==0) {
					return true;
				}
			}
			if(obj instanceof Map<?,?>) {
				if(((Map<?,?>)obj).size()==0) {
					return true;
				}
			}
			if(obj instanceof Object[] || obj.getClass().getName().startsWith("[")) {
				int length = Array.getLength(obj);
				if(length==0) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 从指定的数字数组中比较出最大的一个。
	 * @param numbers 数字数组
	 * @return 最大的数字
	 * */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T getMax(T... numbers) {
		T max= (T)new Integer(0);
		
		for(T number:numbers) {
			if(number.doubleValue()>max.doubleValue()) {
				max = number;
			}
		}
		return max;
	}
	
	/**
	 * 从指定的数字数组中比较出最小的一个。
	 * @param numbers 数字数组
	 * @return 最小的数字
	 * */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T getMin(T... numbers) {
		T min= (T)new Integer(Integer.MAX_VALUE);
		
		for(T number:numbers) {
			if(number.doubleValue()<min.doubleValue()) {
				min = number;
			}
		}
		return min;
	}
	
	/**
	 * 判断数组中是否存在指定的值,如果是一个List，判断是否存在指定的值，如果是一个Map，判断是否存在指定的Key,或者键值和该值相同的。
	 * @param array 要查找的数组
	 * @param obj 要查看的值
	 * @return 是否存在
	 * */
	public static <T> boolean arrayContains(Object array,T obj) {
		if(array==null||obj==null) {
			return false;
		}
		if(array instanceof Object[]) {
			Object[] newObjs = (Object[])array;
			for(Object o:newObjs) {
				if(o!=null&&o.equals(obj)) {
					return true;
				}
			}
		}
		if(array.getClass().getName().startsWith("[")) {
			int length = Array.getLength(array);
			for(int i=0;i<length;i++) {
				if(Array.get(array, i)==null) {
					continue;
				}
				if(Array.get(array, i).toString().equals(obj.toString())) {
					return true;
				}
			}
		}
		
		if(array instanceof List<?>) {
			List<?> newObjs = (List<?>)array;
			for(Object o:newObjs) {
				if(o!=null&&o.equals(obj)) {
					return true;
				}
			}
		}
		if(array instanceof Map<?,?>) {
			Map<?,?> newObjs = (Map<?,?>)array;
			for(Object o:newObjs.keySet()) {
				if(o!=null&&(o.equals(obj)||obj.equals(newObjs.get(o)))) {
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * 根据参数情况，立即调用一个实例的一个方法，并返回该方法的返回值。
	 * 
	 * @author xuechao
	 * 
	 * @param objRunner 要运行方法的实例，如果是String则表示objRunner的内容是要运行的类的名称（包括包名）,<br>
	 * 					 此时调用该类的无参数构造函数，取得该类的一个实例作为运行者。<br>
	 *                   如果要运行String的本身的方法，请确定String objrunner的内容不是一个类的全称（通过添加空格实现即可）。<br>
	 * @param strMethodName 要运行的方法的名称
	 * @param objParameters 要运行的方法执行时候的参数
	 * @param aryResult 该方法执行完毕后的返回结果，放到本List中。
	 */
	public static void invokeMethod(Object objRunner,String strMethodName,Object[] objParameters,List<Object> aryResult) throws Exception{
		boolean classnotfound =false;
		try {
			if(null==objRunner) {
				return;
			}
			Method[] methods = null;
			if(objRunner instanceof String) {
				Class<?> runner = null;
				try {
					runner = Class.forName(objRunner.toString());
					objRunner = runner.newInstance();
				} catch (ClassNotFoundException e) {
					//找不到类，肯定是指String对象本身
					classnotfound = true;
				} catch(Exception e) {
					objRunner = runner;
				}
			}
			
			if(objRunner instanceof Class<?>) {
				methods = ((Class<?>)objRunner).getDeclaredMethods();	
			}
			else {
				methods = objRunner.getClass().getDeclaredMethods();	
			}
			
			objParameters = objParameters==null?new Object[] {}:objParameters;
			Method tmpMethod = null;
			
			for(int i=0;i<methods.length;i++) {
				if(methods[i].getName().trim().equalsIgnoreCase(strMethodName.trim())) {
					tmpMethod = methods[i];
					Class<?>[] paramTypes =methods[i].getParameterTypes();
					if(paramTypes.length!=objParameters.length) {
						continue;
					}
					for(int x=0;x<objParameters.length;x++) {
						if(!paramTypes[x].isInstance(objParameters[x])) {
							continue;
						}
					}
					break;
				}
			}
			if(tmpMethod!=null) {
				if(null==aryResult) {
					tmpMethod.invoke(objRunner, objParameters);
				} else {
					aryResult.add(tmpMethod.invoke(objRunner, objParameters));	
				}
			}else {
				throw new Exception("没有名称一致的方法！");
			}
		} catch (Exception e) {
			if(e instanceof java.lang.IllegalArgumentException) {
				throw new Exception("参数个数与要调用的方法的参数个数不相符！",e);
			}else if(e instanceof java.lang.IllegalAccessException) {
				if(classnotfound) {
					throw new Exception("找不到指定的类确认类路径是否正确（不能包含空格等字符）！",e);
				}else {
					throw new Exception("要调用的方法不可视或没有这个方法！",e);
				}
			}  else{
				throw new Exception("调用方法时出现未捕获异常。。。",e);
			}
		}
	}
}
