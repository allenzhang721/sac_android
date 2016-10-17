package com.hl.android.core.helper.behavior;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.artifex.mupdf.MuPDFActivity;
import com.hl.android.book.entity.BehaviorEntity;
import com.hl.android.controller.BookController;
import com.hl.android.controller.PdfController;
import com.hl.android.core.utils.FileUtils;
/**
 * GoToUrlAction事件处理
 * @author hl
 * @version 1.0
 * @createed 2013-11-5
 */
public class GoToUrlAction extends BehaviorAction {
	@Override
	public void doAction(BehaviorEntity entity) {
		super.doAction(entity);
		try {
			if (entity.Value.startsWith("pdffile://") == true) { // æ‰“å¼€pdfæ–‡ä»¶
				PdfController.getInstance().destroy();
				String fileName = entity.Value.substring(10,
						entity.Value.length());
				FileUtils.getInstance()
						.copyFileToData(
								BookController.getInstance().getViewPage()
										.getContext(), fileName);
				Uri uri = Uri.fromFile(FileUtils.getInstance()
						.getDataFile(
								BookController.getInstance().getViewPage()
										.getContext(), fileName));
				final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setClass(BookController.getInstance().getViewPage()
						.getContext(), MuPDFActivity.class);
				BookController.getInstance().getViewPage().getContext()
						.startActivity(intent);
			} else if (entity.Value.startsWith("video://") == true) {
			} else {
				Intent browse = new Intent(Intent.ACTION_VIEW,
						Uri.parse(entity.Value));
				BookController.getInstance().getViewPage().getContext()
						.startActivity(browse);
			}

		} catch (Exception ex) {
			Log.e("hl", " ViewPage go to URL", ex);
		}
	}
}
