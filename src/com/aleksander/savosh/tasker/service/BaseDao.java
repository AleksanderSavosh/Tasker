package com.aleksander.savosh.tasker.service;

import java.util.List;

public interface BaseDao<Model> {
    public Model create(Model obj);
    public Model createThrowExceptions(Model obj) throws Exception;
    public List<Model> read(Model constraintObj);
    public List<Model> readThrowExceptions(Model constraintObj) throws Exception;
    public Model readFirst(Model constraintObj);
    public Model readFirstThrowExceptions(Model constraintObj) throws Exception;
    public void delete(Model constraintObj);
    public void deleteThrowExceptions(Model constraintObj) throws Exception;
}
