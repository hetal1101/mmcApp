package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.NodesUnlockScreen;
import com.makemusiccount.android.activity.SubCategoryActivity;
import com.makemusiccount.android.activity.SuccessActivity;
import com.makemusiccount.android.adapter.SongEquationAdapter;
import com.makemusiccount.android.listener.onHelpClickListener;
import com.makemusiccount.android.model.SongEquationList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.ui.InvertedTextProgressbar;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
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

import pl.droidsonroids.gif.GifImageView;

import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongEquationFragment extends Fragment {

    MediaPlayer player;

    View view, view1;

    RelativeLayout rlPiano;

    boolean isfirst = true;

    RecyclerView recyclerView;

    List<SongEquationList> songEquationLists = new ArrayList<>();

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "", total_key = "", song_file = "", song_name = "", downloadAudioPath = "";

    SongEquationAdapter songEquationAdapter;

    int ThisVisibleItemCount = 0;

    int currentPosition = 0;

    static onHelpClickListener onHelpClickListener;

    ImageView ivHelp, ivNotification;

    TextView tvHint;

    ArrayList<String> imageList = new ArrayList<>();

    RelativeLayout progressBar;

    boolean isPopup = true;

    String point_text = "", heading = "", song_complete_screen = "";

    TextView tvHeading, tvPointText, tvSongName, tvHelpHint;

    ImageView ivBack1, ivNext1;

    MediaPlayer mediaPlayer;

    LinearLayout llEquation;

    int hint = 0, right = 0, wrong = 0;

    boolean countEdit;

    private ImageView imageView2, imageView3;

    PianoView pianoView;

    InvertedTextProgressbar progress;

    boolean a = true;

    String message = "", hintMessage = "";

    boolean msgshow = true;

    LinearLayout llParentLayout;
    List<Integer> numberOfLine1 = new ArrayList<>();


    public static void setOnHelpClickListener(onHelpClickListener listener) {
        onHelpClickListener = listener;
    }

    public SongEquationFragment() {

    }

    private void counterEnable() {
        if (songEquationLists.size() > currentPosition) {
            switch (songEquationLists.get(currentPosition).getOctave()) {
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
                    if (MainActivity.isHome == 3) {
                        increaseCounter(counterKeys.HINT);
                        onHelpClickListener.onClick("key");
                    }
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

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "ResourceType"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_song_equation, container, false);

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

        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);

        final LinearLayoutManager mLayoutManagerBestProduct = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManagerBestProduct);
        recyclerView.setHasFixedSize(true);
        songEquationAdapter = new SongEquationAdapter(context, songEquationLists);
        recyclerView.setAdapter(songEquationAdapter);

        if (global.isNetworkAvailable()) {
            new GetSongEquation().execute();
        } else {
            retryInternet("song_equation");
        }

        ivNotification.setVisibility(View.GONE);
        ivHelp.setOnClickListener(view -> {

            hintMessage = "";
            hintMessage = MainActivity.CurrentEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    if (MainActivity.isHome == 3) {
                        increaseCounter(counterKeys.HINT);
                        onHelpClickListener.onClick("key");
                    }
                }
            }

            hintMessage = "";
            hintMessage = MainActivity.CurrentEquationHint;
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
            hintMessage = MainActivity.CurrentEquationHint;
            Log.e("msg : ", hintMessage);

            if (isfirst) {
                isfirst = false;
                openFirstPopup();
            } else {
                if (onHelpClickListener != null) {
                    if (MainActivity.isHome == 3) {
                        increaseCounter(counterKeys.HINT);
                        onHelpClickListener.onClick("key");
                    }
                }
            }

            hintMessage = "";
            hintMessage = MainActivity.CurrentEquationHint;
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

        MainActivity.setPianoClickListener(position -> {

            if (position == Integer.parseInt(MainActivity.CurrentEquationPosition)) {

                if (songEquationLists.size() > (currentPosition + 1)) {
                    if (MainActivity.CurrentEquationType.equals("Chord")) {
                        increaseCounter(counterKeys.RIGHT);
                        openChordPopup();
                    } else {
                        increaseCounter(counterKeys.RIGHT);
                        recyclerView.smoothScrollToPosition(currentPosition + 1);
                        TypedValue typedValue=new TypedValue();
                        Resources.Theme theme=context.getTheme();
                        theme.resolveAttribute(R.attr.new_dark,typedValue,true);
                        @ColorInt int color =typedValue.data;
                         /*   String a = String.valueOf(currentPosition);
                            Toast.makeText(context, a, Toast.LENGTH_SHORT).show();*/

                        View view = context.findViewById(numberOfLine1.get(currentPosition));
                        view.setBackgroundColor(color);

                        //tvCount.setText(currentPosition +1 + "/" + songEquationLists.size());
                    }

                } else {
                    increaseCounter(counterKeys.RIGHT);
                    if (isPopup) {
                        openRightPopup();
                    }
                }
            } else {
                message = "";
                message = MainActivity.CurrentEquationHint;
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
                    MainActivity.CurrentEquationSr = songEquationLists.get(ThisVisibleItemCount).getSr();
                    MainActivity.CurrentEquationLabel = songEquationLists.get(ThisVisibleItemCount).getLabel();
                    MainActivity.CurrentEquationValue = songEquationLists.get(ThisVisibleItemCount).getValue();
                    MainActivity.CurrentEquationPosition = songEquationLists.get(ThisVisibleItemCount).getKey_value()!=null?(songEquationLists.get(ThisVisibleItemCount).getKey_value().equalsIgnoreCase("")?"0":songEquationLists.get(ThisVisibleItemCount).getKey_value()):"0";
                    MainActivity.CurrentEquationType = songEquationLists.get(ThisVisibleItemCount).getType();
                    MainActivity.CurrentEquationImage = songEquationLists.get(ThisVisibleItemCount).getImage();
                    MainActivity.CurrentEquationChange_char = songEquationLists.get(ThisVisibleItemCount).getChange_char();
                    MainActivity.CurrentEquationHint = songEquationLists.get(ThisVisibleItemCount).getHint();
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
            if (mLayoutManagerBestProduct.findFirstVisibleItemPosition() < (songEquationLists.size() - 1)) {
                recyclerView.smoothScrollToPosition(mLayoutManagerBestProduct.findLastVisibleItemPosition() + 1);
            } else {
                recyclerView.smoothScrollToPosition(songEquationLists.size() - 1);
            }
        });

        tvSongName.setText(MainActivity.SongName);

        tvHint.setOnClickListener(view -> {
            if (!MainActivity.SongHintImage.equalsIgnoreCase("")) {
                openHintPopup();
                //Toast.makeText(context, MainActivity.SongHintImage, Toast.LENGTH_SHORT).show();
            } else {

            }
        });

        if (!MainActivity.SongHintImage.equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
        } else {
            tvHint.setVisibility(View.GONE);
        }

        return view;
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

    /*private void openHintPopup() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_hint, null);
            LinearLayout llMain = alertLayout.findViewById(R.id.llMain);

            alertDialogBuilder.setView(alertLayout);

            alertDialog = alertDialogBuilder.create();

            final AlertDialog finalAlertDialog = alertDialog;
            llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                }
            });

            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            lp.width = Util.convertDpToPixel(280, context);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

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

            llMain.setOnClickListener(v -> {
                if (a) {
                    a = false;
                   /* //String SHOWCASE_ID = String.valueOf(1111);
                    new MaterialShowcaseView.Builder(context)
                            .setTarget(ivHelp)
                            .setDismissText("GOT IT")
                            .setContentText("Get hint to solve the equation.")
                            //.setDelay(100)
                            //.singleUse(SHOWCASE_ID)
                            .setDismissOnTouch(true)
                            .setShapePadding(40)
                            .show();*/

                    /*new GuideView.Builder(context)
                            .setTitle("Hint")
                            .setContentTextSize(15)
                            .setDismissType(DismissType.anywhere)
                            .setContentTextSize(13)
                            .setContentText("Get hint to solve the equation.")
                            .setTargetView(ivHelp)
                            .build()
                            .show();*/

                    displayTuto();

                }

                finalAlertDialog.dismiss();
            });
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

    protected void displayTuto() {
        TutoShowcase.from(context)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {

                    }
                })
                .setContentView(R.layout.songhelp)
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

    @SuppressLint("SetTextI18n")
    private void openChordPopup() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_chord_type_popup, null);
            LinearLayout llMain = alertLayout.findViewById(R.id.llMain);

            alertDialogBuilder.setView(alertLayout);

            alertDialog = alertDialogBuilder.create();

            ImageView ivImage = alertLayout.findViewById(R.id.ivImage);

            Glide.with(context)
                    .load(MainActivity.CurrentEquationImage)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.app_logo)
                    .into(ivImage);

            final AlertDialog finalAlertDialog = alertDialog;
            llMain.setOnClickListener(v -> finalAlertDialog.dismiss());

            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(alertDialog.getWindow()).getAttributes());
            lp.width = Util.convertDpToPixel(280, context);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(lp);
            alertDialog.setOnDismissListener(dialogInterface -> recyclerView.smoothScrollToPosition(currentPosition + 1));
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            Dialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_right_key_press, null);


            RelativeLayout llMain = alertLayout.findViewById(R.id.llMain);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();

            final Dialog finalAlertDialog = alertDialog;
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

    @SuppressLint("SetTextI18n")
    private void openHintPopup() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
            AlertDialog alertDialog;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.layout_hint_key_press, null);
            ImageView ivImage = alertLayout.findViewById(R.id.ivImage);
            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            final AlertDialog finalAlertDialog = alertDialog;
            GifImageView GIF = alertLayout.findViewById(R.id.GIF);
            Glide.with(context)
                    .load(MainActivity.SongHintImage)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            ivImage.setImageDrawable(resource);
                            GIF.setVisibility(View.GONE);
                            ivImage.setVisibility(View.VISIBLE);
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

    private void onSongComplete() {
        new SongComplete().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SongComplete extends AsyncTask<String, Void, String> {
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
            String strAPI = AppConstant.API_SONG_COMPLETE + UserId
                    + "&songsID=" + MainActivity.SongId
                    + "&right_count=" + right
                    + "&wrong_count=" + wrong
                    + "&hint_count=" + hint
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&second_time=" + getCurrentTime()
                    + "&app_type=" + "Android";

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
                        song_name = jsonObjectList.getString("song_name");
                        song_file = jsonObjectList.getString("song_file");
                        heading = jsonObjectList.getString("heading");
                        point_text = jsonObjectList.getString("point_text");
                        song_complete_screen = jsonObjectList.getString("song_complete_screen");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("image_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        imageList.add(jsonObjectList.getString("image"));
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
                if (song_complete_screen.equalsIgnoreCase("yes")) {
                    Intent intent = new Intent(context, SuccessActivity.class);
                    intent.putExtra("song_id", SubCategoryActivity.SongId);
                    startActivity(intent);
                    context.finish();
                } else {
                    Intent intent = new Intent(context, NodesUnlockScreen.class);
                    intent.putExtra("screen", "equation");
                    intent.putExtra("song_name", song_name);
                    intent.putExtra("song_id", SubCategoryActivity.SongId);
                    startActivity(intent);
                    context.finish();
                    context.overridePendingTransition(0, 0);
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
            a = true;
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_SONG_EQUATION + UserId
                    + "&songsID=" + MainActivity.SongId
                    + "&deviceId=" + Util.getDeviceId(context)
                    + "&app_type=" + "Android";

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
                        if (resCode.equalsIgnoreCase("0")) {
                            total_key = jsonObjectList.getString("total_key");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_eq");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        SongEquationList songEquationList = new SongEquationList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        songEquationList.setSr(jsonObjectList.getString("sr"));
                                        songEquationList.setLabel(jsonObjectList.getString("label"));
                                        songEquationList.setValue(jsonObjectList.getString("value"));
                                        songEquationList.setKey_value(jsonObjectList.getString("key_value"));
                                        songEquationList.setType(jsonObjectList.getString("type"));
                                        songEquationList.setImage(jsonObjectList.getString("image"));
                                        songEquationList.setOctave(jsonObjectList.getString("octave"));
                                        songEquationList.setHint(jsonObjectList.getString("hint"));
                                        songEquationList.setChange_char(jsonObjectList.getString("change_char"));
                                        songEquationList.setEqn_type(jsonObjectList.getString("eqn_type"));
                                        songEquationList.setEqn_image(jsonObjectList.getString("eqn_image"));
                                        songEquationLists.add(songEquationList);
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            if (resCode.equalsIgnoreCase("0")) {
                songEquationAdapter.notifyDataSetChanged();
                lastTime = sdf.format(Calendar.getInstance().getTime());

                for (int i = 0; i < songEquationLists.size(); i++) {
                    CreateView();
                }

                //tvCount.setText("0" + "/" + songEquationLists.size());
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
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

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss.SSS");

    String lastTime;


    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);
        ivNotification = context.findViewById(R.id.ivNotification);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        imageView3 = context.findViewById(R.id.imageView3);
        imageView2 = context.findViewById(R.id.imageView2);
        pianoView = context.findViewById(R.id.pv);
        tvHeading = view.findViewById(R.id.tvHeading);
        tvSongName = view.findViewById(R.id.tvSongName);
        tvPointText = view.findViewById(R.id.tvPointText);
        progressBar = view.findViewById(R.id.progress_view);
        ivBack1 = view.findViewById(R.id.ivBack1);
        ivNext1 = view.findViewById(R.id.ivNext1);
        llEquation = view.findViewById(R.id.llEquation);
        progress = view.findViewById(R.id.progress);
        llParentLayout = view.findViewById(R.id.llParentLayout);
        tvHint = view.findViewById(R.id.tvHint);

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