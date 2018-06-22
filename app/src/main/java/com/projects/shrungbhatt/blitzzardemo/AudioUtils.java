package com.projects.shrungbhatt.blitzzardemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Prachi Mehta on 5/12/2018.
 */
public class AudioUtils {
    private static Context mContext;
    private static AudioUtils sMAudioUtils_;
    static private MediaPlayer mediaPlayer;

    private AudioUtils() {
    }

    public static AudioUtils getInstance(Context context, int soundId){
        mContext = context;
        if (sMAudioUtils_ == null){
            sMAudioUtils_ = new AudioUtils();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.setDataSource(mContext, Uri.parse("android.resource://" + mContext.getPackageName() + "/" + soundId));
        }catch (Exception e){
            Log.e("AudioUtils",e.toString());
        }

        return sMAudioUtils_;
    }

    public void playBookServiceSound() {
        try {
            if (mediaPlayer != null &&
                    mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }
            if(mediaPlayer != null) {
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopBookServiceSound() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
