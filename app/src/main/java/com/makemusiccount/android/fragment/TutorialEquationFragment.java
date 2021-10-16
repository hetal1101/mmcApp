package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.tutoshowcase.TutoShowcase;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.PianoActivity;
import com.makemusiccount.android.activity.TutorialEquationActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.TutorialPlayAlongActivity;
import com.makemusiccount.android.activity.TutorialS2;
import com.makemusiccount.android.activity.TutorialSuccessActivity;
import com.makemusiccount.android.adapter.TutorialEquationAdapter;
import com.makemusiccount.android.listener.onHelpClickListener;
import com.makemusiccount.android.model.TutorialEquationList;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.InvertedTextProgressbar;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.AppConstant.TutorialLists;
import static com.makemusiccount.android.util.AppConstant.tcount;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialEquationFragment extends Fragment {

    View view, view1;

    RelativeLayout rlPiano;

    RecyclerView recyclerView;

    boolean msgshow = true;

    String message = "", hintMessage = "",songID="";

    boolean isfirst = true;

    List<TutorialEquationList> tutorialEquationLists = new ArrayList<>();

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", line1="",line2="",line3="",resCode = "",first_intro_complete="", total_key = "", downloadAudioPath = "";

    TutorialEquationAdapter tutorialEquationAdapter;

    int ThisVisibleItemCount = 0;

    int currentPosition = 0;

    static onHelpClickListener onHelpClickListener;

    ImageView ivHelp;

    TextView tvHelpHint;

    RelativeLayout progressBar;

    boolean isPopup = true;

    TextView tvSongName;

    ImageView ivBack1, ivNext1;

    MediaPlayer mediaPlayer;

    LinearLayout llEquation;

    int hint = 0, right = 0, wrong = 0;

    boolean countEdit;

    private ImageView imageView2, imageView3;

    PianoView pianoView;

    InvertedTextProgressbar progress;

    LinearLayout llParentLayout;
    List<Integer> numberOfLine1 = new ArrayList<>();

    private void counterEnable() {
        if (tutorialEquationLists.size() > currentPosition) {
            switch (tutorialEquationLists.get(currentPosition).getOctave()) {
                case "left":
                    imageView2.setVisibility(View.VISIBLE);
                    imageView3.setVisibility(View.GONE);
                    break;
                case "right":
                    imageView2.setVisibility(View.GONE);
                    imageView3.setVisibility(View.VISIBLE);
                    break;
                default:
                    imageView2.setVisibility(View.GONE);
                    imageView3.setVisibility(View.GONE);
                    break;
            }
            imageView2.setPadding(0, 0, pianoView.getWhiteKeyWidth() / 2, 0);
            imageView3.setPadding(pianoView.getWhiteKeyWidth() / 2, 0, 0, 0);
            countEdit = true;
        }
    }

    private void counterDisable() {
        countEdit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        imageView2.setVisibility(View.GONE);
        imageView3.setVisibility(View.GONE);
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        if (primeThread != null) {
            primeThread.stopMyThread();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ivHelp.setVisibility(View.VISIBLE);
        tvHelpHint.setVisibility(View.VISIBLE);
        counterEnable();
    }

    private enum counterKeys {
        HINT, RIGHT, WRONG
    }

    private void increaseCounter(counterKeys counterKeys) {
        if (countEdit) {
            switch (counterKeys) {
                case HINT:
                    hint++;
                    break;
                case RIGHT:
                    right++;
                    break;
                case WRONG:
                    wrong++;
                    break;
            }
            counterDisable();
        }
        Log.e("Counter : ", "Hint-" + hint + ", Right-" + right + ", Wrong-" + wrong);
    }

    Handler handler;
    Runnable runnable;

    private void restartAutoHintHandler() {
        if (handler != null) {
            startProgress();
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 20000);
        } else {
            handler = new Handler();
            startProgress();
            handler.postDelayed(runnable = () -> {
                if (onHelpClickListener != null) {
                    increaseCounter(counterKeys.HINT);
                    onHelpClickListener.onClick("key");
                }
            }, 20000);
        }
    }

    class PrimeThread extends Thread {
        boolean isStop = false;

        void stopMyThread() {
            this.isStop = true;
        }

        public void run() {
            for (int i = 20; i >= 0; i--) {
                if (!isStop) {
                    int progress_percentage = i * 5;
                    progress.setText(i + " Seconds remaining...");
                    int finalI = i;
                    context.runOnUiThread(() -> {
                        progress.setText(finalI + " Seconds remaining...");
                        progress.setProgress(progress_percentage);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    PrimeThread primeThread;

    private void startProgress() {
        if (primeThread != null) {
            primeThread.stopMyThread();
            primeThread = new PrimeThread();
            primeThread.start();
        } else {
            primeThread = new PrimeThread();
            primeThread.start();
        }
    }

    public static void setOnHelpClickListener(onHelpClickListener listener) {
        onHelpClickListener = listener;
    }

    public TutorialEquationFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tutorial_equation, container, false);

        context = getActivity();

        global = new Global(context);

        initComp(view);

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        mediaPlayer = new MediaPlayer();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        downloadAudioPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(downloadAudioPath + File.separator + "voices");

        if (!audioVoice.exists()) {
            boolean is = audioVoice.mkdir();
            if (is) {
                Log.d("Create dir : --- ", "ok");
            }
        }

        tvSongName.setText(TutorialEquationActivity.tutorialCategoryName);

        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);

        final LinearLayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        tutorialEquationAdapter = new TutorialEquationAdapter(context, tutorialEquationLists);
        recyclerView.setAdapter(tutorialEquationAdapter);

        if (global.isNetworkAvailable()) {
            new GetSongEquation().execute();
        } else {
            retryInternet("song_equation");
        }

        ivHelp.setOnClickListener(view -> {
            hintMessage = "";
            hintMessage = TutorialEquationActivity.CurrentTutorialEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    increaseCounter(counterKeys.HINT);
                    onHelpClickListener.onClick("key");

                }
            }
            hintMessage = "";
            hintMessage = TutorialEquationActivity.CurrentTutorialEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    increaseCounter(counterKeys.HINT);
                    onHelpClickListener.onClick("key");

                }
            }

        });
        tvHelpHint.setOnClickListener(view -> {
            hintMessage = "";
            hintMessage = TutorialEquationActivity.CurrentTutorialEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    increaseCounter(counterKeys.HINT);
                    onHelpClickListener.onClick("key");

                }
            }

            hintMessage = "";
            hintMessage = TutorialEquationActivity.CurrentTutorialEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    increaseCounter(counterKeys.HINT);
                    onHelpClickListener.onClick("key");

                }
            }


        });

        TutorialEquationActivity.setPianoClickListener(position -> {

            if (position == Integer.parseInt(TutorialEquationActivity.CurrentTutorialEquationPosition)) {
                if (tutorialEquationLists.size() > (currentPosition + 1)) {
                    increaseCounter(counterKeys.RIGHT);
                    recyclerView.smoothScrollToPosition(currentPosition + 1);
                    TypedValue typedValue=new TypedValue();
                    Resources.Theme theme=context.getTheme();
                    theme.resolveAttribute(R.attr.new_dark,typedValue,true);
                    @ColorInt int color =typedValue.data;
                    View view = context.findViewById(numberOfLine1.get(currentPosition));
                    view.setBackgroundColor(color);

                } else {
                    increaseCounter(counterKeys.RIGHT);
                    if (isPopup) {
                        openRightPopup();
                    }
                }
            } else {

                message = "";
                message = TutorialEquationActivity.CurrentTutorialEquationHint;
                Log.e("msg : ", message);

                if (isPopup) {
                    increaseCounter(counterKeys.WRONG);
                    openWrongPopup();
                }

            }
        });

        recyclerView.setOnTouchListener((v, event) -> true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                ThisVisibleItemCount = mLayoutManagerBestProduct.findFirstCompletelyVisibleItemPosition();
                if (ThisVisibleItemCount != -1) {
                    currentPosition = ThisVisibleItemCount;
                    counterEnable();
                    restartAutoHintHandler();
                    TutorialEquationActivity.CurrentTutorialEquationPosition = tutorialEquationLists.get(ThisVisibleItemCount).getKey_value();
                    TutorialEquationActivity.CurrentTutorialEquationHint = tutorialEquationLists.get(ThisVisibleItemCount).getHint();
                }
            }
        });

        ivBack1.setOnClickListener(view -> {
            if (mLayoutManagerBestProduct.findFirstVisibleItemPosition() > 0) {
                recyclerView.smoothScrollToPosition(mLayoutManagerBestProduct.findFirstVisibleItemPosition() - 1);
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        ivNext1.setOnClickListener(view -> {
            if (mLayoutManagerBestProduct.findFirstVisibleItemPosition() < (tutorialEquationLists.size() - 1)) {
                recyclerView.smoothScrollToPosition(mLayoutManagerBestProduct.findLastVisibleItemPosition() + 1);
            } else {
                recyclerView.smoothScrollToPosition(tutorialEquationLists.size() - 1);
            }
        });

        return view;
    }

    private void openFirstPopup() {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.popup_firsthint, null);
            TextView tvOk = alertLayout.findViewById(R.id.tvOk);
            HtmlTextView tvHint = alertLayout.findViewById(R.id.tvHint);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            final AlertDialog finalAlertDialog = alertDialog;
            tvHint.setHtml(hintMessage);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                }
            });

            alertDialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            alertDialog.getWindow().setAttributes(lp);
            alertDialog.setOnCancelListener(dialog -> {
                finalAlertDialog.dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void displayTuto() {
        TutoShowcase.from(context)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {
                        //displayTuto1();
                    }
                })
                .setContentView(R.layout.tuthelp)
                .setFitsSystemWindows(true)
                .on(R.id.recyclerView)
                .addRoundRect()
                .withBorder()
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })

                .show();
    }

    protected void displayTuto1() {
        TutoShowcase.from(context)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {

                    }
                })
                .setContentView(R.layout.tuthelp1)
                .setFitsSystemWindows(true)
                .on(R.id.ivHelp)
                .addCircle()
                .withBorder()
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })

                .show();
    }

    public void retryInternet(String extraValue) {
        Intent i = new Intent(context, NoNetworkActivity.class);
        i.putExtra("extraValue", extraValue);
        startActivityForResult(i, NO_NETWORK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NO_NETWORK_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String extraValue = data.getStringExtra("extraValue");
                if (extraValue.equalsIgnoreCase("song_equation")) {
                    new GetSongEquation().execute();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void openWrongPopup() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_wrong_key_press, null);
            RelativeLayout llMain = alertLayout.findViewById(R.id.llMain);

            HtmlTextView tvMsg = alertLayout.findViewById(R.id.tvMsg);
            tvMsg.setHtml(message);
            if (msgshow) {
                msgshow = false;
                tvMsg.setVisibility(View.VISIBLE);
            } else {
                tvMsg.setVisibility(View.GONE);
            }

            alertDialogBuilder.setView(alertLayout);

            alertDialog = alertDialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;
            llMain.setOnClickListener(v -> finalAlertDialog.dismiss());

            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void openRightPopup() {
        try {

            AssetFileDescriptor afd = getActivity().getAssets().openFd("successsong.mpeg");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            AlertDialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_right_key_press, null);
            RelativeLayout llMain = alertLayout.findViewById(R.id.llMain);
            TextView tvMsg = alertLayout.findViewById(R.id.tvMsg);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            final AlertDialog finalAlertDialog = alertDialog;
            tvMsg.setText("You just finished one tutorial!");
            llMain.setOnClickListener(v -> {
                finalAlertDialog.dismiss();
                mediaPlayer.stop();
                onSongComplete();
            });
            alertDialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            alertDialog.getWindow().setAttributes(lp);
            alertDialog.setOnCancelListener(dialog -> {
                finalAlertDialog.dismiss();
                mediaPlayer.stop();
                onSongComplete();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSongComplete() {


        if(AppPreference.getPreference(context, AppPersistence.keys.isEndTutorial)!=null)
        {
            new SongComplete().execute();
        }
        else {
            if(tcount==0)
            {
                tcount=1;
                Intent i = new Intent(context, TutorialEquationActivity.class);
                i.putExtra("tutorialCategoryId", TutorialLists.get(1).getID());
                i.putExtra("tutorialCategoryName", TutorialLists.get(1).getName());
                i.putExtra("url", TutorialLists.get(1).getUrl());
                i.putExtra("hintOff","1");
                startActivity(i);
                pianoView.releaseAutoPlay();
                pianoView =new PianoView(context);
                context.finish();
            }
            else if(tcount==1)
            {
                tcount=2;
                Intent i = new Intent(context, TutorialEquationActivity.class);
                i.putExtra("tutorialCategoryId", TutorialLists.get(2).getID());
                i.putExtra("tutorialCategoryName", TutorialLists.get(2).getName());
                i.putExtra("url", TutorialLists.get(2).getUrl());
                i.putExtra("hintOff","1");
                startActivity(i);
                pianoView.releaseAutoPlay();
                pianoView =new PianoView(context);
                context.finish();
            }
            else
            {
                  Util.getKeyTheme(context);
                Intent intent = new Intent(context, PianoActivity.class);
                intent.putExtra("screen", "tutorial_select");
                intent.putExtra("song_name", AppConstant.songName);
                intent.putExtra("song_id", AppConstant.songId);
                startActivity(intent);
                pianoView.releaseAutoPlay();
                pianoView =new PianoView(context);
                context.finish();
            }
        }


    }

    @SuppressLint("StaticFieldLeak")
    private class SongComplete extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String song_complete_screen = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_Tutorial_COMPLETE + UserId
                    + "&tutorialID=" + TutorialEquationActivity.tutorialCategoryId
                    + "&right_count=" + right
                    + "&wrong_count=" + wrong
                    + "&hint_count=" + hint
                    + "&second_time=" + getCurrentTime()
                    + "&app_type=" + "Android";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        first_intro_complete = jsonObjectList.getString("screen");
                        songID = jsonObjectList.getString("songID");

                        if (resCode.equalsIgnoreCase("0")) {
                            song_complete_screen = jsonObjectList.getString("song_complete_screen");
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
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                if (song_complete_screen.equalsIgnoreCase("Yes")) {

                    if(AppPreference.getPreference(context,AppPersistence.keys.isEndTutorial)==null)
                    {
                        Intent intent = new Intent(context, TutorialS2.class);
                        startActivity(intent);
                        context.finish();
                    }
                    else
                    {
                        Intent intent = new Intent(context, TutorialSuccessActivity.class);
                        intent.putExtra("tutorialID", TutorialEquationActivity.tutorialCategoryId);
                        intent.putExtra("tutorial_name", TutorialEquationActivity.tutorialCategoryName);
                        startActivity(intent);
                        context.finish();
                    }

                } else {

                    Intent intent = new Intent(context, TutorialPlayAlongActivity.class);
                    intent.putExtra("tutorialID", TutorialEquationActivity.tutorialCategoryId);
                    intent.putExtra("tutorial_name", TutorialEquationActivity.tutorialCategoryName);
                    intent.putExtra("songID", songID);
                    intent.putExtra("first_intro_complete", first_intro_complete);
                    startActivity(intent);
                    pianoView.releaseAutoPlay();
                    pianoView =new PianoView(context);
                    context.finish();
                }

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongEquation extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_Tutorial_EQUATION + TutorialEquationActivity.tutorialCategoryId
                    + "";

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        line1 = jsonObjectList.getString("line1");
                        line2 = jsonObjectList.getString("line2");
                        line3 = jsonObjectList.getString("line3");

                        total_key = jsonObjectList.getString("total_key");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("tutorial_question");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TutorialEquationList tutorialEquationList = new TutorialEquationList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        tutorialEquationList.setSr(jsonObjectList.getString("sr"));
                                        tutorialEquationList.setLabel(jsonObjectList.getString("label"));
                                        tutorialEquationList.setValue(jsonObjectList.getString("value"));
                                        tutorialEquationList.setKey_value(jsonObjectList.getString("key_value"));
                                        tutorialEquationList.setOctave(jsonObjectList.getString("octave"));
                                        tutorialEquationList.setHint(jsonObjectList.getString("hint"));
                                        tutorialEquationList.setEqn_type(jsonObjectList.getString("eqn_type"));
                                        tutorialEquationList.setEqn_image(jsonObjectList.getString("eqn_image"));
                                        tutorialEquationLists.add(tutorialEquationList);
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
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                if(TutorialEquationActivity.hintOff.equalsIgnoreCase(""))
                {
                    displayTuto1();
                }

                tutorialEquationAdapter.notifyDataSetChanged();
                TextView tvll1=(TextView)context.findViewById(R.id.tvLine1);
                tvll1.setText(line1);
                TextView tvll2=(TextView)context.findViewById(R.id.tvLine2);
                tvll2.setText(line2);
                TextView tvll3=(TextView)context.findViewById(R.id.tvLine3);
                tvll3.setText(line3);

                lastTime = sdf.format(Calendar.getInstance().getTime());
                for (int i = 0; i < tutorialEquationLists.size(); i++) {
                    CreateView();
                }

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCurrentTime() {
        long seconds;
        try {
            Date Date1 = sdf.parse(lastTime);
            Date Date2 = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
            long millie = Date2.getTime() - Date1.getTime();
            seconds = TimeUnit.MILLISECONDS.toSeconds(millie);
        } catch (ParseException e) {
            seconds = 0;
            e.printStackTrace();
        }
        return String.valueOf(seconds);
    }

    @SuppressLint("ResourceAsColor")
    private void CreateView() {

        if (numberOfLine1.size() == 0) {
            numberOfLine1.add(1);
        } else {
            int i = numberOfLine1.get(numberOfLine1.size() - 1);
            i++;
            numberOfLine1.add(i);
        }

        View linev = new View(context);
        LinearLayout.LayoutParams line = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1, 1);
        line.bottomMargin = Util.pxFromDp(context, 5);
        line.height = Util.pxFromDp(context, 4);
        line.leftMargin = Util.pxFromDp(context, 20);
        linev.setBackgroundColor(R.color.boticon);
        linev.setLayoutParams(line);
        linev.setId(numberOfLine1.get(numberOfLine1.size() - 1));
        llParentLayout.addView(linev);

    }

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss.SSS");

    String lastTime;

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        imageView3 = context.findViewById(R.id.imageView3);
        imageView2 = context.findViewById(R.id.imageView2);
        pianoView = context.findViewById(R.id.pv);
        pianoView.numShow=7;
        pianoView.whitekey=3;

        tvSongName = view.findViewById(R.id.tvSongName);
        progressBar = view.findViewById(R.id.progress_view);
        ivBack1 = view.findViewById(R.id.ivBack1);
        ivNext1 = view.findViewById(R.id.ivNext1);
        llEquation = view.findViewById(R.id.llEquation);
        progress = view.findViewById(R.id.progress);
        llParentLayout = view.findViewById(R.id.llParentLayout);
    }

    @Override
    public void onDestroy() {
        pianoView.releaseAutoPlay();
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
