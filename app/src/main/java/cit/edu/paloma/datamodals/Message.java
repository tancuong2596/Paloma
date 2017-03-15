package cit.edu.paloma.datamodals;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by charlie on 2/28/17.
 */

@IgnoreExtraProperties
public class Message {
    @Exclude public static final int IMAGE = 0;
    @Exclude public static final int FILE = 1;
    @Exclude public static final int TEXT = 2;

    private String messageId;
    private String groupChatId;
    private String senderId;
    private int contentType;
    private String content;
    private long timestamp;

    public Message() {
    }

    public Message(String messageId, String groupChatId, String senderId, int contentType, String content, long timestamp) {
        this.messageId = messageId;
        this.groupChatId = groupChatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    private HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageId", this.messageId);
        map.put("groupChatId", this.groupChatId);
        map.put("senderId", this.senderId);
        map.put("contentType", this.contentType);
        map.put("content", this.content);
        map.put("timestamp", this.timestamp);
        return map;
    }
}
