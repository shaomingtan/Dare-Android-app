package com.dare.activity;

import com.dare.R;
import com.dare.db.ChallengeTable;
import com.dare.fragment.SubmissionsListFragment;
import com.dare.model.Challenge;
import com.dare.model.ChallengeProvider;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ChallengeSubmissionsActivity extends FragmentActivity {

	private Challenge 				_challenge;
	private TextView 				_titleLabel;
	private SubmissionsListFragment	_listFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_submissions);                
        
        Uri challengeUri = null;
        long challengeId = -1;
        if (savedInstanceState != null) {
        	challengeUri =  (Uri) savedInstanceState.getParcelable(ChallengeProvider.CONTENT_ITEM_TYPE);
        	challengeId =  savedInstanceState.getLong(ChallengeProvider.CONTENT_ID_KEY);
        }        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			challengeUri = (Uri) extras.getParcelable(ChallengeProvider.CONTENT_ITEM_TYPE);
			challengeId =  extras.getLong(ChallengeProvider.CONTENT_ID_KEY);
		}

		_titleLabel = (TextView) findViewById(R.id.challenge_detail_title);	
		_titleLabel.setBackgroundResource(R.color.gRed);
		
		_listFragment = new SubmissionsListFragment();
        _listFragment.setArguments(getIntent().getExtras());        
        _listFragment.setChallengeId(challengeId);
        getSupportFragmentManager().beginTransaction().add(R.id.challenge_detail_linear, _listFragment).commit();
		
		loadChallenge(challengeUri);
    }
    
    public void loadChallenge(Uri challengeUri)
    {
    	if (challengeUri != null) {    		
    		String[] projection = { ChallengeTable.COLUMN_BRAND_NAME, ChallengeTable.COLUMN_TITLE, ChallengeTable.COLUMN_DESCRIPTION, ChallengeTable.COLUMN_ID };    		
    		Cursor cursor = getContentResolver().query(challengeUri, projection, null, null, null);
    		
    		if (cursor != null && cursor.moveToFirst()) {
    			_challenge = ChallengeProvider.cursorToChallenge(cursor);
    		}
    		
    		if (_challenge != null){
    			_titleLabel.setText(_challenge.getTitle());
    		}
    	}
    }
    
    public void refresh(View view) {
    	ConnectivityManager connMgr = (ConnectivityManager)     	
    			getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		_listFragment.refresh();            
        } 
    	else {
            //TODO show error
        }
    }
    
    public void launchCamera(View view) {
    	Intent i = new Intent(this, CameraActivity.class);	
    	i.putExtra(ChallengeProvider.CONTENT_ID_KEY, _challenge.getId());
		startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_challenge_submissions, menu);
        return true;
    }

    
}
