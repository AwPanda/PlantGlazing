package com.example.plantglazing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button registerBtn;
    private EditText loginEmail;
    private EditText loginPass;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Pre-Load a bunch of resource ids so i can use them for later
        loginBtn = findViewById(R.id.lgBtn);
        loginEmail = findViewById(R.id.emailText);
        loginPass = findViewById(R.id.passText);
        registerBtn = findViewById(R.id.lgRgBtn);

        // Firebase auth - Used for user login
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Set on click listener on the login button which will then potentially call the user login method
        loginBtn.setOnClickListener(v -> {

            System.out.print(v);
            if (checkNetworkConnection()) {
                // Call login function
                UserLogin();
            } else {
                // Return toast to user letting them know that they are not connected to internet
                // Since my applicaiton is primarly internet based i should give the user an error alteast to let them know!
                Toast.makeText(LoginActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });

        registerBtn.setOnClickListener(v -> {
            // call the OpenRegister method
            OpenRegister();
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();



        if (currentUser != null) {
            // Get current userID
            String userId = currentUser.getUid();

            // Get user documents to check if the user is an admin
            // If he is an admin we want to redirect to adminactivity rather than mainactivity


            MainRedirect();
        }
    }

    /*
    All used resources are disposed properly. The Listeners are removed here.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginBtn.setOnClickListener(null);
        registerBtn.setOnClickListener(null);
    }

    //Function to close the current activity and open the 'Main Activity'
    private void MainRedirect() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //Function to close the current activity and open the 'Register Activity'
    public void OpenRegister() {
        // Open up the registeractivity
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void UserLogin() {

        // Get all the text values so we can check them
        String userEmail = loginEmail.getText().toString();
        String userPassword = loginPass.getText().toString();

        if (TextUtils.isEmpty(userEmail) | TextUtils.isEmpty(userPassword)) {

            // If the user has not filled in information correct we need to let them know!
            Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
        } else {
            // Sign in with email and password firebase
            fAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // This is used for setting data in firestore
                            Map<String, Object> docData = new HashMap<>();
                            docData.put("lastlogin_timestamp", LocalDateTime.now());

                            // Get userId from firebase as we want to create a document for that user id
                            String userId = fAuth.getUid();

                            // Write data to user collection and make a new document with userid
                            // The data we are writing is a lastlogin_timestamp
                            db.collection("users").document(userId)
                                    .set(docData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            // Let the user know they mangaged to login succesfully!
                            Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();

                        } else {
                            String message = task.getException().getMessage(); // Get error exception

                            // Display error to user letting them know why they couldn't login :(
                            Toast.makeText(LoginActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
