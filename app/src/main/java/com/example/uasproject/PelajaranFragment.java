package com.example.uasproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PelajaranFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PelajaranFragment extends Fragment {

    Button btn_cari_pelajaran;

    public PelajaranFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pelajaran, container, false);

        btn_cari_pelajaran = view.findViewById(R.id.btn_cari_pelajaran);

        btn_cari_pelajaran.setOnClickListener(v -> {

        });

        return view;
    }
}