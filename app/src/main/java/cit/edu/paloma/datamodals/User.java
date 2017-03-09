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
    private String recentMessage;
    private boolean isOnline;
    private Map<String, Object> friends;
    private Map<String, Object> invites;

    public User() {
    }

    public User(String userId, String email, String fullName, String avatar, String recentMessage, boolean isOnline, Map<String, Object> friends, Map<String, Object> invites) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.recentMessage = recentMessage;
        this.isOnline = isOnline;
        if (friends == null) {
            friends = new HashMap<>();
        }
        this.friends = friends;
        if (invites == null) {
            invites = new HashMap<>();
        }
        this.invites = invites;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", recentMessage='" + recentMessage + '\'' +
                ", isOnline=" + isOnline +
                ", friends=" + friends +
                ", invites=" + invites +
                '}';
    }

    @NonNull
    public User getReplica() {
        return new User(
                userId, email, fullName, avatar, recentMessage, isOnline, friends, invites
        );
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

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Map<String, Object> getFriends() {
        if (friends == null) {
            friends = new HashMap<>();
        }
        return friends;
    }

    public void setFriends(Map<String, Object> friends) {
        this.friends = friends;
    }

    public Map<String, Object> getInvites() {
        if (invites == null) {
            invites = new HashMap<>();
        }
        return invites;
    }

    public void setInvites(Map<String, Object> invites) {
        this.invites = invites;
    }

    @Exclude
    public Map<String, Object> topMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", this.getUserId());
        map.put("email", this.getEmail());
        map.put("fullName", this.getFullName());
        map.put("avatar", this.getAvatar());
        map.put("recentMessage", this.getRecentMessage());
        map.put("isOnline", this.isOnline());
        map.put("friends", this.getFriends());
        map.put("invites", this.getInvites());
        return map;
    }

}

