package com.dare.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class Submission {
	public static final String ID_KEY = "id";
	public static final String CHALLENGE_ID_KEY = "challenge_id";
	public static final String CONTENT_URL_KEY = "content_url";
	public static final String DESCRIPTION_KEY = "description";
	public static final String CREATED_AT_KEY = "created_at";
	public static final String UPDATED_AT_KEY = "updated_at";
	
	private long   	_id;
	private long 	_challenge_id;
	private String 	_content_url;	
	private String 	_description;
	private String  _local_path;
	private String 	_created_at;
	private String 	_updated_at;
	
	public Submission(){
		_id = -1;
	}
	
	public Submission(JSONObject obj){
		_id = -1;
		_local_path = null;
		
		try{
			_id = obj.getLong(ID_KEY);
			_challenge_id = obj.getLong(CHALLENGE_ID_KEY);
			_content_url = obj.getString(CONTENT_URL_KEY);
			_description = obj.getString(DESCRIPTION_KEY);
			_created_at = obj.getString(CREATED_AT_KEY);
			_updated_at = obj.getString(UPDATED_AT_KEY);
		} catch (Exception ex) {}				
	}
	
	public String getContentFileName(){
		String response = null;
		
		if (_content_url != null){
			try{
				Uri uri = Uri.parse(_content_url);
				response =  uri.getLastPathSegment();
			}
			catch(Exception ex) {
				// shouldn't be a big problem
			}
		}
		return response;
	}
	
	public JSONObject toJson(){
		JSONObject rootObj = new JSONObject();
		JSONObject submissionObj = new JSONObject();
		
		try{
			if (_id != -1){
				submissionObj.put(ID_KEY, _id);
			}
			submissionObj.put(CHALLENGE_ID_KEY, _challenge_id);
			submissionObj.put(CONTENT_URL_KEY, _content_url);
			submissionObj.put(DESCRIPTION_KEY, _description);			
			
			rootObj.put("submission", submissionObj);
		}
		catch(JSONException jsonEx){
			return null;
		}
		
		return rootObj;
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
	public String getContentUrl(){
		return _content_url;		
	}
	public void setContentUrl(String contentUrl){
		_content_url = contentUrl;
	}
	public String getDescription(){
		return _description;
	}
	public void setDescription(String description){
		_description = description;
	}
	public String getLocalPath(){
		return _local_path;
	}
	public void setLocalPath(String localPath){
		_local_path = localPath;
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
