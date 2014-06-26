package com.productlab.quickwrap;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ImageViewerActivity extends Activity {
	
	public static final String URL_EXTRA = "com.productlab.spotflash.URL";
	
	private View rootView;
	private WebView webView;
	private Button btnSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		final String url = getIntent().getStringExtra(URL_EXTRA);
		
		RelativeLayout layout = new RelativeLayout(this);
		rootView = layout;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		
		setContentView(layout, lp);
		
		webView = new WebView(this);
		lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		layout.addView(webView, lp);
		
		btnSave = new Button(this);
		btnSave.setText("Save Image");
		lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp.setMargins(0, 0, 0, 5);
		layout.addView(btnSave, lp);
		btnSave.setVisibility(View.GONE);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

					@Override
					protected Boolean doInBackground(String... params) {
						// TODO Auto-generated method stub
						String path = Utils.saveURLToGallery(ImageViewerActivity.this, params[0]);
						if (path == null)
							return false;
						return true;
					}
					
					@Override
					protected void onPostExecute(Boolean success) {
						if (success == true)
							QuickWrap.notifyOverlay("Image saved.");
						else
							QuickWrap.notifyOverlay("Image could not be saved.");
					}
					
				};
				task.execute(url);
			}
		});
		
		// handle intent
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				QuickWrap.showLoading(rootView);
				btnSave.setVisibility(View.GONE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				QuickWrap.hideLoading(rootView);
				btnSave.setVisibility(View.VISIBLE);
			}
			
		});
		/*
		webView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnSave.getVisibility() == View.GONE)
					btnSave.setVisibility(View.VISIBLE);
				else
					btnSave.setVisibility(View.GONE);
			}
		});
		*/
		webView.loadUrl(url);
		
	}

}
