package com.example.minglishmantra_beta.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.minglishmantra_beta.R;

public class DrawerAboutappFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.drawer_aboutapp_fragment, container, false);


        TextView seePrivacyPolicyTv =view.findViewById(R.id.drawer_aboutapp_fragment_seePrivacyTv);



        seePrivacyPolicyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://tuationclasses.web.app"));
                startActivity(browserIntent);
            }});

        return view;
    }
}
