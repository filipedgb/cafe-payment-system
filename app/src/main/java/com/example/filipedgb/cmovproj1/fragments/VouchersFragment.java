package com.example.filipedgb.cmovproj1.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.R;
import com.example.filipedgb.cmovproj1.classes.User;
import com.example.filipedgb.cmovproj1.classes.Voucher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class VouchersFragment extends Fragment {


    private FirebaseApp app;
    private FirebaseAuth auth;

    public VouchersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);

        View rootView = inflater.inflate(R.layout.fragment_vouchers, container, false);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mPostReference = database.getReference("vouchers_by_user").child(auth.getCurrentUser().getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LinearLayout l=(LinearLayout)  getView().findViewById(R.id.content_vouchers);
                l.removeAllViews();

                for (final DataSnapshot child: dataSnapshot.getChildren()) {

                    DatabaseReference vaucherUserRef = database.getReference("vouchers_by_user").child(child.getValue(String.class));
                    Log.e("dataref",vaucherUserRef.getKey().toString());

                    DatabaseReference vaucherRef = database.getReference("vouchers");
                    LayoutInflater inflator = getActivity().getLayoutInflater();
                    final View voucherView = inflator.inflate(R.layout.content_voucher,null);

                    vaucherRef.child(vaucherUserRef.getKey().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            Voucher voucherObj = snapshot.getValue(Voucher.class);
                            TextView name= (TextView) voucherView.findViewById(R.id.voucher_text);
                            ImageView image= (ImageView) voucherView.findViewById(R.id.list_image);

                            if(voucherObj.getType() == 1) {
                                name.setText("VOUCHER PIPOCAS GRATIS");
                                image.setImageResource(R.mipmap.popcorn);
                            }
                            else if(voucherObj.getType() == 2) {
                                name.setText("DESCONTO 5 %");
                                image.setImageResource(R.mipmap.coupon);

                            }
                            TextView used= (TextView) voucherView.findViewById(R.id.voucher_used);
                            LinearLayout used_band=(LinearLayout) voucherView.findViewById(R.id.voucher_used_band);



                            if(voucherObj.isUsed())  {used.setText("Usado"); used_band.setBackgroundColor(Color.parseColor("#FF4500"));}
                            else  {used.setText("Válido"); used_band.setBackgroundColor(Color.parseColor("#2E8B57"));}
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                    l.addView(voucherView);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mPostReference.addListenerForSingleValueEvent(postListener);
        return rootView;
    }


}
