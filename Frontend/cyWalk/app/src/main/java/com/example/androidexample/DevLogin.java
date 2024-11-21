package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Login page for all users to see upon opening the app
 * */
public class DevLogin extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private TextView errorMsg;
    private TextView orgSwitch;
    private Button loginButton;         // define login button variable
    private static String URL_LOGIN = "http://10.0.2.2:8080/users";
    private static String URL_SIGNUP = "http://10.0.2.2:8080/signup";
    private String key = "";
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devlogin);            // link to Login activity XML

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        errorMsg = findViewById(R.id.errorMsg);
        orgSwitch = findViewById(R.id.switchOrgView);

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                try {
                    makeLoginReq();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        orgSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DevLogin.this, OrgLogin.class);
                startActivity(intent);
            }
        });
    }

    /**
     * attempts to log the user in using the inputted username and password if correct will  switch their view
     * to the dashboard if incorrect will throw an error and send a message to the user to let them know that something
     * was wrong.
     */
    private void makeLoginReq() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);

        final String requestBody = jsonObject.toString();

        //errorMsg.setText(requestBody);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT, URL_LOGIN, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        try {
                            // Parse JSON object data
                            key = response.getString("key");
                            //extraMsg.setText("working " + userKey);
                            if(!key.isEmpty()) {
                                Intent intent = new Intent(DevLogin.this, Dashboard.class);
                                intent.putExtra("key", key);
                                //errorMsg.setText("success " + key);
                                startActivity(intent);
                            } else {
                                //errorMsg.setText("failed " + userKey);
                                errorMsg.setText("Error invalid username/password");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        errorMsg.setText("an error has occured please try again later");
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }


}