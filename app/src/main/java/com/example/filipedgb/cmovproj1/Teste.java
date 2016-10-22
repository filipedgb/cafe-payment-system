package com.example.filipedgb.cmovproj1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class Teste extends AppCompatActivity {


    private FirebaseApp app;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        app= FirebaseApp.getInstance();
        auth=FirebaseAuth.getInstance(app);




        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView name= (TextView) findViewById(R.id.textView_mail);
        name.setText(auth.getCurrentUser().getEmail());

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
