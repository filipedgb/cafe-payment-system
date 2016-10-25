package com.example.filipedgb.cmovproj1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.filipedgb.cmovproj1.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.AuthResult;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    protected EditText email;
    protected EditText password;

    private FirebaseApp app;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storageUsers;
    private DatabaseReference dbRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        app= FirebaseApp.getInstance();
        auth=FirebaseAuth.getInstance(app);
        database= FirebaseDatabase.getInstance(app);
//        Log.e("user",auth.getCurrentUser().getEmail());
        if(auth.getCurrentUser()!=null)
        {
           // startActivity(new Intent(LoginActivity.this, QRcodeReader.class));


            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userReference = database.getReference();
            userReference.child("user_meta").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User user = dataSnapshot.getValue(User.class);

                            SharedPreferences sharedPref = getSharedPreferences("user_info", 0);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("code",user.getCode());
                            editor.commit();


                            if(user.isAdmin()) {
                                Log.e("Login access","ADMINISTRATOR");
                                startActivity(new Intent(LoginActivity.this, AccountTest.class));
                                // AQUI SUPOSTAMENTE MUDARA PARA OUTRA ACTIVITY

                            } else {
                                Log.e("Login access","NORMAL");
                                startActivity(new Intent(LoginActivity.this, AccountTest.class));
                            }

                            //finish();
                            //return;

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );


        }
        email=(EditText)findViewById(R.id.email_login);
        password=(EditText)findViewById(R.id.password_login);



        boolean finish = getIntent().getBooleanExtra("finishLogin", false);
        if (finish) {

            startActivity(new Intent(this, AccountTest.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return;
        }
    }


    public void login(View view) {
        String email_txt = email.getText().toString();
        String password_txt = password.getText().toString();
        if(email_txt.length()==0 || password_txt.length()==0)
            return;

        auth.signInWithEmailAndPassword(email_txt, password_txt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //checking if success
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("finishLogin", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                    startActivity(intent);
                    finish();
                } else {
                    //Log.e("teste2","3333");
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Conta inexistente ou password inválida", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        //intent.putExtra("finishLogin", true);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
       // finish();
    }

}

