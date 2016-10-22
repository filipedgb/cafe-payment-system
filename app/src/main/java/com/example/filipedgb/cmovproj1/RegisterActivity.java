package com.example.filipedgb.cmovproj1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filipedgb.cmovproj1.classes.User;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {


    private FirebaseApp app;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storageUsers;
    private DatabaseReference dbRef;



    private String name;
    private String email;
    private String username;
    private String password;
    private String cardNumber;
    private String code;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        app = FirebaseApp.getInstance();
        auth = FirebaseAuth.getInstance(app);
        database = FirebaseDatabase.getInstance(app);

        boolean finish = getIntent().getBooleanExtra("finishRegister", false);
        if (finish) {
            Intent i=new Intent(this, CodeScreen.class);
            i.putExtra("code",code);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }
    }

    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public void register(View view) {
        name = ((EditText)findViewById(R.id.name_register)).getText().toString();
        email = ((EditText)findViewById(R.id.email_register)).getText().toString();
        username = ((EditText)findViewById(R.id.username_register)).getText().toString();
        password = ((EditText)findViewById(R.id.password_register)).getText().toString();
        cardNumber =((EditText) findViewById(R.id.card_register)).getText().toString();





        boolean error = false;
/*
        if (name == "") {
            ((TextView) findViewById(R.id.name_register)).setError("Preencha este campo");
            error = true;
        }
        if (!validateEmail(email)) {
            ((TextView) findViewById(R.id.email_register)).setError("Preencha este campo correctamente");
            error = true;
        }
        if (username == "") {
            ((TextView) findViewById(R.id.username_register)).setError("Preencha este campo");
            error = true;
        }
        if (password == "") {
            ((TextView) findViewById(R.id.password_register)).setError("Preencha este campo");
            error = true;
        }
        if (cardNumber == "") {
            ((TextView) findViewById(R.id.card_register)).setError("Preencha este campo");
            error = true;
        }

        if (error) {
            return;
        }
        */
        code="";
        Random rand = new Random();

        int  n = rand.nextInt(9);
        code+=n;
        n=rand.nextInt(9); code+=n;
        n=rand.nextInt(9); code+=n;
        n=rand.nextInt(9); code+=n;

        Log.e("code",code);

        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.putExtra("finishRegister", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();


        /*
        Log.e("cred","email:"+email+" pass:"+password);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //checking if success
                if(task.isSuccessful()){
                    Log.e("teste",auth.getCurrentUser().getUid()+"");
                    dbRef=database.getReference("user_meta");
                    DatabaseReference child=dbRef.child(auth.getCurrentUser().getUid());
                    User user= new User(name,cardNumber,code,username);
                    child.setValue(user);
                    child.push().setValue(user);
                    Log.e("register","successful");




                }else{

                    Log.e("register","error"+task.getException().getMessage());
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
        */
    }

}
