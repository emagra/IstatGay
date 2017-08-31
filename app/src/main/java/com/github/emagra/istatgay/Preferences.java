package com.github.emagra.istatgay;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Preferences extends DialogFragment {

    private static Preferences instance;
    private String id = IstatGay.uniqueID; // ID univoco relativo al telefono, evitare votazioni multiple

    private RadioButton yes,
            no,
            male,
            female;
    private Button submit;
    private TextView idTxt;
    private ProgressBar loading;

    private FirebaseDatabase mDB;
    private DatabaseReference mDBResponse, mUser;


    public Preferences() {
        // Required empty public constructor
    }


    // TODO: ???
    public static Preferences getInstance() {
        if (instance == null ){
            instance = new Preferences();
        }
        return instance;
    }
    // TODO: ???
    public static Preferences newInstance(){
        Preferences instance = new Preferences();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_preferences, container, false);

        // Istanza del DB
        mDB = FirebaseDatabase.getInstance();
        // Reference alla tabella response
        mDBResponse = mDB.getReference().child("response");

        yes = (RadioButton)rootView.findViewById(R.id.radioOrientationYes);
        no = (RadioButton)rootView.findViewById(R.id.radioOrientationNo);
        male = (RadioButton)rootView.findViewById(R.id.radioSexMale);
        female = (RadioButton)rootView.findViewById(R.id.radioSexFemale);
        submit = (Button)rootView.findViewById(R.id.submitBtn);
        idTxt = (TextView)rootView.findViewById(R.id.id);
        loading = (ProgressBar)rootView.findViewById(R.id.loadingCircle);
        idTxt.setText(id);

        mUser = mDBResponse.child(IstatGay.uniqueID);
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
                    Toast.makeText(getActivity(), R.string.orientation_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( !male.isChecked() && !female.isChecked()) {
                    Toast.makeText(getActivity(), R.string.sex_error, Toast.LENGTH_SHORT).show();
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
                                dismiss();
                                //startActivity(new Intent(getActivity(), IstatActivity.class));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        idTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.info_id, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

}
