package com.example.akillifareler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity {

	CizimSinifi cs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);       
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		cs = new CizimSinifi(this);
		setContentView(cs);
		cs.requestFocus();
	}

}
