package com.example.filipedgb.cmovproj1.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.Product;
import com.example.filipedgb.cmovproj1.R;
import com.example.filipedgb.cmovproj1.classes.Order;
import com.example.filipedgb.cmovproj1.classes.User;
import com.example.filipedgb.cmovproj1.classes.Voucher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VouchersMenuFragmentFragment extends Fragment {


    Order order;
    private FirebaseApp app;
    private FirebaseAuth auth;

    public VouchersMenuFragmentFragment() {
    }


    public static VouchersMenuFragmentFragment newInstance(Order order) {
        VouchersMenuFragmentFragment fragment = new VouchersMenuFragmentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vouchersmenufragment, container, false);

        order = (Order) getArguments().getSerializable("order");

        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);

        //////////////////VOUCHERS//////////////////////////////////////////////////////////////////////////////
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mPostReference  = database.getReference("vouchers_by_user").child(auth.getCurrentUser().getUid());
        mPostReference.keepSynced(true);
        database.getReference("vouchers").keepSynced(true);

        ValueEventListener postListener_vouchers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LinearLayout l=(LinearLayout)  getView().findViewById(R.id.linearLayoutVouchersMenu);
                final Button buttonProceed =(Button)  getView().findViewById(R.id.buttonmenuvouchers);

                // l.removeAllViews();
                int numberProducts = (int) dataSnapshot.getChildrenCount();

                final CheckBox[] typeButtons = new CheckBox[numberProducts];
                final Voucher[] vouchers = new Voucher[numberProducts];

                int counter = 0;

                for (final DataSnapshot child: dataSnapshot.getChildren()) {

                    DatabaseReference vaucherUserRef = database.getReference("vouchers_by_user").child(child.getValue(String.class));
                    vaucherUserRef.keepSynced(true);
                    Log.e("dataref",vaucherUserRef.getKey().toString());
                    DatabaseReference vaucherRef = database.getReference("vouchers");
                    vaucherRef.keepSynced(true);
                    final CheckBox cb = new CheckBox(getActivity().getApplicationContext());
                    final Voucher voucher = new Voucher();
                    vaucherRef.child(vaucherUserRef.getKey().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Voucher voucherObj = snapshot.getValue(Voucher.class);
                            voucher.setCriptographic_signature(voucherObj.getCriptographic_signature());
                            voucher.setSerial(snapshot.getKey());
                            voucher.setSigned(voucherObj.isSigned());
                            voucher.setType(voucherObj.getType());
                            voucher.setUser_id(voucherObj.getUser_id());
                            cb.setTextColor(Color.BLACK);
                            if(voucherObj.getType() == 1) {
                                cb.setText("VOUCHER PIPOCAS GRATIS");
                                cb.setVisibility(View.VISIBLE);
                            }
                            else if(voucherObj.getType() == 2) {
                                cb.setText("DESCONTO 5 %");
                                cb.setVisibility(View.VISIBLE);
                            }
                            if(voucherObj.isUsed())
                            {
                                cb.setText("invalid");
                                cb.setVisibility(View.GONE );
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                    typeButtons[counter]=cb;
                    vouchers[counter]=voucher;
                    l.addView(typeButtons[counter]);
                    counter++;
                }
                buttonProceed.setOnClickListener(new proceed_listener(typeButtons,vouchers));


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mPostReference.addListenerForSingleValueEvent(postListener_vouchers);


        return rootView;
    }




    public class proceed_listener implements View.OnClickListener
    {
        CheckBox[] typeButtons;
        Voucher[] vouchers;

        public proceed_listener(CheckBox[] typeButtons, Voucher[] vouchers) {
            this.typeButtons = typeButtons;
            this.vouchers=vouchers;
        }

        @Override
        public void onClick(View v)
        {
            int counter=0;
            for (CheckBox cb:typeButtons)
            {
                if(cb.isChecked())
                {
                    Log.e("ola","ola");
                    Log.e("cb",cb.getText().toString()+"-"+vouchers[counter].getCriptographic_signature());

                }
                counter++;
            }

        }



    };

}
