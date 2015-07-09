package com.aleksander.savosh.tasker.service;

import java.util.List;

public interface BaseDao<Model> {
    public Model create(Model obj);
    public List<Model> read(Model constraintObj);
    public Model readFirst(Model constraintObj);
}
