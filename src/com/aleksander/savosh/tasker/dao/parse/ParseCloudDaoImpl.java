package com.aleksander.savosh.tasker.dao.parse;

import com.aleksander.savosh.tasker.dao.CloudDao;

public class ParseCloudDaoImpl<Obj> extends AbstractParseDao<Obj> implements CloudDao<Obj> {

    public ParseCloudDaoImpl(Class<Obj> objClass) {
        super(objClass, true);
    }

}
