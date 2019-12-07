package com.example.plantglazing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
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

        // Setup resource ids so we can reference them later
        registerBtn = findViewById(R.id.rgSubmit);
        loginBtn = findViewById(R.id.rgSignInHere);
        regFullName = findViewById(R.id.regFullnameText);
        regEmail = findViewById(R.id.regEmailText);
        regPass = findViewById(R.id.regPassText);
        regConfirmPass = findViewById(R.id.regConfirmPassText);

        // Get fire base instance
        fAuth = FirebaseAuth.getInstance();

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

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email address is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Confirm Password is invalid", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {

            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Toast.makeText(RegisterActivity.this, "Account Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(RegisterActivity.this, "Error Occured, Please Try Again" + message, Toast.LENGTH_SHORT).show();
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

