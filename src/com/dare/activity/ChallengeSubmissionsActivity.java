package com.dare.activity;

import com.dare.R;
import com.dare.db.ChallengeTable;
import com.dare.model.Challenge;
import com.dare.model.ChallengeProvider;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class ChallengeSubmissionsActivity extends Activity {

	private Challenge 	_challenge;
	private TextView 	_titleLabel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_submissions);
        
        _titleLabel = (TextView) findViewById(R.id.challenge_detail_title);

		Uri challengeUri = null;
        if (savedInstanceState != null) {
        	challengeUri =  (Uri) savedInstanceState.getParcelable(ChallengeProvider.CONTENT_ITEM_TYPE);
        }        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			challengeUri = (Uri) extras.getParcelable(ChallengeProvider.CONTENT_ITEM_TYPE);			
		}		
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_challenge_submissions, menu);
        return true;
    }

    
}
