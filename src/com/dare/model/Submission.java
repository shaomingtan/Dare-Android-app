package com.dare.model;

import org.json.JSONObject;

public class Submission {
	public static final String ID_KEY = "id";
	public static final String CHALLENGE_ID_KEY = "challenge_id";
	public static final String DESCRIPTION_KEY = "description";
	public static final String CREATED_AT_KEY = "created_at";
	public static final String UPDATED_AT_KEY = "updated_at";
	
	private long   	_id;
	private long 	_challenge_id;
	private String 	_description;
	private String 	_created_at;
	private String 	_updated_at;
	
	public Submission(){
		_id = -1;
	}
	
	public Submission(JSONObject obj){
		_id = -1;
		
		try{
			_id = obj.getLong(ID_KEY);
			_challenge_id = obj.getLong(CHALLENGE_ID_KEY);			
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
	public long getChallengeId(){
		return _challenge_id;
	}
	public void setChallengeId(long challengeId){
		_challenge_id = challengeId;
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
