package com.makemusiccount.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {
    TelephonyManager telManager;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        MediaPlayer mediaPlayer;
                        mediaPlayer = new MediaPlayer();
                        try {
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.stop();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        MediaPlayer mediaPlayer1;
                        mediaPlayer1 = new MediaPlayer();
                        try {
                            if (mediaPlayer1 != null) {
                                if (mediaPlayer1.isPlaying()) {
                                    mediaPlayer1.stop();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case TelephonyManager.CALL_STATE_IDLE:

                        MediaPlayer mediaPlayer2;
                        mediaPlayer2 = new MediaPlayer();
                        try {
                            if (mediaPlayer2 != null) {
                                if (mediaPlayer2.isPlaying()) {
                                    mediaPlayer2.stop();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    default:
                        MediaPlayer mediaPlayer3;
                        mediaPlayer3 = new MediaPlayer();
                        try {
                            if (mediaPlayer3 != null) {
                                if (mediaPlayer3.isPlaying()) {
                                    mediaPlayer3.stop();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }
            } catch (Exception ex) {

            }
        }
    };
}