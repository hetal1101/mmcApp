package com.makemusiccount.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Rectangle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makemusiccount.android.R;
import com.makemusiccount.android.activity.MainActivity;
import com.makemusiccount.android.activity.NoNetworkActivity;
import com.makemusiccount.android.activity.SubscribePackageActivity;
import com.makemusiccount.android.adapter.SongAdapter;
import com.makemusiccount.android.model.SongList;
import com.makemusiccount.android.retrofit.RequestMethod;
import com.makemusiccount.android.retrofit.RestClient;
import com.makemusiccount.android.util.AppConstant;
import com.makemusiccount.android.util.Global;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.view.PianoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.makemusiccount.android.activity.MainActivity.drawer;
import static com.makemusiccount.android.activity.MainActivity.ivWelcomeSong;
import static com.makemusiccount.android.activity.MainActivity.llPianoView;
import static com.makemusiccount.android.activity.MainActivity.toggle;
import static com.makemusiccount.android.util.AppConstant.NO_NETWORK_REQUEST_CODE;
import static com.makemusiccount.android.util.Util.convertDpToPixel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    View view, view1;

    RelativeLayout rlPiano;

    PianoView pv;

    RecyclerView recyclerView;

    List<SongList> songLists = new ArrayList<>();

    SongAdapter songAdapter;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId = "", resMessage = "", resCode = "",
            subscription_msg = "", subscription_img = "",
            badge_title = "", badge_msg = "", badge_img = "";

    ImageView ivBack, ivNext, ivLock;

    boolean IsLoading = false;

    ProgressBar pbLoading;

    int page = 0;

    LinearLayoutManager linearLayoutManager;

    String search_text = "";

    ImageView ivNotification, ivHelp;

    TextView tvHelpHint;

    SharedPreferences sharedpreferences;

    TextView tvSongNext;

    MaterialShowcaseSequence sequence;

    @Override
    public void onResume() {
        super.onResume();
        ivHelp.setVisibility(View.GONE);
        tvHelpHint.setVisibility(View.GONE);
        ivNotification.setVisibility(View.GONE);
        if (Util.getUserId(context) == null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu, ((Activity)context).getTheme());
            toggle.setHomeAsUpIndicator(drawable);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ivHelp.setVisibility(View.VISIBLE);
        tvHelpHint.setVisibility(View.VISIBLE);
        ivNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setPadding(40,0,0,0);

        EditText searchEditText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));
        TypedValue typedValue=new TypedValue();
        Resources.Theme theme=getActivity().getTheme();
        theme.resolveAttribute(R.attr.new_dark,typedValue,true);
        @ColorInt int color =typedValue.data;

        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        Drawable whiteIcon = icon.getDrawable();
        whiteIcon.setTint(color); //Whatever color you want it to be
        icon.setImageDrawable(whiteIcon);

        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        super.onCreateOptionsMenu(menu, inflater);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 0;
                search_text = query;
               // new GetSongList().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            page = 0;
            if (!search_text.isEmpty()) {
                search_text = "";
               // new GetSongList().execute();
            }
            return false;
        });
    }

    public SongListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_song_list, container, false);

        context = getActivity();

        setHasOptionsMenu(true);

        UserId = Util.getUserId(context); if(UserId==null) {UserId=""; }

        global = new Global(context);

        initComp(view);

        tvSongNext.setOnClickListener(view -> {
            ivWelcomeSong.setVisibility(View.GONE);
            if (songAdapter != null) {
                songAdapter.startAnimation();
            }
        });

        view1.setVisibility(View.VISIBLE);
        rlPiano.setVisibility(View.VISIBLE);
        pv.setVisibility(View.VISIBLE);

        sequence = new MaterialShowcaseSequence(context, "complete1");
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(300);
        sequence.setConfig(config);

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        songAdapter = new SongAdapter(context, songLists, sequence);
        recyclerView.setAdapter(songAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                boolean endHasBeenReached = lastVisible + 2 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if (IsLoading) {
                        IsLoading = false;
                        page++;
                        new GetSongList().execute();
                    }
                }
            }
        });

        if (global.isNetworkAvailable()) {
            new GetSongList().execute();
        } else {
            retryInternet("song_list");
        }

        songAdapter.setOnItemClickListener((position, view, which) -> {
            if (which == 3) {
                //showPopup(position);

                    MainActivity.SongId = songLists.get(position).getID();
                    MainActivity.SongName = songLists.get(position).getName();
                    MainActivity.SongHintImage = songLists.get(position).getSong_hint_image();
                    Fragment fragment = new SongEquationFragment();
                    llPianoView.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    MainActivity.isHome = 3;

            }
            if( which ==5)
            {
                if (!subscription_msg.isEmpty()) {
                    openPopup();
                }
                else
                {
                    MainActivity.SongId = songLists.get(position).getID();
                    MainActivity.SongName = songLists.get(position).getName();
                    MainActivity.SongHintImage = songLists.get(position).getSong_hint_image();
                    Fragment fragment = new SongEquationFragment();
                    llPianoView.setVisibility(View.VISIBLE);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, fragment);
                    fragmentTransaction.commit();
                    MainActivity.isHome = 3;
                }
            }
        });

        ivBack.setOnClickListener(view -> {
            if (linearLayoutManager.findFirstVisibleItemPosition() > 0) {
                recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - 1);
            } else {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        ivNext.setOnClickListener(view -> {
            if (songLists.size() != 0) {
                if (linearLayoutManager.findFirstVisibleItemPosition() < (songLists.size() - 1)) {
                    recyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                } else {
                    recyclerView.smoothScrollToPosition(songLists.size() - 1);
                }
            }
        });

        return view;
    }

    Dialog dialog_play;

    private ImageView buttonPlayPause;
    private SeekBar seekBarProgress;
    public EditText editTextSongURL;
    ProgressBar progress_bar;
    TextView tvTitle, tvNext;
    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private final Handler handler = new Handler();

    boolean isPrepared;

    @SuppressLint("ClickableViewAccessibility")
    private void showPopup(int position) {
        isPrepared = false;
        dialog_play = new Dialog(context);
        dialog_play.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog_play.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog_play.setContentView(R.layout.popup_play_song);
        Window window = dialog_play.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        buttonPlayPause = dialog_play.findViewById(R.id.ButtonTestPlayPause);
        buttonPlayPause.setOnClickListener(this);

        seekBarProgress = dialog_play.findViewById(R.id.SeekBarTestPlay);
        progress_bar = dialog_play.findViewById(R.id.progress_bar);
        tvTitle = dialog_play.findViewById(R.id.tvTitle);
        tvNext = dialog_play.findViewById(R.id.tvNext);
        ImageView ivImage = dialog_play.findViewById(R.id.ivImage);
        seekBarProgress.setMax(99); // It means 100% .0-99
        seekBarProgress.setOnTouchListener(this);
        editTextSongURL = dialog_play.findViewById(R.id.EditTextSongURL);
        editTextSongURL.setText(songLists.get(position).getSong_file());
        tvTitle.setText(songLists.get(position).getName());
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);

        Glide.with(context)
                .load(songLists.get(position).getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.app_logo)
                .into(ivImage);

        dialog_play.setOnDismissListener(dialogInterface -> {
            mediaPlayer.pause();
            progress_bar.setVisibility(View.GONE);
            buttonPlayPause.setVisibility(View.VISIBLE);
            buttonPlayPause.setImageResource(R.drawable.play_song);
        });

        tvNext.setOnClickListener(view -> {
            dialog_play.dismiss();
            MainActivity.SongId = songLists.get(position).getID();
            MainActivity.SongName = songLists.get(position).getName();
            MainActivity.SongHintImage = songLists.get(position).getSong_hint_image();
            Fragment fragment = new SongEquationFragment();
            llPianoView.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            MainActivity.isHome = 3;
        });

        dialog_play.show();

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
                if (extraValue.equalsIgnoreCase("song_list")) {
                    new GetSongList().execute();
                }
            }
        }
    }

    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        seekBarProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        buttonPlayPause.setImageResource(R.drawable.play_song);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ButtonTestPlayPause) {
            new PrepareSong().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PrepareSong extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isPrepared) {
                progress_bar.setVisibility(View.VISIBLE);
                buttonPlayPause.setVisibility(View.GONE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mediaPlayer.setDataSource(editTextSongURL.getText().toString()); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
                isPrepared = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                progress_bar.setVisibility(View.GONE);
                buttonPlayPause.setVisibility(View.VISIBLE);
                buttonPlayPause.setImageResource(R.drawable.stop_song);
            } else {
                mediaPlayer.pause();
                progress_bar.setVisibility(View.GONE);
                buttonPlayPause.setVisibility(View.VISIBLE);
                buttonPlayPause.setImageResource(R.drawable.play_song);
            }

            primarySeekBarProgressUpdater();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.SeekBarTestPlay) {
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) view;
                int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                mediaPlayer.seekTo(playPositionInMillisecconds);
            }
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSongList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resCode = "";
            resMessage = "";
            if (page == 0) {
                songLists.clear();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                }
            } else {
                pbLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_SONG_LIST + UserId
                    + "&catID=" + MainActivity.CatId
                    + "&subcatID=" + MainActivity.SubCatId
                    + "&pagecode=" + page
                    + "&search_text=" + search_text
                    + "&songType=" + MainActivity.songType
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
                String response = restClient.getResponse();
                Log.e("API", response);

                if (response != null && response.length() != 0) {
                    jsonObjectList = new JSONObject(response);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            subscription_msg = jsonObjectList.getString("subscription_msg");
                            subscription_img = jsonObjectList.getString("subscription_img");
                            badge_title = jsonObjectList.getString("badge_title");
                            badge_msg = jsonObjectList.getString("badge_msg");
                            badge_img = jsonObjectList.getString("badge_img");
                            JSONArray jsonArray = jsonObjectList.getJSONArray("song_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    List<SongList> FreesongLists = new ArrayList<>();
                                    List<SongList> PremiumsongLists = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        SongList songList = new SongList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        songList.setID(jsonObjectList.getString("ID"));
                                        songList.setName(jsonObjectList.getString("name"));
                                        songList.setImage(jsonObjectList.getString("image"));
                                        songList.setStatus(jsonObjectList.getString("status"));
                                        songList.setPlay_songs(jsonObjectList.getString("play_songs"));
                                        songList.setSong_file(jsonObjectList.getString("song_file"));
                                        songList.setSong_hint_image(jsonObjectList.getString("song_hint_image"));
                                        songList.setArtist(jsonObjectList.getString("artist"));
                                        songList.setPlay_autoplay(jsonObjectList.getString("play_autoplay"));
                                        songList.setSong_level(jsonObjectList.getString("song_level"));
                                        songList.setSong_quiz(jsonObjectList.getString("song_quiz"));
                                        songList.setSong_intro_name(jsonObjectList.getString("song_intro_name"));
                                        songList.setSong_intro(jsonObjectList.getString("song_intro"));

                                        if(songList.getSong_level().equalsIgnoreCase("free"))
                                        {
                                            FreesongLists.add(songList);
                                        }
                                        else
                                        {
                                            PremiumsongLists.add(songList);
                                        }
                                       // songLists.add(songList);
                                    }
                                    Collections.shuffle(PremiumsongLists);
                                    if(FreesongLists.size()>0)
                                    {
                                        songLists.addAll(0,FreesongLists);
                                    }
                                    songLists.addAll(PremiumsongLists);


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
        public class Sortbyfree implements Comparator<SongList>
        {
            // Used for sorting in ascending order of
            // roll number

            @Override
            public int compare(SongList songList, SongList t1) {
                if(songList.getSong_level().equalsIgnoreCase("free"))
                {
                    return -1;
                }
                else
                {
                    return 1;
                }

            }
        }

        @Override
        protected void onPostExecute(String s) {
            dismissProgressDialog();
            pbLoading.setVisibility(View.GONE);
            if (resCode.equalsIgnoreCase("0")) {
                IsLoading = true;
                songAdapter.notifyDataSetChanged();
                if (!badge_msg.isEmpty()) {
                    openBadgesPopup();
                }
                if (MainActivity.songType.equals("Premium")) {
                    sharedpreferences = context.getSharedPreferences("showcase", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    String isFirst = sharedpreferences.getString("isFirstSong", "");
                    assert isFirst != null;
                    if (!isFirst.equals("")) {
                        ivWelcomeSong.setVisibility(View.GONE);
                    } else {
                        ivWelcomeSong.setVisibility(View.VISIBLE);
                        editor.putString("isFirstSong", "Yes");
                        editor.apply();
                        editor.commit();
                    }
                }
            } else {
                IsLoading = false;
                if (page == 0) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void openBadgesPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_badges_detail, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnOk = alert_layout.findViewById(R.id.btnOk);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(badge_msg);
        tvTitle.setText(badge_title);

        btnOk.setOnClickListener(v -> dialog.dismiss());
        Glide.with(context)
                .load(badge_img)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivIcon);

        dialog.setOnDismissListener(dialogInterface -> {
            if (global.isNetworkAvailable()) {
                page = 0;
              //  new GetSongList().execute();
            } else {
                Toast.makeText(context, "No internet available!!!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(380, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    AlertDialog dialog;

    private void openPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View alert_layout = inflater.inflate(R.layout.popup_subscribe_package, null);

        alertDialogBuilder.setView(alert_layout);

        dialog = alertDialogBuilder.create();

        dialog.setCancelable(false);

        TextView tvTitle = alert_layout.findViewById(R.id.tvTitle);
        TextView tvMsg = alert_layout.findViewById(R.id.tvMsg);
        TextView btnCancel = alert_layout.findViewById(R.id.btnCancel);
        TextView btnSubscribe = alert_layout.findViewById(R.id.btnSubscribe);
        ImageView ivIcon = alert_layout.findViewById(R.id.ivIcon);

        tvMsg.setText(subscription_msg);
        tvTitle.setText(subscription_msg);

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        btnSubscribe.setOnClickListener(view -> {
            dialog.dismiss();
            startActivity(new Intent(context, SubscribePackageActivity.class));
            /*Fragment fragment = new HomeFragment();
            llPianoView.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            MainActivity.isHome = 0;*/
        });

        Glide.with(context)
                .load(subscription_img)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivIcon);

        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = convertDpToPixel(380, context);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        window.setAttributes(lp);
    }

    private void initComp(View view) {
        progressDialog = new ProgressDialog(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        view1 = context.findViewById(R.id.view1);
        rlPiano = context.findViewById(R.id.rlPiano);
        ivLock = context.findViewById(R.id.ivLock);
        pv = context.findViewById(R.id.pv);
        ivBack = view.findViewById(R.id.ivBack);
        ivNext = view.findViewById(R.id.ivNext);
        pbLoading = view.findViewById(R.id.pbLoading);
        ivNotification = context.findViewById(R.id.ivNotification);
        ivHelp = context.findViewById(R.id.ivHelp);
        tvHelpHint = context.findViewById(R.id.tvHelpHint);
        tvSongNext = context.findViewById(R.id.tvSongNext);
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