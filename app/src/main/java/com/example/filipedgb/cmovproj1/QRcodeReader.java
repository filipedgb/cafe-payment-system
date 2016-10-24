package com.example.filipedgb.cmovproj1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.HashMap;

public class QRcodeReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        message = (TextView) findViewById(R.id.message);
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
                HashMap<String,Integer> products=gson.fromJson(map.get("listOfProducts").toString(), HashMap.class);
                Order new_order=new Order(map.get("user_code").toString());
                new_order.setOrder_id(map.get("order_id").toString());
                new_order.setOrder_price(Double.valueOf(map.get("order_price").toString()));
                new_order.setListOfProducts(products  );
                new_order.setOrder_paid(Boolean.valueOf(map.get("order_paid").toString()));



                Log.e("teste",new_order.getUser_code());

               // Order u = gson.fromJson(contents, Order.class);
              //  Log.e("name",u.getName());

                message.setText("Success!\n\n"+contents);
            }
        }
    }


}
