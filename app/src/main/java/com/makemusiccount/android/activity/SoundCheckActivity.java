package com.makemusiccount.android.activity;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.makemusiccount.android.R;
import com.makemusiccount.android.listener.onPianoClickListener;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.view.PianoView;

public class SoundCheckActivity extends AppCompatActivity implements OnPianoListener, OnLoadAudioListener, OnPianoAutoPlayListener {

    Context context;
    PianoView pv;
    boolean autoPlay = false;
    static onPianoClickListener onPianoClickListener;
    LinearLayout llYes,llNo;

    public static void setPianoClickListener(com.makemusiccount.android.listener.onPianoClickListener listener) {
        onPianoClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_sound_check);
        context = this;
        initComp();

        llYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoOneTimeActivity.class);
                intent.putExtra("videoURL", AppConstant.Help_Video);
                startActivity(intent);

            }
        });

        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,SoundInformationActivity.class);
                startActivity(i);
            }
        });


    }

    private void initComp() {
        pv = findViewById(R.id.pv);
        pv.setPianoListener(this);
        pv.setAutoPlayListener(this);
        pv.setSoundPollMaxStream(100);
        pv.setLoadAudioListener(this);
        pv.numShow=3;
        pv.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));

        llYes = findViewById(R.id.llYes);
        llNo = findViewById(R.id.llNo);

    }

    @Override
    public void loadPianoAudioStart() {
        //Toast.makeText(context, "Audio loading start...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioFinish() {
        //Toast.makeText(context, "Audio loading end...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioError(Exception e) {
        Toast.makeText(context, "Audio loading error...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPianoAudioProgress(int progress) {

    }

    @Override
    public void onPianoAutoPlayStart() {
        autoPlay = true;
    }

    @Override
    public void onPianoAutoPlayEnd() {
        autoPlay = false;
    }

    @Override
    public void onPianoInitFinish() {
    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup) {
        int position = Util.getPianoPosition(type, group, positionOfGroup);
        if (onPianoClickListener != null) {
            if (!autoPlay) {
                onPianoClickListener.onClick(position);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (pv != null) {
            pv.releaseAutoPlay();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }
}
