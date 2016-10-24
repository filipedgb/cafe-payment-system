package com.example.filipedgb.cmovproj1;

import android.content.Context;
import android.content.Intent;
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

                    View[]productViews = new View[numberProducts];
                    ImageView[] minusButtons = new ImageView[numberProducts];
                    ImageView[] plusButtons = new ImageView[numberProducts];
                    TextView[] qnty = new TextView[numberProducts];
                    Product[] listOfAllProducts = new Product[numberProducts];

                    for (DataSnapshot child: dataSnapshot.getChildren()) {

                        listOfAllProducts[counter] = child.getValue(Product.class);
                        listOfAllProducts[counter].setId(child.getKey().toString());

                        Log.e("c",  listOfAllProducts[counter].getName());
                        LayoutInflater inflator= getActivity().getLayoutInflater();
                        productViews[counter]=inflator.inflate(R.layout.content_product,null);

                        TextView name= (TextView)   productViews[counter].findViewById(R.id.from_name);
                        TextView price= (TextView)  productViews[counter].findViewById(R.id.plist_price_text);

                        qnty[counter]= (TextView)productViews[counter].findViewById(R.id.cart_product_quantity_tv);

                        name.setText(listOfAllProducts[counter].getName());
                        price.setText(listOfAllProducts[counter].getPrice().toString() + " €");

                        minusButtons[counter] = (ImageView)   productViews[counter].findViewById(R.id.minus_sign);
                        minusButtons[counter].setOnClickListener(new MenuFragment.minusListener(qnty[counter]));

                        plusButtons[counter] = (ImageView)   productViews[counter].findViewById(R.id.plus_sign);
                        plusButtons[counter].setOnClickListener(new MenuFragment.plusListener(qnty[counter]));

                        l.addView(productViews[counter]);
                        counter++;
                    }


                    LayoutInflater inflator= getActivity().getLayoutInflater();
                    View v=inflator.inflate(R.layout.proceed_button,null);
                    v.findViewById(R.id.button3).setOnClickListener(new proceed_listener(listOfAllProducts,productViews));
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

    public class proceed_listener implements View.OnClickListener
    {

        View[] allViews;
        Product[] allProducts;

        public proceed_listener(Product[] allProductsIn,View[] allViewsIn) {
            this.allViews = allViewsIn;
            this.allProducts = allProductsIn;
        }


        public void updateTotalMoneySpent(final Order order) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference userReference = database.getReference();
            userReference.child("user_meta").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            userReference.child("user_meta").child(auth.getCurrentUser().getUid()).child("moneySpent").setValue(user.getMoneySpent()+order.getOrder_price());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }


        public void saveToFirebase(Order order) {
            app = FirebaseApp.getInstance();
            auth = FirebaseAuth.getInstance(app);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mOrderReference = database.getReference("orders");
            String key = mOrderReference.push().getKey();
            order.setOrder_id(key);
            mOrderReference.child(key).setValue(order);

            //Log.e("Key",key);
        }


        public void saveToFirebaseByUser(Order order) {
            app = FirebaseApp.getInstance();
            auth = FirebaseAuth.getInstance(app);

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mOrderReference = database.getReference("orders_by_user");
            mOrderReference.child(auth.getCurrentUser().getUid().toString()).push().setValue(order.getOrder_id());


            //Log.e("Key",key);
        }


        @Override
        public void onClick(View v)
        {
            int size = allViews.length;
            Double count_price = 0.0;

            Order new_order = new Order(auth.getCurrentUser().getUid().toString());

            for(int i = 0; i < size; i++) {
                String temp = allProducts[i].getName();
                TextView quantity = (TextView) allViews[i].findViewById(R.id.cart_product_quantity_tv);
                Integer quantity_int = Integer.parseInt(quantity.getText().toString());
                //Log.e("Produto " + i + " :", temp + " - " + quantity.getText().toString() + "*" + allProducts[i].getPrice());

                count_price += (double) quantity_int*allProducts[i].getPrice();
                Log.e("Price",Double.toString(count_price));
                if(Integer.parseInt(quantity.getText().toString()) > 0) {
                    new_order.addProductToOrder(allProducts[i], quantity_int);
                }
            }

            new_order.setOrder_price(count_price);
            saveToFirebase(new_order);
            saveToFirebaseByUser(new_order);
            updateTotalMoneySpent(new_order);
        }



    };


}
