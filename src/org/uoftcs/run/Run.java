package org.uoftcs.run;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Run extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Set up click listeners for both buttons
        View newButton = findViewById(R.id.new_button);
        newButton.setOnClickListener(this);
        View exitButton = findViewById(R.id.exit_button); 
        exitButton.setOnClickListener(this);
    }
    
    /** Perform a certain action based on the button clicked. */
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.new_button:
    		Intent i = new Intent(this, Game.class);
    		startActivity(i);
    		break;
    	case R.id.exit_button:
    		finish();
    		break;
    	}
    }
}