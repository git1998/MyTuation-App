package com.example.minglishmantra_beta.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.R;

public class CommonHolder extends RecyclerView.ViewHolder {

    public TextView tittleTv;

    public CommonHolder(@NonNull View itemView) {
        super(itemView);


        tittleTv =itemView.findViewById(R.id.doubt_deep1_single_text_tittleTv);

    }
}
