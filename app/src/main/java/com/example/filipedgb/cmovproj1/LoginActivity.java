package com.example.filipedgb.cmovproj1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.auth.AuthResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


import  com.example.filipedgb.cmovproj1.classes.*;


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
            startActivity(new Intent(this, test.class));
            finish();
            return;
        }
        email=(EditText)findViewById(R.id.email_login);
        password=(EditText)findViewById(R.id.password_login);



        boolean finish = getIntent().getBooleanExtra("finishLogin", false);
        if (finish) {
            startActivity(new Intent(this, test.class));
            finish();
            return;
        }
    }


    public void login(View view) {
        String email_txt=email.getText().toString();
        String password_txt=password.getText().toString();

        auth.signInWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //checking if success
                if(task.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), test.class);
                    intent.putExtra("finishLogin", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                    startActivity(intent);
                    finish();
                }else{
                    Log.e("teste2","3333");
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Conta inexistente ou password inválida", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });






    }

}

