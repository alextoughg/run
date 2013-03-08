//Source(s): http://android-journey.blogspot.ca/2010/01/android-gestures.html
//           http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/TextToSpeechActivity.html
//           http://stackoverflow.com/questions/8848909/get-tilt-angle-android

package org.uoftcs.run;

import org.uoftcs.run.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameBeta extends Activity implements SimpleGestureListener {

	private static final String TAG = "Game";
	/** For detection of swipes */
	private SimpleGestureFilter detector; 
    /** Path of obstacles and distances between them */
	private String [] path =  {"2", "L2", "R2", "J3", "S3"};
	/** Index of current node in path */
	private int current_node = 0;
	/** Time that obstacle is presented at */
	private long obstacle_time = 0;
	/** Time that user swipes */
	private long user_swipe_time = 0;
	/** The amount of stumbles of the player */
	private int stumbles = 0;
	/** Maximum amount of stumbles the user can have 
	 * before the level is failed */
	private final int STUMBLES_MAX = 3;
	/** For playing sounds */
	Sound sound = new Sound(this, 0);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement);
		// Initialize swipe detector
		detector = new SimpleGestureFilter(this,this);
	}
	
    @Override 
	public boolean dispatchTouchEvent(MotionEvent me){ 
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me); 
	}

	/** We count user mistakes here. */
	@Override
	public void onSwipe(int direction) {
		Log.d(TAG, "Swiped");
		user_swipe_time = System.currentTimeMillis();
		// TODO: Classify subtypes of obstacles
		if(user_swipe_time > obstacle_time + 2000) {
			Log.d(TAG, "Took too long to swipe!");
		}
		else {;}
		switch (direction) {
		case SimpleGestureFilter.SWIPE_RIGHT :
			move("R");
			break;
		case SimpleGestureFilter.SWIPE_LEFT :
			move("L");
			break;
		case SimpleGestureFilter.SWIPE_DOWN :  
			move("S");
			break;
		case SimpleGestureFilter.SWIPE_UP : 
			move("J");
			break;
		} 
	}

	@Override
	public void onDoubleTap() {
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show(); 
	}
    
    /** Recreates straight path for sec seconds. */
    public void straightPath (int sec) {
    	long t = System.currentTimeMillis();
    	long end = t + (sec*1000);
    	while (System.currentTimeMillis() < end) {;}
    }
    
    public void startGame() {
    	int initialWait = Integer.parseInt(path[current_node]);
    	straightPath(initialWait);
    	// We have approached first obstacle. 
    	obstacle_time = System.currentTimeMillis();
    	current_node++;
    	// TODO
    }
    
    /** Moves along the path by one node. */
    public void move(String dir) {
    	String node = path[current_node];
    	String current_dir = node.substring(0, 1);
    	int edge = Integer.parseInt(node.substring(1));
    	if (!dir.equals(current_dir)) {
    		if(dir.equals("L") || dir.equals("R") || dir.equals("S")) {
    			endGame(1);
    			return;
    		}
    		else {
    			stumbles += 1;
    			if (stumbles == STUMBLES_MAX) {
    				endGame(1);
    				return;
    			}
    		}
    	}
    	else {;}
    	straightPath(edge);
    	current_node++;
    	if(current_node == path.length) {
    	    endGame(0);
    	}
    	else {
    		// TODO
    		/*mTts.speak(decode(path[current_node].substring(0, 1)),
    				TextToSpeech.QUEUE_FLUSH,
    				null);*/
    		obstacle_time = System.currentTimeMillis();
    	}
    }
    
    /** The full string representation of code. */
    public String decode (String code) {
    	String result = "";
    	if(code.equals("L")) {
    		result = "Left";
    	}
    	else if(code.equals("R")) {
    		result = "Right";
    	}
    	else if (code.equals("J")) {
    		result = "Jump";
    	}
    	else {
    		result = "Slide";
    	}
    	return result;
    }
    
    /** Indicate end of game when finished or when mistake is committed. */
    public void endGame(int reason) {
    	switch (reason) {
    	case 0: /*mTts.speak("Level finished!",
				TextToSpeech.QUEUE_FLUSH,
				null);*/
    	        // Move on to next level
    	break;
    	case 1: /*mTts.speak("You are dead. Try again?",
				TextToSpeech.QUEUE_FLUSH,
				null);*/
    	// TODO: Prompt user if they would like to start level again.
    	straightPath(2);
    	restartGame();
    	break;
    	}
    }
    
    public void restartGame() {
    	current_node = 0;
    	obstacle_time = 0;
    	user_swipe_time = 0;
    	stumbles = 0;
    	startGame();
    }
}
