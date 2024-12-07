package com.example.androidexample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Friends page for the users to see
 */
public class Friends extends AppCompatActivity {

    private Button backButton;
    private Button friendsSubmitButton;
    private Button acceptFriendSubmitButton;
    private EditText friendUsername;
    private EditText acceptedFriendUsername;
    private TextView newFriendTitle;
    private String key;
    private LinearLayout friendTable;
    private LinearLayout requestsTable;
    private CardView profileCard;
    private ImageView profilePicture;
    private TextView profileUsername;
    private TextView profileDistance;
    private RelativeLayout profileOverlay; // Overlay for the profile card

    // URLs for the API requests
    private static String URL_JSON_FRIENDS = null;
    private static String URL_JSON_PENDING = null;
    private static String URL_NEW_FRIEND = null;
    private static String URL_ACCEPT_FRIEND = null;

    private String newFriendUsername;
    private String acceptFriendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends); // Load the layout

        // Initialize views
        backButton = findViewById(R.id.returnButton);
        friendsSubmitButton = findViewById(R.id.friendSubmitBtn);
        acceptFriendSubmitButton = findViewById(R.id.acceptFriendSubmitBtn);
        friendTable = findViewById(R.id.friendsTable);
        requestsTable = findViewById(R.id.requestsTable);
        friendUsername = findViewById(R.id.entryUsername);
        acceptedFriendUsername = findViewById(R.id.acceptedFriendUsername);
        newFriendTitle = findViewById(R.id.newFriendTile);
        profileCard = findViewById(R.id.profileCard);
        profilePicture = findViewById(R.id.profilePicture);
        profileUsername = findViewById(R.id.profileUsername);
        profileDistance = findViewById(R.id.profileDistance);
        profileOverlay = findViewById(R.id.profileOverlay); // Initialize overlay

        Bundle extras = getIntent().getExtras();
        key = extras.getString("key");

        // Set the URLs for the API calls
        URL_JSON_FRIENDS = "http://coms-3090-072.class.las.iastate.edu:8080/friends/" + key;
        URL_JSON_PENDING = "http://coms-3090-072.class.las.iastate.edu:8080/friends/requests/" + key;

        // Return button click listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Friends.this, Social.class);
            intent.putExtra("key", key);
            startActivity(intent);
        });

        // Friend submit button
        friendsSubmitButton.setOnClickListener(view -> {
            newFriendUsername = friendUsername.getText().toString();
            URL_NEW_FRIEND = "http://coms-3090-072.class.las.iastate.edu:8080/friends/" + key + "/request/" + newFriendUsername;
            makeFriendRequest();
        });

        // Accept friend submit button
        acceptFriendSubmitButton.setOnClickListener(view -> {
            acceptFriendUsername = acceptedFriendUsername.getText().toString();
            URL_ACCEPT_FRIEND = "http://coms-3090-072.class.las.iastate.edu:8080/friends/" + key + "/request/approve/" + acceptFriendUsername;
            makeFriendApproval();
        });

        // Handle clicks on the profile card to close it
        profileCard.setOnClickListener(v -> profileCard.setVisibility(View.GONE));

        // Handle clicks outside of the profile card to close the overlay
        profileOverlay.setOnClickListener(v -> closeProfileOverlay());

        // Fetch friends and pending requests
        makeJsonFriendReq();
        makeJsonPendingReq();
    }

    // Function to show the profile card
    private void showProfileCard(String username) {
        // Make the overlay and profile card visible
        profileOverlay.setVisibility(View.VISIBLE);
        profileCard.setVisibility(View.VISIBLE);

        // Set the profile details (You can update this to show actual data)
        profileUsername.setText("Username: " + username);
        profileDistance.setText("Distance:\n" + profileDistance); // Example static distance
    }

    // Function to close the profile overlay
    private void closeProfileOverlay() {
        profileOverlay.setVisibility(View.GONE); // Hide the overlay
        profileCard.setVisibility(View.GONE); // Hide the profile card
    }

    /**
     * Gets the list of current friends for a user from the database using a volley request
     */
    private void makeJsonFriendReq() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL_JSON_FRIENDS,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        if (response.length() > 0) {
                            friendTable.removeAllViews();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    String current = response.getString(i);
                                    TextView tempText = new TextView(Friends.this);
                                    tempText.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    tempText.setTextSize(20);
                                    tempText.setTextColor(Color.parseColor("#000000"));
                                    tempText.setText(current);

                                    // Handle clicks on friend's name to show profile
                                    tempText.setOnClickListener(v -> showProfileCard(tempText.getText().toString()));

                                    friendTable.addView(tempText);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    /**
     * Gets the list of current pending friend requests for a user from the database using a volley request
     */
    private void makeJsonPendingReq() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL_JSON_PENDING,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        if (response.length() > 0) {
                            requestsTable.removeAllViews();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    String current = response.getString(i);
                                    TextView tempText = new TextView(Friends.this);
                                    tempText.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    tempText.setTextSize(20);
                                    tempText.setTextColor(Color.parseColor("#000000"));
                                    tempText.setText(current);

                                    requestsTable.addView(tempText);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    /**
     * Sends a friend request to the username stored in the newFriendUsername variable
     */
    private void makeFriendRequest() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                URL_NEW_FRIEND,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        friendUsername.setText("");
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    /**
     * Approves a friend request from the username stored in the acceptedFriendUsername variable
     */
    private void makeFriendApproval() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT,
                URL_ACCEPT_FRIEND,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        acceptedFriendUsername.setText("");
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}
