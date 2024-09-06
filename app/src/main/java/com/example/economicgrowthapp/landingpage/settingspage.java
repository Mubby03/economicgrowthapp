package com.example.economicgrowthapp.landingpage;

import static android.content.ContentValues.TAG;
import static com.example.economicgrowthapp.logins.login.SHARED_PREFS;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.dialogues.CustomDialog;
import com.example.economicgrowthapp.dialogues.CustomDialogfail;
import com.example.economicgrowthapp.logins.login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class settingspage extends AppCompatActivity {
    private final String myUri = "";
    private ConstraintLayout logoutButton;
    private TextView profile_username, profile_email;
    private ImageView profile_image;
    private Button select_image, save_Image;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Uri imagePath;
    private StorageReference storageProfilePicRef;
    private boolean uploadInProgress = true;
    //TODO: light and dark mode

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingspage);
        logoutButton = findViewById(R.id.logout_btn1);
        profile_email = findViewById(R.id.profile_email);
        profile_username = findViewById(R.id.profile_username);
        profile_image = findViewById(R.id.profile_image);
        select_image = findViewById(R.id.save_image);
        save_Image = findViewById(R.id.save_Image);
        save_Image.setEnabled(false);
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photointent = new Intent(Intent.ACTION_PICK);
                photointent.setType("image/*");
                startActivityForResult(photointent, 1);
            }
        });
        save_Image.setEnabled(false);
        // Add a check if the ImageView has an image
        profile_image.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (profile_image.getDrawable() != null) {
                    // Enable the button if the ImageView has an image
                    save_Image.setEnabled(true);
                } else {
                    // Disable the button if the ImageView is empty
                    save_Image.setEnabled(false);
                }
            }
        });

        save_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("chartData/user");
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            String googleUsername = extractGoogleUsername(userEmail);
            String Username = googleUsername;
            profile_username.setText(Username);
            profile_email.setText(userEmail);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a confirmation dialog
                new AlertDialog.Builder(settingspage.this).setTitle("Logout").setMessage("Are you sure you want to logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User confirmed logout, perform the logout actions
                                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", "");
                                editor.apply();

                                SharedPreferences sharedPreferences1 = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                                editor1.clear();
                                editor1.apply();
                                // FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No", null) // User canceled logout, do nothing
                        .show();
            }
        });
        loadImageFromFirebase();

    }

    private String extractGoogleUsername(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            String username = email.substring(0, atIndex);
            return username.replaceAll("\\d", ""); // Remove digits/numbers
        } else {
            // Handle the case where email doesn't contain '@'
            Log.e("ExtractUsername", "Invalid email format");
            return "";
        }
    }

    // to check if the request code is equal to one
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData(); // Assigning the selected image URI to imagePath
            getImageInImageView();
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profile_image.setImageBitmap(bitmap);
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString()).putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                updateProfilePic(task.getResult().toString());
                                CustomDialog customDialog = new CustomDialog(settingspage.this);
                                customDialog.show();
                                // Show the custom dialog on success
                            } else {
                                Log.d(TAG, "onComplete: failed to get download URL");
                            }
                        }
                    });
                    progressDialog.dismiss();
                } else {
                    CustomDialogfail customDialogfail = new CustomDialogfail(settingspage.this);
                    customDialogfail.show();
                    // Show the custom dialog on failure
                }
                progressDialog.dismiss();
            }

            private void updateProfilePic(String url) {
                FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profilePicture").setValue(url);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });

// Add a timeout to cancel the upload if it takes more than 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (uploadInProgress) {
                    // Cancel the upload task
                    CustomDialogfail customDialogfail = new CustomDialogfail(settingspage.this);
                    customDialogfail.show();
                    // Show the custom dialog on failure
                    progressDialog.dismiss();
                    uploadInProgress = false;
                }
            }
        }, 6000); // 10 seconds timeout
    }

    private void loadImageFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(user.getUid()).child("profilePicture");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String imageUrl = snapshot.getValue(String.class);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(settingspage.this).load(imageUrl).into(profile_image);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    CustomDialogfail customDialogfail = new CustomDialogfail(settingspage.this);
                    customDialogfail.show();
                    // Show the custom dialog on failure
                }
            });
        }
    }

}