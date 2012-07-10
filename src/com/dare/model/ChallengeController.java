package com.dare.model;

import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;

public class ChallengeController {
	private ChallengeDbHelper _db;	
	
	public ChallengeController(Context context){
		_db = new ChallengeDbHelper(context);
	}
		
	public void updateDbWithRefreshResponse(JSONArray response)
	{
		_db.open();
		
		if (response != null && response.length() > 0)
		{
			for (int i = 0; i < response.length(); i++) {
				try{
					Challenge challenge = new Challenge(response.getJSONObject(i)); 
					_db.addChallenge(challenge);
				} catch (JSONException ex) {}
			}
		}
		
		_db.close();
	}		
	
}
