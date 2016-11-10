package com.example.filipedgb.cmovproj1;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filipedgb.cmovproj1.classes.Order;
import com.example.filipedgb.cmovproj1.classes.User;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.junit.runner.Describable;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class QRFragment extends Fragment {

    ImageView qrCodeImageview;
    TextView errorTv;
    byte [] bContent = {83, 111, 109, 101, 58, 32, -40, -41, -9, -90};  // this is the msg which we will encode in QRcode
    String content = null;
    String errorMsg = "";
    public final static int WIDTH=500;


    public Order getOrderQr() {
        return orderQr;
    }

    public void setOrderQr(Order orderQr) {
        this.orderQr = orderQr;
    }

    Order orderQr;


    public QRFragment() {
    }

    private Describable mDescribable;

    public static QRFragment newInstance(Order order) {
        QRFragment fragment = new QRFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("json","teste");
        View rootView = inflater.inflate(R.layout.fragment_qr, container, false);

        orderQr = (Order) getArguments().getSerializable("order");


        TextView titleTv;
        qrCodeImageview=(ImageView) rootView.findViewById(R.id.img_qr_code_image);
        titleTv = (TextView) rootView.findViewById(R.id.title);
        errorTv = (TextView) rootView.findViewById(R.id.error);

        titleTv.setText("Mostre este código");

        User user= new User("a","b","c","d");


        Gson gson = new Gson();

        Log.e("order",orderQr.getUser_code().toString());
        Log.e("order",orderQr.getOrder_id());
        Log.e("order",orderQr.getOrder_paid().toString());
        Log.e("order",orderQr.getListOfProducts().toString());
        Log.e("order",orderQr.getVouchers_to_use().toString());


        Map<String,Object> jsonMap= new HashMap<String,Object>();
        jsonMap.put("order_id",orderQr.getOrder_id());
        jsonMap.put("order_price",orderQr.getOrder_price());
        jsonMap.put("user_code",orderQr.getUser_code());
        jsonMap.put("listOfProducts",orderQr.getListOfProducts());
        jsonMap.put("order_paid",orderQr.getOrder_paid());
        jsonMap.put("vouchers",orderQr.getVouchers_to_use());


        String jsonInString = gson.toJsonTree(jsonMap).toString();
        Log.e("json",jsonInString);

        content=jsonInString;
        //titleTv.setText("Message: \"" + content + "\"");

        Thread t = new Thread(new Runnable() {  // do the creation in a new thread to avoid ANR Exception
            public void run() {
                final Bitmap bitmap;
                try {
                    bitmap = encodeAsBitmap(content);
                    getActivity().runOnUiThread(new Runnable() {  // runOnUiThread method used to do UI task in main thread.
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorTv.setText(errorMsg);
                        }
                    });
            }
        });
        t.start();

        return rootView;
    }
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            Log.e("String",str);
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
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.colorPrimaryDark): Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }

}
