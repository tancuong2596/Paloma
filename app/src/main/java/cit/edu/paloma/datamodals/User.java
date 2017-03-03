package cit.edu.paloma.datamodals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by charlie on 2/27/17.
 */

@IgnoreExtraProperties
public class User {
    private String userId;
    private String email;
    private String fullName;
    private String avatar;
    private boolean isOnline;

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("avatar", avatar);
        map.put("isOnline", isOnline);
        return map;
    }

    @Exclude
    public void copyFrom(User value) {
        this.userId = value.userId;
        this.email = value.email;
        this.fullName = value.fullName;
        this.avatar = value.avatar;
        this.isOnline = value.isOnline;
    }

    @Exclude
    public static User fromBundle(Bundle bundle) {
        return new User(
                bundle.getString("userId"),
                bundle.getString("email"),
                bundle.getString("fullName"),
                bundle.getString("avatar"),
                bundle.getBoolean("isOnline")
        );
    }

    @Exclude
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("userId", userId);
        bundle.putString("email", email);
        bundle.putString("fullName", fullName);
        bundle.putString("avatar", avatar);
        bundle.putBoolean("isOnline", isOnline);

        return bundle;
    }

    public User() {
    }

    public User(String userId, String email, String fullName, String avatar, boolean isOnline) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.isOnline = isOnline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
}
