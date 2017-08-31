package com.github.emagra.istatgay;


import android.os.Bundle;
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
import android.widget.RadioGroup;
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
    private String TAG = getClass().getName();
    private User user;
    private RadioGroup radioOrientation,
            radioSex;
    private Button submit;
    private TextView idTxt;
    private ProgressBar loading;

    private FirebaseDatabase mDB;
    private DatabaseReference mDBResponse, mUser;


    public Preferences() {
        // Required empty public constructor
    }

    // singleton
    public static Preferences getInstance() {
        if (instance == null ){
            instance = new Preferences();
        }
        return instance;
    }

    public static Preferences newInstance(){
        Preferences instance = new Preferences();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_preferences, container, false);

        // Istanza del DB
        mDB = FirebaseDatabase.getInstance();
        // Reference alla tabella response
        mDBResponse = mDB.getReference().child(IstatGay.RESPONSEDB);

        radioOrientation = (RadioGroup)rootView.findViewById(R.id.radioOrientation);
        radioSex = (RadioGroup)rootView.findViewById(R.id.radioSex);

        submit = (Button)rootView.findViewById(R.id.submitBtn);
        idTxt = (TextView)rootView.findViewById(R.id.id);
        loading = (ProgressBar)rootView.findViewById(R.id.loadingCircle);
        idTxt.setText(id);

        mUser = mDBResponse.child(IstatGay.uniqueID);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // user recuperato dal DB. user = null se primo accesso
                user = dataSnapshot.getValue(User.class);
                if (user != null){
                    // load user from DB and check radio button
                    // orientation
                    if (user.isStatus()){
                        radioOrientation.check(R.id.radioOrientationYes);
                    } else {
                        radioOrientation.check(R.id.radioOrientationNo);
                    }
                    // sex
                    if (user.getSex().equalsIgnoreCase("m"))
                        radioSex.check(R.id.radioSexMale);
                    else
                        radioSex.check(R.id.radioSexFemale);
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
                // check child. something must be checked
                if ( radioOrientation.getCheckedRadioButtonId() < 0) {
                    Toast.makeText(getActivity(), R.string.orientation_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( radioSex.getCheckedRadioButtonId() < 0 ) {
                    Toast.makeText(getActivity(), R.string.sex_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                submit.setEnabled(false);

                // Primo accesso user = null. altrimento user da DB
                final User u;
                if (user == null){
                    u = new User();
                } else {
                    u = user;
                }
                // short if. get id of checked radiogroup
                u.setStatus(radioOrientation.getCheckedRadioButtonId() == R.id.radioOrientationYes ? true : false);
                u.setSex(radioSex.getCheckedRadioButtonId() == R.id.radioSexMale ? IstatGay.MALE : IstatGay.FEMALE);

                // at what time change occurs
                u.setCommitTime(System.currentTimeMillis());

                if (!(u.getFirstCommit() > 0)) {
                    u.setFirstCommit(System.currentTimeMillis());
                }

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
