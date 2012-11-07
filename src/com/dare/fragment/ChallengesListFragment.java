package com.dare.fragment;

import com.dare.Constants;
import com.dare.R;
import com.dare.activity.ChallengeSubmissionsActivity;
import com.dare.db.ChallengeTable;
import com.dare.model.ChallengeController;
import com.dare.model.ChallengeProvider;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

public class ChallengesListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> 
{
	private SimpleCursorAdapter _cursorAdapter;
	private ChallengeController _challengeController;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        _challengeController = new ChallengeController(getActivity());
        
        _cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_challenge, null,
                new String[] {ChallengeTable.COLUMN_TITLE, ChallengeTable.COLUMN_BRAND_NAME,  ChallengeTable.COLUMN_DESCRIPTION, ChallengeTable.COLUMN_CHALLENGE_REWARD  },
                new int[] { R.id.challenge_title, R.id.challenge_brand_label, R.id.challenge_description, R.id.challenge_reward}, 0);
        setListAdapter(_cursorAdapter);
        
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(Constants.LOADER_CHALLENGES, null, this);
        
        refresh();
    }
        
    // Uses AsyncTask subclass to download the challenge feed from the server
    public void refresh() {    	
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
		String[] columnProjection = { ChallengeTable.COLUMN_ID, ChallengeTable.COLUMN_BRAND_NAME, ChallengeTable.COLUMN_TITLE, ChallengeTable.COLUMN_DESCRIPTION, ChallengeTable.COLUMN_BRAND_DESC, ChallengeTable.COLUMN_CHALLENGE_REWARD };
		CursorLoader cursorLoader = new CursorLoader(getActivity(), ChallengeProvider.CONTENT_URI, columnProjection, null, null, null);
		
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		_cursorAdapter.swapCursor(cursor);		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		_cursorAdapter.swapCursor(null);		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(getActivity(), ChallengeSubmissionsActivity.class);
		Uri challengeUri = Uri.parse(ChallengeProvider.CONTENT_URI + "/" + id);
		i.putExtra(ChallengeProvider.CONTENT_ITEM_TYPE, challengeUri);
		i.putExtra(ChallengeProvider.CONTENT_ID_KEY, id);

		// Activity returns an result if called with startActivityForResult
		startActivity(i);
	}
	
}
