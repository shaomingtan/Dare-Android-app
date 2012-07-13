package com.dare;

import com.dare.model.ChallengeController;
import com.dare.model.ChallengeDbHelper;
import com.dare.model.ChallengeProvider;

import android.os.AsyncTask;
import android.os.Bundle;

import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class ChallengesListFragment 
	extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor> 

{

	private SimpleCursorAdapter _cursorAdapter;
	private ChallengeController _challengeController;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _challengeController = new ChallengeController(getActivity());
        
        _cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_challenge, null,
                new String[] { ChallengeDbHelper.COLUMN_BRAND_NAME, ChallengeDbHelper.COLUMN_TITLE, ChallengeDbHelper.COLUMN_DESCRIPTION },
                new int[] {R.id.challenge_brand_label, R.id.challenge_title, R.id.challenge_description}, 0);
        setListAdapter(_cursorAdapter);
        
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
        
        //refreshChallenges();
    }
        
    // Uses AsyncTask subclass to download the challenge feed from the server
    public void refreshChallenges() {    	
    	new RefreshTask().execute();                    
    }
    
    // AsyncTask implementation to make sure we get data in the background
    private class RefreshTask extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected Void doInBackground(Void...voids) {    		
    		_challengeController.fetchChallenges();
    		return null;    		    		
    	}

    	@Override
    	protected void onPostExecute(Void param) {    		
    		
    	}
    }

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] columnProjection = { ChallengeDbHelper.COLUMN_ID, ChallengeDbHelper.COLUMN_BRAND_NAME, ChallengeDbHelper.COLUMN_TITLE, ChallengeDbHelper.COLUMN_DESCRIPTION };
		CursorLoader cursorLoader = new CursorLoader(getActivity(), ChallengeProvider.CONTENT_URI, columnProjection, null, null, null);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		_cursorAdapter.swapCursor(cursor);		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		_cursorAdapter.swapCursor(null);		
	}

}
