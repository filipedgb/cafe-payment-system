package com.example.filipedgb.cmovproj1.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.filipedgb.cmovproj1.CodeScreen;
import com.example.filipedgb.cmovproj1.R;
import com.example.filipedgb.cmovproj1.classes.Order;
import com.example.filipedgb.cmovproj1.classes.Voucher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CodeMenuFragment extends Fragment {


    Order order;
    private FirebaseApp app;
    private FirebaseAuth auth;

    public CodeMenuFragment() {
    }


    public static CodeMenuFragment newInstance(Order order) {
        CodeMenuFragment fragment = new CodeMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_code_menu, container, false);

        order = (Order) getArguments().getSerializable("order");

        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);

        SharedPreferences sharedPref = getContext().getSharedPreferences("user_info", 0);
        Log.e("code",sharedPref.getString("code","null"));

        Button b= new Button(getContext());
        b.setText("Clicar para continuar");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText=(EditText) getActivity().findViewById(R.id.codeEditText);
                SharedPreferences sharedPref = getContext().getSharedPreferences("user_info", 0);
                if(editText.getText().toString().contentEquals(sharedPref.getString("code","")))
                {
                    FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                    Fragment fragment = QRFragment.newInstance(order);
                    fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
                    InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                }
                else
                {
                    Context context = getContext();
                    Toast toast = Toast.makeText(context, "Código errado", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        LinearLayout l = (LinearLayout) rootView.findViewById(R.id.linearlayoutMenuCode);
        l.addView(b);

        return rootView;
    }


}

