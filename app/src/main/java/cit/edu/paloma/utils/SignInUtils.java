package cit.edu.paloma.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import cit.edu.paloma.R;

public final class SignInUtils {
    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions mGoogleSignInOptions;

    private SignInUtils() {

    }

    public static GoogleApiClient getGoogleApiClient(Context context) {
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = SignInUtils.getGoogleSignInOptions(context);
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage((FragmentActivity) context, (GoogleApiClient.OnConnectionFailedListener) context)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
        return mGoogleApiClient;
    }

    public static GoogleSignInOptions getGoogleSignInOptions(Context context) {
        if (mGoogleSignInOptions == null) {
            mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        }
        return mGoogleSignInOptions;
    }

    public static void signInWithEmailAndPassword(final String email,
                                                  String pass,
                                                  OnCompleteListener<AuthResult> onCompleteListener) {
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void signInWithGoogle(final GoogleSignInAccount account,
                                        OnCompleteListener<AuthResult> onCompleteListener) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        FirebaseAuth
                .getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void signOut(@NonNull ResultCallback resultCallback) {
        FirebaseAuth.getInstance().signOut();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi
                        .signOut(mGoogleApiClient)
                        .setResultCallback(resultCallback);
            }
            mGoogleApiClient = null;
        }
        mGoogleSignInOptions = null;
    }
}
