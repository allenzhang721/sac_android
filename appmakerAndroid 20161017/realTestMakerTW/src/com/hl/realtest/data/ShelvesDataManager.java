package com.hl.realtest.data;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.hl.common.DataUtils;
import com.hl.common.FileUtils;
import com.hl.common.StringUtils;
import com.hl.realtest.shelves.ShelvesActivity;

/**
 * 书架项目操作数据的管理类
 * 
 * @author zhaoq
 * 
 */
public class ShelvesDataManager {
	public static String FBOOKNAME = "/21890dajas890ad82828";
	public static String SBOOKNAME = "/asqwwqu1293291292929";

	public static String C_Book_Tab_Name = "t_book";
	private static String C_Book_Tab_Create_SQL = "create table t_book(bookorder INTEGER,bookid TEXT,iconPath TEXT,bookPath Text,state INT,downUrl TEXT,booktype TEXT,bookname TEXT)";

	private static String C_Book_Insert_SQL = "insert into t_book(bookorder,iconPath,bookPath,bookid,state,downUrl,booktype,bookname)values(?,?,?,?,0,?,?,?)";
	private static String C_Book_List_SQL = " select bookorder,iconPath,bookPath,bookid,state,downUrl,booktype,bookname from t_book order by bookorder desc";
	private static String C_Book_MaxOrder_SQL = "select max(bookorder) from t_book";

	private static String C_Book_Delete_SQL = " delete from t_book where bookorder=?";
	// 创建书排的时候默认存在的书排结构,这个也是计算书排高度的除数
	public static int C_Default_Shelve_Cnt = 5;
	//
//	private static String C_Book_Exists_WithBook_SQL = " select  bookorder,iconPath,bookPath,bookid,state,downUrl,booktype,bookname  from t_book where bookid=?";
	// 修改书籍状态
	private static String C_Book_Update_Book_State_SQL = " update t_book set state=1,booktype=? where bookid=?";

	public static String Suffix = "gold.hl";

	public static String localRegisterKey = "shelve.localRegisterKey";
	public static String localSerlizableKey = "shelve.localSerlizableKey";
	public static String localRandomKey = "shelve.localRandomKey";
	// public static String Local_Reg_URL =
	// "http://192.168.1.5:8080/publish/register/realTestRegisterCode.hl?";
	public static String Reg_URL = "http://sc.hl.cn/publish/register/realTestRegisterCode.hl?";

	private static ArrayList<Book> bookList = new ArrayList<Book>();

	
	public static String GET_VERSION_URL = "http://update.qidanet.com/zwtd/android/version.xml";
	/**
	 * 检查书籍表是否存在，如果不存在那就重新创建
	 * 
	 * @param activity
	 */
	public static synchronized void detectAndCreateBookTable(Activity activity) {
		
		boolean result = DataUtils.checkTableExists(activity, C_Book_Tab_Name);
		if (!result) {
			try {
				// 创建书籍表
				DataUtils.execSQL(activity, C_Book_Tab_Create_SQL);
			} catch (Exception e) {

			}
		}
	}

	public static int getNextOrder(Activity activity) {
		Cursor cursor = DataUtils.rawQuery(activity, C_Book_MaxOrder_SQL);
		int maxOrder = 0;
		while (cursor.moveToNext()) {

			maxOrder = cursor.getInt(0) + 1;
		}
		return maxOrder;

	}

	public static void deleteBook(Activity activity, Book book) {
		synchronized(bookList){
			book.state = -1;
			Object[] p = new Object[1];
			p[0] = book.mOrder;
			DataUtils.execSQL(activity, C_Book_Delete_SQL, p);
			// 删除文件
			// String appPath = Utill.getAppDataPath(activity);
			getBookList(activity).remove(book);
			String bookBasePath = book.mData;
			// StringUtils.contactForPath(appPath, "book",
			// Integer.toString(book.mOrder));
			FileUtils.delFolder(bookBasePath);
		}
		
	}

	/**
	 * 下载完成修改数据库状态
	 * 
	 * @param activity
	 * @param book
	 */
	public static void finishBook(Activity activity, Book book) {
		book.state = 1;
		Object[] p = new Object[2];
		p[0] = book.bookType;
		p[1] = book.bookID;
		DataUtils.execSQL(activity, C_Book_Update_Book_State_SQL, p);
	}

	/**
	 * 插入book数据
	 * 
	 * @param activity
	 */
	public static void createNewBook(Activity activity, Book book) {
		book.mOrder = ShelvesDataManager.getNextOrder(activity);
		ShelvesDataManager.setBookPath(activity, book);
		synchronized (bookList) {
			//bookList.remove(book);
			Log.d("hl", "add new book");
			//if (getBookByID(activity, book.bookID) == null) {
				
				bookList.add(0, book);
				Object[] p = new Object[7];
				p[0] = book.mOrder;
				p[1] = book.mIcon;
				p[2] = book.mData;
				if (StringUtils.isEmpty(book.bookID)) {
					book.bookID = "test-" + book.mOrder;
				}
				p[3] = book.bookID;
				p[4] = book.downUrl;
				p[5] = book.bookType;
				p[6] = book.mName;

				DataUtils.execSQL(activity, C_Book_Insert_SQL, p);
			//}
		}

	}

