package cit.edu.paloma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cit.edu.paloma.utils.GoogleSignInUtils;


public class SignInActivity
        extends AppCompatActivity
        implements View.OnClickListener, FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_GOOGLE_SIGN_IN = 0;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private Button mGooglePlusButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ConstraintLayout mWaitingLayout;
    private ConstraintLayout mSignInLayout;
    private String mServiceName;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();

        mAuth = FirebaseAuth.getInstance();
        mGoogleApiClient = null;
        gso = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    private void initViews() {
        mGooglePlusButton = (Button) findViewById(R.id.google_plus_button);
        mWaitingLayout = (ConstraintLayout) findViewById(R.id.waiting_layout);
        mSignInLayout = (ConstraintLayout) findViewById(R.id.sign_in_layout);

        mGooglePlusButton.setOnClickListener(this);
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
        }
    }

    private void signInWithGoogle() {
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = GoogleSignInUtils.getGoogleApiClient(gso, this);

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
            new AlertDialog.Builder(this, R.style.DialogTheme)
                    .setIcon(R.mipmap.ic_failed)
                    .setTitle("Sign-in failed")
                    .setMessage(result.getStatus().getStatusMessage())
                    .show();
        } else {
            GoogleSignInAccount account = result.getSignInAccount();

            AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            mAuth
                    .signInWithCredential(cred)
                    .addOnCompleteListener(this, mOnCompleteListener);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            Log.v(TAG, mUser.getDisplayName());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Log.v(TAG, "user is null");
        }
    }

    private OnCompleteListener<AuthResult> mOnCompleteListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            showWaitingScreen(false);
            if (!task.isSuccessful()) {
                Exception exception = task.getException();

                String message = "Cannot sign in with your " + mServiceName + " account";
                if (exception != null) {
                    message = exception.getMessage();
                }

                new AlertDialog.Builder(SignInActivity.this, R.style.DialogTheme)
                        .setIcon(R.mipmap.ic_failed)
                        .setTitle("Sign-in failed")
                        .setMessage(message)
                        .show();
            }
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
