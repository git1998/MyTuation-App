package com.example.minglishmantra_beta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.Activity.MaterialVideoActivity;
import com.example.minglishmantra_beta.Modal.LiveLecturesModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.VideoListHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MaterialVideoAdapter extends RecyclerView.Adapter<VideoListHolder> {

    private ConnectionInterface mClickListener;

    public ArrayList<LiveLecturesModal> mData =new ArrayList<>();
    public ArrayList<MaterialVideoActivity.videoStruct> videoStructArrayList =new ArrayList<>();
    private LayoutInflater mInflater;
    public Context mContex;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    Calendar c =Calendar.getInstance();

    //firebase
    DatabaseReference mRef;
    FirebaseAuth mAuth;


    int positionWasSelected=1,positionIsSelected=1;


    // data is passed into the constructor
    public MaterialVideoAdapter(Context context, ArrayList<LiveLecturesModal> mData, ArrayList<MaterialVideoActivity.videoStruct> videoStructArrayList, DatabaseReference mRef, FirebaseAuth mAuth) {
        this.mInflater = LayoutInflater.from(context);
        mContex =context;
        this.mData =mData;
        this.videoStructArrayList =videoStructArrayList;

        //firebase
        this.mRef =mRef;
        this.mAuth =mAuth;

    }

    // inflates the row layout from xml when needed
    @Override
    public VideoListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.live_single_recentlectures, parent, false);

        final VideoListHolder viewHolder = new VideoListHolder(view);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListHolder holder, final int position) {

        final LiveLecturesModal modal =getItem(position);
        final String postId =modal.getPostId();

        holder.chevRightIv.setVisibility(View.GONE);


            if(postId.equals("x")){

                    holder.tittleTv.setText(modal.getTopic());
                    holder.availableTv.setVisibility(View.GONE);
                    holder.imageCv.setVisibility(View.GONE);
                    holder.viewV.setVisibility(View.GONE);


            }
            else {

                holder.viewV.setVisibility(View.VISIBLE);

                holder.tittleTv.setText(modal.getSub_topic());
                holder.availableTv.setText("[Number] Watched");


                if (videoStructArrayList.get(indexOfVideo(postId)).isPlaying.equals("yes")) {

                    holder.itemView.setBackgroundColor(mContex.getResources().getColor(R.color.faint_google_blue));

                } else {

                    holder.itemView.setBackgroundColor(mContex.getResources().getColor(R.color.cardColor));
                }


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        positionWasSelected=positionIsSelected;
                        positionIsSelected=position;
                        mClickListener.onItemClick(modal,positionWasSelected,positionIsSelected);
                    }
                });


            }






    }



    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }



    // convenience method for getting data at click position
    LiveLecturesModal getItem(int id) {
        return mData.get(id);
    }



    public int indexOfVideo(String postId){

        int i=0;
        for(MaterialVideoActivity.videoStruct obj : videoStructArrayList){

            if(obj.postId.equals(postId)){

                return i;

            }

            i++;

        }


        return -1;

    }



    public interface ConnectionInterface {
        public void onItemClick(LiveLecturesModal modal, int positionWasSelected, int positionIsSelected);
    }

    public void setOnClickListener(MaterialVideoAdapter.ConnectionInterface clickListener){
        mClickListener =clickListener;
    }




}
