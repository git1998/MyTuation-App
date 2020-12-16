package com.example.minglishmantra_beta.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minglishmantra_beta.Activity.MainActivity;
import com.example.minglishmantra_beta.ApplicationClass;
import com.example.minglishmantra_beta.Modal.InstallmentModal;
import com.example.minglishmantra_beta.Modal.TestModal;
import com.example.minglishmantra_beta.R;
import com.example.minglishmantra_beta.ViewHolders.BrowseCourseHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DrawerPremiumFragment extends Fragment {

    ApplicationClass applicationClass;

    View mView;

    //
    Dialog dialog;
    TextView errorTv;


    RecyclerView selectUnitRv;

    private FirebaseRecyclerAdapter<TestModal, BrowseCourseHolder> firebaseRecyclerAdapter = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    //
    String userId;
    HashMap courseHashmap =new HashMap();
    HashMap daysHashmap =new HashMap();
    HashMap buyDaysHashmap =new HashMap();

    //calender
     Calendar c ;
    int mYear;
    int mMonth ;
    int mDay ;
    int hour;
    int minute;

    //progress dialog
    ProgressDialog pd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.drawer_premium_fragment, container, false);

        applicationClass = (ApplicationClass) getActivity().getApplicationContext();
        mAuth =FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().getRoot();
        userId =mAuth.getCurrentUser().getUid();


        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(false);


        //time date
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);



        mRef.child("student").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                courseHashmap.clear();
                daysHashmap.clear();
                buyDaysHashmap.clear();



                for(DataSnapshot snapshot : dataSnapshot.getChildren()){



                    String endDateStr=snapshot.child("buycourse_enddate").getValue().toString();
                    if(! endDateStr.equals("x")){

                        String[] strings = endDateStr.split("/");

                        int day = Integer.parseInt(strings[0]);
                        int month = Integer.parseInt(strings[1]);
                        int year = Integer.parseInt(strings[2]);
                        int hour = Integer.parseInt(strings[3]);
                        int minute = Integer.parseInt(strings[4]);


                        Calendar testCalender =Calendar.getInstance();
                        testCalender.set(year,month-1,day,hour,minute,0);

                        long msDiff =  testCalender.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                        int daysDiff = (int) TimeUnit.MILLISECONDS.toDays(msDiff);


                        //  Log.d("cat","days:"+(daysDiff+1));

                        buyDaysHashmap.put(snapshot.getKey(),""+(daysDiff+1));
                    }


                    String endDateStr1=snapshot.child("freetrial_enddate").getValue().toString();
                    if(! endDateStr1.equals("x")){

                        String[] strings = endDateStr1.split("/");

                        int day = Integer.parseInt(strings[0]);
                        int month = Integer.parseInt(strings[1]);
                        int year = Integer.parseInt(strings[2]);
                        int hour = Integer.parseInt(strings[3]);
                        int minute = Integer.parseInt(strings[4]);


                        Calendar testCalender =Calendar.getInstance();
                        testCalender.set(year,month-1,day,hour,minute,0);

                        long msDiff =  testCalender.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                        int daysDiff = (int) TimeUnit.MILLISECONDS.toDays(msDiff);


                      //  Log.d("cat","days:"+(daysDiff+1));

                        daysHashmap.put(snapshot.getKey(),""+(daysDiff+1));

                    }


                    courseHashmap.put(snapshot.getKey(),snapshot.child("is_freetrial").getValue().toString());

                }



                selectUnitRv =mView.findViewById(R.id.browse_choosecourse_fragment_courseRv);
                //setting up recyclerview
                //  recyclerView.setHasFixedSize(true);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                manager.setReverseLayout(false);
                selectUnitRv.setLayoutManager(manager);

                attachFirebaseRecycler();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});










        return mView;
    }



    private void attachFirebaseRecycler() {

        Log.d("abhi","0");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TestModal, BrowseCourseHolder>(

                TestModal.class,
                R.layout.drawer_premium_single_course,
                BrowseCourseHolder.class,
                mRef.child("ALL-COURSES")

        ) {

            @Override
            protected void populateViewHolder(final BrowseCourseHolder holder, final TestModal modal, final int position) {

                holder.courseNameTv.setText(modal.getName());

                if(courseHashmap.containsKey(modal.getName())){


                    holder.freeTrialTv.setVisibility(View.GONE);
                    holder.freeTrialLl.setVisibility(View.VISIBLE);

                    if(courseHashmap.get(modal.getName()).toString().equals("yes")){



                        int daysNo = Integer.parseInt(daysHashmap.get(modal.getName()).toString());

                        if(daysNo <= 0){

                            holder.freeTrialEndsInTv.setText("Free trial ended");
                            holder.daysTv.setText("00");

                        }else {
                            holder.freeTrialEndsInTv.setText("Free trial ends in");
                            holder.daysTv.setText(""+(daysNo+1));
                        }


                    }else{

                        holder.freeTrialEndsInTv.setText("Course validity left");
                        holder.freeTrialEndsInTv.setTextColor(getResources().getColor(R.color.colorPrimaryGreen));

                        int daysNo = Integer.parseInt(buyDaysHashmap.get(modal.getName()).toString());

                        if(daysNo <= 0){

                            holder.freeTrialEndsInTv.setText("Purchase ended");
                            holder.daysTv.setText("00");
                            holder.buyCourseTv.setText("PURCHASE AGAIN");

                        }else {
                            holder.daysTv.setText(""+(daysNo+1));
                            holder.buyCourseTv.setText("PURCHASED");
                            holder.buyCourseTv.setEnabled(false);
                        }


                    }

                }else {

                    holder.freeTrialTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            pd.show();

                            HashMap hashMap = new HashMap();

                            Calendar calendar =Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_MONTH,2);

                            final Calendar myCalender = Calendar.getInstance();
                            int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                            int minute = myCalender.get(Calendar.MINUTE);


                            hashMap.put("freetrial_enddate", (calendar.get(Calendar.DAY_OF_MONTH))+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+"/"+hour+"/"+minute);
                            hashMap.put("buycourse_enddate", "x");
                            hashMap.put("is_freetrial", "yes");

                            mRef.child("student").child(userId).child(modal.getName())
                                    .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(getContext(), "2-Day free trial started", Toast.LENGTH_SHORT).show();
                                        updateApplicationClass();

                                    } else {
                                        Toast.makeText(getContext(), "ERROR:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }



                                }
                            });


                        }
                    });




                }


                holder.buyCourseTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    showDialog(modal.getName());

                    }});


            }};

        // firebaseRecyclerAdapter.
        selectUnitRv.setAdapter(firebaseRecyclerAdapter);

    }



    private void showDialog(final String courseName) {

        dialog = new BottomSheetDialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.drawer_premium_dilog_buycourse);

        Window window = dialog.getWindow();
        // window.setGravity(Gravity.CENTER);

        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        final TextView tittleTv =dialog.findViewById(R.id.browse_dialog_buycourse_tittleTv);
        final LinearLayout addViewInstallmentll =dialog.findViewById(R.id.browse_dialog_buycourse_addViewInstallmentsTv);

        final LinearLayout courseInstallmentLl =dialog.findViewById(R.id.browse_dialog_buycourse_courseInstallmentLl);
        final LinearLayout redeemNowLl =dialog.findViewById(R.id.browse_dialog_buycourse_redeemNowLl);
        final LinearLayout contactLl =dialog.findViewById(R.id.browse_dialog_buycourse_contactRl);

        TextView getCouponTv =dialog.findViewById(R.id.browse_dialog_buycourse_getCouponTv);

        final EditText editTextEt =dialog.findViewById(R.id.browse_dialog_buycourse_edittextEt);
        final TextView applynowTv =dialog.findViewById(R.id.browse_dialog_buycourse_applynowTv);
        errorTv =dialog.findViewById(R.id.browse_dialog_buycourse_errorTv);

        //installment card vars
        final TextView parentMonthTv = dialog.findViewById(R.id.browse_dialog_single_installments_monthTv);
        final TextView parentOfferTv = dialog.findViewById(R.id.browse_dialog_single_installments_OfferTv);
        final TextView parentSavedMoneyTv = dialog.findViewById(R.id.browse_dialog_single_installments_savedMoneyTv);
        final TextView parentAmountsPerMo = dialog.findViewById(R.id.browse_dialog_single_installments_AmountPerMonthTv);
        final TextView parentTotalFeesTv = dialog.findViewById(R.id.browse_dialog_single_installments_totalFeesTv);



        //addview installments
        mRef.child("installments").child(courseName).orderByChild("month").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addViewInstallmentll.removeAllViewsInLayout();

                int i=0;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    final InstallmentModal modal =snapshot.getValue(InstallmentModal.class);

                    final View child = getLayoutInflater().inflate(R.layout.drawer_premium_dilog_single_installments, null);
                    child.setId(i);

                    final RelativeLayout parentRl =child.findViewById(R.id.browse_dialog_single_installment_parentRl);
                    TextView monthTv = child.findViewById(R.id.browse_dialog_single_installments_monthTv);
                    TextView offerTv = child.findViewById(R.id.browse_dialog_single_installments_OfferTv);
                    TextView savedMoneyTv = child.findViewById(R.id.browse_dialog_single_installments_savedMoneyTv);
                    TextView amountsPerMo = child.findViewById(R.id.browse_dialog_single_installments_AmountPerMonthTv);
                    TextView totalFeesTv = child.findViewById(R.id.browse_dialog_single_installments_totalFeesTv);

                    monthTv.setText(modal.getMonth()+" Month");
                    offerTv.setText(modal.getOffer() +"% OFF");
                    savedMoneyTv.setText("SAVE Rs."+modal.getSavedAmount());
                    amountsPerMo.setText("Rs."+modal.getAmountPerMonth()+" / MONTH");
                    totalFeesTv.setText("TOTAL FEES : "+modal.getTotalFees());


                    child.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            parentMonthTv.setText(modal.getMonth()+" Month");
                            parentOfferTv.setText(modal.getOffer() +"% OFF");
                            parentSavedMoneyTv.setText("SAVE Rs."+modal.getSavedAmount());
                            parentAmountsPerMo.setText("Rs."+modal.getAmountPerMonth()+" / MONTH");
                            parentTotalFeesTv.setText("TOTAL FEES : "+modal.getTotalFees());

                            tittleTv.setText("Redeem coupon");
                            tittleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left_black_24dp,0,0,0);

                            editTextEt.setFocusable(true);
                            courseInstallmentLl.setVisibility(View.GONE);
                            redeemNowLl.setVisibility(View.VISIBLE);


                            /*
                            int p = child.getId();
                            parentRl.setBackground(getResources().getDrawable(R.drawable.box_cornered_whitefilled_green_jada));

                            for (int k = 0; k <= installmentCount; k++ ) {

                                if(k != p){

                                    RelativeLayout relativeLayout = addViewInstallmentll.getChildAt(k ).findViewById(R.id.browse_dialog_single_installment_parentRl);
                                    relativeLayout.setBackground(getResources().getDrawable(R.drawable.box_cornered_whitefilled_greystroke));

                                }


                            }
                             */


                        }});


                    i++;
                    addViewInstallmentll.addView(child);





                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});



        tittleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tiitleStr =tittleTv.getText().toString();

                tittleTv.setText("Buy course - MPSC Pre 12 Months");
                tittleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_black_24dp,0,0,0);

                if(tiitleStr.equals("Purchase coupon")){

                    courseInstallmentLl.setVisibility(View.VISIBLE);
                    contactLl.setVisibility(View.GONE);


                }else if(tiitleStr.equals("Redeem coupon")){

                    courseInstallmentLl.setVisibility(View.VISIBLE);
                    redeemNowLl.setVisibility(View.GONE);

                }else {


                    dialog.dismiss();
                }

            }});



        getCouponTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tittleTv.setText("Purchase coupon");
                tittleTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_chevron_left_black_24dp,0,0,0);


                courseInstallmentLl.setVisibility(View.GONE);
                contactLl.setVisibility(View.VISIBLE);


            }});




        editTextEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                applynowTv.setBackgroundTintList(getActivity().getColorStateList(R.color.colorPrimaryGreen));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }});


        applynowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTextEt.clearFocus();

                if(editTextEt.getText().toString().trim().isEmpty()){
                    //   Toast.makeText(getContext(),"please type"+textViewTv.getText()+" before proceding",Toast.LENGTH_SHORT).show();
                    editTextEt.setError("You must fill required info");

                }else{

                    pd.show();
                    String installmentName =parentMonthTv.getText().toString();
                    checkCouponOnCloud(editTextEt.getText().toString().trim(),courseName,installmentName);


                }

            }});



        dialog.show();
    }


    private void checkCouponOnCloud(final String couponCodeStr, final String courseName, String installmentName) {

        mRef.child("coupons").child(courseName).child(installmentName).orderByChild("name")
                .equalTo(couponCodeStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.getChildrenCount()==0){

                    errorTv.setText("Invalid coupon code");
                    errorTv.setTextColor(getResources().getColor(R.color.primaryRed));
                    pd.dismiss();
                }

                else {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String keyId = snapshot.getKey();
                        String is_used = snapshot.child("is_used").getValue().toString();

                        if (is_used.equals("yes")) {

                            errorTv.setText("Applied coupon is used alreday");
                            errorTv.setTextColor(getResources().getColor(R.color.primaryRed));

                        } else {


                            Map updatedUserData = new HashMap();
                            Calendar calendar =Calendar.getInstance();
                            calendar.add(Calendar.MONTH,2);

                            Map hashMap1 = new HashMap();
                            hashMap1.put("coupon", couponCodeStr);
                            hashMap1.put("is_used", "yes");

                            String singleRowPath = "coupon" + "/" + keyId;
                            updatedUserData.put(singleRowPath, hashMap1);

                            final Calendar myCalender = Calendar.getInstance();
                            int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                            int minute = myCalender.get(Calendar.MINUTE);

                            Map hashMap2 = new HashMap();
                            hashMap2.put("is_freetrial", "no");
                            hashMap2.put("freetrial_enddate", "x");
                            hashMap2.put("buycourse_enddate", (calendar.get(Calendar.DAY_OF_MONTH))+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+"/"+hour+"/"+minute);
                            String singleRowPath2 = "student" + "/" + mAuth.getCurrentUser().getUid() + "/" + courseName;
                            updatedUserData.put(singleRowPath2, hashMap2);


                        /*
                        mRef.child("coupon").child(keyId).child("is_used")
                                .setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){


                                    mRef.child("student").child(mAuth.getCurrentUser().getUid()).child(courseName).child("is_freetrial")
                                            .setValue("no").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {



                                        }});



                                    Toast.makeText(getApplicationContext(),"Coupon applied successfully",Toast.LENGTH_SHORT);
                                    dialog.dismiss();

                                }

                            }});



                         */


                            mRef.updateChildren(updatedUserData).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {


                                    Toast.makeText(getContext(), "Coupon applied successfully", Toast.LENGTH_SHORT);
                                    dialog.dismiss();
                                    updateApplicationClass();




                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    errorTv.setText("ERROR:"+e.getMessage());
                                    errorTv.setTextColor(getResources().getColor(R.color.primaryRed));
                                    pd.dismiss();
                                }
                            });



                        }
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

    }



    private void updateApplicationClass() {


        mRef.child("student").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() !=0) {

                    applicationClass.enrolledCourse.clear();


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String courseName = snapshot.getKey();
                        applicationClass.enrolledCourse.add(courseName);

                    }

                }


                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});


    }




}

