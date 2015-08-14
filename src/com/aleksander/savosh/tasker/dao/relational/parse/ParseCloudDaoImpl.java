package com.aleksander.savosh.tasker.dao.relational.parse;

import com.aleksander.savosh.tasker.dao.relational.CloudDao;

public class ParseCloudDaoImpl<Obj> extends AbstractParseDao<Obj> implements CloudDao<Obj> {

    public ParseCloudDaoImpl(Class<Obj> objClass) {
        super(objClass, true);
    }

}
