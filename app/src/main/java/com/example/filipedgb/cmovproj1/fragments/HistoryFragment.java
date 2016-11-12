package com.example.filipedgb.cmovproj1.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.AccountTest;
import com.example.filipedgb.cmovproj1.R;
import com.example.filipedgb.cmovproj1.classes.Order;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryFragment extends Fragment {


    private FirebaseApp app;
    private FirebaseAuth auth;

    public HistoryFragment() {
        // Required empty public constructor
    }


    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        ((AccountTest) getActivity()).setActionBarTitle("Histórico");



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mPostReference = database.getReference("orders_by_user").child(auth.getCurrentUser().getUid());
        mPostReference.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final LinearLayout l=(LinearLayout)  getView().findViewById(R.id.linearlayout_history);
                l.removeAllViews();

                for (final DataSnapshot child: dataSnapshot.getChildren()) {

                    DatabaseReference ordersUserRef = database.getReference("orders_by_user").child(child.getValue(String.class));
                    ordersUserRef.keepSynced(true);
                    Log.e("dataref",ordersUserRef.getKey().toString());

                    DatabaseReference vaucherRef = database.getReference("orders");
                    ordersUserRef.keepSynced(true);
                    LayoutInflater inflator = getActivity().getLayoutInflater();
                    final View ordersView = inflator.inflate(R.layout.content_order_history,null);

                    vaucherRef.child(ordersUserRef.getKey().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            final Order orderObj = snapshot.getValue(Order.class);
                            TextView price= (TextView) ordersView.findViewById(R.id.tv_price);
                            TextView hours= (TextView) ordersView.findViewById(R.id.hours_history);
                            TextView date= (TextView) ordersView.findViewById(R.id.date_history);

                            Double price_db=orderObj.getOrder_price();
                            price.setText(round(price_db,2)+"€");

                            hours.setText(orderObj.getCreated_at().substring(11));
                            date.setText(orderObj.getCreated_at().substring(0,9));


                            if(((LinearLayout) ordersView.findViewById(R.id.linearlayoutproducts)).getChildCount() > 0)
                                ((LinearLayout) ordersView.findViewById(R.id.linearlayoutproducts)).removeAllViews();

                            if(orderObj.getListOfProducts()!=null)
                            {
                                for (final String key : orderObj.getListOfProducts().keySet())
                                {
                                    DatabaseReference productRef = database.getReference("products");
                                    productRef.keepSynced(true);

                                    productRef.child(key).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {

                                            LinearLayout linearlayoutproducts=(LinearLayout)ordersView.findViewById(R.id.linearlayoutproducts);

                                            TextView tv= new TextView(linearlayoutproducts.getContext());
                                            tv.setTextColor(Color.BLACK);
                                            double total= Double.parseDouble( (snapshot.child("price").getValue().toString())) * Double.parseDouble( orderObj.getListOfProducts().get(key).toString());

                                            tv.setText(orderObj.getListOfProducts().get(key)+"  "+snapshot.child("name").getValue()+"   "+ round(total,2));

                                            linearlayoutproducts.addView(tv);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                    l.addView(ordersView);
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
