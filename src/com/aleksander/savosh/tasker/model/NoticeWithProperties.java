package com.aleksander.savosh.tasker.model;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NoticeWithProperties implements Serializable {

    private Notice notice;
    private Map<Integer, List<Property>> propertiesMap;

    public NoticeWithProperties(Notice notice, Map<Integer, List<Property>> propertiesMap) {
        this.notice = notice;
        this.propertiesMap = propertiesMap;
    }

    public Notice getNotice() {
        return notice;
    }

    public Map<Integer, List<Property>> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public String toString() {
        return "NoticeWithProperties{" +
                "notice=" + notice +
                ", propertiesMap=" + propertiesMap +
                '}';
    }
}
