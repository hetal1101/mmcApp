package com.makemusiccount.android.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makemusiccount.android.R;
import com.makemusiccount.android.preference.AppPersistence;
import com.makemusiccount.android.preference.AppPreference;
import com.makemusiccount.android.util.Util;
import com.makemusiccount.pianoview.entity.Piano;
import com.squareup.picasso.Picasso;

public class SongDisplayActivity extends AppCompatActivity {

    ImageView song_image;
    TextView tvSongName,tvLoginTextUpper;
    LinearLayout tvSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_song_display);


        tvSignUp=findViewById(R.id.tvSignUp);
        song_image=findViewById(R.id.song_image);
        tvSongName=findViewById(R.id.tvSongName);
        tvLoginTextUpper=findViewById(R.id.tvLoginTextUpper);

        tvSongName.setText( getIntent().getStringExtra("SongNameintroname"));
        tvLoginTextUpper.setText( getIntent().getStringExtra("SongNameintro"));
        Picasso.get().load( getIntent().getStringExtra("songImage")).fit().into(song_image);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Util.getKeyTheme(SongDisplayActivity.this);
                if(!DashboardActivity.CatId.equalsIgnoreCase("0"))
                {
                    Intent i = new Intent(SongDisplayActivity.this, MainActivity.class);
                    i.putExtra("SongId", getIntent().getStringExtra("SongId"));
                    i.putExtra("SongName",getIntent().getStringExtra("SongName"));
                    i.putExtra("songImage",getIntent().getStringExtra("songImage"));
                    i.putExtra("SongHintImage", getIntent().getStringExtra("SongHintImage"));

                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(SongDisplayActivity.this, TutorialEquationActivity.class);
                    i.putExtra("tutorialCategoryId", getIntent().getStringExtra("SongId"));
                    i.putExtra("tutorialCategoryName",getIntent().getStringExtra("SongName"));
                    TutorialEquationActivity.tutorialCategoryId=getIntent().getStringExtra("SongId");
                    TutorialEquationActivity.tutorialCategoryName=getIntent().getStringExtra("SongName");
                    i.putExtra("screenType","tutorial_question");
                    i.putExtra("url", getIntent().getStringExtra("url"));
                    i.putExtra("songImage",getIntent().getStringExtra("songImage"));
                    i.putExtra("SongHintImage", getIntent().getStringExtra("SongHintImage"));
                    startActivity(i);
                    finish();
                }
            }
        });


    }
}
