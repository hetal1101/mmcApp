package com.makemusiccount.android.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makemusiccount.android.R;
import com.makemusiccount.android.util.Util;


public class Student extends Fragment {


    public Student() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getContext().setTheme(Util.getTheme(getContext()));
        View view=inflater.inflate(R.layout.fragment_student, container, false);
        // Inflate the layout for this fragment
        return view;
    }
}