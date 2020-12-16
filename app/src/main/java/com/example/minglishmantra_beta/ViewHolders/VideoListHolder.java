package com.example.minglishmantra_beta.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.R;

public class VideoListHolder extends RecyclerView.ViewHolder {


    public TextView tittleTv,availableTv;
    public CardView imageCv;
   public ImageView chevRightIv;
   public View viewV;
   public RelativeLayout parentRl;

   public ImageView senderImageIv;

    public VideoListHolder(@NonNull View itemView) {
        super(itemView);

        senderImageIv =itemView.findViewById(R.id.live_single_recentlectures_senderImageIv);

        tittleTv =itemView.findViewById(R.id.main_single_recentlectures_tittleTv);
        availableTv =itemView.findViewById(R.id.main_single_recentlectures_availableTv);
        imageCv =itemView.findViewById(R.id.main_single_recentlectures_cardViewCv);
        chevRightIv=itemView.findViewById(R.id.main_single_recentlectures_chevRightIv);
        viewV =itemView.findViewById(R.id.main_single_recentlectures_viewV);
        parentRl =itemView.findViewById(R.id.main_single_recentlectures_parentRl);


    }
}
