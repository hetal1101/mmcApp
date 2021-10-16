package com.makemusiccount.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class MyBodyTextView extends TextView {

    public MyBodyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyBodyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBodyTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "sf_bold.ttf");
            setTypeface(tf);
        }
    }
}