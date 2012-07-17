package com.dare.activity;

import com.dare.R;
import com.dare.fragment.ChallengesListFragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.support.v4.app.FragmentActivity;;

public class ChallengesActivity extends FragmentActivity {

	private ChallengesListFragment _listFragment;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);
        
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
