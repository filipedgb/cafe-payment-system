package com.example.filipedgb.cmovproj1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Base64;
import android.widget.Toast;

import com.example.filipedgb.cmovproj1.classes.Order;
import com.example.filipedgb.cmovproj1.classes.User;
import com.example.filipedgb.cmovproj1.classes.Voucher;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.commons.lang3.RandomUtils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;



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
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("user_meta").keepSynced(true);
        database.getReference("blacklist").keepSynced(true);


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

                if(!map.get("vouchers").toString().equals("{}")) {
                    for (int i = 0; i < vouchers_string.length; i++) {
                        String current = vouchers_string[i];
                        String[] elements = current.split("=");
                        vouchers.put(elements[0], elements[1]);
                    }
                }

                HashMap<String,Integer> products=gson.fromJson(map.get("listOfProducts").toString(), HashMap.class);

                Order new_order=new Order(map.get("user_code").toString());
              //  new_order.setOrder_id(map.get("order_id").toString());
                new_order.setOrder_price(Double.valueOf(map.get("order_price").toString()));
                new_order.setListOfProducts(products);
                new_order.setVouchers_to_use(vouchers);
                new_order.setOrder_paid(Boolean.valueOf(map.get("order_paid").toString()));
                new_order.setCreated_at(map.get("created_at").toString());

                Log.e("Numero d vouchers:",new_order.getVouchers_to_use().size()+"");
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference blacklistRef = database.getReference("blacklist");
                blacklistRef.keepSynced(true);
                final String userId_new_order=new_order.getUser_code();
                final  HashMap<String,String> vouchers_final=vouchers;
                final Order order_Final=new_order;
                Log.e("userid",userId_new_order);
                blacklistRef.child(userId_new_order).keepSynced(true);
                blacklistRef.child(userId_new_order).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.getValue(Boolean.class))
                        {
                            DatabaseReference card = database.getReference("user_meta");
                            card.keepSynced(true);
                            card.child(userId_new_order).child("cardDate").keepSynced(true);
                            card.child(userId_new_order).child("cardDate").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String string = dataSnapshot.getValue(String.class);
                                    String[] parts = string.split("-");
                                    String month = parts[0]; // 004
                                    String year = "20"+ parts[1]; // 034556
                                    int month_int= Integer.parseInt(month);
                                    int year_int=Integer.parseInt(year);
                                    DateFormat df = new SimpleDateFormat("MM-yyyy");
                                    String date = df.format(Calendar.getInstance().getTime());
                                    Log.e("date",date);
                                    String[] parts2 = date.split("-");
                                    int month_int_real=Integer.parseInt(parts2[0]);
                                    int year_int_real=Integer.parseInt(parts2[1]);
                                    Log.e("card",month_int+"-"+year_int);
                                    Log.e("card",month_int_real+"-"+year_int_real);
                                    if((year_int_real==year_int && month_int_real>=month_int) || (year_int_real<year_int))
                                    {
                                        if(vouchers_final.size()==0){
                                            processOrder(order_Final);
                                        }
                                        else{
                                            checkVouchersValidity(order_Final);
                                        }
                                    }
                                    else {
                                        Log.e("userid",userId_new_order);
                                        Context context = QRcodeReader.this;
                                        new AlertDialog.Builder(context)
                                                .setTitle("Conta Bloqueada")
                                                .setMessage("A sua conta foi bloqueada porque usou um cartão inválido.\nContacte um administrador para mais informação.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        blackListUser(userId_new_order);
                                        Log.e("card","wrong date");
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                        else {
                            Log.e("blacklist","black");
                            Context context = QRcodeReader.this;
                            new AlertDialog.Builder(context)
                                    .setTitle("Conta Bloqueada")
                                    .setMessage("Contacte um administrador para mais informação.")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            blackListUser(userId_new_order);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void processOrder(final Order new_order) {
        TextView tv= new TextView(getApplicationContext());

        //create order code
        String code="";
        Random rand = new Random();
        int  n = rand.nextInt(9);code+=n;
        n=rand.nextInt(9); code+=n;
        n=rand.nextInt(9); code+=n;

        //get user name
        final LayoutInflater inflator= getLayoutInflater();
      //  final View orderView=inflator.inflate(R.layout.content_order_history,null);
       // ((RelativeLayout)orderView.findViewById(R.id.relativelayoutparentOrder)).setPadding(0,0,0,0);

        final String finalCode = code;

        LinearLayout llcodes=(LinearLayout) findViewById(R.id.linearlayout_terminalcodes);

        final View orderViewFull=inflator.inflate(R.layout.content_order_history,null);
        ((RelativeLayout)orderViewFull.findViewById(R.id.relativelayoutparentOrder)).setPadding(0,0,0,0);
        ((TextView) orderViewFull.findViewById(R.id.codeProduct)).setText("nº: "+finalCode);
        ((TextView) orderViewFull.findViewById(R.id.hours_history)).setText(new_order.getCreated_at().substring(11));
        ((TextView) orderViewFull.findViewById(R.id.date_history)).setText(new_order.getCreated_at().substring(0,9));
        Double price_db=new_order.getOrder_price();
        ((TextView) orderViewFull.findViewById(R.id.tv_price)).setText(round(price_db,2)+"€");

        if(((LinearLayout) orderViewFull.findViewById(R.id.linearlayoutproducts)).getChildCount() > 0)
            ((LinearLayout) orderViewFull.findViewById(R.id.linearlayoutproducts)).removeAllViews();

        if(new_order.getListOfProducts()!=null)
        {
            for (final String key : new_order.getListOfProducts().keySet())
            {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference productRef = database.getReference("products");
                productRef.keepSynced(true);

                productRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        LinearLayout linearlayoutproducts=(LinearLayout)orderViewFull.findViewById(R.id.linearlayoutproducts);

                        final View orderView=inflator.inflate(R.layout.content_oder_termianl,null);
                        RelativeLayout rel= (RelativeLayout) orderView.findViewById(R.id.relativelayoutTerminalorder);
                        final float scale = getResources().getDisplayMetrics().density;
                        int pixels5 = (int) (5 * scale + 0.5f);
                        int pixels10 = (int) (10 * scale + 0.5f);

                        rel.setPadding(pixels10,pixels5,pixels10,pixels5);

                        Log.e("teste",key+"");

                        Log.e("teste",new_order.getListOfProducts().get(key)+"");
//                        double price= (double) snapshot.child("price").getValue();
                        Log.e("array",new_order.getListOfProducts().toString());
                        //int  number=  new_order.getListOfProducts().get(key).intValue();
                        String number_Str= new_order.getListOfProducts().get(key)+"";
                        number_Str=number_Str.substring(0,number_Str.length()-2);

                        Double number=  Double.parseDouble( new_order.getListOfProducts().get(key)+"");
                        Double price=  Double.parseDouble( snapshot.child("price").getValue()+"");


                        ((TextView) orderView.findViewById(R.id.nameOrderTerminal)).setText(number_Str+"  "+snapshot.child("name").getValue());
                        ((TextView) orderView.findViewById(R.id.codeOrderTerminal)).setText(round(number*price,2)+"€");


                        linearlayoutproducts.addView(orderView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        }

        if(new_order.getVouchers_to_use()!=null)
        {
            Iterator it = new_order.getVouchers_to_use().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                Log.e("lol",pair.getKey() + " = " + pair.getValue());


                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference productRef = database.getReference("vouchers").child(pair.getKey().toString().replaceAll("\\s+",""));
                productRef.keepSynced(true);

                productRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Voucher voucherObj = snapshot.getValue(Voucher.class);
                        if(voucherObj == null)
                        {
                            Log.e("null","");
                        }

                        LinearLayout linearlayoutproducts=(LinearLayout)orderViewFull.findViewById(R.id.listvouchershistory);

                        final View orderView=inflator.inflate(R.layout.content_oder_termianl,null);
                        RelativeLayout rel= (RelativeLayout) orderView.findViewById(R.id.relativelayoutTerminalorder);
                        final float scale = getResources().getDisplayMetrics().density;
                        int pixels5 = (int) (5 * scale + 0.5f);
                        int pixels10 = (int) (10 * scale + 0.5f);

                        rel.setPadding(pixels10,pixels5,pixels10,pixels5);

                        String name="";
                         if(voucherObj.getType() ==0)
                        {
                            name="Café Grátis";
                        }
                        else if(voucherObj.getType() ==1)
                        {
                            name="Pipocas Grátis";
                        }
                        else if(voucherObj.getType() ==2)
                        {
                            name="Desconto 5%";
                        }
                        ((TextView) orderView.findViewById(R.id.nameOrderTerminal)).setText(name);
                        ((TextView) orderView.findViewById(R.id.codeOrderTerminal)).setText("");


                        linearlayoutproducts.addView(orderView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }

        }


            final FirebaseDatabase database = FirebaseDatabase.getInstance();


        llcodes.addView(orderViewFull);

        Log.e("vouchers",new_order.getVouchers_to_use().toString());


        saveToFirebase(new_order);
      saveToFirebaseByUser(new_order);
      updateTotalMoneySpent(new_order);
        checkNewVouchers(new_order);

    }

    public void checkVouchersValidity(final Order order) {
        /* Verifies connection */
        boolean connected = true;
        final boolean accepted = false;
        final String userID_new_order=order.getUser_code();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = isNetworkAvailable();
                if(connected) {
                    Log.e("connection","connected");

                    database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference();

                    ref.child("vouchers_by_user").child(order.getUser_code()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean blacklisted = false;
                                    HashMap<String, Object> td = (HashMap<String,Object>) dataSnapshot.getValue();
                                    HashMap<String,String> vouchers = order.getVouchers_to_use();

                                    Collection<String> keys_from_qr = vouchers.keySet();
                                    Collection<Object> values_from_db = td.values();


                                    Log.e("LIDO DA DB", td.toString());
                                    Log.e("LIDO DO QR",vouchers.toString());

                                    Log.e("ID FROM QR",keys_from_qr.toString());
                                    Log.e("ID from DB",values_from_db.toString());


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
                                        Log.e("ACEITE", "Vai processar a ORDER");
                                        processOrder(order);
                                    }
                                    else {
                                        new AlertDialog.Builder(QRcodeReader.this)
                                                .setTitle("Conta Bloqueada")
                                                .setMessage("A sua conta foi bloqueada porque usou um voucher inválido.\nContacte um administrador para mais informação.")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // continue with delete
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();

                                    }


                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    Log.e("connection","not connected");
                    HashMap<String,String> vouchers = order.getVouchers_to_use();
                    boolean blacklisted = false;

                    try {
                        Iterator it = vouchers.entrySet().iterator();
                        PublicKey public_key = null;

                        Signature signature = Signature.getInstance("SHA1withRSA");
                        SharedPreferences sharedPref = getSharedPreferences("public_key", 0);
                        String public_key_pem = sharedPref.getString("key", "Nao encontrou");

                        Log.e("Publicfromshared",public_key_pem+"");

                        String public_key_not_pem = public_key_pem.replace("-----BEGIN PUBLIC KEY-----\n", "");
                        public_key_not_pem = public_key_not_pem.replace("\n-----END PUBLIC KEY-----", "");

                        try {
                            public_key = getKey(public_key_not_pem);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }


                        Log.e("public key object",public_key.toString()+"");


                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();

                            String signature_fixed = pair.getValue().toString() + "==";

                            byte[] sig = Base64.decode(signature_fixed,Base64.DEFAULT);

                            Log.e("signature",pair.getValue().toString());
                            Log.e("signature_fixed",signature_fixed);
                            Log.e("signature", Arrays.toString(sig));

                            try {
                                signature.initVerify(public_key);
                                signature.update(pair.getKey().toString().getBytes());

                                boolean verify_result = signature.verify(sig); //sign é a signature do voucher em bytes

                                if(verify_result){
                                    Log.e("VoucherVerification", "Successful.");
                                }
                                else{
                                    Context context = QRcodeReader.this;
                                    new AlertDialog.Builder(context)
                                            .setTitle("Conta Bloqueada")
                                            .setMessage("A sua conta foi bloqueada porque usou um voucher inválido.\nContacte um administrador para mais informação.")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // continue with delete
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();

                                    Log.e("VoucherVerification", "Failed.");
                                    blackListUser(order.getUser_code());
                                    blacklisted = true;
                                    break;
                                }

                            } catch(Exception e) {
                                e.printStackTrace();

                            }


                            //System.out.println(pair.getKey() + " = " + pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException

                        }

                        if(!blacklisted) {
                            Log.e("ACEITE", "Vai processar a ORDER");
                            processOrder(order);
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
    }


    public static PublicKey getKey(String key){
        try{
            Log.e("Dentro do getkey",key);
            byte[] byteKey = Base64.decode(key.getBytes(), Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
            Log.e("error",e.toString());
        }

        return null;
    }

    public void blackListUser(String userId) {
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference ref = database2.getReference("blacklist");
        ref.keepSynced(true);
        ref.child(userId).setValue(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void checkNewVouchers(Order order) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("vouchers");
        ref.keepSynced(true);

        if(order.getOrder_price() > 20) {
            Voucher voucher = new Voucher(order.getUser_code(), RandomUtils.nextInt(0,1));
            String key = ref.push().getKey();
            voucher.setSerial(key);
            order.setOrder_id(key);
            ref.child(key).setValue(voucher);

            DatabaseReference mOrderReference = database.getReference("vouchers_by_user");
            mOrderReference.keepSynced(true);

            mOrderReference.child(order.getUser_code().toString()).push().setValue(key);
        }
    }


    public void updateTotalMoneySpent(final Order order) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userReference = database.getReference();
        userReference.keepSynced(true);

        userReference.child("user_meta").child(order.getUser_code()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Double old_money = user.getMoneySpent();
                        Double new_money = user.getMoneySpent()+order.getOrder_price();

                        userReference.child("user_meta").child(order.getUser_code()).child("moneySpent").setValue(new_money);

                        /* Check if user's spent money is a multiple of 100 for vouchers */
                        if( ((int)((old_money%1000)/100)) !=  ((int) ((new_money%1000)/100)) ) { // donwload dos codigos comparar o numero das centenas
                            final DatabaseReference ref = database.getReference("vouchers");
                            Voucher voucher = new Voucher(order.getUser_code(),2);
                            String key = ref.push().getKey();
                            voucher.setSerial(key);
                            order.setOrder_id(key);
                            ref.child(key).setValue(voucher);
                            DatabaseReference mOrderReference = database.getReference("vouchers_by_user");
                            mOrderReference.child(order.getUser_code().toString()).push().setValue(key);
                        }
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
        mOrderReference.keepSynced(true);
        String key = mOrderReference.push().getKey();
        order.setOrder_id(key);
        mOrderReference.child(key).setValue(order);

        Log.e("vouchers",order.getVouchers_to_use().toString());

        //Log.e("Key",key);
    }


    public void saveToFirebaseByUser(Order order) {
        app = FirebaseApp.getInstance();
        auth = FirebaseAuth.getInstance(app);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mOrderReference = database.getReference("orders_by_user");
        mOrderReference.keepSynced(true);

        mOrderReference.child(order.getUser_code()).push().setValue(order.getOrder_id());


        //Log.e("Key",key);
    }


}
