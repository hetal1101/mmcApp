package com.makemusiccount.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.listener.onPianoClickListener;
import com.makemusiccount.android.model.RecordPianoKeyList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.CountDownAnimation;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.PlaySound;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.AutoPlayEntity;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.listener.OnLoadAudioListener;
import com.makemusiccount.pianoview.listener.OnPianoAutoPlayListener;
import com.makemusiccount.pianoview.listener.OnPianoListener;
import com.makemusiccount.pianoview.view.PianoView;
import com.makemusiccount.pianoview.view.PianoView1;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

public class PianoActivity extends AppCompatActivity implements OnPianoListener,
        OnLoadAudioListener, OnPianoAutoPlayListener {

    static onPianoClickListener onPianoClickListener;
    Activity context;
    ImageView ivPlay, ivPlay1;
    ProgressDialog progressDialog;
    String resMessage = "", resCode = "", song_name = "", song_file = "", song_id = "",
            screen = "", image = "", artist = "";
    List<RecordPianoKeyList> recordPianoKeyLists = new ArrayList<>();
    TextView tvName, tvArtist,tvName1;
    LottieAnimationView lottieAnimationView;

    MediaPlayer mediaPlayer, mediaPlayer1;
    LinearLayout rlCountdown,llSongData,llplay1,llwhitekey,llblackkey;
    RelativeLayout llData;
    Chronometer chronometer;
    PianoView pianoView;
    boolean autoPlay = false, exit = false;
    List<AutoPlayEntity> autoPlayEntities = new ArrayList<>();
    AudioManager audio;
    TextView tvCounter;
    CountDownAnimation countDownAnimation;
    String song_file_path = "";
    ImageView ivImage,ivplay1Stop,ivSongImage,ivImage2;
    LinearLayout tvNext,tvBack, llCounterBg;
    Global global;

    @Override
    protected void onResume() {
        super.onResume();
        Util.getKeyTheme(this);
        pianoView =findViewById(R.id.pianoView);
        pianoView.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));
        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setSoundPollMaxStream(100);
        pianoView.setLoadAudioListener(this);


    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
        setTheme(Util.getTheme(context));             setContentView(      R.layout.activity_piano);

        context = this;

        global = new Global(context);

        initToolbar();


        initComp();

        PianoActivity.setPianoClickListener(Position -> {

        });
        chronometer.setText("00 min : 00 sec");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            song_name = bundle.getString("song_name", "");
            song_id = bundle.getString("song_id", "");
            screen = bundle.getString("screen", "");

        }

        if (screen.equals("record")) {
            tvNext.setVisibility(View.GONE);
            tvBack.setVisibility(View.VISIBLE);
        } else {
            tvNext.setVisibility(View.VISIBLE);
            tvBack.setVisibility(View.GONE);
        }

        if(screen.equals("playsong"))
        {
            tvNext.setVisibility(View.GONE);
            tvBack.setVisibility(View.VISIBLE);
        }
        else
        {
            tvNext.setVisibility(View.VISIBLE);
            tvBack.setVisibility(View.GONE);
        }
        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setSoundPollMaxStream(100);
        pianoView.setLoadAudioListener(this);



        tvNext.setOnClickListener(view -> {
            if(screen.equalsIgnoreCase("tutorial_select"))
            {
                Intent intent = new Intent(context, TutorialS2.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("screenType", "test_question");
                startActivity(intent);
                context.finish();
                context.overridePendingTransition(0, 0);
            }
        });
        tvBack.setOnClickListener(view -> {
            finish();
        });

        countDownAnimation = new CountDownAnimation(tvCounter, 5);
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f,
                1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);
        countDownAnimation.setCountDownListener(animation -> {
            llCounterBg.setVisibility(View.GONE);
            startMusic(song_file_path);
        });

        if (global.isNetworkAvailable()) {
            new AutoPlayKey().execute();
        } else {
            global.retryInternet("auto_play_key");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("auto_play_key")) {
                    new AutoPlayKey().execute();
                }
            }
        }
    }

    @Override
    public void loadPianoAudioStart() {

    }

    @Override
    public void loadPianoAudioFinish() {

    }

    @Override
    public void loadPianoAudioError(Exception e) {
        Toast.makeText(getApplicationContext(), "loadPianoMusicError", Toast.LENGTH_SHORT).show();
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
    public static void setPianoClickListener(onPianoClickListener listener) {
        onPianoClickListener = listener;
    }

    @Override
    public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group, int positionOfGroup) {
        int position = Util.getPianoPosition(type, group, positionOfGroup);
        if (onPianoClickListener != null) {
            onPianoClickListener.onClick(position);
           /* if (!autoPlay) {

            }*/
        }
    }



    @SuppressLint("StaticFieldLeak")
    private class AutoPlayKey extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rlCountdown.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_AUTO_PLAY_KEY + Util.getUserId(context)
                    + "&songsID=" + song_id
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String api = restClient.getResponse();
                if (api != null && api.length() != 0) {
                    jsonObjectList = new JSONObject(api);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        song_name = jsonObjectList.getString("name");
                        song_file = jsonObjectList.getString("song_file");
                        artist = jsonObjectList.getString("artist");
                        image = jsonObjectList.getString("image");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("key_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        RecordPianoKeyList recordPianoKeyList = new RecordPianoKeyList();
                                        recordPianoKeyList.setName(jsonObjectList.getString("value"));
                                        recordPianoKeyList.setPosition(jsonObjectList.getString("key_value"));
                                        recordPianoKeyList.setWaiting_time(jsonObjectList.getString("type"));
                                        recordPianoKeyLists.add(recordPianoKeyList);
                                        autoPlayEntities.add(Util.getAutoPlayObject
                                                (Integer.parseInt(jsonObjectList.getString("key_value")),
                                                        Long.parseLong(jsonObjectList.getString("type"))));

                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            tvArtist.setText(artist);
            tvName.setText(song_name);
            Glide.with(context)
                    .load(image)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivImage);
            Glide.with(context)
                    .load(image)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivSongImage);
            Glide.with(context)
                    .load(image)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivImage2);
            setClickOn();
            if (resCode.equalsIgnoreCase("0"))
            {
                //String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
               // String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(getFilesDir() + "/voices/");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdirs();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                String ImgPath = audioVoice.getPath() + filename;
               // downloadAudioPath = getFilesDir() + "/voices/" + filename;
                new DownloadFile().execute(song_file, ImgPath);
            }
            else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPianoDisable() {
        pianoView.setEnabled(true);
    }

    private void setPianoEnable() {
        pianoView.setEnabled(true);
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
        stopMusic();
        super.onPause();
    }

    @Override
    protected void onStop() {
        /*if (handler != null) {
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
        stopMusic();*/

        super.onStop();
    }

    @SuppressLint("SetTextI18n")
    private void stopMusic()
    {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        llSongData.setVisibility(View.VISIBLE);
        llblackkey.setVisibility(View.GONE);
        llwhitekey.setVisibility(View.GONE);
        llplay1.setVisibility(View.GONE);
        ivImage.setVisibility(View.GONE);
        ivPlay1.setTag("PLAY");
        ivPlay1.setImageResource(R.drawable.play_song);
        setPianoEnable();
        ivPlay1.setVisibility(View.VISIBLE);
        ivPlay.setTag("PLAY");
        ivPlay.setImageResource(R.drawable.play);
        stopTime();
        if (autoPlay) {
            pianoView.stopAutoPlay();
        }
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (mediaPlayer1 != null) {
                if (mediaPlayer1.isPlaying()) {
                    mediaPlayer1.stop();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setClickOn() {
        ivPlay.setOnClickListener(view -> {
            if (ivPlay.getTag().toString().equals("PLAY")) {
                String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(downloadAudioPath + File.separator + "voices");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdir();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
                File file = new File(downloadAudioPath);
                if (file.exists()) {
                    startMusic(downloadAudioPath);
                }
            } else {
                stopMusic();
            }
        });

        ivPlay1.setOnClickListener(view -> {
            if (ivPlay1.getTag().toString().equals("PLAY")) {
                ivPlay1.setTag("STOP");
                ivPlay1.setImageResource(R.drawable.stop_song);
                String downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File audioVoice = new File(downloadAudioPath + File.separator + "voices");
                if (!audioVoice.exists()) {
                    boolean is = audioVoice.mkdir();
                    if (is) {
                        Log.d("Create dir : --- ", "ok");
                    }
                }
                String filename = Util.extractFilename(song_file);
                downloadAudioPath = downloadAudioPath + File.separator + "voices" + File.separator + filename;
                File file = new File(downloadAudioPath);
                if (file.exists()) {
                    startSong(downloadAudioPath);
                }
            } else {
                stopMusic();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL urls = new URL(url[0]);
                URLConnection connection = urls.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(urls.openStream());
                OutputStream output = new FileOutputStream(url[1]);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return url[1];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            song_file_path = s;
            rlCountdown.setVisibility(View.GONE);
            llCounterBg.setVisibility(View.VISIBLE);
            llData.setVisibility(View.VISIBLE);
            countDownAnimation.start();
            PlaySound.play(context, R.raw.countdown);
        }
    }

    Runnable runnable;

    Handler handler = new Handler();

    private void startSong(String s) {
        Uri myUri = Uri.parse(s);
        mediaPlayer1 = new MediaPlayer();
        Float maxVolume = Float.parseFloat(Util.getSongSound(context));
        float volume = (float) (maxVolume / 100.00);
        mediaPlayer1.setVolume(volume, volume);
        mediaPlayer1.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer1.start();
        });
        mediaPlayer1.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer1.release();
            ivPlay1.setTag("PLAY");
            ivPlay1.setImageResource(R.drawable.play_song);
            setPianoEnable();
        });
        try {
            mediaPlayer1.setDataSource(context, myUri);
            mediaPlayer1.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startMusic(String downloadAudioPath) {

        llSongData.setVisibility(View.GONE);
        llblackkey.setVisibility(View.VISIBLE);
        llwhitekey.setVisibility(View.VISIBLE);
        tvName1.setText(tvName.getText().toString());
        llplay1.setVisibility(View.VISIBLE);
        ivImage.setVisibility(View.VISIBLE);
        ivPlay.setTag("STOP");
        ivPlay.setImageResource(R.drawable.pause);
        setPianoDisable();
        Uri myUri = Uri.parse(downloadAudioPath);
        mediaPlayer = new MediaPlayer();
        Float maxVolume = Float.parseFloat(Util.getSongSound(context));
        float volume = (float) (maxVolume / 100.00);
        mediaPlayer.setVolume(volume, volume);
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            startTime();
            pianoView.autoPlay(autoPlayEntities, "song");
            mediaPlayer.start();

            startAutoPlay();
            exit = true;
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.release();
            stopMusic();
        });

        try {
            mediaPlayer.setDataSource(context, myUri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceType")
    private void startAutoPlay() {

        View phv1=findViewById(R.id.phv1);
        View phv2=findViewById(R.id.phv2);
        View phv3=findViewById(R.id.phv3);
        View phv4=findViewById(R.id.phv4);
        View phv5=findViewById(R.id.phv5);
        View phv6=findViewById(R.id.phv6);
        View phv7=findViewById(R.id.phv7);
        View phv8=findViewById(R.id.phv8);
        View phv9=findViewById(R.id.phv9);
        View phv10=findViewById(R.id.phv10);
        View phv11=findViewById(R.id.phv11);
        View phv12=findViewById(R.id.phv12);
        View phv13=findViewById(R.id.phv13);
        View phv14=findViewById(R.id.phv14);
        View phv15=findViewById(R.id.phv15);

        View bphv1=findViewById(R.id.bphv1);
        View bphv2=findViewById(R.id.bphv2);
        View bphv3=findViewById(R.id.bphv3);
        View bphv4=findViewById(R.id.bphv4);
        View bphv5=findViewById(R.id.bphv5);
        View bphv6=findViewById(R.id.bphv6);
        View bphv7=findViewById(R.id.bphv7);
        View bphv8=findViewById(R.id.bphv8);
        View bphv9=findViewById(R.id.bphv9);
        View bphv10=findViewById(R.id.bphv10);
        View bphv11=findViewById(R.id.bphv11);
        View bphv12=findViewById(R.id.bphv12);
        View bphv13=findViewById(R.id.bphv13);
        View bphv14=findViewById(R.id.bphv14);
        View bphv15=findViewById(R.id.bphv15);

        // phv16=findViewById(R.id.phv16);
        //View phv1=findViewById(R.id.phv1);



        View view=phv1;
        int delay=0;
        TypedValue typedValue=new TypedValue();
        Resources.Theme theme=context.getTheme();
        theme.resolveAttribute(R.attr.new_light,typedValue,true);
        @ColorInt int color =typedValue.data;
        for(int j=0;j<recordPianoKeyLists.size();j++)
        {

            RecordPianoKeyList key = recordPianoKeyLists.get(j);
            switch(key.getPosition()){


                case "1": /** Start a new Activity MyCards.java */
                    view=phv1;
                    view.setBackgroundColor(color);
                    break;
                case "2": /** Start a new Activity MyCards.java */
                    view=bphv2;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "3": /** Start a new Activity MyCards.java */
                    view=phv2;
                    view.setBackgroundColor(color);
                    break;
                case "4": /** Start a new Activity MyCards.java */
                    view=bphv3;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "5": /** Start a new Activity MyCards.java */
                    view=phv3;
                    view.setBackgroundColor(color);
                    break;
                case "6": /** Start a new Activity MyCards.java */
                    view=phv4;
                    view.setBackgroundColor(color);
                    break;
                case "7": /** Start a new Activity MyCards.java */
                    view=bphv5;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "8": /** Start a new Activity MyCards.java */
                    view=phv5;
                    view.setBackgroundColor(color);
                    break;
                case "9": /** Start a new Activity MyCards.java */
                    view=bphv6;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "10": /** Start a new Activity MyCards.java */
                    view=phv6;
                    view.setBackgroundColor(color);
                    break;
                case "11": /** Start a new Activity MyCards.java */
                    view=
                            bphv7;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "12": /** Start a new Activity MyCards.java */
                    view=phv7;
                    view.setBackgroundColor(color);
                    break;
                case "13": /** Start a new Activity MyCards.java */
                    view=phv8;
                    view.setBackgroundColor(color);
                    break;
                case "14": /** Start a new Activity MyCards.java */
                    view=bphv9;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "15": /** Start a new Activity MyCards.java */
                    view=phv9;
                    view.setBackgroundColor(color);
                    break;
                case "16": /** Start a new Activity MyCards.java */
                    view=bphv10;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "17": /** Start a new Activity MyCards.java */
                    view=phv10;
                    view.setBackgroundColor(color);
                    break;
                case "18": /** Start a new Activity MyCards.java */
                    view=phv11;
                    view.setBackgroundColor(color);
                    break;
                case "19": /** Start a new Activity MyCards.java */
                    view=bphv12;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "20": /** Start a new Activity MyCards.java */
                    view=phv12;
                    view.setBackgroundColor(color);
                    break;
                case "21": /** Start a new Activity MyCards.java */
                    view=bphv13;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "22": /** Start a new Activity MyCards.java */
                    view=phv13;
                    view.setBackgroundColor(color);
                    break;
                case "23": /** Start a new Activity MyCards.java */
                    view=bphv14;
                    view.setBackgroundColor(getResources().getColor(R.color.text_color));
                    break;
                case "24": /** Start a new Activity MyCards.java */
                    view=phv14;
                    view.setBackgroundColor(color);
                    break;
                case "25": /** Start a new Activity MyCards.java */
                    view=phv15;
                    view.setBackgroundColor(color);
                    break;

            }


            delay=delay+Integer.parseInt(key.getWaiting_time())-20;
            // if(key.getPosition().equalsIgnoreCase(""))
            view.setVisibility(View.INVISIBLE);
            startanimation(delay,view);
        }
    }

    private void startanimation(int delay,View view) {

        new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                slideUp(view);
                Log.d("Handler", "Running Handler");
            }
        }, delay);

    }

    public void slideUp(View view){

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -(view.getHeight()),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(800);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        view.setVisibility(View.VISIBLE);
        slideDown(view);


    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                (view.getHeight())); // toYDelta
        animate.setDuration(800);
        animate.setFillAfter(true);
        view.startAnimation(animate);

    }

    @SuppressLint("SetTextI18n")
    public void startTime() {
        chronometer.setOnChronometerTickListener(chronometer -> {
            CharSequence text = chronometer.getText();
            if (text.length() == 5) {
                String a[] = text.toString().split(":");
                String min = a[0] + " min : ";
                String sec = a[1] + " sec";
                chronometer.setText(min + sec);
            } else if (text.length() == 7) {
                String a[] = text.toString().split(":");
                String min = a[1] + " min : ";
                String sec = a[2] + " sec";
                chronometer.setText(min + sec);
            }
        });
        chronometer.setBase(SystemClock.elapsedRealtime() - 1000);
        chronometer.start();
    }

    @SuppressLint("SetTextI18n")
    private void stopTime() {
        chronometer.stop();
        chronometer.setText("00 min : 00 sec");
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);

        pianoView =new PianoView(context);
        pianoView = findViewById(R.id.pianoView);

        pianoView.setPianoVolume(Float.parseFloat(Util.getPianoSound(context)));
        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setSoundPollMaxStream(100);
        pianoView.setLoadAudioListener(this);



        ivPlay = findViewById(R.id.ivPlay);
        llSongData = findViewById(R.id.llSongData);
        llwhitekey = findViewById(R.id.llwhitekey);
        llblackkey = findViewById(R.id.llblackkey);
        llplay1 = findViewById(R.id.llplay1);
        tvName = findViewById(R.id.tvName);
        tvName1 = findViewById(R.id.tvName1);
        tvArtist = findViewById(R.id.tvArtist);
        tvNext = findViewById(R.id.tvNext);
        tvBack = findViewById(R.id.tvBack);
        ivImage = findViewById(R.id.ivImage);
        ivSongImage = findViewById(R.id.ivSongImage);
        ivImage2 = findViewById(R.id.ivImage2);
        ivplay1Stop = findViewById(R.id.ivplay1Stop);
        ivPlay1 = findViewById(R.id.ivPlay1);
        rlCountdown = findViewById(R.id.rlCountdown);
        tvCounter = findViewById(R.id.tvCounter);
        llData = findViewById(R.id.llData);
        llCounterBg = findViewById(R.id.llCounterBg);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        chronometer = findViewById(R.id.chronometer);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ivplay1Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.release();
                stopMusic();
            }
        });
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        ImageView ivHelp = findViewById(R.id.ivHelp);
        ivHelp.setVisibility(View.GONE);
        ImageView ivDashboard = findViewById(R.id.ivDashboard);
        ivDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        });
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(view -> {
            if (Util.getUserId(context) == null) {
                Util.loginDialog(context, "You need to be signed in to this action.");
            } else {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        ImageView ivBack= findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView tvDate = findViewById(R.id.tvDate);
        Util.setDate(tvDate);
        TextView tvPage = findViewById(R.id.tvPage);
        tvPage.setText("Play Along");
        TextView lblDashboard = findViewById(R.id.lblDashboard);
        lblDashboard.setText("Song");
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            if (screen.equals("record")) {
                super.onBackPressed();
                finish();
                overridePendingTransition(0, 0);
            } else {
                goToSongList();
            }
        }
    }

    private long exitTime = 0;

    private void goToSongList() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_to_go_song_list, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}