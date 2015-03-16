package com.aleksander.savosh.tasker.beans;

import android.util.Log;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@DatabaseTable(tableName = "messages")
public class Message implements Serializable {

    private static final long serialVersionUID = 7526471155622776147L;
    public static final String DATE_PATTERN = "dd.MM.yyyy";


    public static class PriorityComparator implements Comparator<Message>{
        @Override
        public int compare(Message message, Message t1) {
            return new Integer(t1.priority).compareTo(message.priority);
        }
    }

    public static Collection<Message> filterByType(Collection<Message> list, String type){
        List<Message> result = new ArrayList<Message>();
        for(Message message : list){
            if(message.type.getName().equals(type)){
                result.add(message);
            }
        }
        return result;
    }


    @DatabaseField(generatedId = true, canBeNull = false)
    private Integer id = -1;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Type type = new Type();
    @DatabaseField(canBeNull = false)
    private Date createDate = new Date();
    @DatabaseField(canBeNull = false)
    private Date lastModifiedDate = new Date();
    @DatabaseField(canBeNull = false)
    private int priority = 0;
    @DatabaseField(canBeNull = false)
    private String message = "";
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Status status;

    public Message() {}

    public Message(Type type, int priority, String message, Status status) {
        this.type = type;
        this.priority = priority;
        this.message = message;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", type=" + type +
                ", createDate=" + createDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", priority=" + priority +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
