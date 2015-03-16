package com.aleksander.savosh.tasker.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "types")
public class Type implements Serializable {

    private static final long serialVersionUID = 7526471155622776143L;

    @DatabaseField(generatedId = true, canBeNull = false)
    private int id = -1;
    @DatabaseField(canBeNull = false)
    private String name = "";
    @DatabaseField(canBeNull = false)
    private int imageId = -1;

    public Type() {
    }

    public Type(String name) {
        this.name = name;
    }

    public Type(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Type(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Type)) return false;

        Type type = (Type) o;

        if (id != type.id) return false;
        if (imageId != type.imageId) return false;
        if (!name.equals(type.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + imageId;
        return result;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageId=" + imageId +
                '}';
    }
}
