package com.dare.activity;

import com.dare.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class RewardsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
	}

public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_rewards, menu);
    return true;
}

}