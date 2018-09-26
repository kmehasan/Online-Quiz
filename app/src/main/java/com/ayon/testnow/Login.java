package com.ayon.testnow;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private static final int RC_SIGN_IN = 9001;
    public static String uid;
    public static boolean isAdmin = false;
    public static String name = "";

    public static FirebaseUser user;
    TextView data;
    // [END declare_auth]
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button signIn, signOut;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END onactivityresult]
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideProgressDialog();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Signing in please wait");
        signIn = findViewById(R.id.sign_in);
        //signOut = findViewById(R.id.sign_out);
//        signOut.setEnabled(false);
        ImageView imageView = findViewById(R.id.imageView);
        ImageView sust = findViewById(R.id.sust);

        Glide.with(this).load(R.drawable.common_full_open_on_phone).into(imageView);
        Glide.with(this).load(R.drawable.googleg_disabled_color_18).into(sust);

        data = findViewById(R.id.data);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        if (!isNetworkAvailable()) {
            signIn.setVisibility(View.VISIBLE);
        }
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && isNetworkAvailable()) {
//            signIn(null);
            return;
        }
        if (!isNetworkAvailable()) {
            signIn.setVisibility(View.VISIBLE);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
//                signOut.setEnabled(true);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        signIn(null);
        hideProgressDialog();
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            checkIsNewUser(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void checkIsNewUser(final FirebaseUser user) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getString(R.string.db_key_users));
        uid = user.getUid();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        sharedPref.edit().putString(getString(R.string.uid), uid).apply();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(uid).exists()) {
                    finish();
                    hideProgressDialog();
                    startActivity(new Intent(Login.this, SignUpActivity.class));
                } else {
                    if (dataSnapshot.child(uid).child(getString(R.string.admin)).exists()) {
                        isAdmin = (boolean) dataSnapshot.child(uid).child(getString(R.string.admin)).getValue();
                    } else isAdmin = false;
                    updateUI(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name, email;
            name = user.getDisplayName();
            Login.name = name;
            email = user.getEmail();
            data.setText("Name : " + name + "\nEmail : " + email);
            Log.d(TAG, "updateUI: active");
            hideProgressDialog();
            finish();
            if(isAdmin)
                startActivity(new Intent(this, TeacherMainActivity.class));
            else startActivity(new Intent(this, MainActivity.class));
        } else data.setText("Logged Out");
        hideProgressDialog();

    }

    private void hideProgressDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: ");
        dialog.show();

    }

    // [END auth_with_google]
    public void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        user = mAuth.getCurrentUser();
        if (user == null)
            startActivityForResult(signInIntent, RC_SIGN_IN);
        else {
            showProgressDialog();
            checkIsNewUser(user);
        }
    }

    public void signOut(View view) {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
//                        signOut.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
