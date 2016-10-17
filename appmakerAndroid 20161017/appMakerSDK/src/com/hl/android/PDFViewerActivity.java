package com.hl.android;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.acts.BaseViewerActivity;
import org.vudroid.pdfdroid.codec.PdfContext;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PDFViewerActivity extends BaseViewerActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected DecodeService createDecodeService() {
		return new DecodeServiceBase(new PdfContext());
	}

}
