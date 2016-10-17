package com.hl.realtest.data;



/**
 * 书本
 * 
 * @author zhaoq
 * 
 */
public class Book {
	
	// 名字
	public String mName="appMaker"; // 书籍的图片名字 比例是120*160
	public String mIcon; // 书籍对应的数据文件
	public String mData;// 书籍对应的位置
	public int mOrder = -1;
	public String bookID;//书籍id，如果是测试的话，我们就是test-morder
	public int state = 0;//0代表下载未结束，1代表下载成功,-1代表被删除 -2网络连接错误,-3下载书籍内容有误
	public String downUrl;//下载的路径
	public String bookType = BOOK_TYPE_MEB;//
	public String mSingleID;
	public Double mCurrentRate = 0.00;
	public static String BOOK_TYPE_PDF = "PDF";
	public static String BOOK_TYPE_EPUB = "EPUB";
	public static String BOOK_TYPE_MEB = "INTERACT_BOOK";
	public boolean isReadOnly = false;
	
	public String mBookCoverUrl;
	/*public RelativeLayout bookView;*/
	
	@Override
	public boolean equals(Object o) {
		if(o == null)return false;
		Book b = (Book) o;
		return bookID.equals(b.bookID);
	}
}
