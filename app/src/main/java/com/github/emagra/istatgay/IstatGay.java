package com.github.emagra.istatgay;

import android.app.Application;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.Locale;

public class IstatGay extends Application{

    public static final String
            MALE = "m", // char on DB stand for male and female
            FEMALE = "f", // same
            RESPONSEDB = "response", // child name in firebase DB
            SHAREDPREF = "ISTAT_GAY", AGREE = "AGREE";
    public static String uniqueID;
    public static Locale lang;

    @Override
    public void onCreate() {
        super.onCreate();
        uniqueID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        lang = Locale.getDefault();
    }

    public static boolean agree(SharedPreferences p, SharedPreferences.Editor e){

        if ( ! p.getBoolean("AGREE", false) ) {
            e.putBoolean("AGREE", true);
            e.apply();
        }

        return true;
    }
}
