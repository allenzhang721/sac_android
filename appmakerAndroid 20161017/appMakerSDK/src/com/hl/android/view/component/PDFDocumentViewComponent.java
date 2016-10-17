package com.hl.android.view.component;

import java.io.File;
import java.io.InputStream;

import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentViewNew;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.pdfdroid.codec.PdfContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.hl.android.book.entity.ComponentEntity;
import com.hl.android.book.entity.PDFComponentEntity;
import com.hl.android.core.utils.FileUtils;
import com.hl.android.view.component.bean.ViewRecord;
import com.hl.android.view.component.inter.Component;

/**
 * PDF文件view component
 * 
 * @author webcat
 * 
 */
@SuppressLint("NewApi")
public class PDFDocumentViewComponent extends DocumentViewNew implements
		Component {
	public ComponentEntity entity = null;

	public PDFDocumentViewComponent(Context context, ComponentEntity entity) {
		super(context);
		try {
			this.entity = entity;

			setDecodeService(new DecodeServiceBase(new PdfContext()));

			DecodingProgressModel progressModel = new DecodingProgressModel();
			progressModel.addEventListener(this);

			currentPageModel = new CurrentPageModel();
			currentPageModel.addEventListener(this);
			setPageModel(currentPageModel);
			setProgressModel(progressModel);

			this.zoomModel = new ZoomModel();
			initMultiTouchZoomIfAvailable(zoomModel);
			this.zoomModel.addEventListener(this);

			setKeepScreenOn(true);

			this.entity = entity;

		} catch (Exception ex) {
			Toast.makeText(this.getContext(), ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	public PDFDocumentViewComponent(Context context) {
		super(context);
	}

	@Override
	public ComponentEntity getEntity() {
		// TODO Auto-generated method stub
		return this.entity;
	}

	@Override
	public void setEntity(ComponentEntity entity) {
		this.entity = entity;

	}
 

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(InputStream is) {
		// TODO Auto-generated method stub

	}

 
	@Override
	public void setRotation(float rotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void play() {
		String storageState = Environment.getExternalStorageState();

		goToPage(Integer.valueOf(((PDFComponentEntity) entity)
				.getPdfPageIndex()) - 1);

		if (Environment.MEDIA_MOUNTED.equals(storageState)) {
			File sdPath = Environment.getExternalStorageDirectory();
			FileUtils.getInstance().copyFileToSDCard(this.getContext(),
					((PDFComponentEntity) entity).getPdfSourceID());
			openDoc(sdPath.getPath() + "/"
					+ ((PDFComponentEntity) entity).getPdfSourceID());
		}

	}

//	protected Bitmap loadPage(int index) {
//		return FileUtil.getInstance().load(this.entity.localSourceId,
//				this.getLayoutParams().width, this.getLayoutParams().height,
//				this.getContext());
//	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}
	/***************************下面都是属性动画使用相关代码*******************************/
	public ViewRecord initRecord;
	@SuppressLint("NewApi")
	public ViewRecord getCurrentRecord(){
		ViewRecord curRecord = new ViewRecord();
		curRecord.mHeight = getLayoutParams().width;
		curRecord.mWidth = getLayoutParams().height;
		
		curRecord.mX = getX();
		curRecord.mY = getY();
		curRecord.mRotation = getRotation();
//		curRecord.mAlpha = getAlpha();
		return curRecord;
	}
}
