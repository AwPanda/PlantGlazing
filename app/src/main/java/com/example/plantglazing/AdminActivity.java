package com.example.plantglazing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminActivity extends AppCompatActivity {

    // Create variables we need for later
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference enquiriesRef = db.collection("enquiries");
    private TextView textView;
    private Button btnLogout, btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Admin Menu - All enquries");

        // Get views by id
        textView = findViewById(R.id.text_view_data);
        btnLogout = findViewById(R.id.adminLogout);
        btnLoad = findViewById(R.id.adminLoad);

        btnLogout.setOnClickListener( v -> {
            if (checkNetworkConnection()) {
                Logout();
            } else {
                Toast.makeText(AdminActivity.this, "No Internet Connection, Please Connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logout user from firebase and redirect to loginactivity
    private void Logout() {
        Intent logoutIntent = new Intent(AdminActivity.this, LoginActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        FirebaseAuth.getInstance().signOut(); // Sign out user
        startActivity(logoutIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get all the enquiries from the firestore database
        enquiriesRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                // Set data as string
                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Enquiry enquiry = documentSnapshot.toObject(Enquiry.class);

                    // Go through the enquiry object and get all details related to the enquiry
                    String userid = enquiry.getuserId();
                    String fullname = enquiry.getName();
                    String email = enquiry.getEmail();
                    String phone = enquiry.getPhone();
                    String postcode = enquiry.getLocation();
                    String message = enquiry.getEnquirytext();
                    String DateSent = enquiry.getDateSent();


                    data += "User ID: " + userid
                            + "\nFull Name: " + fullname + "\nEmail Address: " + email
                            + "\nPhone: " + phone + "\nPostcode: " + postcode
                            + "\nDate: " + DateSent + "\n"
                            + "\nMessage: " + message + "\n\n";
                }

                textView.setText(data);
            }
        });
    }

    // Called from the load button
    // This will load enquiries incase there has been more created when the admin has logged on
    // I don't want them to have to logout then back in to get enquiries!
    public void loadEnquiries(View v) {
        enquiriesRef
                .orderBy("DateSent", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Enquiry enquiry = documentSnapshot.toObject(Enquiry.class);

                            // Go through the enquiry object and get all details related to the enquiry
                            String userid = enquiry.getuserId();
                            String fullname = enquiry.getName();
                            String email = enquiry.getEmail();
                            String phone = enquiry.getPhone();
                            String postcode = enquiry.getLocation();
                            String message = enquiry.getEnquirytext();
                            String DateSent = enquiry.getDateSent();


                            data += "User ID: " + userid
                                    + "\nFull Name: " + fullname + "\nEmail Address: " + email
                                    + "\nPhone: " + phone + "\nPostcode: " + postcode
                                    + "\nDate: " + DateSent + "\n"
                                    + "\nMessage: " + message + "\n\n";
                        }

                        textView.setText(data);
                    }
                });
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
