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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    private FirebaseApp app;
    private FirebaseAuth auth;

        public MenuFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_menu, container, false);


            app= FirebaseApp.getInstance();
            auth= FirebaseAuth.getInstance(app);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mPostReference = database.getReference("products");



            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    LinearLayout l=(LinearLayout)  getView().findViewById(R.id.lineralayoutmenu);
                    l.removeAllViews();

                    int counter = 0;


                    int numberProducts = (int) dataSnapshot.getChildrenCount();

                    ImageView[] minusButtons = new ImageView[numberProducts];
                    ImageView[] plusButtons = new ImageView[numberProducts];
                    TextView[] qnty = new TextView[numberProducts];

                    for (DataSnapshot child: dataSnapshot.getChildren()) {

                        Product product = child.getValue(Product.class);
                        Log.e("c",product.getName());
                        LayoutInflater inflator= getActivity().getLayoutInflater();
                        View v=inflator.inflate(R.layout.content_product,null);

                        TextView name= (TextView)v.findViewById(R.id.from_name);
                        TextView price= (TextView)v.findViewById(R.id.plist_price_text);
                        TextView quantity= (TextView)v.findViewById(R.id.cart_product_quantity_tv);

                        name.setText(product.getName());
                        price.setText(product.getPrice().toString() + " €");

                        minusButtons[counter] = (ImageView) v.findViewById(R.id.cart_minus_img);
                        minusButtons[counter].setOnClickListener(new MenuFragment.minusListener(quantity));

                        plusButtons[counter] = (ImageView) v.findViewById(R.id.cart_plus_img);
                        plusButtons[counter].setOnClickListener(new MenuFragment.plusListener(quantity));

                        l.addView(v);
                        counter++;
                    }



                    LayoutInflater inflator= getActivity().getLayoutInflater();
                    View v=inflator.inflate(R.layout.proceed_button,null);
                    l.addView(v);


                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.e("teste","chegou aqui 4");

                    Log.e("loadPost:onCancelled", databaseError.toException().toString());
                }
            };
            mPostReference.addListenerForSingleValueEvent(postListener);

            return rootView;
        }




    public class minusListener implements View.OnClickListener
    {

        TextView qnty;

        public minusListener(TextView qntyIn) {
            this.qnty = qntyIn;
        }

        @Override
        public void onClick(View v)
        {
            Integer qnty_int = Integer.parseInt(qnty.getText().toString());
            if(qnty_int > 0) qnty_int -= 1;
            qnty.setText(qnty_int.toString());
        }

    };

    public class plusListener implements View.OnClickListener
    {

        TextView qnty;

        public plusListener(TextView qntyIn) {
            this.qnty = qntyIn;
        }

        @Override
        public void onClick(View v)
        {
            Integer qnty_int = Integer.parseInt(qnty.getText().toString());
            qnty_int += 1;
            qnty.setText(qnty_int.toString());
        }

    };


}
