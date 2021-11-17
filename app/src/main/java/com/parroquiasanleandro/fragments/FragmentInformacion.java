package com.parroquiasanleandro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.parroquiasanleandro.R;

public class FragmentInformacion extends Fragment {

    public FragmentInformacion() {}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion,container,false);

        return view;
    }
}