package com.example.plantglazing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity  {

    private Button logoutBtn;
    private Button enquiryBtn;
    private Button websiteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Menu");

        // Setup resource ids so we can reference them later
        logoutBtn = findViewById(R.id.btnLogout);
        enquiryBtn = findViewById(R.id.btnEnquiry);
        websiteBtn = findViewById(R.id.btnWebsite);

        enquiryBtn.setOnClickListener( v -> {
            if (checkNetworkConnection()) {
                EnquiryStart();
            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        logoutBtn.setOnClickListener( v -> {
            if (checkNetworkConnection()) {
                Logout();
            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        websiteBtn.setOnClickListener( v -> {
            if (checkNetworkConnection()) {
                ViewWebsite();
            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Function to close the current activity and open the 'EnquiryActivity'
    private void EnquiryStart() {
        Intent enqIntent = new Intent(MainActivity.this, EnquiryActivity.class);
        enqIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(enqIntent);
        finish();
    }

    //Function to close the current activity and open the 'EnquiryActivity'
    private void ViewWebsite() {
        Intent webIntent = new Intent(MainActivity.this, WebviewActivity.class);
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(webIntent);
        finish();
    }

    // Logout user from firebase and redirect to loginactivity
    private void Logout() {
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        FirebaseAuth.getInstance().signOut(); // Sign out user
        startActivity(logoutIntent);
        finish();
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
