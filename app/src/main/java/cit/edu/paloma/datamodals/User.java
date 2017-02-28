package cit.edu.paloma.datamodals;

import android.net.Uri;

/**
 * Created by charlie on 2/27/17.
 */

public class User {
    private int userId;
    private String email;
    private String fullName;
    private String avatar;
    private boolean isOnline;
    private boolean isMale;

    public User(String email, String fullName, String avatar, boolean isOnline, boolean isMale) {
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.isMale = isMale;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }
}
