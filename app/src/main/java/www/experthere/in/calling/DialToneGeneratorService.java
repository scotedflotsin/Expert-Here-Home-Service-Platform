package www.experthere.in.calling;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import www.experthere.in.R;

public class DialToneGeneratorService extends Service {


    public static final String ACTION_STOP = "www.experthere.in.calling.STOP";

    private ToneGenerator toneGenerator;
    private Handler handler;
    private boolean isSpeakerOn = false;
    boolean isMicON =true;
    AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    private BroadcastReceiver myReceiver;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the ToneGenerator with the STREAM_VOICE_CALL audio stream
        Log.d("SLNSLKBSB", "onCreate: GG");

        toneGenerator = new ToneGenerator(AudioManager.MODE_IN_COMMUNICATION, 50);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(false);


        // Initialize and register BroadcastReceiver
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter("www.experthere.in.calling.SWITCH_AUDIO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(myReceiver, intentFilter);

        }

        IntentFilter intentFilter2 = new IntentFilter("www.experthere.in.calling.SWITCH_MIC");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myReceiver, intentFilter2, Context.RECEIVER_NOT_EXPORTED);
        }else {
            registerReceiver(myReceiver, intentFilter2);

        }

        // Initialize the handler to manage tones and pauses
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start playing the dialing tone
        Log.d("SLNSLKBSB", "onCreate: COMAND");

        playTone();



        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                // User clicked on "Dismiss Call" action, stop the service
                stopSelf();
            }
        }




                // Return START_NOT_STICKY because we don't want the service to be restarted automatically
        return START_NOT_STICKY;
    }


    private void playTone() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());

            Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring_full);

            mediaPlayer.setDataSource(this, url);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to stop playing outgoing ringing tone
    private void stopOutgoingRingtone() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the ToneGenerator resources when the service is destroyed
        if (toneGenerator != null) {
            toneGenerator.release();
        }
        // Stop any pending tone playback
        isSpeakerOn = false;
        isMicON = true;
        handler.removeCallbacksAndMessages(null);
        stopOutgoingRingtone();
        stopSelf();
        unregisterReceiver(myReceiver);

        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(false);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Inner BroadcastReceiver class
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle broadcast reception
            if ("www.experthere.in.calling.SWITCH_AUDIO".equals(intent.getAction())) {
                // Custom action received, perform necessary tasks

                if (isSpeakerOn) {

                    audioManager.setSpeakerphoneOn(false);
                    isSpeakerOn = false;

                } else {

                    audioManager.setSpeakerphoneOn(true);
                    isSpeakerOn = true;

                }


            }
            if ("www.experthere.in.calling.SWITCH_MIC".equals(intent.getAction())) {
                // Custom action received, perform necessary tasks

                if (isMicON) {
                    audioManager.setMicrophoneMute(true);
                    isMicON = false;

                } else {

                    audioManager.setMicrophoneMute(false);
                    isMicON = true;

                }


            }
        }
    }

}
