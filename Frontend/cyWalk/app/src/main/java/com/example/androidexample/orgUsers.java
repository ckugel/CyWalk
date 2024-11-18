package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class orgUsers extends AppCompatActivity {

    private Button usersButton;
    private Button leaderboardButton;
    private Button goalsButton;
    private Button profileButton;
    private String key;

    private static String URL_SET_GOAL = null;

    private String newFriendUsername;
    private String acceptFriendUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orgusers);
        usersButton = findViewById(R.id.usersButton);
        leaderboardButton = findViewById(R.id.leaderboardButton);
        goalsButton = findViewById(R.id.goalsButton);
        profileButton = findViewById(R.id.profileButton);

        Bundle extras = getIntent().getExtras();
        key = extras.getString("key");

        //URL_SET_GOAL = "http://10.0.2.2:8080/friends/"+key;

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orgUsers.this, orgUsers.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orgUsers.this, orgLeaderboards.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orgUsers.this, orgSetGoals.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(orgUsers.this, OrgProfile.class);
                intent.putExtra("key", key);
                startActivity(intent);
            }
        });

        //setNewGoalReq();
    }
    private void setNewGoalReq() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL_SET_GOAL,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

}