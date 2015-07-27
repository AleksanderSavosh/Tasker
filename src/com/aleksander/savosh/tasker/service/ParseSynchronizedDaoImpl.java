package com.aleksander.savosh.tasker.service;

import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParseSynchronizedDaoImpl<Model> implements SynchronizedDao<Model> {
    private CloudDao<Model> cloudDao;
    private LocalDao<Model> localDao;

    public ParseSynchronizedDaoImpl(CloudDao<Model> cloudDao, LocalDao<Model> localDao) {
        this.cloudDao = cloudDao;
        this.localDao = localDao;
    }

    @Override
    public Model create(Model obj) {
        try {
            return createThrowExceptions(obj);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Model createThrowExceptions(Model obj) throws Exception {
        obj = cloudDao.createThrowExceptions(obj);
        return localDao.createThrowExceptions(obj);
    }

    @Override
    public List<Model> read(Model constraintObj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Model> readThrowExceptions(Model constraintObj) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public Model readFirst(Model constraintObj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Model readFirstThrowExceptions(Model constraintObj) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Model constraintObj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteThrowExceptions(Model constraintObj) throws Exception {
        cloudDao.deleteThrowExceptions(constraintObj);
        localDao.deleteThrowExceptions(constraintObj);
    }
}
