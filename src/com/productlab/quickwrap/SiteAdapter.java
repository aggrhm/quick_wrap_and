package com.productlab.quickwrap;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	private static HashMap<String, SiteAdapter> adapters = new HashMap<String, SiteAdapter>();
	
	public AsyncHttpClient client = new AsyncHttpClient();
	public final int STATE_READY = 1;
	public final int STATE_PAUSED = 2;
	
	private String HOST_URL = "http://localhost:80";
	private ArrayList<Request> requests = new ArrayList<Request>();
	private ArrayList<AdapterListener> listeners = new ArrayList<AdapterListener>();
	private int state = STATE_READY;
	
	
	
	public static SiteAdapter registerAdapter(String s, String host, Context c) {
		SiteAdapter a = new SiteAdapter(host, c);
		adapters.put(s, a);
		return a;
	}
	
	public static SiteAdapter getAdapter(String s) {
		return adapters.get(s);
	}
	
	public SiteAdapter(String host_url, Context c) {
		HOST_URL = host_url;
		PersistentCookieStore cstore = new PersistentCookieStore(c);
		client.setCookieStore(cstore);
	}
	
	public void setHeader(String key, String val) {
		if (val != null)
			client.addHeader(key, val);
		else
			client.removeHeader(key);
	}
	
	public void get(String url, RequestParams params, Responder responder) {
		connect("GET", url, params, responder);
	}

	public void post(String url, RequestParams params, final Responder responder) {
		connect("POST", url, params, responder);
	}
	
	public void connect(String method, String url, RequestParams params, final Responder responder) {
		// build request
		Request r = new Request();
		r.method = method;
		r.url = url;
		r.params = params;
		r.responder = responder;
		
		for (AdapterListener l : listeners) {
			l.beforeRequest(this, r);
		}
		
		if (this.state == STATE_READY) {
			executeRequest(r);
		} else {
			r.responder.onStart();
			requests.add(r);
		}
	}
	
	private void executeRequest(Request r) {
		Log.d("debug", "Issuing request: " + r.toSummaryString());
		if (r.method.equals("GET")) {
			client.get(getAbsoluteUrl(r.url), r.params, r.responder);
		} else if (r.method.equals("POST")) {
			client.post(getAbsoluteUrl(r.url), r.params, r.responder);
		}
	}
	
	public void processRequests() {
		for (Request r : requests) {
			this.executeRequest(r);
			requests.remove(r);
		}
	}
	
	public void pauseRequests() {
		this.state = STATE_PAUSED;
	}
	
	public void resumeRequests() {
		this.state = STATE_READY;
		processRequests();
	}
	
	public void addAdapterListener(AdapterListener l) {
		listeners.add(l);
	}

	private String getAbsoluteUrl(String relativeUrl) {
		return HOST_URL + relativeUrl;
	}
	
	public static class AdapterListener {
		public void beforeRequest(SiteAdapter a, Request r) {
			
		}
		
		public void afterResponse(SiteAdapter a) {
			
		}
	}
	
	
	public static class Request {
		public String method;
		public String url;
		public RequestParams params;
		public Responder responder;
		
		public String toSummaryString() {
			return method + " " + url;
		}
	}
	
	public static abstract class Response {
		protected String body;
		protected int status;
		
		public Response() { }
		public Response(String body, int status) {
			this.body = body;
			this.status = status;
		}
		
		public String body() {
			return body;
		}
		
		
	}
	
	public static class JSONResponse extends Response {
		protected JSONObject body_json = null;
		
		public JSONResponse() {
			super();
		}
		
		public JSONResponse(String body, int status) {
			super(body, status);
			try {
				this.body_json = new JSONObject(body);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public JSONObject json() {
			return this.body_json;
		}
		
		public boolean isOk() {
			return this.status == 200;
		}
		
		public String getString(String key) {
			if (this.body_json == null) return null;
			try {
				return this.body_json.getString(key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	public static class APIResponse extends JSONResponse {
		
		public APIResponse() {
			super();
			
		}
		
		public APIResponse(String body, int status) {
			super(body, status);
			try {
				this.body_json = new JSONObject(body);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				this.setError("An error occurred at the server.", 500);
			}
		}
		public JSONArray dataArray() {
			try {
				return this.body_json.getJSONArray("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		public JSONObject dataObj() {
			try {
				return this.body_json.getJSONObject("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		public String dataString() {
			try {
				return this.body_json.getString("data");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		public void setError(String msg, int meta) {
			try {
				this.body_json = new JSONObject("{\"data\" : {}, \"meta\" : 500, \"error\" : \"An error occurred.\", \"success\" : false}");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public int meta() {
			int ret;
			try {
				ret = this.body_json.getInt("meta");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret = -1;
			}
			return ret;
		}
		
		public boolean isOk() {
			return this.status == 200 && this.meta() == 200;
		}
		
		public String getErrorMessage() {
			try {
				return this.body_json.getString("error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Could not parse server response";
			}
		}
	
	}
	
	// INTERNAL RESPONSE HANDLER
	public static class Responder extends AsyncHttpResponseHandler {
		
		@Override
		public void onStart() {
			
		}
		
		@Override
		public void onProgress(int bytesWritten, int totalSize) {
			int percent = Math.round( (bytesWritten / (float)totalSize) * 100 );
			onProgress(bytesWritten, totalSize, percent);
		}
		
		public void onProgress(int bytesWritten, int totalSize, int percent) {
			
		}
		
		@Override
		public void onSuccess(int statusCode, String response) {
			Log.d("debug", statusCode + " - " + response);
			onResponse(statusCode, response);
		}
		
		@Override
		public void onFailure(int statusCode, Throwable e, String response) {
			if (response != null) Log.d("debug", statusCode + " - " + response);
			onResponse(statusCode, response);
		}
		
		public void onResponse(int status, String str) {
			
		}
	
	}
	
	public static class JSONResponder extends Responder {
		
		@Override
		public void onResponse(int status, String str) {
			onResponse(new JSONResponse(str, status));
		}
		
		public void onResponse(JSONResponse resp) {
			
		}
		
	}
	
	public static class APIResponder extends Responder {
		
		@Override
		public void onResponse(int status, String str) {
			onResponse(new APIResponse(str, status));
		}
		
		public void onResponse(APIResponse resp) {
			
		}
	}
	
	public static class AuthToken extends Model {
		private long expires_at;
		
		public AuthToken(JSONObject obj) {
			super(obj);
			expires_at = QuickWrap.nowTime() + this.getInt("expires_in");
			//expires_at = QuickWrap.nowTime() + 10;
		}
		
		public String getAccessToken() {
			return this.getString("access_token");
		}
		
		public String getRefreshToken() {
			return this.getString("refresh_token");
		}
		
		public boolean hasExpired() {
			return QuickWrap.nowTime() > expires_at;
		}
	}
	
}
