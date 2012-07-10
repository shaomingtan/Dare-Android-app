package com.dare;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import com.dare.R;
import com.dare.model.Challenge;
import com.dare.model.ChallengeController;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ArrayList<Challenge> _challenges;
	private ChallengeController _challengeController;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        refreshChallenges(null);
        _challenges = new ArrayList<Challenge>();
        _challengeController = new ChallengeController(this);
        
        TextView label = (TextView) findViewById(R.id.tempText);
		label.setText("Refreshing ...");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    // Uses AsyncTask subclass to download the challenge feed from the server
    public void refreshChallenges(View view) {
    	ConnectivityManager connMgr = (ConnectivityManager)     	
    			getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		new DownloadTask().execute();            
        } 
    	else {
            //TODO show error
        }
    }
    
    // AsyncTask implementation to make sure we get data in the background
    private class DownloadTask extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected Void doInBackground(Void...voids) {    		
    		fetchChallenges();
    		return null;    		    		
    	}

    	@Override
    	protected void onPostExecute(Void param) {
    		TextView label = (TextView) findViewById(R.id.tempText);
    		label.setText(Integer.toString(_challenges.size()));
    	}
    }
    
    private void fetchChallenges()
    {		
    	try
    	{
    		String urlString = "http://quiet-mist-1776.herokuapp.com/challenges.json";
    		URL url = new URL(urlString);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000 /* milliseconds */);
    		conn.setConnectTimeout(15000 /* milliseconds */);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);

    		// Starts the query
    		conn.connect();
    		InputStream stream = conn.getInputStream();
    		
    		String response = new java.util.Scanner(stream).useDelimiter("\\A").next();
    		_challengeController.updateDbWithRefreshResponse(new JSONArray(response));
    	}
    	catch (IOException ioEx) {}    	
    	catch (JSONException jsonEx) {}
    	catch (Exception ex) {}
    }

}
