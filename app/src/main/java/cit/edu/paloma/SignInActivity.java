package cit.edu.paloma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.GoogleAuthProvider;

import cit.edu.paloma.utils.SignInUtils;

public class SignInActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0;
    private static final int RC_REGISTER = 1;

    private ImageButton mSignInWithGoogleButton;
    private Button mSignInButton;
    private TextView mRegisterLink;
    private EditText mEmailEdit;
    private EditText mPassEdit;
    private ProgressBar mProgressBar;
    private ConstraintLayout mProgressBarLayout;
    private ConstraintLayout mSignInInputLayout;
    private FirebaseAuth.AuthStateListener mStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();

        mStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    System.out.println("Signed in as " + firebaseAuth.getCurrentUser().getDisplayName());
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        FirebaseAuth.getInstance().addAuthStateListener(mStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mStateListener);
    }

    private void initViews() {
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);

        mSignInWithGoogleButton = (ImageButton) findViewById(R.id.sign_in_with_google_button);
        mSignInWithGoogleButton.setOnClickListener(this);

        mRegisterLink = (TextView) findViewById(R.id.register_link);
        mRegisterLink.setOnClickListener(this);

        mEmailEdit = (EditText) findViewById(R.id.email_edit);

        mPassEdit = (EditText) findViewById(R.id.pass_edit);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mProgressBarLayout = (ConstraintLayout) findViewById(R.id.progressbar_layout);

        mSignInInputLayout = (ConstraintLayout) findViewById(R.id.sign_in_input_layout);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign_in_button:
                signInWithEmail();
                break;
            case R.id.sign_in_with_google_button:
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(SignInUtils.getGoogleApiClient(this));
                startActivityForResult(intent, RC_SIGN_IN);
                break;
            case R.id.register_link:
                Intent registerIntent = new Intent(this, UserRegisterActivity.class);
                startActivityForResult(registerIntent, RC_REGISTER);
                break;
        }
    }

    private void signInWithEmail() {
        final String email = mEmailEdit.getText().toString();
        String pass = mEmailEdit.getText().toString();

        if (!validateInput(email, pass)) {
            return;
        }

        showProgressBar(true);
        SignInUtils.signInWithEmailAndPassword(email, pass, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Cannot sign in with email " + email, Toast.LENGTH_SHORT).show();
                }
                showProgressBar(false);
            }
        });
    }

    private void showProgressBar(boolean b) {
        if (b) {
            mSignInInputLayout.setVisibility(View.INVISIBLE);
            mProgressBarLayout.setVisibility(View.VISIBLE);
        } else {
            mSignInInputLayout.setVisibility(View.VISIBLE);
            mProgressBarLayout.setVisibility(View.INVISIBLE);
        }
    }

    private boolean validateInput(String email, String pass) {
        boolean isValid = true;

        if (email.trim().isEmpty()) {
            mEmailEdit.setError("Email is mandatory");
            mEmailEdit.setText(null);
            isValid = false;
        } else {
            mEmailEdit.setError(null);
        }

        if (pass.isEmpty()) {
            mPassEdit.setError("Password is mandatory");
            mPassEdit.setText(null);
            isValid = false;
        } else {
            mPassEdit.setError(null);
        }

        return isValid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    signInWithGoogle(account);
                } else {
                    Toast.makeText(this, "Failed to sign in with Google", Toast.LENGTH_LONG).show();
                }
                break;
            case RC_REGISTER:

                break;
        }
    }

    private void signInWithGoogle(final GoogleSignInAccount account) {
        showProgressBar(true);
        SignInUtils.signInWithGoogle(account, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Cannot sign in with account " + account.getDisplayName(), Toast.LENGTH_LONG).show();
                }
                showProgressBar(false);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        Log.v(TAG, "failed to connect to google api client");
    }
}
