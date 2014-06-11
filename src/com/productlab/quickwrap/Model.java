package com.productlab.quickwrap;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model {
	
	private JSONObject data;
	
	public static void parseJSONArray(JSONArray array, ArrayList<Model> list) {
		try {
			for (int i=0; i<array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Model m = new Model(obj);
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Model() {
		
	}
	
	public Model(JSONObject obj) {
		super();
		data = obj;
	}
	
	public Model(String str) {
		super();
		try {
			data = new JSONObject(str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			data = null;
		}
	}
	
	public Integer getInt(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		return Integer.parseInt(val);
	}
	
	public Long getLong(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		else
			return Long.parseLong(val);
	}
	
	public Boolean getBoolean(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		else
			return Boolean.parseBoolean(val);
	}
	
	public String getString(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		else
			return val;
	}
	
	public JSONArray getJSONArray(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		try {
			return new JSONArray(val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Model getSubModel(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		return new Model(val);
	}
	
	public ArrayList<Model> getSubModelArray(String field) {
		String val = this.parseKey(field);
		if (val == null)
			return null;
		ArrayList<Model> ret = new ArrayList<Model>();
		JSONArray arr = null;
		try {
			Model.parseJSONArray(new JSONArray(val), ret);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public String toJSON() {
		return data.toString();
	}
	
	private String parseKey(String key) {
		try {
			if (key.contains(".")) {
				String[] fields = key.split("\\.");
				JSONObject ret = data;
				for (int i = 0; i<fields.length -1; i++) {
					if (ret.isNull(fields[i]))
						return null;
					else
						ret = ret.getJSONObject(fields[i]);
				}
				return ret.getString(fields[fields.length-1]);
			} else {
				if (data.isNull(key))
					return null;
				else
					return data.getString(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
