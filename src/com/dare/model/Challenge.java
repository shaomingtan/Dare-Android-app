package com.dare.model;

import org.json.JSONObject;

public class Challenge  {
	public static final String ID_KEY = "id";
	public static final String BRAND_KEY = "brand_name";
	public static final String TITLE_KEY = "title";
	public static final String DESCRIPTION_KEY = "description";
	public static final String CREATED_AT_KEY = "created_at";
	public static final String UPDATED_AT_KEY = "updated_at";
	
	private long   _id;
	private String _brand;
	private String _title;
	private String _description;
	private String _created_at;
	private String _updated_at;
	
	public Challenge()
	{
		_id = -1;
	}
	
	public Challenge(JSONObject obj)
	{
		_id = -1;
		
		try{
			_id = obj.getLong(ID_KEY);
			_brand = obj.getString(BRAND_KEY);
			_title = obj.getString(TITLE_KEY);
			_description = obj.getString(DESCRIPTION_KEY);
			_created_at = obj.getString(CREATED_AT_KEY);
			_updated_at = obj.getString(UPDATED_AT_KEY);
		} catch (Exception ex) {}				
	}
	
	public boolean isValid(){
		if (_id == -1){ return false;}
		else if (_brand == null){ return false;}
		else if (_title == null){ return false;}
		else if (_description == null){ return false;}
		else if (_created_at == null){ return false;}
		else if (_updated_at == null){ return false;}
		
		return true;
	}
	
	public long getId(){
		return _id;
	}
	public String getBrand(){
		return _brand;
	}	
	public String getTitle(){
		return _title;
	}
	public String getDescription(){
		return _description;
	}
	public String getCreatedAt(){
		return _created_at;
	}
	public String getUpdatedAt(){
		return _updated_at;
	}
}
