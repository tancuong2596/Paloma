package cit.edu.paloma.datamodals;

/**
 * Created by charlie on 2/28/17.
 */

public class Message {
    public static final String IMAGE = "IMAGE";
    public static final String FILE = "FILE";
    public static final String TEXT = "TEXT";

    private String messageId;
    private String senderId;
    private String contentType;
    private Object content;
    private long timestamp;

    public Message() {
    }

    public Message(String messageId, String senderId, String contentType, Object content, long timestamp) {
        this.messageId = messageId;
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", content=" + content +
                ", timestamp=" + timestamp +
                '}';
    }
}
