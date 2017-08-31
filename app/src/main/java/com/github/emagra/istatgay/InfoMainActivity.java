package com.github.emagra.istatgay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InfoMainActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Intent toNext;
    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_main);
        toNext = new Intent(InfoMainActivity.this, IstatActivity.class);

        sp = getSharedPreferences("ISTAT_GAY", Context.MODE_PRIVATE);
        editor = sp.edit();

        Button agree = (Button)findViewById(R.id.agreeBtn);

        sp = getSharedPreferences("ISTAT_GAY", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.getBoolean("AGREE", false)){
            startActivity(toNext);
        }

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IstatGay.agree(sp, editor);
                startActivity(toNext);
            }
        });
    }
}
