package com.aleksander.savosh.tasker.service;

public class ParseCloudDaoImpl<Obj> extends AbstractParseDao<Obj> implements CloudDao<Obj> {

    public ParseCloudDaoImpl(Class<Obj> objClass) {
        super(objClass, true);
    }

}
