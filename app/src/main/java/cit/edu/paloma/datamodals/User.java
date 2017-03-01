package cit.edu.paloma.datamodals;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by charlie on 2/27/17.
 */

@IgnoreExtraProperties
public class User {
    private int userId;
    private String email;
    private String fullName;
    private String avatar;
    private boolean isOnline;
    private boolean isMale;

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("avatar", avatar);
        map.put("isOnline", isOnline);
        map.put("isMale", isMale);
        return map;
    }

    public User(String email, String fullName, String avatar, boolean isOnline, boolean isMale) {
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.isMale = isMale;
    }

    public User(int userId, String email, String fullName, String avatar, boolean isOnline, boolean isMale) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.isMale = isMale;
    }

    public User() {
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
