package cit.edu.paloma.datamodals;

/**
 * Created by charlie on 2/28/17.
 */

public class Conversation {
    private String conId;
    private String senderId;
    private ContentType contentType;
    private Object content;
    private long timestamp;

    public enum ContentType {
        IMAGE,
        FILE,
        TEXT;
    }

    public Conversation(String conId, String senderId, ContentType contentType, Object content, long timestamp) {
        this.conId = conId;
        this.senderId = senderId;
        this.contentType = contentType;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
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
}
