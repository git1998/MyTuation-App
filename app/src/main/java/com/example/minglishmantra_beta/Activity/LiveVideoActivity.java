package com.example.minglishmantra_beta.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.Modal.ChatModal;
import com.example.minglishmantra_beta.Modal.LiveLecturesModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.ChatHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LiveVideoActivity extends YouTubeBaseActivity {

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;

    String selectedLink="JuwIru9jsSo";

    LiveLecturesModal liveLecturesModal;


    private RecyclerView chatRv;
    private FirebaseRecyclerAdapter<ChatModal, ChatHolder> firebaseRecyclerAdapter = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String liveId,selectedCourseName;
    private  ApplicationClass applicationClass;

    private EditText doubtEt;
    private TextView sendTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video);


        //getting intent data
        liveId =getIntent().getStringExtra("liveId");
        selectedCourseName =getIntent().getStringExtra("course_name");
        String isRecentStr =getIntent().getStringExtra("is_recent_lectures");
        Gson gson = new Gson();
        liveLecturesModal = gson.fromJson(getIntent().getStringExtra("myjson"), LiveLecturesModal.class);


        mAuth =FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child(selectedCourseName);
        applicationClass = (ApplicationClass) getApplicationContext();


        //init vars
        TextView topicName =findViewById(R.id.activity_live_video_topicName);
        TextView subTopicName =findViewById(R.id.activity_live_video_subTopicName);

        topicName.setText(liveLecturesModal.getSubject());
        subTopicName.setText(liveLecturesModal.getTopic()+" | "+liveLecturesModal.getSub_topic());


        youTubePlayerView = findViewById(R.id.player_view);
        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.loadVideo(liveLecturesModal.getUrl());

                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize("AIzaSyAzb7drNcg9tYtCesmXz8Ax-CO3gK70OAA",onInitializedListener);


        if(isRecentStr.equals("no")) {

            //doubt init
            doubtEt = findViewById(R.id.activity_video_doubtTextEt);
            sendTv = findViewById(R.id.activity_video_sendTv);


            doubtEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    sendTv.setBackgroundTintList(getColorStateList(R.color.colorPrimaryGreen));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            sendTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String doubtStr = doubtEt.getText().toString().trim();

                    if (doubtStr.isEmpty()) {

                        doubtEt.setError("Fill the text field");

                    } else {

                        uploadChat(doubtStr);

                    }

                }
            });

        }else
            {
                RelativeLayout sendDoubtLl =findViewById(R.id.activity_video_sendDoubtRl);
                sendDoubtLl.setVisibility(View.GONE);
            }



        chatRv =findViewById(R.id.activity_video_chatRv);
        //setting up recyclerview
        //  recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setReverseLayout(false);
        chatRv.setLayoutManager(manager);

        attachFirebaseRecycler();


    }

    private void uploadChat(String doubtStr) {

        String pushId =mRef.push().getKey();
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTimeStr = sdf.format(c.getTime());

        Map hashMap =new HashMap();

        hashMap.put("doubtText",doubtStr);
        hashMap.put("sender",applicationClass.getUserName());
        hashMap.put("profileUrl",applicationClass.getImageUrl());
        hashMap.put("time",currentTimeStr);

        mRef.child("live_chats").child(liveId).child(pushId)
                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    doubtEt.setText("");
                    sendTv.setBackgroundTintList(getColorStateList(R.color.jast_grey));

                }else{


                }

            }});


    }


    private void attachFirebaseRecycler() {

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatModal, ChatHolder>(

                ChatModal.class,
                R.layout.livevideo_single_chat,
                ChatHolder.class,
                mRef.child("live_chats").child(liveId)

        ) {
            @Override
            protected void populateViewHolder(final ChatHolder holder, ChatModal modal, int position) {

                Picasso.get().load(modal.getProfileUrl()).placeholder(R.drawable.profile_placeholder).into(holder.imageViewIv);
                holder.nameTv.setText(modal.getSender());
                holder.timeTv.setText(modal.getTime());
                holder.doubtTextTv.setText(modal.getDoubtText());

            }};


        // firebaseRecyclerAdapter.
        chatRv.setAdapter(firebaseRecyclerAdapter);

    }



}
