package com.example.filipedgb.cmovproj1;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.classes.User;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.lang3.SerializationUtils;

import java.io.UnsupportedEncodingException;

public class QRcodeGenerator extends AppCompatActivity {

    ImageView qrCodeImageview;
    TextView errorTv;
    byte [] bContent = {83, 111, 109, 101, 58, 32, -40, -41, -9, -90};  // this is the msg which we will encode in QRcode
    String content = null;
    String errorMsg = "";
    public final static int WIDTH=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("qrcode","here");
        TextView titleTv;
        setContentView(R.layout.activity_qrcode_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        qrCodeImageview=(ImageView) findViewById(R.id.img_qr_code_image);
        titleTv = (TextView) findViewById(R.id.title);
        errorTv = (TextView) findViewById(R.id.error);

        User user= new User("a","b","c","d");

        Gson gson = new Gson();

        Order order= (Order) getIntent().getSerializableExtra("orderObject");

        String jsonInString = gson.toJson(order);
        Log.e("json",jsonInString);

        byte[] data = SerializationUtils.serialize(jsonInString);



        Log.e("content",data.toString());

        try {
            content = new String(data, "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            errorMsg = e.getMessage();
            errorTv.setText(errorMsg);
        }

        content=jsonInString;
        titleTv.setText("Message: \"" + content + "\"");

        Thread t = new Thread(new Runnable() {  // do the creation in a new thread to avoid ANR Exception
            public void run() {
                final Bitmap bitmap;
                try {
                    bitmap = encodeAsBitmap(content);
                    runOnUiThread(new Runnable() {  // runOnUiThread method used to do UI task in main thread.
                        @Override
                        public void run() {
                            qrCodeImageview.setImageBitmap(bitmap);
                        }
                    });
                }
                catch (WriterException e) {
                    errorMsg += "\n" + e.getMessage();
                }
                if (!errorMsg.isEmpty())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorTv.setText(errorMsg);
                        }
                    });
            }
        });
        t.start();
    }


    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        }
        catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorPrimaryDark):Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }


}
