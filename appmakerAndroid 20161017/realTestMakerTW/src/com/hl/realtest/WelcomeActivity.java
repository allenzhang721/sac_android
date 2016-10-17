package com.hl.realtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hl.realtest.shelves.ShelvesActivity;
import com.hl.realtestTW2.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		int screenw=getWindow().getWindowManager().getDefaultDisplay().getWidth();
		findViewById(R.id.iv_center).getLayoutParams().width=screenw*440/640;
		findViewById(R.id.iv_center).getLayoutParams().height=screenw*438/640;
		ScreenAdapter.setScreenSize(this);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				ScreenAdapter.setScreenSize(WelcomeActivity.this);
				Intent intent = new Intent(WelcomeActivity.this,
						ShelvesActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);

	}
}
