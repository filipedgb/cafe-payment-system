package com.example.filipedgb.cmovproj1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

public class MenuActivity extends AppCompatActivity {
    private FirebaseApp app;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mPostReference = database.getReference("products");

        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                LinearLayout l=(LinearLayout) findViewById(R.id.lineralayoutmenu);
                l.removeAllViews();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    Log.e("c",product.getName());
                    LayoutInflater inflator= getLayoutInflater();
                    View v=inflator.inflate(R.layout.content_product,null);
                    TextView name= (TextView)v.findViewById(R.id.from_name);
                    TextView price= (TextView)v.findViewById(R.id.plist_price_text);
                    name.setText(product.getName());
                    price.setText(product.getPrice().toString() + " €");

                    ImageView plus = (ImageView) v.findViewById(R.id.cart_minus_img);
                    plus.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v1) {
                            TextView qnty = (TextView) v1.findViewById(R.id.cart_product_quantity_tv);
                            Integer qnty_int = Integer.parseInt(qnty.getText().toString());
                            Log.e("Numero",qnty_int.toString());
                            qnty_int += 1;
                            Log.e("novo numero",qnty_int.toString());

                            qnty.setText(qnty_int.toString());
                            //v.getId() will give you the image id
                        }
                    });

                    l.addView(v);
                   // RelativeLayout content= (RelativeLayout) findViewById(R.id.productcontent);

                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("teste","chegou aqui 4");

                Log.e("loadPost:onCancelled", databaseError.toException().toString());
            }
        };
        mPostReference.addListenerForSingleValueEvent(postListener);


    }

    public void logOut(View view) {

        auth.signOut();


        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("finish", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
}
