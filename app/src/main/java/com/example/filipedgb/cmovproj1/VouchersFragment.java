package com.example.filipedgb.cmovproj1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        DatabaseReference mPostReference = database.getReference("vouchers");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout l=(LinearLayout)  getView().findViewById(R.id.content_vouchers);
                l.removeAllViews();

                int numberOfVouchers = (int) dataSnapshot.getChildrenCount();
                int counter = 0;

                View[] vouchersViews = new View[numberOfVouchers];
                Voucher[] listOfAllVouchers = new Voucher[numberOfVouchers];

                for (DataSnapshot child: dataSnapshot.getChildren()) {

                    listOfAllVouchers[counter] = child.getValue(Voucher.class);
                    LayoutInflater inflator= getActivity().getLayoutInflater();

                    vouchersViews[counter]= inflator.inflate(R.layout.content_voucher,null);

                    TextView name= (TextView) vouchersViews[counter].findViewById(R.id.voucher_text);

                    /* mudar esta porcaria */
                    if(listOfAllVouchers[counter].getType() == 1) name.setText("VOUCHER PIPOCAS GRATIS");
                    else if(listOfAllVouchers[counter].getType() == 2)  name.setText("DESCONTO 5 %");
                    l.addView(vouchersViews[counter]);
                    counter++;
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
