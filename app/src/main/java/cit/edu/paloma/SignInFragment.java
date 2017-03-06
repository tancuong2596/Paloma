package cit.edu.paloma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

public class SignInFragment
        extends Fragment
        implements View.OnClickListener, FirebaseAuth.AuthStateListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_GOOGLE_SIGN_IN = 0;
    private static final String TAG = SignInFragment.class.getSimpleName();

    private Button mGooglePlusButton;
    private FirebaseAuth mFirebaseAuth;
    private ConstraintLayout mWaitingLayout;
    private ConstraintLayout mSignInLayout;
    private View mRootView;

    public interface UserSignInSuccessful {
        void onUserSignInSuccessful();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        initViews();
        mFirebaseAuth = FirebaseAuth.getInstance();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    private void initViews() {
        mGooglePlusButton = (Button) mRootView.findViewById(R.id.google_plus_button);
        mWaitingLayout = (ConstraintLayout) mRootView.findViewById(R.id.waiting_layout);
        mSignInLayout = (ConstraintLayout) mRootView.findViewById(R.id.sign_in_layout);

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
                break;
        }
    }

    private void signInWithGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(((MainActivity) getActivity()).getGoogleApiClient());
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                    .setIcon(R.mipmap.ic_failed)
                    .setTitle("Sign-in failed")
                    .setMessage(result.getStatus().getStatusMessage())
                    .show();
        } else {
            GoogleSignInAccount account = result.getSignInAccount();

            AuthCredential cred = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            mFirebaseAuth
                    .signInWithCredential(cred)
                    .addOnCompleteListener(getActivity(), mOnCompleteListener);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            Log.v(TAG, user.getDisplayName());
            ((UserSignInSuccessful) getActivity()).onUserSignInSuccessful();
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

                String message = "Cannot sign in with your google account";
                if (exception != null) {
                    message = exception.getMessage();
                }

                new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
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
