package com.productlab.quickwrap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class SiteAdapter {
	public static String HOST_URL = "http://localhost:80";
	
	public static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void initialize(Context c, String host_url) {
		HOST_URL = host_url;
		PersistentCookieStore cstore = new PersistentCookieStore(c);
		client.setCookieStore(cstore);
	}
	
	public static void get(String url, RequestParams params, Responder responder) {
		client.get(getAbsoluteUrl(url), params, new InternalResponseHandler(responder));
	}

	public static void post(String url, RequestParams params, final Responder responder) {
		client.post(getAbsoluteUrl(url), params, new InternalResponseHandler(responder));
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return HOST_URL + relativeUrl;
	}
	
	public static class Responder {
		
		public void onStart() {
			
		}
		
		public void onResponse(Response response) {
			
		}
		
	}
	
	public static class Response {
		private JSONObject resp;
		
		public Response(String resp) {
			try {
				this.resp = new JSONObject(resp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				this.setError("Could not parse server response", 500);
			}
		}
		public Response(JSONObject obj) {
			this.resp = obj;
		}
		public Response(String msg, int meta) {
			this.setError(msg, meta);
		}
		public JSONObject raw() {
			return this.resp;
		}
		
		public JSONArray dataArray() {
			try {
				return this.resp.getJSONArray("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		public JSONObject dataObj() {
			try {
				return this.resp.getJSONObject("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		public void setError(String msg, int meta) {
			try {
				this.resp = new JSONObject("{\"data\" : {}, \"meta\" : 500, \"error\" : \"An error occurred.\", \"success\" : false}");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public int meta() {
			int ret;
			try {
				ret = this.resp.getInt("meta");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = -1;
			}
			return ret;
		}
		
		public boolean isOk() {
			return this.meta() == 200;
		}
		
		public String getErrorMessage() {
			try {
				return this.resp.getString("error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Could not parse server response";
			}
		}
	
	}
	
	// INTERNAL RESPONSE HANDLER
	private static class InternalResponseHandler extends AsyncHttpResponseHandler {
		private Responder responder;
		
		public InternalResponseHandler(Responder responder) {
			super();
			this.responder = responder;
		}
		
		@Override
		public void onStart() {
			responder.onStart();
		}
		@Override
		public void onSuccess(String response) {
			Log.d("debug", response);
			Response resp = new Response(response);
			responder.onResponse(resp);
		}
		@Override
		public void onFailure(Throwable e, String content) {
			if (content != null) Log.d("debug", content);
			Response resp = new Response("An error occurred at the server", 500);
			
			responder.onResponse(resp);
		}
	
	}
}
