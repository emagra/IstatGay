package com.github.emagra.istatgay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import static java.security.AccessController.getContext;

public class InfoUserMainActivity extends AppCompatActivity {

    private RadioButton yes, no, male, female;
    private Button submit;
    private TextView idTxt;
    private RelativeLayout loading;
    private String TAG = this.getClass().getName();;
    private String id;
    private boolean twice,
            cache;

    private User user;
    private FirebaseDatabase mDB;
    private DatabaseReference mDBResponse, mUser;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_main);

        // Istanza del DB
        mDB = FirebaseDatabase.getInstance();
        // Reference alla tabella response
        mDBResponse = mDB.getReference().child("response");

        // carica le SharedPreferences per recuperare o creare l'UID
        sp = getSharedPreferences("ISTAT_GAY", Context.MODE_PRIVATE);
        editor = sp.edit();
        // carico se esiste altrimenti ne crea uno
        //id = IstatGay.loadUid(sp, editor);
        // Genera un id univoco per installazione (non permette di votare più volte)
        id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //id = IstatGay.uniqueID;

        yes = (RadioButton)findViewById(R.id.radioYes);
        no = (RadioButton)findViewById(R.id.radioNo);
        male = (RadioButton)findViewById(R.id.radioSexMale);
        female = (RadioButton)findViewById(R.id.radioSexFemale);
        submit = (Button)findViewById(R.id.submitBtn);
        idTxt = (TextView)findViewById(R.id.id);
        loading = (RelativeLayout)findViewById(R.id.loadingPanel);
        //loading.setVisibility(View.INVISIBLE);
        Log.v(TAG, "##########################################################\n" +
                "## InfoUserMainActivity.onCreate: Loaded UID " + id + "\n" +
                "##########################################################");

        idTxt.setText(this.id);

        mUser = mDBResponse.child(id);
        Log.v(TAG, "##########################################################\n" +
                "## InfoUserMainActivity.onCreate: userDB reference " + mUser + "\n" +
                "##########################################################");

        //TODO controlla la cache  if (cache())
        /*if (cache){
            user = cache(sp ,editor, null);
        }*/

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                // null se primo avvio
                if (u != null){
                    if (u.isStatus()){
                        yes.setChecked(true);
                        no.setChecked(false);
                    } else {
                        yes.setChecked(false);
                        no.setChecked(true);
                    }
                    if (u.getSex().equalsIgnoreCase("m")){
                        male.setChecked(true);
                        female.setChecked(false);
                    } else {
                        male.setChecked(false);
                        female.setChecked(true);
                    }
                }
                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !yes.isChecked() && !no.isChecked()) {
                    Toast.makeText(InfoUserMainActivity.this, "Seleziona l'orientamento", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( !male.isChecked() && !female.isChecked()) {
                    Toast.makeText(InfoUserMainActivity.this, "Seleziona il sesso", Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                submit.setEnabled(false);
                final User u = new User();
                // TODO: if if if rly??
                if (male.isChecked()) u.setSex("m");
                if (female.isChecked()) u.setSex("f");
                if (yes.isChecked()) u.setStatus(true);
                if (no.isChecked()) u.setStatus(false);

                mDBResponse.child(id).setValue(u)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loading.setVisibility(View.GONE);
                                submit.setEnabled(true);
                                startActivity(new Intent(InfoUserMainActivity.this, IstatActivity.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(InfoUserMainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        //loading.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO rimuovere i listner
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (twice == true){
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            System.exit(0);
        }
        twice = true;
        Toast.makeText(this, "Premi di nuovo per uscire", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
                Log.d(TAG, "twice: " + twice);
            }
        }, 2000);
    }

    private User cache(SharedPreferences p, SharedPreferences.Editor e, User u){
        User uCached = u;
        String uid = p.getString("UID", "null");
        String sex = p.getString("sex", "null");
        boolean gay = p.getBoolean("gay", false);

        if ( uid.equalsIgnoreCase("null") && u != null ){
            // first run
            e.putString("UID", IstatGay.uniqueID);
            e.putBoolean("gay", u.isStatus());
            e.putString("sex", u.getSex());
            e.apply();
            cache = true;
        } else {
            uCached = new User();
            uCached.setSex(sex);
            uCached.setStatus(gay);
        }

        Log.v(TAG, "##########################################################\n" +
                "## InfoUserMainActivity.cache: user" + uCached.toString() + "var cache:" + cache + "\n" +
                "##########################################################");
        return uCached;
    }
}
