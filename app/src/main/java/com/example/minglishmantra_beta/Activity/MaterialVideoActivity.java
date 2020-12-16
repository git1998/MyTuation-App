package com.example.minglishmantra_beta.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minglishmantra_beta.Adapter.MaterialVideoAdapter;
import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.Modal.LiveLecturesModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.VideoListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MaterialVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    ArrayList<LiveLecturesModal> mData =new ArrayList<>();
    ArrayList<LiveLecturesModal> mDataFinal =new ArrayList<>();

    RecyclerView videosRv;
    private FirebaseRecyclerAdapter<LiveLecturesModal, VideoListHolder> firebaseRecyclerAdapterRecentSessions = null;

    String selectedLink="x";
    String API_KEY ="AIzaSyBr740lDszRALBbqMEhuPKVU6fjMfx-0pU";
    private YouTubePlayer youTubePlayer;


    public static class videoStruct
    {
        public String postId;
        public String isPlaying;

        public videoStruct() {

        }

        public videoStruct(String postId, String isPlaying) {
            this.postId = postId;
            this.isPlaying = isPlaying;
        }
    }

    //quiz
    ArrayList<videoStruct> videoStructArrayList =new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String selectedSubjectStr,selectedCourseName;
    private ApplicationClass applicationClass;


    int tempInt =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_video);


        mAuth =FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().getRoot();

        selectedSubjectStr =getIntent().getStringExtra("subject");
        selectedCourseName =getIntent().getStringExtra("course_name");
        applicationClass = (ApplicationClass) getApplicationContext();


        videosRv =findViewById(R.id.activity_material_videos_videosRv);
        final TextView topicNameTv =findViewById(R.id.activity_material_video_topicNameTv);
        final TextView subtopicNameTv =findViewById(R.id.activity_material_video_subtopicNameTv);


        mRef.child(selectedCourseName).child("videos").child(selectedSubjectStr).orderByChild("topic_no").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() != 0) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        LiveLecturesModal modal = snapshot.getValue(LiveLecturesModal.class);
                        modal.setPostId(snapshot.getKey());

                        videoStruct postStruct;

                        if (tempInt == 0) {
                            postStruct = new videoStruct(snapshot.getKey(), "yes");
                            selectedLink = modal.getUrl();
                            tempInt++;
                        } else
                            postStruct = new videoStruct(snapshot.getKey(), "no");

                        videoStructArrayList.add(postStruct);


                        mData.add(modal);
                    }


                    String topic = mData.get(0).getTopic();

                    LiveLecturesModal modal1 = new LiveLecturesModal("x", "x", topic, "x", "x", "x", "x", "x", "x", "x", "x", "x", "x",1,"x","x");
                    modal1.setPostId("x");
                    mDataFinal.add(modal1);
                    int tempCount = 0;

                    for (LiveLecturesModal modal : mData) {

                        String tempTopic = modal.getTopic();


                        if (tempTopic.equals(topic)) {


                        } else {

                            topic = tempTopic;
                            LiveLecturesModal modal2 = new LiveLecturesModal("x", "x", topic, "x", "x", "x", "x", "x", "x", "x", "x", "x", "x",1,"x","x");
                            modal2.setPostId("x");
                            mDataFinal.add(modal2);


                        }

                        mDataFinal.add(modal);


                        tempCount++;

                    }


                    for (LiveLecturesModal k : mDataFinal) {

                        Log.d("papapa", k.getSub_topic());

                    }


                    // set up the LIVE RecyclerView
                    videosRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                    final MaterialVideoAdapter liveAdapter = new MaterialVideoAdapter(getApplicationContext(), mDataFinal, videoStructArrayList, mRef, mAuth);
                    videosRv.setAdapter(liveAdapter);



                    topicNameTv.setText(mDataFinal.get(1).getTopic());
                    subtopicNameTv.setText(mDataFinal.get(1).getSub_topic());


                    liveAdapter.setOnClickListener(new MaterialVideoAdapter.ConnectionInterface() {
                        @Override
                        public void onItemClick(LiveLecturesModal modal, int positionWasSelected, int positionIsSelected) {

                            selectedLink = modal.getUrl();

                            topicNameTv.setText(modal.getTopic());
                            subtopicNameTv.setText(modal.getSub_topic());

                            if (youTubePlayer != null) {
                                youTubePlayer.release();
                            }
                            youTubePlayerView.initialize(API_KEY, MaterialVideoActivity.this);


                            liveAdapter.videoStructArrayList.get(liveAdapter.indexOfVideo(mDataFinal.get(positionIsSelected).getPostId())).isPlaying="yes";
                            liveAdapter.videoStructArrayList.get(liveAdapter.indexOfVideo(mDataFinal.get(positionWasSelected).getPostId())).isPlaying="no";


                            /*
                            int tempI =liveAdapter.indexOfVideo(modal.getPostId());

                            for (int i = 0; i < liveAdapter.videoStructArrayList.size(); i++) {

                                if (i == tempI) {

                                    liveAdapter.videoStructArrayList.get(i).isPlaying = "yes";

                                } else {

                                    liveAdapter.videoStructArrayList.get(i).isPlaying = "no";

                                }


                            }

                             */

                            liveAdapter.notifyItemChanged(positionWasSelected);
                            liveAdapter.notifyItemChanged(positionIsSelected);


                        }
                    });


                    youTubePlayerView = findViewById(R.id.player_view);
                    youTubePlayerView.initialize(API_KEY, MaterialVideoActivity.this);


                }else{  // if dataSnapshot.getChildrenCount() != 0 loop ends



                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});



    }




    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        this.youTubePlayer = youTubePlayer;
        if (!wasRestored) {/// here videoid you need to pass not entire video URL
            youTubePlayer.loadVideo(selectedLink);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        Toast.makeText(this, "ERROR:" + youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();


    }






    public int indexOfVideo(String postId){

        int i=0;
        for(videoStruct obj : videoStructArrayList){

            if(obj.postId.equals(postId)){

                return i;

            }

            i++;

        }


        return -1;

    }




}
