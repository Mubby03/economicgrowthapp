package com.example.economicgrowthapp.logins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.dialogues.CustomDialog;
import com.example.economicgrowthapp.dialogues.CustomDialogfail;
import com.example.economicgrowthapp.landingpage.landing_page;
import com.example.economicgrowthapp.landingpage.settingspage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class google_login extends AppCompatActivity {
    private final static int RC_SIGN_IN = 123;
    private static final String SHARED_PREFS_GOOGLE = "sharedPrefs";
    private static final String KEY_GOOGLE_LOGIN = "google_login";
    CardView google_signIn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ImageView facebook_btn, twitter_btn;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && checkGoogleLogin()) {
            goToLandingPage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        google_signIn = findViewById(R.id.google_signIn);

        google_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        createRequest();

        // Check Google login and redirect to landing page
        if (checkGoogleLogin()) {
            goToLandingPage();
        }
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("AIzaSyDf5b-3OVEwt7nYXMSYykbekBF8Fnc1tMk")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveGoogleLoginPreference();
                    CustomDialog customDialog = new CustomDialog(google_login.this);
                    customDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToLandingPage();
                        }
                    }, 3000);
                } else {
                    CustomDialogfail customDialogfail = new CustomDialogfail(google_login.this);
                    customDialogfail.show();
                }
            }
        });
    }

    private void saveGoogleLoginPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_GOOGLE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_GOOGLE_LOGIN, true);
        editor.apply();
    }

    private boolean checkGoogleLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_GOOGLE, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_GOOGLE_LOGIN, false);
    }

    private void goToLandingPage() {
        Intent intent = new Intent(getApplicationContext(), landing_page.class);
        startActivity(intent);
        finish();
    }
}
