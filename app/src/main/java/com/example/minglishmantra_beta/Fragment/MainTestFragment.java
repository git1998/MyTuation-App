package com.example.minglishmantra_beta.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.Activity.ClassTestActivity;
import com.example.minglishmantra_beta.Activity.PractiseTestActivity;
import com.example.minglishmantra_beta.Adapter.LiveAdapter;
import com.example.minglishmantra_beta.Adapter.RecentLiveAdapter;
import com.example.minglishmantra_beta.Adapter.TestRecentAdapter;
import com.example.minglishmantra_beta.Adapter.TestUpcomingAdapter;
import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.Modal.LiveLecturesModal;
import com.example.minglishmantra_beta.Modal.TestListModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.TestListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class MainTestFragment extends Fragment {

    ApplicationClass applicationClass;


    RecyclerView upcomingTestsRv,recentTestRv;
    TextView upcomingLoadingTv,recentLoadingTv;
    ArrayList<TestListModal> mData =new ArrayList<>();
    ArrayList<TestListModal> mDataRecent =new ArrayList<>();
    Date today;


    TestUpcomingAdapter testUpcomingAdapter;
    TestRecentAdapter testRecentAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static class testStruct
    {
        public String postId,test_status,test_marks;

        public testStruct() {

        }

        public testStruct(String postId, String test_status,String test_marks) {
            this.postId = postId;
            this.test_status = test_status;

            this.test_marks =test_marks;

        }

    }

    //quiz
    ArrayList<testStruct> testStatusArrayList =new ArrayList<>();


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    //dialog course
    Dialog dialogCourse;


    View mView;
    ViewGroup mContainer;

    int count=0;

    Boolean isCreateViewUpcoming=false,isCreateviewRecent=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.main_test_fragment, container, false);
        mContainer =container;

        applicationClass = (ApplicationClass) getActivity().getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().getRoot();



        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        today = cal.getTime();



        upcomingTestsRv = mView.findViewById(R.id.main_prepare_fragment_upcomingTestsRv);
        recentTestRv =mView.findViewById(R.id.main_prepare_fragment_recentTestsRv);

        RelativeLayout upcomingTestFilterTv =mView.findViewById(R.id.main_test_fragment_allCoursesRl);
        TextView recentTestFilterTv =mView.findViewById(R.id.main_test_fragment_recentTestFilter);

        upcomingLoadingTv =mView.findViewById(R.id.main_test_fragment_upcomingLoadingTv);
        recentLoadingTv =mView.findViewById(R.id.main_test_fragment_recentLoadingTv);


        //setting adapter

        // set up the LIVE RecyclerView
        upcomingTestsRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        testUpcomingAdapter = new TestUpcomingAdapter(getContext(),mData,testStatusArrayList,mRef,mAuth);
        upcomingTestsRv.setAdapter(testUpcomingAdapter);


        // set up the RECENT LIVE RecyclerView
        recentTestRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        testRecentAdapter = new TestRecentAdapter(getContext(),mDataRecent,testStatusArrayList,mRef,mAuth);
        recentTestRv.setAdapter(testRecentAdapter);



        mRef.child("students_metadata").child(mAuth.getCurrentUser().getUid()).child("test").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.getChildrenCount()!=0) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        testStruct postStruct =new testStruct(snapshot.getKey(),snapshot.child("test_status").getValue().toString(),snapshot.child("test_marks").getValue().toString());
                        testStatusArrayList.add(postStruct);
                    }

                }

                int count=0;

                for(final String courseName : applicationClass.getEnrolledCourse()){

                    final int finalCount = count;
                    mRef.child(courseName).child("test_list").orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                            TestListModal modal =dataSnapshot.getValue(TestListModal.class);
                            modal.setPostId(dataSnapshot.getKey());
                            modal.setCourseName(courseName);

                            try {
                                Date testDate = sdf.parse(modal.getAvailable_date());
                                if(today.after(testDate)) {   //recent sessions

                                    Log.d("tuta",""+modal.getAvailable_date());
                                    int insertIndex = getInsertIndexRecent(modal);
                                    mDataRecent.add(insertIndex,modal);
                                    testRecentAdapter.notifyItemInserted(insertIndex);

                                }else { //upcoming tests

                                    int insertIndex = getInsertIndexUpcoming(modal);
                                    mData.add(insertIndex,modal);
                                    testUpcomingAdapter.notifyItemInserted(insertIndex);

                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }




                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mRef.child(courseName).child("test_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(finalCount ==applicationClass.getEnrolledCourse().size()-1){

                                if(mData.isEmpty())
                                    upcomingLoadingTv.setText("No upcoming test");
                                else
                                    upcomingLoadingTv.setVisibility(View.GONE);


                                if(mDataRecent.isEmpty())
                                    recentLoadingTv.setText("No recent tests found");
                                else
                                    recentLoadingTv.setVisibility(View.GONE);


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }});




                    /*

                    mRef.child(courseName).child("test_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            count++;


                            if(dataSnapshot.getChildrenCount() !=0) {

                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    TestListModal modal =snapshot.getValue(TestListModal.class);
                                    modal.setPostId(snapshot.getKey());
                                    modal.setCourseName(courseName);
                                    try {
                                        Date testDate = sdf.parse(modal.getAvailable_date());
                                        if(today.after(testDate)) {   //recent sessions

                                            mDataRecent.add(modal);

                                        }else { //upcoming tests

                                            mData.add(modal);
                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }







                            if(count == applicationClass.getEnrolledCourse().size()){


                                if(mData.isEmpty()){

                                }else{
                                    varInitForData(); }


                                if(mDataRecent.isEmpty()){

                                }else{
                                    varInitForDataRecent(); }





                            } //end of final loop




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }});

                     */  // old logic



                    count++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});





        showAllCourseDialog();

        upcomingTestFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogCourse.show();

            }});


        recentTestFilterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();

            }});





        return mView;
    }


    @Override
    public void onStart() {
        super.onStart();





    }



    private int getInsertIndexRecent(TestListModal modal) {

        Long timestamp =modal.getTimestamp();

        int tempCount=0;
        for (TestListModal tempModal : mDataRecent){  // 12 ,10 , 8 , 6 , 9


            Long innerTimestamp =tempModal.getTimestamp();

            if(timestamp > innerTimestamp){

                return tempCount;
            }

            tempCount++;

        }




        return tempCount;

    }


    private int getInsertIndexUpcoming(TestListModal modal) {

        Long timestamp =modal.getTimestamp();

        int tempCount=0;
        for (TestListModal tempModal : mData){  // 6 ,8 , 10 , 12


            Long innerTimestamp =tempModal.getTimestamp();


            if(timestamp < innerTimestamp){

                return tempCount;
            }


            tempCount++;
        }




        return tempCount;

    }



    private void showAllCourseDialog() {

        dialogCourse = new BottomSheetDialog(getContext());
        dialogCourse.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCourse.setContentView(R.layout.post_dialog_filter);

        Window window = dialogCourse.getWindow();
        // window.setGravity(Gravity.CENTER);

        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialogCourse.setTitle(null);
        dialogCourse.setCancelable(true);
        dialogCourse.setCanceledOnTouchOutside(true);

        final LinearLayout addViewLl =dialogCourse.findViewById(R.id.home_dialog_filter_addViewLl);
        TextView dialogTittleTv =dialogCourse.findViewById(R.id.home_dialog_filter_tittleTv);
        dialogTittleTv.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close_black_24dp,0);
        dialogTittleTv.setText("CHOOSE COURSE");

        mRef.child("course").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.getChildrenCount() !=0){

                    addViewLl.removeAllViewsInLayout();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String courseName = snapshot.child("name").getValue().toString();
                        final View child = getLayoutInflater().inflate(R.layout.doubt_main_single_chapters, mContainer, false);


                        TextView tittleTv =child.findViewById(R.id.doubt_main_single_chapters_tittleTv);
                        tittleTv.setText(courseName);


                        tittleTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                dialogCourse.hide();

                            }});

                        addViewLl.addView(child);

                    }

                }

                else {



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});



    }




    private void showDialog() {

        Dialog dialog = new BottomSheetDialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.post_dialog_filter);

        Window window = dialog.getWindow();
        // window.setGravity(Gravity.CENTER);

        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.setTitle(null);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final LinearLayout addViewLl =dialog.findViewById(R.id.home_dialog_filter_addViewLl);
      //  TextView dialogTittleTv =dialog.findViewById(R.id.home_dialog_filter_tittleTv);
       // dialogTittleTv.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_close_black_24dp,0);
       // dialogTittleTv

        for(int i=0;i<3;i++) {

            final View child = getLayoutInflater().inflate(R.layout.doubt_main_single_chapters, mContainer, false);
            child.setId(i);
            TextView tittleTv = child.findViewById(R.id.doubt_main_single_chapters_tittleTv);
            if(i==0)
                tittleTv.setText("All");
            else if(i==1){
                tittleTv.setText("Paused");
            }else if(i==2) {
                tittleTv.setText("Attempted");
            }else
                tittleTv.setText("Unattempted");


            addViewLl.addView(child);
        }



        dialog.show();

    }




    //important methods



    private void varInitForData() {


        Log.d("puss",""+count);
        Collections.sort(mData, new Comparator<TestListModal>() {
            @Override
            public int compare(TestListModal lhs, TestListModal rhs) {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                String lhsString =format.format(Date.parse(lhs.getAvailable_date()+" "+lhs.getStart_time()));
                String rhsString =format.format(Date.parse(rhs.getAvailable_date()+" "+lhs.getStart_time()));

                try {
                    Date date =format.parse(lhsString);
                    Long ttLhs =date.getTime();

                    try {
                        Date rhsDate =format.parse(rhsString);
                        Long ttRhs =rhsDate.getTime();


                        return ttLhs.compareTo(ttRhs);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;

            }
        });





        isCreateViewUpcoming=true;

    }


    private void varInitForDataRecent(){




        for(TestListModal k : mDataRecent){

            Log.d("TestRecent", k.getAvailable_date());

        }



        Collections.sort(mDataRecent, new Comparator<TestListModal>() {
            @Override
            public int compare(TestListModal lhs, TestListModal rhs) {

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                String lhsString =format.format(Date.parse(lhs.getAvailable_date()+" "+lhs.getStart_time()));
                String rhsString =format.format(Date.parse(rhs.getAvailable_date()+" "+lhs.getStart_time()));

                try {
                    Date date =format.parse(lhsString);
                    Long ttLhs =date.getTime();


                    try {
                        Date rhsDate =format.parse(rhsString);
                        Long ttRhs =rhsDate.getTime();


                        return ttLhs.compareTo(ttRhs);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;

            }
        });

        Collections.reverse(mDataRecent);



        for(TestListModal k : mDataRecent){

            Log.d("TestRecentAfter", k.getAvailable_date());

        }



        isCreateviewRecent=true;


    }


    public String checkContainsPostByReturnTestStatus(String postId){


        for(testStruct obj : testStatusArrayList){

            if(obj.postId.equals(postId)){


                return obj.test_status;

            }


        }


        return "START NOW";

    }



    public String checkContainsPostByReturnTestMarks(String postId){


        for(testStruct obj : testStatusArrayList){

            if(obj.postId.equals(postId)){


                return obj.test_marks;

            }


        }


        return "x";

    }



    public Boolean checkContainsPost(String postId){


        for(testStruct obj : testStatusArrayList){

            if(obj.postId.equals(postId)){

                return true;

            }


        }


        return false;

    }


    public int indexOfPost(String postId){

        int i=0;
        for(testStruct obj : testStatusArrayList){

            if(obj.postId.equals(postId)){

                return i;

            }

            i++;

        }


        return -1;

    }




}
