package cit.edu.paloma.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public final class GoogleSignInUtils {
    private static volatile GoogleApiClient mGoogleApiClient = null;

    private GoogleSignInUtils() {
    }

    public static GoogleApiClient getGoogleApiClient(GoogleSignInOptions gso, Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage((FragmentActivity)context /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener)context /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();
        }
        return mGoogleApiClient;
    }

    public static void signOut() {
        Log.v("GoogleSignInUtils", mGoogleApiClient.toString() + ": " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient = null;
        }
        FirebaseAuth.getInstance().signOut();
    }
}
