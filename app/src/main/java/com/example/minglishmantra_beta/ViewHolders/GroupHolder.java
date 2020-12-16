package com.example.minglishmantra_beta.ViewHolders;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.R;

public class GroupHolder extends RecyclerView.ViewHolder {


    public LinearLayout testAddviewLl;

    public GroupHolder(@NonNull View itemView) {
        super(itemView);


        testAddviewLl =itemView.findViewById(R.id.activity_test_addviewLl);


    }
}
