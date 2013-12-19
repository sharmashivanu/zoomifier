package com.zoomactivity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class DocumentView extends Activity{
	//WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.document_view);
		overridePendingTransition(R.anim.zoom_out_animation, R.anim.zoom_in_popupview);
		//webView=(WebView) findViewById(R.id.webview);
		
	   	
		
	}
	
 
	
}
