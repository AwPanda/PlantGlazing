package com.example.plantglazing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button registerBtn;
    private EditText loginEmail;
    private EditText loginPass;
    private FirebaseAuth fbAuth;

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
        fbAuth = FirebaseAuth.getInstance();


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

    //Function to close the current activity and open the 'Register Activity'
    public void OpenRegister() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
        finish();
    }

    private void UserLogin() {
        String userEmail = loginEmail.getText().toString();
        String userPassword = loginPass.getText().toString();
        if (TextUtils.isEmpty(userEmail) | TextUtils.isEmpty(userPassword)) {
            // If the user has not filled in information correct we need to let them know!
            Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show();
        } else {

            fbAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
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
