package com.dare.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

import com.dare.R;
import com.dare.fragment.ChallengesListFragment;

public class ChallengesActivity extends FragmentActivity {

	private ChallengesListFragment _listFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        
        _listFragment =  (ChallengesListFragment) getSupportFragmentManager().findFragmentById(R.id.list_challenges_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_challenges, menu);
        return true;
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
}
