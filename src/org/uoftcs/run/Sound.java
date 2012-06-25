package org.uoftcs.run;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class Sound {

	private SoundPool soundPool;
	private int soundID;
	boolean loaded = false;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	private Activity context;

	public Sound(Activity context) {
		this.context = context;
		// Set the hardware buttons to control the music
		this.context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sounds
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;
			}
		});
		// Ogg format!
		soundID = soundPool.load(this.context, R.raw.car_screech_sound_effect, 1);
	}

	public void playSound(int mode) {
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
            	soundPool.play(soundID, volume, 0, 1, 0, 1f);
            	break;
            case RIGHT:
            	soundPool.play(soundID, 0, volume, 1, 0, 1f);
            	break;
            }
		}
	}
}
