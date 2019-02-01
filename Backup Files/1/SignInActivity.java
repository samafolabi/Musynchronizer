package com.imperial.musynchronizer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final String TAG = SignInActivity.class.getSimpleName();
    EditText emailField, passwordField;
    String emailFieldText, passwordFieldText;
    ProgressBar progressBar;
    Button button;
    String app_server_url = "https://musynchronizerserver.000webhostapp.com/musynchronizer/musynchronizer_insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        Button signIn = (Button) findViewById(R.id.signIn);
        Button signUp = (Button) findViewById(R.id.signUp);
        /*signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailField = (EditText) findViewById(R.id.emailField);
                passwordField = (EditText) findViewById(R.id.passwordField);
                emailFieldText = emailField.getText().toString();
                passwordFieldText = passwordField.getText().toString();
                createAccount(emailFieldText, passwordFieldText);
               //startNextActivity();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailField = (EditText) findViewById(R.id.emailField);
                passwordField = (EditText) findViewById(R.id.passwordField);
                emailFieldText = emailField.getText().toString();
                passwordFieldText = passwordField.getText().toString();
                signIn(emailFieldText, passwordFieldText);
                //startNextActivity();
            }
        });*/
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                // [END subscribe_topics]

                // Log and toast
                String msg = getString(R.string.msg_subscribed);
                Log.d(TAG, msg);
                Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        button = (Button) findViewById(R.id.regToken);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fcm_token = FirebaseInstanceId.getInstance().getToken();
                Map<String,String> params = new HashMap<>();
                params.put("fcm_token", fcm_token);

                if (isOnline(SignInActivity.this) == true) {
                    Log.d(TAG, "Samson Online" + fcm_token);
                    new HttpServiceHandler().execute(app_server_url, params);
                }

                /*try {
                    String response = sendPostData(app_server_url, params);
                    Log.d(TAG, "Samson " + fcm_token);
                    Log.d(TAG, "Samson " + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                //final String token = sharedPreferences.getString(getString(R.string.FCM_TOKEN),FirebaseInstanceId.getInstance().getToken());
                //final TokenClass tokenClass = new TokenClass(SignInActivity.this);
                //String token = tokenClass.getToken();
                final String token = FirebaseInstanceId.getInstance().getToken();
                RequestQueue queue = Volley.newRequestQueue(SignInActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "error");
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("fcm_token",token);

                        return params;
                    }
                };
                MySingleton.getMyInstance(SignInActivity.this).addToRequestQueue(stringRequest);
                Log.d(TAG, "REG TOKEN" + token);
            }
        });*/

    }

    public void sendTokenToServer () {
        //DefaultHttp
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void createAccount (String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword (email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        /*Log.w(TAG, "create: ", task.getException());

                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();*/
                        try {
                            throw task.getException();
                        } catch(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        startNextActivity();
                    }
                }
            });
    }
    private void signIn (String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    private String getUserInfo () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        if (user != null) {
            // Name, email address, and profile photo Url
            //String name = user.getDisplayName();
            email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            //TextView textView = (TextView) findViewById(R.id.userEmail);
            //textView.setText(email);
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            //String uid = user.getUid();
        }
        return email;
    }
    private void startNextActivity () {
        // Currently in MainActivity
        String email = getUserInfo();
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("email", email);
        SignInActivity.this.startActivity(intent);
    }

    /*private String getPostDataString (HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String,String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry))
        }
    }*/

    private String sendPostData (String URL, Map<String, String> params) throws Exception {
        URL url = new URL(URL);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        StringBuilder stringBuilder = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            stringBuilder.append((char) c);
        String response = stringBuilder.toString();

        return response;
    }

    /*@Override
    public void onTokenRefresh() {
        // Get updated InstanceID token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);
    }*/

    class HttpServiceHandler extends AsyncTask<Object, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            progressBar.setMax(10);
        }

        @Override
        protected String doInBackground(Object... oParams) {
            HttpURLConnection conn = null;
            URL url = null;
            Map <String, String> params;
            String response = "failed";

            try {
                url = new URL((String) oParams[0]);
                params = (Map<String, String>) oParams[1];

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder stringBuilder = new StringBuilder();
                for (int c; (c = in.read()) >= 0; )
                    stringBuilder.append((char) c);
                response = stringBuilder.toString();
            } catch (Exception e) {
                Log.e(TAG, "Samson Exception: ", e);
                e.printStackTrace();
                return null;
            } finally {
                if (conn == null) {
                    conn.disconnect();
                }

                publishProgress(10);
                Log.d(TAG, response);
                return response;
            }
        }

        @Override
        protected void onProgressUpdate (Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute (String response) {
            super.onPostExecute(response);
            Log.d(TAG, "Samson " +response);
        }
    }
}
