package com.makemusiccount.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;

public class MoreSubjectSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Util.getTheme(this));             setContentView(      R.layout.activity_more_subject_selection);
    }
}
