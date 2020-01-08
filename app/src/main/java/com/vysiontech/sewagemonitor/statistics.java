package com.vysiontech.sewagemonitor;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class statistics extends Fragment {

    private Button ToSpredSheet;

    public statistics() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ToSpredSheet=view.findViewById(R.id.Statistics_button);

        ToSpredSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseToSheet=new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheets/d/1sRP8TSPBC5__6zZJD0du2Y9dKpaSms4X9k8_5JARrqo/edit?usp=sharing"));
                startActivity(browseToSheet);
            }
        });
    }
}
