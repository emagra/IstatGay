package com.github.emagra.istatgay;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IstatActivity extends AppCompatActivity {
    private DatabaseReference responseReference;
    private TextView totalResponseTxt,
            totalGayTxt,
            totalMaleTxt,
            totalFemaleTxt,
            totalGenderTxt;
    private Button preference;
    private ProgressBar loading;

    private boolean twice;

    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istat);

        totalResponseTxt = (TextView)findViewById(R.id.totalTxtViewNum);
        totalGayTxt = (TextView)findViewById(R.id.gayTxtViewNum);
        totalMaleTxt = (TextView)findViewById(R.id.malePerNum);
        totalFemaleTxt = (TextView)findViewById(R.id.femalePerrNum);
        totalGenderTxt  = (TextView)findViewById(R.id.gayGenderTxtViewNum);
        preference = (Button)findViewById(R.id.preferenceBtn);
        loading = (ProgressBar)findViewById(R.id.loadingCircle);

        responseReference = FirebaseDatabase.getInstance().getReference().child(IstatGay.responseDB);
        responseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.setVisibility(View.VISIBLE);
                getData(dataSnapshot);
                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                IstatActivity.super.onBackPressed();
            }
        });

        preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                Preferences preferenceFragment = Preferences.getInstance();
                //Preferences preferenceFragment = new Preferences();
                preferenceFragment.show(fm, "preference_fragment");
            }
        });

        // easter eggs
        totalMaleTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(IstatActivity.this, "Anche Dido è gay ma lui non lo sa!", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.malePer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:
            }
        });

        /*responseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void getData(DataSnapshot d){
        int totalResponse = 0;
        int totalGay = 0;
        int totalMale = 0;
        int totalFemale = 0;
        int totalGayMale = 0;
        int totalGayFemale = 0;

        for (DataSnapshot sub : d.getChildren()){
            User u = sub.getValue(User.class);
            //Log.v(TAG, "\n\n" + u.toString() + "\n\n");
            if (u == null) continue;
            totalResponse++;
            if (u.getSex().equalsIgnoreCase("m")){
                totalMale++;
                if (u.isStatus()){
                    totalGay++;
                    totalGayMale++;
                }
            } else {
                totalFemale++;
                if (u.isStatus()){
                    totalGay++;
                    totalGayFemale++;
                }
            }

        }
        if(totalGay > 0 ) {
            totalGayMale = totalGayMale * 100 / totalGay;
            totalGayFemale = totalGayFemale * 100 / totalGay;
            totalGay = totalGay * 100 / totalResponse;
        }
        if (totalResponse > 0) {
            totalMale = totalMale * 100 / totalResponse;
            totalFemale = totalFemale * 100 / totalResponse;
        }

        totalResponseTxt.setText(String.format(IstatGay.lang, "%d", totalResponse)); // + " - " + String.valueOf(totalMale) + "% Uomini - " + String.valueOf(totalFemale) + "% Donne");
        totalMaleTxt.setText(String.format(IstatGay.lang, "%d%%", totalMale));
        totalFemaleTxt.setText(String.format(IstatGay.lang, "%d%%", totalFemale));
        totalGayTxt.setText(String.format(IstatGay.lang, "%d%%", totalGay)); // + String.valueOf(totalGayMale) + "% Uomini \n" + String.valueOf(totalGayFemale) + "% Donne");
        totalGenderTxt.setText(String.format(IstatGay.lang, "%d%% Uomini - %d%% Donne", totalGayMale, totalGayFemale));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if ( twice ){
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            System.exit(0);
        }
        twice = true;
        Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 2000);
    }
}
