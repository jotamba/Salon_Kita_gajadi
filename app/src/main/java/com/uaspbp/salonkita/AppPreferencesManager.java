package com.uaspbp.salonkita;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesManager {
    Context context;
    SharedPreferences sharedPreferences;

    public AppPreferencesManager(Context context) {
        this.context = context;
        sharedPreferences =  context.getSharedPreferences("APP_PREFERENCES",Context.MODE_PRIVATE);
    }

    public void setDarkModeState(boolean enable){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("darkMode",enable);
        editor.apply();
    }

    public boolean getDarkModeState(){
        return sharedPreferences.getBoolean("darkMode",false);
    }
}