	private static Book lastDownBook = null;

	public static Book getDownLoadBook(Activity activity) {
		/*if (lastDownBook != null && lastDownBook.state == 0) {
			return lastDownBook;
		}*/
		try {
			// 先遍历需要下载的
			for (Book b : getBookList(activity)) {
				if (b.state !=1) {
					lastDownBook = b;
					return b;
				}
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public static boolean checkNewDownLoadBook(Activity activity) {
		Book b = getDownLoadBook(activity);
		return b != lastDownBook;
	}

	/**
	 * 判断此书是否已经下载
	 * 
	 * @param activity
	 * @param id
	 *            书籍编号
	 * @return
	 */
	public static Book getBookByID(Activity activity, String id) {
		if (StringUtils.isEmpty(id))
			return null;
		// 检查书表是否存在
		
		for(Book book:getBookList(activity)){
			if(book.bookID.equals(id) && book.state!=-1) return book;
		}
		return null;
	}
	public static ArrayList<Book> initBookList(Activity activity){
		bookList = new ArrayList<Book>();
		Cursor cursor = DataUtils.rawQuery(activity, C_Book_List_SQL);
		while (cursor.moveToNext()) {
			Book book = new Book();
			book.mOrder = cursor.getInt(0);
			
			book.mData = cursor.getString(2);
			book.mIcon = book.mData + "/cover.png";
			book.bookID = cursor.getString(3);
			book.state = cursor.getInt(4);
			book.downUrl = cursor.getString(5);
			book.bookType = cursor.getString(6);
			book.mName = book.mData + "/bookName.text";
			if(book.state==1){
				bookList.add(book);
			}else{
				Object[] p = new Object[1];
				p[0] = book.mOrder;
				DataUtils.execSQL(activity, C_Book_Delete_SQL, p);
				String bookBasePath = book.mData;
				FileUtils.delFolder(bookBasePath);
			}
		}
//		//先把默认的书籍放进去
//		Book defaultBook = new Book();
//		defaultBook.mOrder = -9999999;
//		defaultBook.isReadOnly = true;
//		defaultBook.state=1;
//		bookList.add(defaultBook);
		return bookList;
	}

	public static ArrayList<Book> getBookList(Activity activity) {
		return bookList;
		/*if (bookList != null )return bookList;
		synchronized (bookList) {
			
			bookList.clear();
			Cursor cursor = DataUtils.rawQuery(activity, C_Book_List_SQL);

			while (cursor.moveToNext()) {
				Book book = new Book();
				book.mOrder = cursor.getInt(0);
				book.mIcon = cursor.getString(1);
				book.mData = cursor.getString(2);
				book.bookID = cursor.getString(3);
				book.state = cursor.getInt(4);
				book.downUrl = cursor.getString(5);
				book.bookType = cursor.getString(6);
				book.mName = cursor.getString(7);
				bookList.add(book);
			}
			return bookList;
		}*/
	}

	/**
	 * 设置书籍的书籍位置和图标位置
	 * 
	 * @param book
	 */
	public static void setBookPath(Activity activity, Book book) {
		//String appPath = Utill.getAppDataPath(activity);
		String appPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hl/realtesttw/";
		//DataUtils.initDBFilePath(dbPath);
		
		String bookPath = StringUtils.contactForPath(appPath, "book",
				book.bookID);
		// String iconPath = StringUtils.contactForFile(bookPath,"icon.png");
		// String bookPath = StringUtils.contactForPath(bookBasePath,"book");

		// book.mIcon = iconPath;
		book.mData = bookPath;
	}

	public static void initBookDownRunnable(ShelvesActivity activity) {
		if (bookDownRunnable == null) {
			bookDownRunnable = new BookDownRunnable(activity);
			Thread t = new Thread(bookDownRunnable);
			t.setDaemon(true);
			t.start();
		}

	}

	private static BookDownRunnable bookDownRunnable;

	public static void notifyBookDownRunnable(ShelvesActivity activity) {
		if (bookDownRunnable == null) {
			Log.d("hl", "why bookDownRunnable is null?");
			return;
		}
		if(!BookDownRunnable.isDown){
			synchronized (bookDownRunnable) {
				bookDownRunnable.notify();
			}
		}
	}

	public static void releaseBookDownRunnable() {
		if (bookDownRunnable != null) {
			bookDownRunnable = null;
			return;
		}
	}
	
}
