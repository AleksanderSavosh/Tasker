package com.aleksander.savosh.tasker.service;

public class ParseLocalDaoImpl<Obj> extends AbstractParseDao<Obj> implements LocalDao<Obj> {

    public ParseLocalDaoImpl(Class<Obj> objClass) {
        super(objClass, false);
    }
}
