package com.dare.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import com.dare.MainActivity;
import com.dare.R;

import android.content.Context;
import android.util.Log;

public class ChallengeController {
	private ChallengeDbHelper _db;	
	
	public ChallengeController(Context context){
		_db = new ChallengeDbHelper(context);
	}
	
	public void fetchChallenges()
    {		
    	try
    	{
    		String urlString = (R.string.dare_service_url + "/challenges.json");
    		URL url = new URL(urlString);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000 /* milliseconds */);
    		conn.setConnectTimeout(25000 /* milliseconds */);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);

    		// Starts the query
    		conn.connect();
    		InputStream stream = conn.getInputStream();
    		
    		String response = new java.util.Scanner(stream).useDelimiter("\\A").next();
    		updateDbWithRefreshResponse(new JSONArray(response));
    	}
    	catch (IOException ioEx) {}    	
    	catch (JSONException jsonEx) {}
    	catch (Exception ex) {
    		Log.e(MainActivity.class.toString(), ("Error Fetching Challenges: " + ex.toString()));
    	}
    }
		
	private void updateDbWithRefreshResponse(JSONArray response)
	{
		_db.open();
		
		if (response != null && response.length() > 0)
		{
			for (int i = 0; i < response.length(); i++) {
				try{
					Challenge challenge = new Challenge(response.getJSONObject(i));
					Date lastUpdate = _db.challengeExists(challenge.getId());
					if (lastUpdate == null){
						_db.addChallenge(challenge);
					}
					else{
						Date currentUpdate = _db.dateFormatter.parse(challenge.getUpdatedAt());
						if (currentUpdate.after(lastUpdate)){
							_db.updateChallenge(challenge);
						}
					}
				} 
				catch (JSONException jsonEx) {} 
				catch (ParseException parseEx) {}
			}
		}
		
		_db.close();
	}		
	
}
