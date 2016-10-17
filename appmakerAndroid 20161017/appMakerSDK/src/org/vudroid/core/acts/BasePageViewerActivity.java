package org.vudroid.core.acts;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DocumentViewNew;
import org.vudroid.core.events.CurrentPageListener;
import org.vudroid.core.events.DecodingProgressListener;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.views.PageViewZoomControls;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hl.android.R;

public abstract class BasePageViewerActivity extends Activity implements
		DecodingProgressListener, CurrentPageListener {
	private static final int MENU_EXIT = 0;
	private static final int MENU_GOTO = 1;
	private static final int MENU_FULL_SCREEN = 2;
	private static final int DIALOG_GOTO = 0;
	private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";
	private DecodeService decodeService;
	private DocumentViewNew documentView;
	private ViewerPreferences viewerPreferences;
	private Toast pageNumberToast;
	private CurrentPageModel currentPageModel;
	Intent intent;
	private EditText editText;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Toast.makeText(this, "dfsdfdsfdsfdfdfdsf", Toast.LENGTH_LONG);
		setContentView(R.layout.pdfpage);
		intent = this.getIntent();
		initDecodeService();
		documentView = (DocumentViewNew) findViewById(R.id.pdf_viewnew);
		documentView.setDecodeService(decodeService);
		// //((ViewGroup)documentView.getParent()).addView(createZoomControls(documentView.zoomModel));
		//
		final DecodingProgressModel progressModel = new DecodingProgressModel();// 渲染时显示的图标
		progressModel.addEventListener(this);
		currentPageModel = new CurrentPageModel();// 页面更换时的事件，存储当前页面的索引
		currentPageModel.addEventListener(this);
		documentView.setPageModel(currentPageModel);
		documentView.setProgressModel(progressModel);
		//
		MyButtonHandler l = new MyButtonHandler();
		findViewById(R.id.btn_nextnew).setOnClickListener(l);
		findViewById(R.id.btn_prevnew).setOnClickListener(l);
		findViewById(R.id.backnew).setOnClickListener(l);
		
		findViewById(R.id.testButton).setOnClickListener(l);

		findViewById(R.id.gotopage).setOnClickListener(l);
		editText = (EditText) findViewById(R.id.pagenumber);
		// Toast.makeText(this, "dfsdfdsfdsfdfdfdsf", 4000);
		// //findViewById(R.id.pdf_toolbar).setVisibility(View.GONE);
		//
		/*final SharedPreferences sharedPreferences = getSharedPreferences(
				DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		*/
		//
		viewerPreferences = new ViewerPreferences(this);
		viewerPreferences.addRecent(getIntent().getData());
		
		documentView.setBackgroundColor(Color.WHITE);
		//
		/*
		 * Configuration cf= this.getResources().getConfiguration(); int ori =
		 * cf.orientation ; if(ori == Configuration.ORIENTATION_PORTRAIT) {
		 * documentView.setPortrait(true); } else {
		 * documentView.setPortrait(false); }
		 */
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		if (hasFocus == true){
			documentView.goToPage(0);
			documentView.openDoc(getIntent().getData().getPath());
		}
	}
	public void onConfigurationChanged(Configuration cf) {
		super.onConfigurationChanged(cf);
		documentView.refresh();
	}

	public void decodingProgressChanged(final int currentlyDecoding) {
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().setFeatureInt(
						Window.FEATURE_INDETERMINATE_PROGRESS,
						currentlyDecoding == 0 ? 10000 : currentlyDecoding);
			}
		});
	}

	/**
	 * pageIndex:当前显示页面的索引
	 */
	public void currentPageChanged(int pageIndex) {
		final String pageText = (pageIndex + 1) + "/"
				+ decodeService.getPageCount();
		if (pageNumberToast != null) {
			pageNumberToast.setText(pageText);
		} else {
			pageNumberToast = Toast.makeText(this, pageText, Toast.LENGTH_SHORT);
		}
		pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
		pageNumberToast.show();
		saveCurrentPage();
	}

	private void setWindowTitle() {
		final String name = getIntent().getData().getLastPathSegment();
		getWindow().setTitle(name);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setWindowTitle();
	}

	protected void setFullScreen() {
		viewerPreferences = new ViewerPreferences(this);
		if (viewerPreferences.isFullScreen()) {
			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		}
	}

	protected PageViewZoomControls createZoomControls(ZoomModel zoomModel) {
		final PageViewZoomControls controls = new PageViewZoomControls(this,
				zoomModel);
		controls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		zoomModel.addEventListener(controls);
		return controls;
	}

	protected FrameLayout createMainContainer() {
		return new FrameLayout(this);
	}

	private void initDecodeService() {
		if (decodeService == null) {
			decodeService = createDecodeService();
		}
	}

	protected abstract DecodeService createDecodeService();

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		decodeService.recycle();
		decodeService = null;
		documentView.recyle();
		super.onDestroy();
	}

	private void saveCurrentPage() {
		final SharedPreferences sharedPreferences = getSharedPreferences(
				DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(getIntent().getData().toString(),
				documentView.getCurrentPage());
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_EXIT, 0, "Exit");
		menu.add(0, MENU_GOTO, 0, "Go to page");
		final MenuItem menuItem = menu
				.add(0, MENU_FULL_SCREEN, 0, "Full screen").setCheckable(true)
				.setChecked(viewerPreferences.isFullScreen());
		setFullScreenMenuItemText(menuItem);
		return true;
	}

	private void setFullScreenMenuItemText(MenuItem menuItem) {
		menuItem.setTitle("Full screen "
				+ (menuItem.isChecked() ? "on" : "off"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_EXIT:
			System.exit(0);
			return true;
		case MENU_GOTO:
			showDialog(DIALOG_GOTO);
			return true;
		case MENU_FULL_SCREEN:
			item.setChecked(!item.isChecked());
			setFullScreenMenuItemText(item);
			viewerPreferences.setFullScreen(item.isChecked());

			finish();
			startActivity(getIntent());
			return true;
		}
		return false;
	}

	private class MyButtonHandler implements OnClickListener {
		public void onClick(View v) {
			if (v == findViewById(R.id.btn_prevnew))
				documentView.changePage(-1);
			else if (v == findViewById(R.id.btn_nextnew))
				documentView.changePage(+1);
			else if (v == findViewById(R.id.backnew)) {
				BasePageViewerActivity.this.setResult(0, intent);
				BasePageViewerActivity.this.finish();
			} else if (v == findViewById(R.id.gotopage)) {
				try {
					if (null != editText.getText()
							&& !editText.getText().equals("")) {
						Integer iNum = Integer.valueOf(editText.getText()
								.toString());
						if (iNum < 0) {
							return;
						}
						if (iNum > documentView.pageCount) {
							return;
						}
						iNum = iNum - 1;
						documentView.goToPage(iNum);

						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(
										BasePageViewerActivity.this
												.getCurrentFocus()
												.getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}else if (v == findViewById(R.id.testButton)) {
				documentView.setTestState(true);
				documentView.goToPage(documentView.pageIndex);
			} 
		}
	}
}
