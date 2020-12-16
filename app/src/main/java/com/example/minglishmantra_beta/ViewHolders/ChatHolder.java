package com.example.minglishmantra_beta.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.R;

public class ChatHolder extends RecyclerView.ViewHolder {

    public TextView nameTv,timeTv,doubtTextTv;
    public ImageView imageViewIv;
    public ChatHolder(@NonNull View itemView) {
        super(itemView);


        //prepare
        imageViewIv =itemView.findViewById(R.id.livevideo_single_chat_imageIv);
        nameTv =itemView.findViewById(R.id.video_single_nameTv);
        timeTv =itemView.findViewById(R.id.video_single_timeTv);
        doubtTextTv =itemView.findViewById(R.id.video_single_doubtTextTv);

    }
}
