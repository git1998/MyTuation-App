package com.example.minglishmantra_beta.Fragment;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.Modal.CommentModal;
import com.example.minglishmantra_beta.Modal.DoubtModal;
import com.example.minglishmantra_beta.Modal.LiveLecturesModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.CommentHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoubtCommentFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DoubtModal doubtModal;


    RecyclerView recyclerViewRv;

    private FirebaseRecyclerAdapter<CommentModal, CommentHolder> firebaseRecyclerAdapter = null;


    EditText commentEt;
    TextView sendTv;

    ProgressDialog mProgressDialog;

    View mView;
    private ApplicationClass applicationClass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         mView = inflater.inflate(R.layout.doubt_comment_fragment, container, false);



        applicationClass = (ApplicationClass) getActivity().getApplication();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().getRoot();

        Bundle args =getArguments();
        Gson gson = new Gson();
        doubtModal = gson.fromJson(args.getString("myjson"), DoubtModal.class);


        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("uploading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);



        //pre vars
        TextView subjectOrPaperTv =mView.findViewById(R.id.doubt_comment_fragment_paperOrSubjectTv);
        TextView dateAndTopicTv =mView.findViewById(R.id.doubt_comment_fragment_dateAndTopicTv);
        TextView text1Tv =mView.findViewById(R.id.doubt_comment_fragment_text1Tv);
        ImageView imageViewIv =mView.findViewById(R.id.doubt_comment_fragment_imageviewIv);


        recyclerViewRv =mView.findViewById(R.id.doubt_comment_fragmenr_recyclerviewRv);
        commentEt =mView.findViewById(R.id.doubt_comment_fragmenr_startAnsweringEt);
        sendTv =mView.findViewById(R.id.doubt_comment_fragmenr_sendTv);


        //update ui

        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String date =format1.format(Date.parse(doubtModal.getDate()));

        //update ui
        subjectOrPaperTv.setText(doubtModal.getPaper());
        dateAndTopicTv.setText(date + "   ."+doubtModal.getSubject());

        if(doubtModal.getText1().equals("x"))
            text1Tv.setVisibility(View.GONE);
        else
            text1Tv.setText(doubtModal.getText1());


        if(doubtModal.getImageUrl().equals("x"))
            imageViewIv.setVisibility(View.GONE);

        else
            Picasso.get().load(doubtModal.getImageUrl()).into(imageViewIv);




        //init recent searches rv
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(false);
        recyclerViewRv.setLayoutManager(manager);

        attachFirebaseRecycler();



        commentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(commentEt.getText().toString().trim().isEmpty())
                    sendTv.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.grey))));
                else
                sendTv.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.colorPrimaryGreen))));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        sendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String doubtStr = commentEt.getText().toString().trim();

                if (doubtStr.isEmpty()) {

                    commentEt.setError("Fill the text field");

                } else {

                    mProgressDialog.show();
                    uploadChat(doubtStr);

                }

            }
        });




        return mView;
    }

    private void uploadChat(String doubtStr) {

        String pushId =mRef.push().getKey();
        final Calendar c = Calendar.getInstance();

        Map hashMap =new HashMap();

        hashMap.put("doubtText",doubtStr);
        hashMap.put("sender",applicationClass.getUserName());
        hashMap.put("sender_image",applicationClass.getImageUrl());
        hashMap.put("time",""+c.getTime());



        mRef.child(doubtModal.getCourse()).child("doubt_comment").child(doubtModal.getPostId()).child(pushId)
                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    commentEt.setText("");
                    sendTv.setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.jast_grey))));

                }else{


                }

                mProgressDialog.dismiss();

            }});


    }


    private void attachFirebaseRecycler() {


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CommentModal, CommentHolder>(

                CommentModal.class,
                R.layout.doubt_comment_single_comment,
                CommentHolder.class,
                mRef.child(doubtModal.getCourse()).child("doubt_comment").child(doubtModal.getPostId())

        ) {
            @Override
            protected void populateViewHolder(final CommentHolder holder, final CommentModal modal, final int position) {

                final String commentPostId =getRef(position).getKey();

                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                String date =format1.format(Date.parse(modal.getTime()));



                holder.doubtTextTv.setText(modal.getDoubtText());
                holder.doubtTimeTv.setText(date);
                holder.doubtSenderTv.setText(modal.getSender());
                Picasso.get().load(modal.getSender_image()).placeholder(R.drawable.profile_placeholder).into(holder.doubtSenderImageIv);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Gson gson = new Gson();
                        String myJson = gson.toJson(modal);

                        Bundle args =new Bundle();
                        args.putString("post_id",doubtModal.getPostId() );
                        args.putString("comment_id",commentPostId );
                        args.putString("course_name",doubtModal.getCourse() );
                        args.putString("myjson",myJson);

                        Navigation.findNavController(mView).navigate(R.id.doubtDiscussionFragment,args);

                    }});



            }};


        // firebaseRecyclerAdapter.
        recyclerViewRv.setAdapter(firebaseRecyclerAdapter);



    }




}
