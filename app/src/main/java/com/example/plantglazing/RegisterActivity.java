package com.example.plantglazing;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // Firebase variables

    private DatabaseReference myRef;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;



    // Variables for text, buttons etc...
    private Button registerBtn;
    private Button loginBtn;
    private EditText regFullName;
    private EditText regEmail;
    private EditText regPass;
    private EditText regConfirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Register Account");

        // Setup resource ids so we can reference them later
        registerBtn = findViewById(R.id.rgSubmit);
        loginBtn = findViewById(R.id.rgSignInHere);
        regFullName = findViewById(R.id.regFullnameText);
        regEmail = findViewById(R.id.regEmailText);
        regPass = findViewById(R.id.regPassText);
        regConfirmPass = findViewById(R.id.regConfirmPassText);

        // Get fire base instance
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn.setOnClickListener( v -> {
            if (checkNetworkConnection()) {
                RegisterAccount();
            } else {
                Toast.makeText(RegisterActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void RegisterAccount() {

        // Get user entered data
        String email = regEmail.getText().toString();
        String password = regPass.getText().toString();
        String confirmPassword = regConfirmPass.getText().toString();
        String fullname = regFullName.getText().toString();

        // Check all input fields to make sure they are correct when submited
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email address is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(this, "Full Name is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Confirm Password is invalid", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {

            // Create user with email and password firebase method
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

// This is used for setting data in firestore
                        Map<String, Object> docData = new HashMap<>();
                        docData.put("role_admin", false); // User is not an admin!
                        docData.put("user_firstname", fullname); // Set users full name in firestore

                        FirebaseUser user = fAuth.getCurrentUser(); // Get current user

                        // Get userId from firebase as we want to create a document for that user id
                        String userId = user.getUid();

                        // Write data to user collection and make a new document with userid
                        // The data we are writing is a role_admin, user_firstname
                        db.collection("users").document(userId)
                                .set(docData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "User details successfully created in database!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Something went wrong with writing user details to database!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Toast.makeText(RegisterActivity.this, "Account Registered", Toast.LENGTH_SHORT).show();
                        MainRedirect(); // Redirect to mainactivity
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Occured, Please Try Again" + message, Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        // Check to see if user is already logged in
        if (currentUser != null) {
            MainRedirect();
        }
    }

    //Function to close the current activity and open the 'Main Activity'
    private void MainRedirect() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    /*
    All used resources are disposed properly. The Listeners are removed here.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerBtn.setOnClickListener(null);
        loginBtn.setOnClickListener(null);

    }
    // Used to check if the user is created to the internet
    public boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        return isConnected;
    }
}

