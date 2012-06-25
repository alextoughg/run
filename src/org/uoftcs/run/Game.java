//Source(s): http://android-journey.blogspot.ca/2010/01/android-gestures.html
//           http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/TextToSpeechActivity.html
//           http://stackoverflow.com/questions/8848909/get-tilt-angle-android

package org.uoftcs.run;

import org.uoftcs.run.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class Game extends Activity implements SimpleGestureListener {

	private static final String TAG = "Game";
	/** Path of obstacles and distances between them */
	private String [] path =  {"B2", "L2", "L2", "L3", "L3"};
	/** Index of current node in path */
	private int current_node = 0;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private boolean LEFT_ON = false;
	private boolean RIGHT_ON = false;
	/** For detection of swipes */
	private SimpleGestureFilter detector; 
	/** For playing sounds */
	Sound sound;
	private static long clip_length = 3000;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement);
		// Initialize swipe detector
		detector = new SimpleGestureFilter(this,this);
		sound = new Sound(this);
	}
	
    @Override 
	public boolean dispatchTouchEvent(MotionEvent me){ 
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me); 
	}
    
    @Override
	public void onDoubleTap() {startGame();}

	/** We count user mistakes here. */
	@Override
	public void onSwipe(int direction) {
		switch (direction) {
		case SimpleGestureFilter.SWIPE_RIGHT :
			Log.d(TAG, "Swiped right");
			if (RIGHT_ON) {Log.d(TAG, "Correct right swipe");}
			else {Log.d(TAG, "Incorrect right swipe");}
			break;
		case SimpleGestureFilter.SWIPE_LEFT :
			Log.d(TAG, "Swiped left");
			if (LEFT_ON) {Log.d(TAG, "Correct left swipe");}
			else {Log.d(TAG, "Incorrect left swipe");}
			break;
		case SimpleGestureFilter.SWIPE_DOWN :  
			Log.d(TAG, "Swiped down");
			break;
		case SimpleGestureFilter.SWIPE_UP : 
			Log.d(TAG, "Swiped up");
			break;
		} 
	}
    
    public void pause (long milisec) {
    	long t = System.currentTimeMillis();
    	long end = t + milisec;
    	while (System.currentTimeMillis() < end) {;}
    }
    
    public void startGame() {
    	for(int i=0; i<5; i++) {
    		Log.d(TAG, "Here");
    		String node = path[current_node];
    		String code = node.substring(0, 1);
    		final int straightPathLength = 1000 * (Integer.parseInt(node.substring(1, 2)));
    		if(code.equals("L")) {
    			new Thread(new Runnable() {
    				public void run() {
    					LEFT_ON = true;
    					sound.playSound(LEFT);
    					pause(clip_length);
    					LEFT_ON = false;
    				}
    			}).start();
    			pause(straightPathLength);
    		}
    		else if(code.equals("R")) {
    			RIGHT_ON = true;
    			sound.playSound(RIGHT);
                pause(clip_length);
                RIGHT_ON = false;
    		}
    		else {
    			new Thread(new Runnable() {
    				public void run() {
    					pause(straightPathLength);
    				}
    			}).start(); 
    		}
    		current_node++;
    	}
    	endGame();
    }
    
    /** Indicate end of game when finished or when mistake is committed. */
    public void endGame() {
    	Log.d(TAG, "Game over");
    }
}