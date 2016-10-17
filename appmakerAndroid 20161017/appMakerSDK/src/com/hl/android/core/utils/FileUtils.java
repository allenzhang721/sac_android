package com.hl.android.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.hl.android.book.BookDecoder;
import com.hl.android.common.BookSetting;
import com.hl.android.common.HLSetting;
import com.hl.android.controller.BookController;

public class FileUtils {
	public static FileUtils fileUtils;
	// private Context mContext;
	private String bookStorePath;

	public FileUtils() {

	}

	public void init(Context context) {
		// mContext = context;
	}

	public static FileUtils getInstance() {
		if (fileUtils == null)
			fileUtils = new FileUtils();
		return fileUtils;
	}

	public AssetFileDescriptor getFileFD(Context con, String fileName) {
		AssetFileDescriptor assFD = null;
		try {
			assFD = con.getAssets().openFd(
					BookSetting.BOOK_RESOURCE_DIR + fileName);
		} catch (IOException e) {
			Log.e("hl", "getFileFD  ",e); 
		}
		return assFD;
	}

	public InputStream getFileInputStream(Context con, String fileName) {
		InputStream in = null;
		try {
			if (HLSetting.IsResourceSD) {
				in = getFileInputStream(fileName);
			}else{
				in = con.getAssets().open(BookSetting.BOOK_RESOURCE_DIR + fileName);
			}
			
		} catch (IOException e) {

			Log.e("hl", " getFileInputStream ",e); 
		}
		return in;
	}

	// public static InputStream getFileInputStream(String fileName)
	// {
	//
	// File file = new File(filePath + File.separatorChar, fileName);
	// FileInputStream in = null;
	// try {
	// in = new FileInputStream(file);
	// } catch (FileNotFoundException e) {
	//
	// return null;
	// }
	//
	// return in;
	// }
	public String getFilePath(String fileName) {
		return BookSetting.BOOK_PATH + "/" + fileName;
	}

