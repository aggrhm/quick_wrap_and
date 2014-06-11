package com.productlab.quickwrap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class QuickWrap {
	
	public static ImageLoader imageLoader;
	public static Context context;
	public static int progressLoadingID;
	public static String seed = "myreallylongcryptoseedforencryption";
	
	public static void initialize(Context c) {
		imageLoader = new ImageLoader(c);
		context = c;
	}
	
	public static void notifyOverlay(Context context, String msg) {
		Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		//t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
		t.show();
	}
	
	public static void setParam(String key, String val) {
		SharedPreferences settings = context.getSharedPreferences("PREFS", 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString(key, val);
	    editor.commit();
	}
	
	public static void setEncryptedParam(String key, String val) {
		try {
			QuickWrap.setParam(key, SimpleCrypto.encrypt(seed, val));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public static String getParam(String key) {
		SharedPreferences settings = context.getSharedPreferences("PREFS", 0);
		return settings.getString(key, null);
	}
	
	public static String getEncryptedParam(String key) {
		String encrypted = QuickWrap.getParam(key);
		String ret = null;
		try {
			ret = SimpleCrypto.decrypt(seed, encrypted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void showLoading(View rootView) {
		ProgressBar prog = (ProgressBar) rootView.findViewById(progressLoadingID);
		if (prog == null) {
			// create progress bar
			prog = new ProgressBar(context);
			prog.setId(progressLoadingID);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			prog.setLayoutParams(params);
			prog.setIndeterminate(true);
			((ViewGroup) rootView).addView(prog);
		}
		
		prog.setVisibility(View.VISIBLE);
	}
	
	public static void hideLoading(View rootView) {
		ProgressBar prog = (ProgressBar)rootView.findViewById(progressLoadingID);
		prog.setVisibility(View.GONE);
	}

}
