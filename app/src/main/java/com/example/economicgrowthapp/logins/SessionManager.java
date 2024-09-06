package com.example.economicgrowthapp.logins;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    //shared prefrence for login

    //variables
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session names
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";
//userSession variables
    private static final String Is_Login = "IsLoggedIn";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";


    //rmen=mber me variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONEMAIL = "email";
    public static final String KEY_SESSIONPASSWORD = "eassword";


    //create a constructor
    public SessionManager(Context _context, String sessionName) {
        //here you can declear different sessions for different pages, including the onBoarding screen
        // the login and other things you feel like saving. Just follow this way if you want to do it for the onoadring screen
        context = _context;
        userSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();

    }

    public void createLogInSession(String email) {
        //store a boolean in the session
        //this gets the session so basically it could be true
        editor.putBoolean(Is_Login, true);
        //the data is being stored in a key and valuepair
        editor.putString(KEY_EMAIL, email);
        //commit to store.
        editor.commit();

    }


    // to get user detail from the session
    public HashMap<String, String> getUserDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        // get the stored data from above
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    //check if the user is loggedin or not
    public boolean checkLogin() {
        return userSession.getBoolean(Is_Login, false);
    }

    public void logourUserFromSession() {
        editor.clear();
        editor.commit();
    }






    //remember me session functions

    public void createRememberMeSession(String email, String password) {
        //store a boolean in the session
        //this gets the session so basically it could be true
        editor.putBoolean(IS_REMEMBERME, true);
        //the data is being stored in a key and valuepair
        editor.putString(KEY_SESSIONEMAIL, email);
        editor.putString(KEY_SESSIONPASSWORD, password);
        //commit to store.
        editor.commit();

    }


    public HashMap<String, String> getRememberMeFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();
        // get the stored data from above
        userData.put(KEY_SESSIONEMAIL, userSession.getString(KEY_SESSIONEMAIL, null));
        userData.put(KEY_SESSIONPASSWORD, userSession.getString(KEY_SESSIONPASSWORD, null));

        return userData;
    }
    public boolean checkRememberMe() {
        return userSession.getBoolean(IS_REMEMBERME, false);
    }
}