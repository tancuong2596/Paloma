package cit.edu.paloma.utils;

import android.net.Uri;

/**
 * Created by charlie on 2/27/17.
 */

public class User {
    private int mUserId;
    private String mEmail;
    private String mFullName;
    private Uri mAvatar;

    public User(int mUserId, String mEmail, String mFullName, Uri mAvatar) {
        this.mUserId = mUserId;
        this.mEmail = mEmail;
        this.mFullName = mFullName;
        this.mAvatar = mAvatar;
    }

    public User() {
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int mUserId) {
        this.mUserId = mUserId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public Uri getAvatar() {
        return mAvatar;
    }

    public void setAvatar(Uri mAvatar) {
        this.mAvatar = mAvatar;
    }
}
