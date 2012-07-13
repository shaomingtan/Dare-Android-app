package com.dare.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import com.dare.ChallengesListFragment;
import com.dare.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class ChallengeController {
	private ChallengeDbHelper _db;	
	private Context _context;
	
	public ChallengeController(Context context){
		_context = context;
		_db = new ChallengeDbHelper(context);
	}
	
	public void fetchChallenges()
    {		
    	try
    	{
			String urlString = (Constants.DARE_SERVICE_URL + "/challenges.json");
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
    	catch (IOException ioEx) {
    		Log.e(ChallengesListFragment.class.toString(), ("IOException Fetching Challenges: " + ioEx.toString()));
    	}    	
    	catch (JSONException jsonEx) {
    		Log.e(ChallengesListFragment.class.toString(), ("JSONException Fetching Challenges: " + jsonEx.toString()));
    	}
    	catch (Exception ex) {
    		Log.e(ChallengesListFragment.class.toString(), ("Exception Fetching Challenges: " + ex.toString()));
    	}
    }
		
	private void updateDbWithRefreshResponse(JSONArray response)
	{		
		
		if (response != null && response.length() > 0)
		{
			for (int i = 0; i < response.length(); i++) {
				try{
					Challenge challenge = new Challenge(response.getJSONObject(i));
					ContentValues challengeValues = ChallengeProvider.challengeToContentValues(challenge);
					long challengeId = challenge.getId();
					
					Date lastUpdate = _db.challengeExists(challengeId);
					if (lastUpdate == null){
						_context.getContentResolver().insert(ChallengeProvider.CONTENT_URI, challengeValues);						
					}
					else{
						Date currentUpdate = _db.dateFormatter.parse(challenge.getUpdatedAt());
						if (currentUpdate.after(lastUpdate)){
							Uri updateUri = Uri.withAppendedPath(ChallengeProvider.CONTENT_URI, String.valueOf(challengeId));
							_context.getContentResolver().update(updateUri, challengeValues, null, null);
						}
					}
				} 
				catch (JSONException jsonEx) {} 
				catch (ParseException parseEx) {}
			}
		}			
	}		
	
}
