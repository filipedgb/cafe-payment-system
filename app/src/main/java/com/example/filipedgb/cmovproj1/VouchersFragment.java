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
        DatabaseReference mPostReference = database.getReference("vouchers_by_user").child(auth.getCurrentUser().getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LinearLayout l=(LinearLayout)  getView().findViewById(R.id.content_vouchers);
                l.removeAllViews();

                int numberOfVouchers = (int) dataSnapshot.getChildrenCount();
                int counter = 0;

                final View[] vouchersViews = new View[numberOfVouchers];
                final Voucher[] listOfAllVouchers = new Voucher[numberOfVouchers];

                for (final DataSnapshot child: dataSnapshot.getChildren()) {

                    DatabaseReference vaucherUserRef = database.getReference("vouchers_by_user").child(child.getValue(String.class));
                    Log.e("dataref",vaucherUserRef.getKey().toString());

                    DatabaseReference vaucherRef = database.getReference("vouchers");

                    final int counter2 = counter;

                    vaucherRef.child(vaucherUserRef.getKey().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            listOfAllVouchers[counter2] = snapshot.getValue(Voucher.class);
                            LayoutInflater inflator= getActivity().getLayoutInflater();

                            vouchersViews[counter2]= inflator.inflate(R.layout.content_voucher,null);

                            TextView name= (TextView) vouchersViews[counter2].findViewById(R.id.voucher_text);
                            if(listOfAllVouchers[counter2].getType() == 1) name.setText("VOUCHER PIPOCAS GRATIS");
                            else if(listOfAllVouchers[counter2].getType() == 2)  name.setText("DESCONTO 5 %");
                            l.addView(vouchersViews[counter2]);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                    counter=counter++;


                   /* listOfAllVouchers[counter] = child.getValue(Voucher.class);
                    LayoutInflater inflator= getActivity().getLayoutInflater();

                    vouchersViews[counter]= inflator.inflate(R.layout.content_voucher,null);

                    TextView name= (TextView) vouchersViews[counter].findViewById(R.id.voucher_text);

                    /* mudar esta porcaria */
                  /*  */
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
