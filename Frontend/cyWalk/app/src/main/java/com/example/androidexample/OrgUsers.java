package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The organizations view that shows them all users in their organizations
 * */
public class OrgUsers extends AppCompatActivity {

    private Button usersButton;
    private Button leaderboardButton;
    private Button goalsButton;
    private Button profileButton;
    private String key;
    private String orgId="";
    private TextView usersText;

    private static String URL_ORG_USERS = null;

    private String newFriendUsername;
    private String acceptFriendUsername;

    /**
     * creates the page that shows the organization all the users included in their organization
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orgusers);
        usersText = findViewById(R.id.usersText);

        Bundle extras = getIntent().getExtras();
        key = extras.getString("key");
        orgId = extras.getString("orgId");

        URL_ORG_USERS = "http://10.0.2.2:8080/organizations/"+orgId+"/users";

        // NAVIGATION BAR
        BottomNavigationView botnav = findViewById(R.id.orgbottomNavigation);
        botnav.setSelectedItemId(R.id.nav_social);
        botnav.setOnItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.nav_org_users) {
                intent = new Intent(OrgUsers.this, OrgUsers.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.nav_org_goals) {
                intent = new Intent(OrgUsers.this, OrgSetGoals.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.nav_org_social) {
                intent = new Intent(OrgUsers.this, OrgLeaderboards.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.nav_org_profile) {
                intent = new Intent(OrgUsers.this, OrgProfile.class);
                intent.putExtra("key", key);
                startActivity(intent);
                finish();
                return true;
            }
            else {
                return false;
            }
        });

        getUsersReq();
    }

    /**
     * gets the list of all uses that are in the organization. Takes the returned list of person objects and just prints out their
     * usernames to the screen
     */
    private void getUsersReq() {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL_ORG_USERS,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        //usersText.setText(response.toString());

                        try {
                            for(int j = 0; j<response.length(); j++) {
                                JSONObject user = response.getJSONObject(j);
                                String username = user.getString("username");

                                String current = usersText.getText().toString() + username + "\n";
                                usersText.setText(current);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

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