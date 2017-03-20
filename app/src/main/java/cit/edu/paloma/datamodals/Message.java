package cit.edu.paloma.datamodals;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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
    private HashMap<String, Object> timestamp;

    public Message() {
    }

    public Message(String messageId, String groupChatId, String senderId, int contentType, String content, Map<String, String> timestamp) {
        this.messageId = messageId;
        this.groupChatId = groupChatId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.timestamp = new HashMap<>();
        this.timestamp.put("date", timestamp);
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

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(HashMap<String, Object> timestamp) {
        this.timestamp = timestamp;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("messageId", this.messageId);
        map.put("groupChatId", this.groupChatId);
        map.put("senderId", this.senderId);
        map.put("contentType", this.contentType);
        map.put("content", this.content);
        map.put("timestamp", this.timestamp);
        return map;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", groupChatId='" + groupChatId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", contentType=" + contentType +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
