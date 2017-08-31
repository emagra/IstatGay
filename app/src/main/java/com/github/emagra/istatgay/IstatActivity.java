package com.github.emagra.istatgay;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView totalResponse,
            totalMale,
            totalFemale,
            totalGay,
            totalGayGender;
    private Button preference;
    private ProgressBar loading;

    private boolean twice;

    private String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istat);

        totalResponse = (TextView)findViewById(R.id.totalUser);
        totalMale = (TextView)findViewById(R.id.totalMale);
        totalFemale = (TextView)findViewById(R.id.totalFemale);
        totalGay = (TextView)findViewById(R.id.totalGay);
        totalGayGender = (TextView)findViewById(R.id.totalGayGender);
        preference = (Button)findViewById(R.id.preferenceBtn);
        loading = (ProgressBar)findViewById(R.id.loadingCircle);

        responseReference = FirebaseDatabase.getInstance().getReference().child(IstatGay.RESPONSEDB);

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
        totalMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(IstatActivity.this, "Anche Dido è gay ma lui non lo sa!", Toast.LENGTH_SHORT).show();
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
        int     totalResponse = 0,
                totalMale = 0,
                totalFemale = 0,
                totalGay = 0,
                totalGayMale = 0,
                totalGayFemale = 0,
                totalMalePer = 0,
                totalFemalePer = 0,
                totalGayPer = 0,
                totalGayMalePer = 0,
                totalGayFemalePer = 0;
        for (DataSnapshot sub : d.getChildren()){
            User u = sub.getValue(User.class);
            //Log.v(TAG, "\n\n" + u.toString() + "\n\n");
            if (u == null) continue;
            totalResponse++;
            if (u.getSex().equalsIgnoreCase(IstatGay.MALE)){
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
            totalGayMalePer = totalGayMale * 100 / totalGay;
            totalGayFemalePer = totalGayFemale * 100 / totalGay;
            totalGayPer = totalGay * 100 / totalResponse;
        }
        if (totalResponse > 0) {
            totalMalePer = totalMale * 100 / totalResponse;
            totalFemalePer = totalFemale * 100 / totalResponse;
        }

        this.totalResponse.setText(String.format(IstatGay.lang, "%d", totalResponse)); // + " - " + String.valueOf(totalMale) + "% Uomini - " + String.valueOf(totalFemale) + "% Donne");
        this.totalMale.setText(String.format(IstatGay.lang, "%d - %d%%", totalMale, totalMalePer));
        this.totalFemale.setText(String.format(IstatGay.lang, "%d - %d%%", totalFemale, totalFemalePer));
        this.totalGay.setText(String.format(IstatGay.lang, "%d - %d%%", totalGay, totalGayPer)); // + String.valueOf(totalGayMale) + "% Uomini \n" + String.valueOf(totalGayFemale) + "% Donne");
        this.totalGayGender.setText(String.format(IstatGay.lang, "%d - %d%% Uomini // %d - %d%% Donne", totalGayMale, totalGayMalePer, totalGayFemale, totalGayFemalePer));
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

    @Override
    protected void onStop() {
        super.onStop();
        // TODO: rimuovere i listner vari
    }
}
