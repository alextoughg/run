//Source(s): http://android-journey.blogspot.ca/2010/01/android-gestures.html
//           http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/TextToSpeechActivity.html
//           http://stackoverflow.com/questions/8848909/get-tilt-angle-android

// TODO: accomodate grace periods, and random sounds for 
// right, down and up obstacles.
package org.uoftcs.run;

import java.util.Calendar;
import java.util.Random;

import org.uoftcs.run.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity implements SimpleGestureListener {

	private static final String TAG = "Game";
	/** Path of obstacles and distances between them */
	private String [] path =  {"1", "R",  "0.5", "L", "0.2", 
			                   "L",  "0.1", "R", "2", "LVL2", 
			                   "0.2", "L", "0.2", "R", "0.3", 
			                   "D", "0.1", "R", "D", "2", 
			                   "LVL3", "0.2", "D", "0.1", 
			                   "R", "U", "0.1", "XX", "L", 
			                   "U", "D", "R", "D", "L", "U","R","3"};
	//private String [] path = {"1", "R", "1", "R", "1", "R", "3"};
	/* With each x we reduce the grace period logarithmically. */
	/** Index of current node in path */
	private int current_node = 0;
	private static final int WALK = -1; 
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	//private static final int CAT = 2;
	private static final int SAFE = 3;
	private static final int GUNS = 4;
	private static final int CITY = 5;
	//private static final int MONKEY = 5;
	private static final int BABY_CRY = 6;
	private static final int BABY_LAUGH = 7;
	private static final int YES = 8;
	private static final int WOOHOO = 9;
	private static final int OUCH = 10;
	//private static final int DAMN = 11;
	private static final int SPLAT = 12;
	private static final int CRASH = 13;
	//private static final int DOINK = 14;
	private static final int LION_LEFT = 15;
	//private static final int AIRPLANE = 16;
	private static final int VAN_HALEN = 17;
	private static final int ELEPHANT_RIGHT = 18;
	private static final int TOO_EASY = 19;
	private static final int STRONG = 20;
	private boolean LEFT_ON = false;
	private boolean RIGHT_ON = false;
	private boolean DOWN_ON = false;
	private boolean UP_ON = false;
	private boolean SWIPED = false;
	/** For detection of swipes */
	private SimpleGestureFilter detector; 
	/** For playing sounds */
	Sound sound0;
	Sound sound1;
	Sound sound2;
	private static long clip_length_car = 1500;
	private static long clip_length_car_p = 1500;
	//private static long clip_length_cat = 1590;
	private static long clip_length_safe = 1200;
	private static long clip_length_guns = 1530;
	private static long clip_length_guns_p = 1530;
	private static long clip_length_baby_cry = 5000;
	private static long clip_length_baby_laugh = 10000;
	private static long clip_length_yes = 1132;
	private static long clip_length_woohoo = 1000;
	private static long clip_length_ouch = 700;
	//private static long clip_length_damn = 700;
	private static long clip_length_splat = 700;
	private static long clip_length_crash = 466;
	//private static long clip_length_doink = 1300;
	private static long clip_length_lion = 933;
	private static long clip_length_lion_p = 933;
	//private static long clip_length_airplane = 1750;
	private static long clip_length_van_halen = 1100;
	private static long clip_length_van_halen_p = 1100;
	private static long clip_length_elephant = 957;
	private static long clip_length_elephant_p = 957;
	private static long clip_length_too_easy = 1100;
	private static long clip_length_strong = 2100;
	
	private long grace_period = 600;
	private long grace_period_x = 400;
	private long grace_period_xx = 300;
	// For dramatic effect
	private static long pause_end = 2000; 
	/** To play looping sound only once */
	private boolean walkPlaying = false;
	//private int monkeyStreamID = 0; //be careful with this initial value
	private int stumbles = 0;
	private boolean gameEnded = false;
	private boolean doubleTouched = false;
	private long soundStartTime = 0;
	Random generator;
	PlayBackgroundSoundsTask b;
	PlayCitySoundTask t;
	CheckForCancelTask c;
	boolean taskCancelled = false;
	boolean cancelRunning = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Disable status bar (make app go fullscreen)
		// Source: http://stackoverflow.com/questions/7457730/preventing-status-bar-expansion
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.movement);
		// Initialize swipe detector
		detector = new SimpleGestureFilter(this, this);
	}
	
	private class SwipeFrameTask extends AsyncTask <Integer,Void,Boolean> {
	    protected Boolean doInBackground(Integer... sound_key) {
	    	Log.d(TAG, "Listening to swipes!");
	    	switch (sound_key[0]) {
	    	case LEFT: 
	    		LEFT_ON = true;
	    	    pause(clip_length_car_p + grace_period);
	    	    LEFT_ON = false;
	    	 // User disregarding obstacle = stumble!
	            if(!SWIPED) 
	            {
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(CRASH);
	            	pause(clip_length_crash);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(OUCH);
	            	pause(clip_length_ouch);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    // sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    	    break;
	    	case RIGHT:
	    		RIGHT_ON = true;
	    	    pause(clip_length_car_p + grace_period);
	    	    RIGHT_ON = false;
	    	    if(!SWIPED) 
	            {
	    	    	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(CRASH);
	            	pause(clip_length_crash);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(OUCH);
	            	pause(clip_length_ouch);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    //sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    	    break;
	    	case GUNS:
	    		UP_ON = true;
	    	    pause(clip_length_guns_p + grace_period);
	    	    UP_ON = false;
	    	    if(!SWIPED) 
	            {
	    	    	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(OUCH);
	            	pause(clip_length_ouch);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    //sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    	    break;
	    	case LION_LEFT:
	    		LEFT_ON = true;
	    	    pause(clip_length_lion_p + grace_period);
	    	    LEFT_ON = false;
	    	    if(!SWIPED) 
	            {
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(OUCH);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	pause(clip_length_ouch);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    // sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    	    break;
	    	case ELEPHANT_RIGHT:
	    		RIGHT_ON = true;
	    	    pause(clip_length_elephant_p + grace_period);
	    	    RIGHT_ON = false;
	    	    if(!SWIPED) 
	            {
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(OUCH);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	pause(clip_length_ouch);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    // sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    	    break;
	    	case VAN_HALEN: 
	    		DOWN_ON = true;
	    		pause(clip_length_van_halen_p + grace_period);
	    		DOWN_ON = false;
	    		if(!SWIPED) 
	            {
	    			if(taskCancelled || gameEnded) {
	                	return false;
	                }
	            	sound1.playSound(SPLAT);
	            	pause(clip_length_splat);
	            	//sound1.playSound(DAMN);
	            	//pause(clip_length_damn);
	            	stumbles++;
	            	// Increase volume of monkeys
		    	    //sound0.setVolume(monkeyStreamID, stumbles);
	            }
	            SWIPED = false;
	    		break;
	    	}
	    	return true;
	    }	
	    protected void onPostExecute(Boolean result) {
	    	if (result) {
	    		if(stumbles == 3) {
            		endGame("fail");
		    	}
	    	}
	    }
	}
	
	// Checks is background music should be stopped. If so, it releases the resources.
	private class CheckForCancelTask extends AsyncTask<Object,Void,Object> {
		protected Object doInBackground(Object... objects) {			
			while (!taskCancelled){;}
		    releaseResources();		
			return null;
		}
	}
	
	private class PlayVerdictTask extends AsyncTask<String,Void,Boolean> {
		protected Boolean doInBackground(String... verdict) {
			sound0.autoPause();
			sound1.autoPause();
			sound2.autoPause();
			// If task is cancelled, don't do anything else.
			if(taskCancelled) {
            	return false;
            }
			pause(pause_end);
		    if(verdict[0].equals("fail")) {
		    	if(taskCancelled) {
                	return false;
                }
		    	sound0.playSound(BABY_CRY);
		    	if(taskCancelled) {
                	return false;
                }
		    	pause(clip_length_baby_cry);
		    }
		    else {
		    	if(taskCancelled) {
                	return false;
                }
		    	sound1.playSound(BABY_LAUGH);
		    	if(taskCancelled) {
                	return false;
                }
		    	pause(clip_length_baby_laugh);
		    }
		    if(taskCancelled) {
		    	return false;
		    }
		    return true;
		}
		// Returns to UI thread for this method
	    protected void onPostExecute(Boolean result) {
	    	if (result) {
		    	// taskCancelled = true;
			    releaseResources();	
			    pause(200);
			    reset();
			    Log.d(TAG, "I made it");
	    	}
	    }
	}
	
	private class PlayBackgroundSoundsTask extends AsyncTask<Object,Void,Object> {
		protected Object doInBackground(Object... objects) {			
			if(!walkPlaying) {
	    		sound0.playSound(WALK);
	    		Log.d(TAG, "Here!!");
	    		sound2.playSound(CITY);
	    		Log.d(TAG, "Here!!!");
	    		//monkeyStreamID = sound0.playSound(MONKEY);
	    		walkPlaying = true;
	    	}
			while (!gameEnded);
			return null;
		}
	}
	
	private class PlayCitySoundTask extends AsyncTask<String,Void,Boolean> {
	    protected Boolean doInBackground(String... mode) {
	        if (mode[0].equals("L")) {
	        	soundStartTime = Calendar.getInstance().getTimeInMillis();	
	            // Flip a coin!
	            int choice = generator.nextInt(2);
	            if (choice == 0) {
	            	SwipeFrameTask sft = new SwipeFrameTask();
	            	sft.execute(LEFT);
	                sound0.playSound(LEFT);
	                if(taskCancelled || gameEnded) {
	                	return false;
	                }
	                pause(clip_length_car + grace_period);
	            }
	            else {
	            	SwipeFrameTask sft = new SwipeFrameTask();
	            	sft.execute(LION_LEFT);
	            	sound2.playSound(LION_LEFT);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	                pause(clip_length_lion + grace_period);
	            }
	            if(taskCancelled || gameEnded) {
                	return false;
                }
	        }
	        else if (mode[0].equals("R")){
	        	soundStartTime = Calendar.getInstance().getTimeInMillis();
	            // Flip a coin!
	            int choice = generator.nextInt(2);
	            if (choice == 0) {
	            	SwipeFrameTask sft = new SwipeFrameTask();
	            	sft.execute(RIGHT);
	                sound0.playSound(RIGHT);
	                if(taskCancelled || gameEnded) {
	                	return false;
	                }
	                pause(clip_length_car + grace_period);
	            }
	            else {
	            	SwipeFrameTask sft = new SwipeFrameTask();
	            	sft.execute(ELEPHANT_RIGHT);
	            	sound2.playSound(ELEPHANT_RIGHT);
	            	if(taskCancelled || gameEnded) {
	                	return false;
	                }
	                pause(clip_length_elephant + grace_period);
	            }
	            if(taskCancelled || gameEnded) {return false;}
	        }
	        else if (mode[0].equals("D")){
	        	soundStartTime = Calendar.getInstance().getTimeInMillis();
	        	// Flip a coin!
	            /*int choice = generator.nextInt(2);
	            if (choice == 0) {
	            	sound0.playSound(CAT);   
	            	if(taskCancelled || gameEnded) {
	                	
	                	return false;
	                }
	                pause(clip_length_cat + grace_period);
	            }*/
	            //else {
	        	SwipeFrameTask sft = new SwipeFrameTask();
            	sft.execute(VAN_HALEN);
	            sound2.playSound(VAN_HALEN);
	            if(taskCancelled || gameEnded) {
	                return false;
	            }
	            pause(clip_length_van_halen + grace_period);
	            //}
	        }
	        else if (mode[0].equals("U")){
	        	soundStartTime = Calendar.getInstance().getTimeInMillis();
	        	// Flip a coin!
	        	//int choice = generator.nextInt(2);
	            //if (choice == 0) {
	        	SwipeFrameTask sft = new SwipeFrameTask();
            	sft.execute(GUNS);
	            sound0.playSound(GUNS);
	            if(taskCancelled || gameEnded) {
	                return false;
	            }
		        pause(clip_length_guns + grace_period);
	            //}
	            /*else {
	            	sound2.playSound(AIRPLANE);
	            	if(taskCancelled || gameEnded) {
	                	
	                	return false;
	                }
	                pause(clip_length_airplane + grace_period);
	            }*/
	        }
	        else if (mode[0].equals("X")) {
	        	grace_period = grace_period_x;
	        }
	        else if (mode[0].equals("XX")) {
	        	grace_period = grace_period_xx;
	        	clip_length_car = 600;
	        	clip_length_lion = 400;
	        	clip_length_elephant = 400;
	        	clip_length_guns = 500;
	        	clip_length_van_halen = 500;
	        }
	        else if (mode[0].equals("LVL2")) {
	        	stumbles = 0;
	        	sound2.playSound(TOO_EASY);
	        	pause(clip_length_too_easy);
	        }
	        else if (mode[0].equals("LVL3")) {
	        	stumbles = 0;
	        	sound2.playSound(STRONG);
	        	pause(clip_length_strong);
	        }
	        else {
	        	long walkTime = (long)(1000 * (Double.parseDouble(mode[0])));
	        	if(taskCancelled || gameEnded) {
                	return false;
                }
	        	pause(walkTime);
	        }
	        return true;
	    }
	    // Returns to UI thread for this method
	    protected void onPostExecute(Boolean result) {
	    	if(taskCancelled || gameEnded) {
            	return;
            }
	    	if(current_node < path.length - 1) {
	    		// To link PlaySoundTasks sequentially
	    		if(taskCancelled || gameEnded) {
	    			return;
	    		}
	    		current_node++;
	    		new PlayCitySoundTask().execute(path[current_node]);
	    	}
	    	else {
	    		if(taskCancelled || gameEnded) {
	    			return;
	    		}
	    		// Makes sense, since one second delay at end was added.
	    		endGame("ok");
	    	}
	    }
	}
	
    @Override 
	public boolean dispatchTouchEvent(MotionEvent me){ 
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me); 
	}
    

	/** We count user mistakes here. */
    // TODO: Handle this.
	@Override
	public void onSwipe(int direction) {
		if(!gameEnded){
			SWIPED = true;
			if(taskCancelled || gameEnded) {
            	return;
            }
			switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT :
				if (LEFT_ON) {
					Log.d(TAG, "User swiped to the right " 
				+ String.valueOf(Calendar.getInstance().getTimeInMillis() - soundStartTime)
				+ " miliseconds after sound started");
					sound1.playSound(YES);
					if(taskCancelled || gameEnded) {
	                	return;
	                }
					pause(clip_length_yes);
				}
				else {
					// Can't move outside boundaries - warning sound comes on
					/*if(!RIGHT_ON && !UP_ON && !DOWN_ON) {
						sound1.playSound(DOINK);
						if(taskCancelled || gameEnded) {
		                	
		                	return;
		                }
						pause(clip_length_doink);
					}*/	
					// We're screwed
					stumbles++;
					// Slipped on can!
					if(DOWN_ON) {
						sound1.playSound(SPLAT);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_splat);
						//sound1.playSound(DAMN);
						//pause(clip_length_damn);
					}
					else if (RIGHT_ON) {
						sound1.playSound(CRASH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_crash);
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_ouch);
					}
					else if (UP_ON){
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_ouch);
						// Increase sound of monkeys
						//sound0.setVolume(monkeyStreamID, stumbles);
					}
					else {
						if(taskCancelled || gameEnded) {
							return;
						}
					}
				}
				if(stumbles == 3) {
					if(taskCancelled || gameEnded) {
	                	return;
	                }
            		endGame("fail");
					return;
				}
				break;
			case SimpleGestureFilter.SWIPE_LEFT :
				if (RIGHT_ON) {
					Log.d(TAG, "User swiped to the left " 
							+ String.valueOf(Calendar.getInstance().getTimeInMillis() - soundStartTime)
							+ " miliseconds after sound started");
					sound1.playSound(YES);
					if(taskCancelled || gameEnded) {
	                	return;
	                }
					pause(clip_length_yes);
				}
				else {
					// Can't move outside boundaries - warning sound comes on
					/*if(!LEFT_ON && !UP_ON && !DOWN_ON) {
						sound1.playSound(DOINK);
						if(taskCancelled || gameEnded) {
		                	
		                	return;
		                }
						pause(clip_length_doink);
					}*/	
					// We're screwed
					stumbles++;
					if(DOWN_ON) {
						sound1.playSound(SPLAT);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_splat);
						//sound1.playSound(DAMN);
						//pause(clip_length_damn);
					}
					else if (LEFT_ON) {
						sound1.playSound(CRASH);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_crash);
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_ouch);
					}
					else if (UP_ON){
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_ouch);
					}
					else {
						if(taskCancelled || gameEnded) {
							return;
						}
					}
					// Increase volume of monkeys
					//sound0.setVolume(monkeyStreamID, stumbles);
				}
				if(stumbles == 3) {
					if(taskCancelled || gameEnded) {
						return;
					}
            		endGame("fail");
					return;
				}
				break;
			case SimpleGestureFilter.SWIPE_DOWN :
				if (UP_ON) {
					Log.d(TAG, "User swiped down " 
							+ String.valueOf(Calendar.getInstance().getTimeInMillis() - soundStartTime)
							+ " miliseconds after sound started");
					sound1.playSound(SAFE);
					if(taskCancelled || gameEnded) {
	                	return;
	                }
					pause(clip_length_safe);
				}
				else {
					// Can't move outside boundaries - warning sign comes on
					/*if(!RIGHT_ON && !LEFT_ON && !DOWN_ON) {
						sound1.playSound(DOINK);
						if(taskCancelled || gameEnded) {
		                	return;
		                }
						pause(clip_length_doink);
					}*/	
					// We're screwed
					stumbles++;
					if(DOWN_ON) {
						sound1.playSound(SPLAT);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_splat);
						//sound1.playSound(DAMN);
						//pause(clip_length_damn);
					}
					else if(LEFT_ON || RIGHT_ON) {
						sound1.playSound(CRASH);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_crash);
						if(taskCancelled || gameEnded) {

							return;
						}
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {

							return;
						}
						pause(clip_length_ouch);
					}
					else {
						if(taskCancelled || gameEnded) {
							return;
						}
					}
					// Increase volume of monkeys
					//sound0.setVolume(monkeyStreamID, stumbles);
				}
				if(stumbles == 3) {
					if(taskCancelled || gameEnded) {
						return;
					}
            		endGame("fail");
					return;
				}
				break;
			case SimpleGestureFilter.SWIPE_UP :
				if (DOWN_ON) {
					Log.d(TAG, "User swiped up " 
							+ String.valueOf(Calendar.getInstance().getTimeInMillis() - soundStartTime)
							+ " miliseconds after sound started");
					sound1.playSound(WOOHOO);
					if(taskCancelled || gameEnded) {
	                	return;
	                }
					pause(clip_length_woohoo);
				}
				else {
					// Can't move outside boundaries - warning sound comes on
					/*if(!RIGHT_ON && !UP_ON && !LEFT_ON) {
						sound1.playSound(DOINK);
						if(taskCancelled || gameEnded) {
		                	
		                	return;
		                }
						pause(clip_length_doink);
					}*/	
					// We're screwed
					stumbles++;
					if(RIGHT_ON || LEFT_ON) {
						sound1.playSound(CRASH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_crash);
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_ouch);
					}
					else if (UP_ON){
						sound1.playSound(OUCH);
						if(taskCancelled || gameEnded) {
							return;
						}
						pause(clip_length_ouch);
					}
					else {
						if(taskCancelled || gameEnded) {
							return;
						}
					}
					// Increase volume of monkeys
					//sound0.setVolume(monkeyStreamID, stumbles);
				}
				if(stumbles == 3) {
					if(taskCancelled || gameEnded) {
						return;
					}
            		endGame("fail");
            		return;
				}
				break;
			}	
		}
	}
    
    public void pause (long milisec) {
    	long t = System.currentTimeMillis();
    	long end = t + milisec;
    	while (System.currentTimeMillis() < end) {;}
    }
    
    @Override
    public void onDoubleTap() {
    	if(!doubleTouched) {
    		doubleTouched = true;
	    	Log.d(TAG, "Here!");
	    	b = new PlayBackgroundSoundsTask();
	    	t = new PlayCitySoundTask();
	    	if (!cancelRunning) {
		    	c = new CheckForCancelTask();
		    	c.execute((Object) null);
		    	cancelRunning =  true;
	    	}
	    	b.execute((Object) null);
	    	t.execute(path[current_node]);
    	}
    }
    
    @Override
    protected void onResume()  {
    	super.onResume();
    	reset();
    }
    
    public void reset() {
    	sound0 = new Sound(this, 0);
		sound1 = new Sound(this, 1);
		sound2 = new Sound(this, 2);
		generator = new Random(3287382);
		doubleTouched = false;
		taskCancelled = false;
		stumbles = 0;
		current_node = 0;
		walkPlaying = false;
		gameEnded = false;
		SWIPED = false;
		LEFT_ON = false;
		RIGHT_ON = false;
		DOWN_ON = false;
		UP_ON = false;
		grace_period = 600;
		grace_period_x = 400;
		grace_period_xx = 300;
		soundStartTime = 0;
    }
    
    @Override
    protected void onPause() {
    	super.onPause(); 
    	// Beware of CHAINING!
    	if(doubleTouched) {
	    	t.cancel(true);
	    	taskCancelled = true;
    	}
    }
    
    /** Indicate end of game when finished or when mistake is committed. */
    // TODO: Find out why SoundPool is crashing. More resources perhaps?
    public void endGame(String verdict) {
    	gameEnded = true;
    	if(taskCancelled) {
        	return;
        }
		PlayVerdictTask t = new PlayVerdictTask();
		t.execute(verdict);
	}
    
    public void releaseResources() {
    	sound0.release();
    	sound1.release();
    	sound2.release();
    }
}