package com.hl.android.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据操作工具类 <br>
 * <br>
 * 可以对数据进行保存、获取操作的工具类。 <br>
 * <br>
 * 提供数据库建立、查询、插入、删除等操作，提供properties文件的读取和写入，提供xml文件的读取和写入。<br>
 * <br>
 * 
 * @author ThinkPad
 * @LastModified 2011-8-5
 */
public class DataUtils {
	//软件存放log的地方
	//sqlite的数据库名字
	private static String C_Str_DataBaseName = "database";
	//跟人偏好存放的文件名字
	private static String C_Str_SharedPreferencesName = "sharedpreferences";
	
	
	//系统保存偏好设置的引用
	private static SharedPreferences hlPreferences = null;

	// 数据保存的路径，android系统的绝对路径例如：/data/data/com.hl.shelvesDemo/dbdata
	private static String dataFilePath = null;
	private static File dataFile = null;
	// 系统数据库实例
	private static SQLiteDatabase hlDb = null;
	

	
	/**
	 * 获取系统数据库实例
	 */
	private static SQLiteDatabase getDb(Activity activity) {
		if (hlDb == null || !hlDb.isOpen()) {
			if (dataFilePath == null) {
				//创建数据库时必须使用sd卡以外的系统文件夹，保证有权限的同时，数据不会随着sd卡丢失。
				dataFilePath = StringUtils.contactForFile(AppUtils.getAppPath(activity), C_Str_DataBaseName);
			}
			if(dataFile==null) {
				dataFile = new File(dataFilePath);
			}
			if (!dataFile.exists()) {
				InputStream in = null;
				FileOutputStream fos = null;
				try {
					in = activity.getResources().getAssets().open(C_Str_DataBaseName);
					fos = new FileOutputStream(dataFilePath);
					byte[] buffer = new byte[1024];
					int count = 0;
					while ((count = in.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
					in.close();
					fos.close();
				} catch (Exception e) {
				}
			}
			hlDb = SQLiteDatabase.openOrCreateDatabase(dataFilePath, null);
		}
		try {
			SQLiteDatabase.releaseMemory();
			if (hlDb.isDbLockedByOtherThreads()) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
		}
		
		return hlDb;
	}
	
	
	/**
	 * 检查表是否存在
	 * @param activity 当前使用的activity
	 * @param tableName 表名字
	 * @return true存在,false不存在
	 */
	public static boolean checkTableExists(Activity activity,String tableName){
		
		String strSql = "select * from sqlite_master where tbl_name=?";
		Cursor cursor = null;
		try {
			cursor = rawQuery(activity,strSql,tableName);
			if(cursor.getCount()==0) {
				return false;
			}
			return true;
		} catch (Exception e) {
		}finally{
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return false;
	}
	
	/**
	 * 执行原始的Sql查询，返回一个Cursor对象。
	 * @param activity 当前使用的activity
	 * @param strSql 要执行的SQL查询，中间可用?代替参数
	 * @param params 数量必须和strSql中的?数量保持一致
	 * */
	public static Cursor rawQuery(Activity activity,String strSql,String... params) {
		try {
			return getDb(activity).rawQuery(strSql, params);
		} catch (Exception e) {
			Log.e("hl","查询数据库时出现错误！",e);
		}
		return null;
	}
	
	/**
	 * 执行原始的Sql，没有返回值
	 * @param activity 当前使用的activity
	 * @param strSql 要执行的SQL查询，中间可用?代替参数
	 * @param params 数量必须和strSql中的?数量保持一致
	 * */
	public static void execSQL(Activity activity,String strSql,Object... params) {
		try {
			if(params==null||params.length==0) {
				getDb(activity).execSQL(strSql);
			}else {
				getDb(activity).execSQL(strSql, params);
			}
		} catch (Exception e) {
			Log.e("hl","执行SQL语句时出现错误！",e);
		}
	}
	
	/**
	 * 插入数据库
	 * @param activity 当前使用的activity
	 * @param strTable 表名
	 * @param nullColumnHack 当要插入的值是空列时，需要指定表中的列名以完成完整的SQL语法
	 * */
	public static long insert(Activity activity,String strTable,String nullColumnHack,ContentValues values) {
		try {
			return getDb(activity).insert(strTable, nullColumnHack, values);
		} catch (Exception e) {
			Log.e("hl","执行插入语句时出现错误！",e);
		}
		return -1;
	}
	
	/**
	 * 更新数据库
	 * @param activity 当前使用的activity
	 * @param strTable 表名
	 * @param values 要更新的列的值（Key-Value Paire）
	 * @param whereClause 更新数据库的条件
	 * @param params 更新条件的参数
	 * */
	public static int update(Activity activity,String strTable,ContentValues values,String whereClause,String[] params) {
		try {
			return getDb(activity).update(strTable, values,whereClause, params);
		} catch (Exception e) {
			Log.e("hl","执行更新语句时出现错误！",e);
		}
		return -1;
	}
	
	/**
	 * 查询数据库以 获取SQL对应的查询结果。<br>
	 * @param activity 当前使用的activity 
	 * @param sql 要查询的SQL语句
	 * @param values 查询使用的参数
	 */
	public static ArrayList<HashMap<String, String>> getSqlResult(Activity activity,String sql, String... values) {
		Cursor cursor = getDb(activity).rawQuery(sql, values);
		ArrayList<HashMap<String, String>> lstResult = new ArrayList<HashMap<String, String>>();
		try {
			if (cursor == null || cursor.getCount() == 0) {
				return null;
			}
			while (cursor.moveToNext()) {
				HashMap<String, String> mapRow = new HashMap<String, String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					mapRow.put(cursor.getColumnName(i), cursor.getString(i));
				}
				lstResult.add(mapRow);
			}

		} catch (Exception e) {
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return lstResult;
	}

	/**
	 * 根据参数执行对应的语句<br>
	 * 
	 * @param activity 当前使用的activity
	 * @param sql 要执行的SQL语句
	 * @param values 要执行的语句中需要的参数
	 */
	public static void insertTableValue(Activity activity,String table, String columnName, ContentValues values) {
		try {
			getDb(activity).insert(table, columnName, values);
		} catch (Exception e) {
			Log.e("hl","执行插入语句时出现错误！",e);
		}
	}

	/**
	 * 关闭数据库连接。
	 */
	public static void closeDb() {
		if (hlDb == null) {
			return;
		}
		hlDb.close();
		hlDb = null;
	}

	//--------------保存和获取系统偏好设置------------------------------
	private static SharedPreferences getPreferences(Context activity) {
		if(hlPreferences==null) {
			if(activity ==null) {
				return null;
			}
			hlPreferences = activity.getSharedPreferences(C_Str_SharedPreferencesName, Context.MODE_WORLD_READABLE&Context.MODE_WORLD_WRITEABLE);
		}
		return hlPreferences;
	}
	public static void savePreference(Activity activity,String key,Boolean value) {
		try {
			getPreferences(activity).edit().putBoolean(key, value).commit();
		} catch (Exception e) {
			Log.e("hl","保存偏好设置时出错！",e);
		}
	}
	public static boolean getPreference(Activity activity,String key,boolean defValue) {
		try {
			return getPreferences(activity).getBoolean(key, defValue);
		} catch (Exception e) {
			Log.e("hl","获取偏好设置时出错！",e);
		}
		return defValue;
	}
	
	public static void savePreference(Activity activity,String key,float value) {
		try {
			getPreferences(activity).edit().putFloat(key, value).commit();
		} catch (Exception e) {
			Log.e("hl","保存偏好设置时出错！",e);
		}
	}
	public static float getPreference(Activity activity,String key,float defValue) {
		try {
			return getPreferences(activity).getFloat(key, defValue);
		} catch (Exception e) {
			Log.e("hl","获取偏好设置时出错！",e);
		}
		return defValue;
	}
	
	public static void savePreference(Activity activity,String key,int value) {
		try {
			getPreferences(activity).edit().putInt(key, value).commit();
		} catch (Exception e) {
			Log.e("hl","保存偏好设置时出错！",e);
		}
	}
	public static int getPreference(Activity activity,String key,int defValue) {
		try {
			return getPreferences(activity).getInt(key, defValue);
		} catch (Exception e) {
			Log.e("hl","获取偏好设置时出错！",e);
		}
		return defValue;
	}
	
	public static void savePreference(Activity activity,String key,long value) {
		try {
			getPreferences(activity).edit().putLong(key, value).commit();
		} catch (Exception e) {
			Log.e("hl","保存偏好设置时出错！",e);
		}
	}
	public static long getPreference(Activity activity,String key,long defValue) {
		try {
			return getPreferences(activity).getLong(key, defValue);
		} catch (Exception e) {
			Log.e("hl","获取偏好设置时出错！",e);
		}
		return defValue;
	}
	
	public static void savePreference(Activity activity,String key,String value) {
		try {
			getPreferences(activity).edit().putString(key, value).commit();
		} catch (Exception e) {
			Log.e("hl","保存偏好设置时出错！",e);
		}
	}
	public static String getPreference(Activity activity,String key,String defValue) {
		try {
			return getPreferences(activity).getString(key, defValue);
		} catch (Exception e) {
			Log.e("hl","获取偏好设置时出错！",e);
		}
		return defValue;
	}
	/**
	 * 将序列化对象存储到应用的存储目录上
	 * @param activity  当前使用的activity
	 * @param key存储的key
	 * @param value 序列化对象
	 */
	public static void saveSerializable(Activity activity,String key,Serializable value) {
		try {
			String filePath =  AppUtils.getAppPath(activity) +"/"+ key;
			if(!new File(filePath).exists())new File(filePath).createNewFile();
			FileOutputStream fos=new FileOutputStream(filePath);
			ObjectOutputStream sos = new ObjectOutputStream(fos);
			sos.writeObject(value);
			sos.close();
		} catch (Exception e) {
			Log.e("hl","保存序列化对象的时候出错",e);
		}
	}
	/**
	 * 将存储的序列化对象获取出来
	 * 如果没有则返回null
	 * @param <T>  序列化对象类型
	 * @param activity  当前使用的activity
	 * @param key 存储的key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static<T> T getSerializable(Activity activity,String key) {
		try {
			String filePath = AppUtils.getAppPath(activity) +"/" + key;
			File file = new File(filePath);
			//如果不存在
			if(!file.exists())return null;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o =  ois.readObject();
			T result = (T)o;
			ois.close();
			return result;
			
		} catch (Exception e) {
			Log.d("hl","获得序列化对象的时候出错");
			return null;
		}
	}
	//--------------保存和获取系统偏好设置------------------------------
	
	
}
