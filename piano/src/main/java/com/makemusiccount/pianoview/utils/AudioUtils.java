package com.makemusiccount.pianoview.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseIntArray;

import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.entity.PianoKey;
import com.makemusiccount.pianoview.listener.LoadAudioMessage;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.view.PianoView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Gautam on 2016-11-26.
 */

public class AudioUtils implements LoadAudioMessage {

    private ExecutorService service = Executors.newCachedThreadPool();

    private final static int MAX_STREAM = 11;

    @SuppressLint("StaticFieldLeak")
    private static AudioUtils instance = null;

    private final static int LOAD_START = 1;
    private final static int LOAD_FINISH = 2;
    private final static int LOAD_ERROR = 3;
    private final static int LOAD_PROGRESS = 4;

    private final static int SEND_PROGRESS_MESSAGE_BREAK_TIME = 500;

    private SoundPool pool;

    private Context context;

    private OnLoadAudioListener loadAudioListener;
    private SparseIntArray whiteKeyMusics = new SparseIntArray();
    private SparseIntArray blackKeyMusics = new SparseIntArray();
    private boolean isLoadFinish = false;
    private boolean isLoading = false;
    private Handler handler;
    private long currentTime;
    private int loadNum;
    private int minSoundId = -1;
    private int maxSoundId = -1;

    private AudioUtils(Context context, OnLoadAudioListener loadAudioListener, int maxStream) {
        this.context = context;
        this.loadAudioListener = loadAudioListener;
        handler = new AudioStatusHandler(context.getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pool = new SoundPool.Builder().setMaxStreams(maxStream)
                    .setAudioAttributes(
                            new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build())
                    .build();
        } else {
            pool = new SoundPool(maxStream, AudioManager.STREAM_MUSIC, 1);
        }
    }

    public static AudioUtils getInstance(Context context, OnLoadAudioListener listener) {
        return getInstance(context, listener, MAX_STREAM);
    }

    public static AudioUtils getInstance(Context context, OnLoadAudioListener listener,
                                         int maxStream) {
        if (instance == null || instance.pool == null) {
            synchronized (AudioUtils.class) {
                if (instance == null || instance.pool == null) {
                    instance = new AudioUtils(context, listener, maxStream);
                }
            }
        }
        return instance;
    }

    public void loadMusic(final Piano piano) throws Exception {
        if (pool == null) {
            throw new Exception("SoundPool");
        }
        if (piano != null) {
            if (!isLoading && !isLoadFinish) {
                isLoading = true;
                pool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                    loadNum++;
                    if (loadNum == Piano.PIANO_NUMS) {
                        isLoadFinish = true;
                        sendProgressMessage(100);
                        sendFinishMessage();
                        pool.play(whiteKeyMusics.get(0), 0, 0, 1, -1, 2f);
                    } else {
                        if (System.currentTimeMillis() - currentTime >= SEND_PROGRESS_MESSAGE_BREAK_TIME) {
                            sendProgressMessage((int) (((float) loadNum / (float) Piano.PIANO_NUMS) * 100f));
                            currentTime = System.currentTimeMillis();
                        }
                    }
                });
                service.execute(() -> {
                    sendStartMessage();
                    ArrayList<PianoKey[]> whiteKeys = piano.getWhitePianoKeys();
                    int whiteKeyPos = 0;
                    for (int i = 0; i < whiteKeys.size(); i++) {
                        for (PianoKey key : whiteKeys.get(i)) {
                            try {
                                int soundID = pool.load(context, key.getVoiceId(), 1);
                                whiteKeyMusics.put(whiteKeyPos, soundID);
                                if (minSoundId == -1) {
                                    minSoundId = soundID;
                                }
                                whiteKeyPos++;
                            } catch (Exception e) {
                                isLoading = false;
                                sendErrorMessage(e);
                                return;
                            }
                        }
                    }
                    ArrayList<PianoKey[]> blackKeys = piano.getBlackPianoKeys();
                    int blackKeyPos = 0;
                    for (int i = 0; i < blackKeys.size(); i++) {
                        for (PianoKey key : blackKeys.get(i)) {
                            try {
                                int soundID = pool.load(context, key.getVoiceId(), 1);
                                blackKeyMusics.put(blackKeyPos, soundID);
                                blackKeyPos++;
                                if (soundID > maxSoundId) {
                                    maxSoundId = soundID;
                                }
                            } catch (Exception e) {
                                isLoading = false;
                                sendErrorMessage(e);
                                return;
                            }
                        }
                    }
                });
            }
        }
    }

    public void playMusic(final PianoKey key) {
        if (key != null) {
            if (isLoadFinish) {
                if (key.getType() != null) {
                    service.execute(() -> {
                        switch (key.getType()) {
                            case BLACK:
                                playBlackKeyMusic(key.getGroup(), key.getPositionOfGroup());
                                break;
                            case WHITE:
                                playWhiteKeyMusic(key.getGroup(), key.getPositionOfGroup());
                                break;
                        }
                    });
                }
            }
        }
    }

    private void playWhiteKeyMusic(int group, int positionOfGroup) {
        int position;
        if (group == 0) {
            position = positionOfGroup;
        } else {
            position = (group - 1) * 7 + 2 + positionOfGroup;
        }
        if (whiteKeyMusics != null) {
            play(whiteKeyMusics.get(position));
        }
    }

    private void playBlackKeyMusic(int group, int positionOfGroup) {
        int position;
        if (group == 0) {
            position = positionOfGroup;
        } else {
            position = (group - 1) * 5 + 1 + positionOfGroup;
        }
        if (blackKeyMusics != null) {
            play(blackKeyMusics.get(position));
        }
    }

    private void play(int soundId) {

        float volume = (float) (PianoView.pianoVolume / 100.00);

        pool.play(soundId, volume, volume, 1, 0, 1f);
    }

    public void stop() {
        try {
            context = null;
            pool.release();
            pool = null;
            whiteKeyMusics.clear();
            whiteKeyMusics = null;
            blackKeyMusics.clear();
            blackKeyMusics = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendStartMessage() {
        handler.sendEmptyMessage(LOAD_START);
    }

    @Override
    public void sendFinishMessage() {
        handler.sendEmptyMessage(LOAD_FINISH);
    }

    @Override
    public void sendErrorMessage(Exception e) {
        handler.sendMessage(Message.obtain(handler, LOAD_ERROR, e));
    }

    @Override
    public void sendProgressMessage(int progress) {
        handler.sendMessage(Message.obtain(handler, LOAD_PROGRESS, progress));
    }

    private class AudioStatusHandler extends Handler {
        AudioStatusHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handleAudioStatusMessage(msg);
        }
    }

    private void handleAudioStatusMessage(Message msg) {
        if (loadAudioListener != null) {
            switch (msg.what) {
                case LOAD_START:
                    loadAudioListener.loadPianoAudioStart();
                    break;
                case LOAD_FINISH:
                    loadAudioListener.loadPianoAudioFinish();
                    break;
                case LOAD_ERROR:
                    loadAudioListener.loadPianoAudioError((Exception) msg.obj);
                    break;
                case LOAD_PROGRESS:
                    loadAudioListener.loadPianoAudioProgress((int) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
