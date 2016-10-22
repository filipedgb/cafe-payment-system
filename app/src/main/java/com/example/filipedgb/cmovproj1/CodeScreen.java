package com.example.filipedgb.cmovproj1;

import android.content.Intent;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CodeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_screen);

        String code = getIntent().getStringExtra("code");
        TextView tv=(TextView)this.findViewById(R.id.code_text_view);
        tv.setText(code);
        ((TextView)this.findViewById(R.id.text_alert_code)).setText("Por motivos de segurança, irá ser pedido um código a cada compra.\n\nO seu código é:");
        boolean finish = getIntent().getBooleanExtra("finishCode", false);
        if (finish) {
            Intent i=new Intent(this, MenuActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            return;
        }
    }



    public void codeAccept(View view) {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("finishCode", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        finish();
    }
}
