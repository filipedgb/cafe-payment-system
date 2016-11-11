package com.example.filipedgb.cmovproj1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.classes.Order;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Base64;



public class QRcodeReader extends AppCompatActivity {

    private FirebaseApp app;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        message = (TextView) findViewById(R.id.message);

        app= FirebaseApp.getInstance();
        auth= FirebaseAuth.getInstance(app);


        boolean finish = getIntent().getBooleanExtra("finishQR", false);
        if (finish) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence("Message", message.getText());
    }


    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    TextView message;

    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException anfe) {
          //  showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void logout(View v) {
        auth.signOut();
        SharedPreferences sharedPref = getSharedPreferences("user_info", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("code","");
        editor.putString("admin","");
        editor.commit();


        Intent intent = new Intent(this, QRcodeReader.class);
        intent.putExtra("finishQR", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }



    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                }
                catch (ActivityNotFoundException anfe) {
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.e("format",format);
                Log.e("resul",contents);

                Gson gson = new Gson();
                HashMap<String,Object> map=gson.fromJson(contents, HashMap.class);

                Log.e("Map",map.toString());

                Log.e("test1",map.get("listOfProducts").toString());
                Log.e("test2",map.get("vouchers").toString());


                String temp1 = map.get("vouchers").toString();

                // remove brackets
                temp1 = temp1.substring(1, temp1.length()-1);
                String[] vouchers_string = temp1.split(",");
                HashMap<String,String> vouchers = new HashMap<String,String>();

                for(int i = 0; i < vouchers_string.length; i++) {
                    String current = vouchers_string[i];
                    String[] elements = current.split("=");
                    vouchers.put(elements[0],elements[1]);
                }

                HashMap<String,Integer> products=gson.fromJson(map.get("listOfProducts").toString(), HashMap.class);


                Order new_order=new Order(map.get("user_code").toString());
                new_order.setOrder_id(map.get("order_id").toString());
                new_order.setOrder_price(Double.valueOf(map.get("order_price").toString()));
                new_order.setListOfProducts(products);
                new_order.setVouchers_to_use(vouchers);
                new_order.setOrder_paid(Boolean.valueOf(map.get("order_paid").toString()));

                boolean approved = checkVouchersValidity(new_order);
                approved=true;
                TextView tv= new TextView(getApplicationContext());

                tv.setText(new_order.getUser_code()+"-"+approved);
                LinearLayout llcodes=(LinearLayout) findViewById(R.id.linearlayout_terminalcodes);
                llcodes.addView(tv);

                Log.e("Vouchers approved:",""+approved);

                Log.e("teste",new_order.getUser_code());
                Log.e("teste vouchers",new_order.getVouchers_to_use().toString());

               // Order u = gson.fromJson(contents, Order.class);
              //  Log.e("name",u.getName());

               // message.setText("Success!\n\n"+contents);
            }
        }
    }

    public boolean checkVouchersValidity(final Order order) {
        /* Verifies connection */
        boolean connected = true;
        boolean accepted = false;

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (false) {
                    database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference();

                    ref.child("vouchers_by_user").child(order.getUser_code()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    HashMap<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
                                    HashMap<String,String> vouchers = order.getVouchers_to_use();

                                    Collection<String> keys_from_qr = vouchers.keySet();
                                    Collection<Object> values_from_db = td.values();


                                    Log.e("LIDO DA DB", td.toString());
                                    Log.e("LIDO DO QR",vouchers.toString());

                                    Log.e("ID FROM QR",keys_from_qr.toString());
                                    Log.e("ID from DB",values_from_db.toString());
                                    boolean blacklisted = false;

                                    for (String iterable_element : keys_from_qr) {
                                        if(values_from_db.contains(iterable_element.replaceAll("\\s+",""))) {
                                          Log.e("yes","YES " + iterable_element + " is contained in db" );
                                        } else {
                                            blackListUser(order.getUser_code());
                                            blacklisted = true;
                                            break;
                                        }
                                    }

                                    if(!blacklisted) {
                                        // Process order
                                    }


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    Log.e("connection","not connected");
                    HashMap<String,String> vouchers = order.getVouchers_to_use();

                    try {
                        Iterator it = vouchers.entrySet().iterator();
                        RSAPublicKey public_key = null;

                        Signature signature = Signature.getInstance("SHA1withRSA");
                        SharedPreferences sharedPref = getSharedPreferences("public_key", 0);
                        String public_key_pem = sharedPref.getString("key", "Nao encontrou");
                        Log.e("Publicfromshared",public_key_pem+"");


                        System.out.println(public_key_pem);

                        try {
                            public_key = getPublicKeyFromString(public_key_pem);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        Log.e("public key NOT pem",public_key+"");


                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            byte[] sig = Base64.decode(pair.getValue().toString(),Base64.DEFAULT);


                            try {
                                signature.initVerify(public_key);
                                signature.update(sig);

                                boolean verify_result = signature.verify(sig); //sign é a signature do voucher em bytes


                                if(verify_result){
                                    Log.e("VOUCHER", "RSA VOUCHER BOM!!");
                                }
                                else{
                                    Log.e("VOUCHER", "RSA VOUCHER MAU!!");
                                }

                            } catch(Exception e) {
                                e.printStackTrace();

                            }


                            //System.out.println(pair.getKey() + " = " + pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException

                        }

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }





                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


        return accepted;
    }

    /**
     * FUNCTION COPIED FROM https://ricardo-sequeira.com/java-encryptiondecryption-with-rsa/
     *
     * Constructs a public key (RSA) from the given string
     *
     * @param key PEM Public Key
     * @return RSA Public Key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static RSAPublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;

        // Remove the first and last lines
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");

        Log.e("Coisas",publicKeyPEM);
        // Base64 decode data
        byte[] encoded = Base64.decode(publicKeyPEM,Base64.DEFAULT);

        Log.e("encoded",encoded.toString());

        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    public void blackListUser(String userId) {
        final DatabaseReference ref = database.getReference("blacklist");
        ref.keepSynced(true);
        String key = ref.push().getKey();
        ref.child(key).setValue(userId);
    }


}
