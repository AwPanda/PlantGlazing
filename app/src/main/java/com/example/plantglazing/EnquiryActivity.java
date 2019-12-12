package com.example.plantglazing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EnquiryActivity extends AppCompatActivity {

    // Sell all the variables we need up
    private Button subBtn;
    private Button backBtn;
    private Button locationBtn;
    private EditText enqName, enqEmail, enqPhone, enqLocation, enqText;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        setTitle("Enquiry Form"); // Set App title to enquiry form

        requestPermission(); // Request user location

        // Get submit btn resource
        subBtn = findViewById(R.id.btnSubmitEnq);
        locationBtn = findViewById(R.id.btnLocation);
        backBtn = findViewById(R.id.btnBack);
        enqName = findViewById(R.id.enqFullname);
        enqEmail = findViewById(R.id.enqEmail);
        enqPhone = findViewById(R.id.enqPhone);
        enqLocation = findViewById(R.id.enqLocation);
        enqText = findViewById(R.id.enqMsg);

        // get location services
        client = LocationServices.getFusedLocationProviderClient(this);

        // Get fire base instance
        fAuth = FirebaseAuth.getInstance();
        // Get firestore instance
        db = FirebaseFirestore.getInstance();

        // OnClick listener for submiting the enquiry
        subBtn.setOnClickListener( v -> {
            // Check internet connection
            if (checkNetworkConnection()) {
                // Call submitenquiry method
                SubmitEnquiry();
            } else {
                Toast.makeText(EnquiryActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        // OnClick listener for getting the users location
        locationBtn.setOnClickListener( v -> {
            // Check internet connection
            if (checkNetworkConnection()) {

                if(ActivityCompat.checkSelfPermission(EnquiryActivity.this, ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EnquiryActivity.this, "Permission isn't granted for location!", Toast.LENGTH_SHORT).show();
                }
                client.getLastLocation().addOnSuccessListener(EnquiryActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location userLocation) {
                        if(userLocation != null)
                        {
                            // Convert latitude and longitude into postcode
                            // Makes it easier for finding location
                            String postcode = getRegionName(userLocation.getLatitude(), userLocation.getLongitude());
                            // Set location text to the location found!
                            enqLocation.setText(postcode);
                        }
                        else
                        {
                            // If location is null display this
                            Toast.makeText(EnquiryActivity.this, "No Location Found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(EnquiryActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        backBtn.setOnClickListener( v -> {
            // Go Back to mainactivity
            Intent mainIntent = new Intent(EnquiryActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        });
    }

    private String getRegionName(double lati, double longi) {
        String regioName = "";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lati, longi, 1);
            if (addresses.size() > 0) {
                regioName = addresses.get(0).getPostalCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regioName;
    }

    // Used for the location permission
    // Gets called when EnquiryActivity first starts!
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private void SubmitEnquiry() {
        // Used to determine if the enquiry is good to save to firestore
        boolean isOk = true;

        // Get text from all fields then check to see if valid
        String name = enqName.getText().toString();
        String email = enqEmail.getText().toString();
        String phone = enqPhone.getText().toString();
        String location = enqLocation.getText().toString();
        String message = enqText.getText().toString();

        // Go through checks of all the input fields to make sure they are not empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Full Name is invalid!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email Address is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone Number is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Location is invalid", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Enquiry Message is invalid", Toast.LENGTH_SHORT).show();
        }
        else {
            // Get current firebase user
            FirebaseUser user = fAuth.getCurrentUser();

            // Get userId from firebase as we want to create a document for that user id
            String userId = user.getUid();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();

            // This is used for setting data in firestore
            Map<String, Object> docData = new HashMap<>();
            docData.put("userId", userId);
            docData.put("name", name);
            docData.put("email", email);
            docData.put("phone", phone);
            docData.put("location", location);
            docData.put("enquirytext", message);
            docData.put("DateSent", dateFormat.format(date));

            // Get random string for storing enquiry
            String generatedString =  getSaltString();

            // Write data to the enquiry collection
            // We are going to save the userid on the enquiry just incase i ever need to reference it
            // This app(hopefully) will be going to a real client so some features are implement for future changes!
            db.collection("enquiries").document(generatedString)
                    .set(docData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EnquiryActivity.this, "Enquiry Created Successfully!", Toast.LENGTH_SHORT).show();

                            MainRedirect(); // Call this method and go back to main activity
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EnquiryActivity.this, "Error when creating enquiry!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    // Redirect to main activity
    private void MainRedirect() {
        Intent mainIntent = new Intent(EnquiryActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    // Method to produce a random string using letters and number
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

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
