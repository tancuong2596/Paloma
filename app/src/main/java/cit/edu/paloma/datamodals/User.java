package cit.edu.paloma.datamodals;

import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", this.getUserId());
        map.put("email", this.getEmail());
        map.put("fullName", this.getFullName());
        map.put("avatar", this.getAvatar());
        map.put("online", this.isOnline());
        return map;
    }

}

