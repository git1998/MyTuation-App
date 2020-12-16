package com.example.minglishmantra_beta.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.R;

public class LiveLecturesHolder extends RecyclerView.ViewHolder {


    public LinearLayout liveNowRl;
    public TextView availableAtTv,tittleTv;
    public RelativeLayout parentRl;
    public ImageView lockIv;
    public ImageView senderImageIv;

    //quiz
    public RelativeLayout quizParentRl;
    public ImageView quizLockIv;

    public LiveLecturesHolder(@NonNull View itemView) {
        super(itemView);

        senderImageIv =itemView.findViewById(R.id.live_single_livelectures2_senderImageIv);
        liveNowRl =itemView.findViewById(R.id.main_single_livelectures_liveNowRl);
        availableAtTv =itemView.findViewById(R.id.main_single_livelectures_avaialbeAtTv);
        tittleTv =itemView.findViewById(R.id.main_single_livelectures_tittleTv);
        parentRl =itemView.findViewById(R.id.main_single_livelectures_parentRl);
        lockIv =itemView.findViewById(R.id.live_single_livelectures_videoLockIv);



        //quiz
        quizParentRl =itemView.findViewById(R.id.main_single_livelectures_quizParentRl);
        quizLockIv =itemView.findViewById(R.id.live_single_livelectures_quizLockIv);


    }
}
