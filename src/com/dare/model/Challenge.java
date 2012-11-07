package com.dare.model;

import org.json.JSONObject;

public class Challenge  {
	public static final String ID_KEY = "id";
	public static final String BRAND_KEY = "brand_name";
	public static final String BRANDDESC_KEY = "brand_description";
//	public static final String BRANDLOGO_KEY = "brand_logo_url";
//	public static final String CHALLENGEPHOTO_KEY = "challenge_cover_photo";
	public static final String CHALLENGEREWARD_KEY = "challenge_reward";
	public static final String TITLE_KEY = "title";
	public static final String DESCRIPTION_KEY = "description";
	public static final String CREATED_AT_KEY = "created_at";
	public static final String UPDATED_AT_KEY = "updated_at";
	
	private long   _id;
	private String _brand;
	private String _branddesc;
//	private String _brandlogo;
//	private String _challengephoto;
	private String _challengereward;
	private String _title;
	private String _description;
	private String _created_at;
	private String _updated_at;
	
	public Challenge(){
		_id = -1;
	}
	
	public Challenge(JSONObject obj){
		_id = -1;
		
		try{
			_id = obj.getLong(ID_KEY);
			_brand = obj.getString(BRAND_KEY);
			_branddesc = obj.getString(BRANDDESC_KEY);
//			_brandlogo = obj.getString(BRANDLOGO_KEY);
//			_challengephoto = obj.getString(CHALLENGEPHOTO_KEY);
			_challengereward = obj.getString(CHALLENGEREWARD_KEY);
			_title = obj.getString(TITLE_KEY);
			_description = obj.getString(DESCRIPTION_KEY);
			_created_at = obj.getString(CREATED_AT_KEY);
			_updated_at = obj.getString(UPDATED_AT_KEY);
		} catch (Exception ex) {}				
	}
		
	public long getId(){
		return _id;
	}
	public void setId(long newId){
		_id = newId;
	}
	public String getBrand(){
		return _brand;
	}	
	public void setBrand(String brand){
		_brand = brand;
	}
	
	public String getBrandDesc(){
		return _branddesc;
	}	
	public void setBrandDesc(String branddesc){
		_branddesc = branddesc;
	}
	
//	public String getBrandLogo(){
//		return _brandlogo;
//	}	
//	public void setBrandLogo(String brand){
//		_brandlogo = brand;
//	}
	
//	public String getChallengePhoto(){
//		return _challengephoto;
//	}	
//	public void setChallengePhoto(String challengephoto){
//		_challengephoto = challengephoto;
//	}

	public String getChallengeReward(){
		return _challengereward;
	}	
	public void setChallengeReward(String challengereward){
		_challengereward = challengereward;
	}
	
	public String getTitle(){
		return _title;
	}
	public void setTitle(String title){
		_title = title;
	}
	public String getDescription(){
		return _description;
	}
	public void setDescription(String description){
		_description = description;
	}
	public String getCreatedAt(){
		return _created_at;
	}
	public void setCreatedAt(String createdAt){
		_created_at = createdAt;
	}
	public String getUpdatedAt(){
		return _updated_at;
	}
	public void setUpdatedAt(String updatedAt){
		_updated_at = updatedAt;
	}
}