	public InputStream getFileInputStream(String fileName) {
		File file = new File(BookSetting.BOOK_PATH + "/" + fileName);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}
		return in;
	}

	public InputStream getFileInputStreamFilePath(String fileName) {
		File file = new File(fileName);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (Exception e) {
			return null;
		}
		return in;
	}

	public byte[] readFileToByteArrayForHead(Context con, String fileName)
			throws IOException {

		if (HLSetting.IsResourceSD)
			return readFileToByteArray(fileName,0,1024*1024);
		else {
			InputStream in = null;
			try {
				in = con.getAssets().open(
						BookSetting.BOOK_RESOURCE_DIR + fileName);
				byte[] buffer = new byte[in.available()];
				in.read(buffer);
				return buffer;
			} catch (Exception e) {
				//e.printStackTrace();
				Log.e("hl",fileName + " not exists");
				return null;
			}
		}

	}

	public static String inputStream2String(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}
	
	/**
	 * 通用的读取文件流
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public static InputStream readFile(String fileName) throws IOException {
		InputStream in = null;
		if (HLSetting.IsResourceSD) {
			File file = new File(BookSetting.BOOK_PATH + "/" + fileName);
			if (!file.exists()) {
				return null;
			}
			try {
				in = new FileInputStream(file);
			} catch (Exception e) {
				return null;
			}
		} else {
			in = BookController.getInstance().hlActivity.getAssets()
					.open(BookSetting.BOOK_RESOURCE_DIR + fileName);

		}
		return in;
	}

	/**
	 * 从配置信息中读取page实体
	 * @param fileName  配置文件
	 * @param startIndex  开始位置
	 * @param endIndex 结束位置
	 * @return
	 */
	public static InputStream readBookPage(String fileName,int startIndex,int endIndex){
		InputStream in = null;
		InputStream is = null;
		
		try {
			in = readFile(fileName);
			if(in ==null)return null;
			byte[] indexLen = new byte[4];
			in.read(indexLen);
			int l = BookDecoder.fromArray(indexLen);
			int len = endIndex - startIndex;
			byte[] dd = new byte[len];
			in.skip(l+startIndex);
			in.read(dd);
			is = new ByteArrayInputStream(dd);
			return is;
		} catch (Exception e) {
			Log.e("hl", "readBookPage "+ fileName + " error " + e.getMessage());
		} finally {
			try {
				if(in!=null)in.close();
				if(is!=null)is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 从文件中解析获取bookindex数据信息
	 * @param fileName
	 * @return
	 */
	public static String readBookIndex(String fileName){
		InputStream in = null;
		InputStream is = null;
		
//		
//		StringBuffer resultBuffer = new StringBuffer();
//		InputStream in = null;
//		InputStream is = null;
//		byte[] buf = new byte[512];
//		boolean isOpen = false;
		try {
			in = readFile(fileName);
			if(in ==null)return null;
			byte[] indexLen = new byte[4];
			in.read(indexLen);
			int l = BookDecoder.fromArray(indexLen);
			int len = l;
			byte[] dd = new byte[len];
			in.read(dd);
			is = new ByteArrayInputStream(dd);
			String tmpStr = FileUtils.inputStream2String(is);
			return tmpStr;
		} catch (Exception e) {
			Log.e("hl", "get bookindex error " + e.getMessage());
		} finally {
			try {
				if(in!=null)in.close();
				if(is!=null)is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@SuppressWarnings("resource")
	public static byte[] readFileToByteArray(String fileName,int startIndex,int endIndex)
			throws IOException {
		InputStream in = null;
		if (HLSetting.IsResourceSD){
			File file = new File(BookSetting.BOOK_PATH + "/" + fileName);
			if(!file.exists()){
				return null;
			}
			try {
				in = new FileInputStream(file);
			} catch (Exception e) {
				return null;
			}
		}else {
			in = BookController.getInstance().hlActivity.getAssets().open(BookSetting.BOOK_RESOURCE_DIR + fileName);
		}
		ByteArrayOutputStream bouts = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		int curSize = 0;
		boolean started = false;
		while ((len = in.read(buf)) != -1) {
			curSize += len;
			InputStream is = new ByteArrayInputStream(buf);
			String aaa = FileUtils.inputStream2String(is);
			Log.d("hl","bookdata content is " + aaa);
			if(curSize>=startIndex&& !started){
				int startWriteIndex = startIndex + buf.length - curSize;
				if(startWriteIndex<0)startWriteIndex=0;
				bouts.write(buf, 0, len);
				started = true;
			}
			if(curSize>endIndex)break;
		}
		byte[] data = bouts.toByteArray();
		in.close();
		bouts.close();
		ByteArrayInputStream is = new ByteArrayInputStream(bouts.toByteArray());
		String aaa = FileUtils.inputStream2String(is);
		Log.d("hl",aaa);
		
		return data;
	}

	public Bitmap load(String localSourceID, int width, int height,
			Context context) {
		Bitmap bitmap = null;
		BitmapFactory.Options _option = new BitmapFactory.Options();
		_option.inDither = false; // Disable Dithering mode
		_option.inPurgeable = true; // Tell to gc that whether it needs free
									// memory, the Bitmap can be cleared
		_option.inInputShareable = true; // Which kind of reference will be used
											// to recover the Bitmap data after
											// being clear, when it will be used
											// in the future
		_option.inTempStorage = new byte[32 * 1024];

		InputStream is = null;

		try {

			try {
				if (HLSetting.IsResourceSD)
					is = FileUtils.getInstance().getFileInputStream(
							localSourceID);
				else
					is = FileUtils.getInstance().getFileInputStream(context,
							localSourceID);
			} catch (OutOfMemoryError e) {

				Log.e("hl", "  load",e);  
			}

			bitmap = BitmapFactory.decodeStream(is, null, _option);
		} catch (Exception e) {
			return null;
		} catch (OutOfMemoryError e) {
			_option.inSampleSize = 2;
			bitmap = BitmapFactory.decodeStream(is, null, _option);
			return bitmap;
		}

		Bitmap resizeBmp = Bitmap.createScaledBitmap(bitmap, width, height,
				true);
		return resizeBmp;
	}

	/**
	 * 拷贝资源文件到sdcard 根目录
	 * 
	 * @param con
	 * @param fileName
	 */
	public String getDataFilePath(Context con, String fileName) {
		for (String name : con.getFilesDir().list()) {
			if (name.contains(fileName)) {
				return con.getFilesDir().getAbsolutePath() + "/" + fileName;
			}

		}
		return null;
	}

	public File getDataFile(Context con, String fileName) {
		for (String name : con.getFilesDir().list()) {
			if (name.contains(fileName)) {
				return new File(con.getFilesDir().getAbsolutePath() + "/"
						+ fileName);
			}

		}
		return null;
	}

	public void copyFileToData(Context con, String fileName) {
		InputStream in;
		try {
			for (String name : con.getFilesDir().list()) {
				if (name.contains(fileName)) {
					return;
				}
			}

			if (HLSetting.IsResourceSD)
				in = FileUtils.getInstance().getFileInputStream(fileName);
			else
				in = FileUtils.getInstance().getFileInputStream(con, fileName);
			FileOutputStream outputStream = con.openFileOutput(fileName,
					Activity.MODE_PRIVATE);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			in.close();
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void copyFileToSDCard(Context con, String fileName) {
		InputStream in;
		try {
			File sdPath = Environment.getExternalStorageDirectory();
			File outFile = new File(sdPath + "/", fileName);

			if (outFile.exists()) {
				return;
			}
			if (HLSetting.IsResourceSD)
				in = FileUtils.getInstance().getFileInputStream(fileName);
			else
				in = FileUtils.getInstance().getFileInputStream(con, fileName);
			OutputStream out = new FileOutputStream(outFile);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 复制单个文件
	 * 
	 * @param inStream 准备复制的文件来源
	 * @param newPathFile 拷贝到新绝对路径带文件名
	 */
	public static boolean copyFile(InputStream inStream, String newPathFile){
		if(inStream==null) {
			return false;
		}
		boolean result = false;
		try {
			int byteread = 0;
			//检查新文件是否存在
			File outFile = new File(newPathFile);
			if(!outFile.exists()) {
				if(outFile.getParentFile()!=null&&!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				outFile.createNewFile();
			}
			FileOutputStream fs = new FileOutputStream(outFile,false);
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.flush();
			fs.close();
			result = true;
		} catch (Exception e) {
			Log.e("MarsorErroring","复制单个文件时出错！文件：" + inStream + ",新路径：" + newPathFile, e);
		}
		return result;
	}
	public static boolean isExist(String filePath) {

		try {

			File file = new File(filePath);
			return file.exists();

		} catch (OutOfMemoryError e) {
			Log.e("hl", "  load",e); 
		}
		return false;
	}

	public static void deleteFile(String filePath) {
		try {
			del(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getBookStorePath() {
		return bookStorePath;
	}

	public void setBookStorePath(String bookStorePath) {
		this.bookStorePath = bookStorePath;
	}

	public static void del(String filepath) throws IOException {
		File f = new File(filepath);
		if (f.exists() && f.isDirectory()) {
			if (f.listFiles().length == 0) {
				f.delete();
			} else {
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						del(delFile[j].getAbsolutePath());
					}
					delFile[j].delete();
				}
			}
		}
	}
}
