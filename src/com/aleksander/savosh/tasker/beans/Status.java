package com.aleksander.savosh.tasker.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "statuses")
public class Status implements Serializable {

    private static final long serialVersionUID = 7526471133622776147L;

    @DatabaseField(generatedId = true, canBeNull = false)
    private int id = -1;
    @DatabaseField(canBeNull = false)
    private String name = "";

    public Status() {
    }

    public Status(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
