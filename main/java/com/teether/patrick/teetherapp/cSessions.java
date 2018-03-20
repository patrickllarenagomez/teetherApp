package com.teether.patrick.teetherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Patrick on 1/18/2018.
 */

public class cSessions
{
        private SharedPreferences prefs;

    public cSessions(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

        public void setEmail(String email) {
            prefs.edit().putString("user_email_address", email).commit();
        }

        public String getEmail() {
            String email = prefs.getString("user_email_address","");
            return email;
        }

        public void setFirstName(String firstName) {
            prefs.edit().putString("user_first_name", firstName).commit();
        }

        public String getfirstName() {
            String firstName = prefs.getString("user_first_name","");
            return firstName;
        }

        public void setUserID(int userID) {
            prefs.edit().putInt("user_id", userID).commit();
        }

        public int getUserID() {
            int user_id = prefs.getInt("user_id", 0);
            return user_id;
        }

        public void setUserLevel(int userLevel) {
            prefs.edit().putInt("user_level", userLevel).commit();
        }

        public int getUserLevel() {
            int user_level = prefs.getInt("user_level",0);
            return user_level;
        }

        public void clearAll(Context context)
        {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor spreferencesEditor = prefs.edit();
            spreferencesEditor.clear();
            spreferencesEditor.commit();
        }
}
