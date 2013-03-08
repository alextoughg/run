// Source: http://soundbible.com/356-Monkeys-Monkeying-Around.html
package org.uoftcs.run;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class Sound {

	private SoundPool soundPool;
	private int car_screech; 
	private int concrete_run;
	//private int monkey;
	private int city;
	private int baby_cry;
	private int baby_laugh;
	private int yes;
	private int woohoo;
	private int ouch;
	//private int damn;
	private int splat;
	private int car_crash;
	private int doink;
	private int cat;
	private int safe;
	private int guns;
	private int lion;
	private int airplane;
	private int van_halen;
	private int elephant;
	private int too_easy;
	private int strong;
	
	boolean loaded = false;
	private static final int WALK = -1;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int CAT = 2;
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
	private static final int DOINK= 14;
	private static final int LION_LEFT = 15;
	private static final int AIRPLANE = 16;
	private static final int VAN_HALEN = 17;
	private static final int ELEPHANT_RIGHT = 18;
	private static final int TOO_EASY = 19;
	private static final int STRONG = 20;
	
	private Activity context;
    //private MediaPlayer mp;
	
	public Sound(Activity context, int select) {
		this.context = context;
		// Set the hardware buttons to control the music
		this.context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sounds. Give time for process to decompress audio
		// to raw PCM format for playback.
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		//mp = MediaPlayer.create(this.context, R.raw.city);
		if(select == 0){
			// Ogg format!
			car_screech = soundPool.load(this.context, R.raw.car_screech_sound_effect, 1);
			concrete_run = soundPool.load(this.context, R.raw.run_1_20_min, 1);
			//can = soundPool.load(this.context, R.raw.trash_can_single, 1); 
			//swoosh = soundPool.load(this.context, R.raw.swoosh, 1);
			//truck_horn = soundPool.load(this.context, R.raw.truck_horn, 1);
			cat = soundPool.load(this.context, R.raw.cat, 1);
			guns = soundPool.load(this.context, R.raw.alien2, 1);
			//monkey = soundPool.load(this.context, R.raw.monkey, 1);
			baby_cry = soundPool.load(this.context, R.raw.no, 1);
		}
		else if (select == 1){
			baby_laugh = soundPool.load(this.context, R.raw.song2, 1);
		    yes = soundPool.load(this.context, R.raw.yeahbaby, 1);
		    woohoo = soundPool.load(this.context, R.raw.woohoo, 1);
		    ouch = soundPool.load(this.context, R.raw.ouch2, 1);
		    //damn = soundPool.load(this.context, R.raw.goddamnit, 1);
		    splat = soundPool.load(this.context, R.raw.splat, 1);
		    car_crash = soundPool.load(this.context, R.raw.car_crash, 1);
		    safe = soundPool.load(this.context, R.raw.hurray2, 1);
		    doink = soundPool.load(this.context, R.raw.doink, 1);
		}
		else {
			// It is not a problem of a lack of space.
			city = soundPool.load(this.context, R.raw.city, 1);
			lion = soundPool.load(this.context, R.raw.lion, 1);
			airplane = soundPool.load(this.context, R.raw.airplane, 1);
			van_halen = soundPool.load(this.context, R.raw.jump, 1);
			elephant = soundPool.load(this.context, R.raw.elephant, 1);
			too_easy = soundPool.load(this.context, R.raw.too_easy, 1);
			strong = soundPool.load(this.context, R.raw.strong, 1);
		}
	}

	// NOTE: be careful with looping sounds > 5 sec. This may cause "AudioFlinger could not create track"
	// and other similar errors.
	public int playSound(int mode) {
		// Getting the user sound settings
		AudioManager audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		// Is the sound loaded already?
		if (loaded) {
            switch (mode) {
            case LEFT:
            	return soundPool.play(car_screech, volume, 0, 1, 0, 1f); 
            case RIGHT:
            	return soundPool.play(car_screech, 0, volume, 1, 0, 1f);
            case WALK: 
            	return soundPool.play(concrete_run, 1, 1, 1, 0, 1f);
            case CAT:
            	return soundPool.play(cat, 1f, 1f, 1, 0, 1f);
            case SAFE:
            	return soundPool.play(safe, 0.5f, 0.5f, 1, 0, 1f);
            case GUNS:
            	return soundPool.play(guns, 1, 1, 1, 0, 1f);
            case CITY:
            	/*mp.start();
            	return 1;*/
            	return soundPool.play(city, volume, volume, 1, -1, 1f);
            //case MONKEY:
            	//return soundPool.play(monkey, (float)0.1, (float)0.1, 1, -1, 1f);
            case BABY_CRY: 
            	return soundPool.play(baby_cry, volume, volume, 1, 0, 1f);	
            case BABY_LAUGH: 
            	return soundPool.play(baby_laugh, volume, volume, 1, 0, 1f);
            case YES:
            	return soundPool.play(yes, 0.7f, 0.7f, 1, 0, 1f);
            case WOOHOO:
            	return soundPool.play(woohoo, volume, volume, 1, 0, 1f);
            case OUCH:
            	return soundPool.play(ouch, volume, volume, 1, 0, 1f);
            case LION_LEFT:
            	return soundPool.play(lion, 1, 0, 1, 0, 1f);
            //case DAMN:
            	//return soundPool.play(damn, volume, volume, 1, 0, 1f);
            case SPLAT:
            	return soundPool.play(splat, 1, 1, 1, 0, 1f);
            case CRASH:
            	return soundPool.play(car_crash, volume, volume, 1, 0, 1.5f);
            case DOINK:
            	return soundPool.play(doink, volume, volume, 1, 0, 1f);
            case AIRPLANE: 
            	return soundPool.play(airplane, volume, volume, 1, 0, 1f);
            case VAN_HALEN:
            	return soundPool.play(van_halen, volume, volume, 1, 0, 1f);
            case ELEPHANT_RIGHT:
            	return soundPool.play(elephant, 0, volume, 1, 0, 1f);
            case TOO_EASY:
            	return soundPool.play(too_easy, volume, volume, 1, 0, 1f);
            case STRONG:
            	return soundPool.play(strong, 1f, 1f, 1, 0, 1f);
            default:
            	return -1;
            }
		}
		return -1;
	}
	
	/** Modify volume of streamID to volume indicated by stumbles */
	/*public void setVolume(int streamID, int stumbles) {
		switch (stumbles){
			case 0: soundPool.setVolume(streamID, (float)0.1, (float)0.1);
			break;
			case 1: soundPool.setVolume(streamID, (float)0.3, (float)0.3);
			break;
			case 2: soundPool.setVolume(streamID, (float)0.5, (float)0.5);
			break;
			case 3: soundPool.setVolume(streamID, (float)0.9, (float)0.9);
			break;
			default: break;
		}
	}*/
	
	public void pause(int streamID) {
		soundPool.pause(streamID);
	}
	
	public void resume(int streamID) {
		soundPool.resume(streamID);
	}
	
	public void autoPause() {
		soundPool.autoPause();
	}
	
	public void autoResume() {
		soundPool.autoResume();
	}
	
	public void autoStop() {
		soundPool.stop(car_screech);
		soundPool.stop(concrete_run);
		soundPool.stop(cat);
		soundPool.stop(guns);
		soundPool.stop(baby_cry);
		soundPool.stop(baby_laugh);
		soundPool.stop(yes);
		soundPool.stop(woohoo);
		soundPool.stop(ouch);
		soundPool.stop(splat);
		soundPool.stop(car_crash);
		soundPool.stop(safe);
		soundPool.stop(doink);
		soundPool.stop(city);
		soundPool.stop(lion);
		soundPool.stop(airplane);
		soundPool.stop(van_halen);
		soundPool.stop(elephant);
		soundPool.stop(too_easy);
		soundPool.stop(strong);
	}
	
	public void autoUnload() {
		soundPool.unload(car_screech);
		soundPool.unload(concrete_run);
		soundPool.unload(cat);
		soundPool.unload(guns);
		soundPool.unload(baby_cry);
		soundPool.unload(baby_laugh);
		soundPool.unload(yes);
		soundPool.unload(woohoo);
		soundPool.unload(ouch);
		soundPool.unload(splat);
		soundPool.unload(car_crash);
		soundPool.unload(safe);
		soundPool.unload(doink);
		soundPool.unload(city);
		soundPool.unload(lion);
		soundPool.unload(airplane);
		soundPool.unload(van_halen);
		soundPool.unload(elephant);
		soundPool.unload(too_easy);
		soundPool.unload(strong);
	}
	
	public void release() {
		soundPool.release();
		soundPool = null;
	}
	
}
