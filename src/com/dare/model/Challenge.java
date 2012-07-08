package com.dare.model;

import org.json.JSONObject;

public class Challenge  {
	
	public static final String BRAND_STR = "brand_name";
	public static final String TITLE_STR = "title";
	public static final String DESCRIPTION_STR = "description";
	
	private String _brand;
	private String _title;
	private String _description;
	
	public Challenge(JSONObject obj)
	{
		try{
			_brand = obj.getString(BRAND_STR);
			_title = obj.getString(TITLE_STR);
			_description = obj.getString(DESCRIPTION_STR);
		} catch (Exception ex) {}				
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
}
