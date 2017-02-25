package cit.edu.paloma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<AuthResult> {
    private static final int RC_GOOGLE_SIGN_IN = 0;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private Button mGooglePlusButton;
    private Button mFacebookButton;
    private Button mTwitterButton;
    private Button mGithubButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ConstraintLayout mWaitingLayout;
    private ConstraintLayout mSignInLayout;
    private String mServiceName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    private void initViews() {
        mGooglePlusButton = (Button) findViewById(R.id.google_plus_button);
        mFacebookButton = (Button) findViewById(R.id.facebook_button);
        mTwitterButton = (Button) findViewById(R.id.twitter_button);
        mGithubButton = (Button) findViewById(R.id.github_button);
        mWaitingLayout = (ConstraintLayout) findViewById(R.id.waiting_layout);
        mSignInLayout = (ConstraintLayout) findViewById(R.id.sign_in_layout);

        mGooglePlusButton.setOnClickListener(this);
        mFacebookButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        mGithubButton.setOnClickListener(this);
    }

    private void showWaitingScreen(boolean yes) {
        if (yes) {
            mSignInLayout.setVisibility(View.INVISIBLE);
            mWaitingLayout.setVisibility(View.VISIBLE);
        } else {
            mSignInLayout.setVisibility(View.VISIBLE);
            mWaitingLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_plus_button:
                signInWithGoogle();
                mServiceName = "google";
                break;
            case R.id.facebook_button:
                mServiceName = "facebook";
                break;
            case R.id.twitter_button:
                mServiceName = "twitter";
                break;
            case R.id.github_button:
                mServiceName = "github";
                break;
        }
    }

    private void signInWithGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_GOOGLE_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                firebaseSignInWithGoogle(result);
                break;
        }
    }

    private void firebaseSignInWithGoogle(GoogleSignInResult result) {
        showWaitingScreen(true);
        if (!result.isSuccess()) {
            serviceSignInFailed(result);
        } else {
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential cred = GoogleAuthProvider
                    .getCredential(account.getIdToken(), null);
            FirebaseAuth
                    .getInstance()
                    .signInWithCredential(cred)
                    .addOnCompleteListener(this);
        }
    }

    private void serviceSignInFailed(Result result) {
        new AlertDialog.Builder(this, R.style.WaitingTheme)
                .setIcon(R.mipmap.ic_failed)
                .setTitle("Sign-in failed")
                .setMessage(result.getStatus().getStatusMessage())
                .show();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = firebaseAuth.getCurrentUser();
        if (mUser != null) {
            Log.v(TAG, mUser.getDisplayName());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Log.v(TAG, "user is null");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        showWaitingScreen(false);
        if (!task.isSuccessful()) {
            Exception exception = task.getException();
            String message = "Cannot sign in with your " + mServiceName + " account";
            if (exception != null) {
                message = exception.getMessage();
            }
            new AlertDialog.Builder(SignInActivity.this, R.style.WaitingTheme)
                    .setIcon(R.mipmap.ic_failed)
                    .setTitle("Sign-in failed")
                    .setMessage(message)
                    .show();
        }
    }
}
