package com.aleksander.savosh.tasker.dao.relational.parse.exx;

import com.aleksander.savosh.tasker.dao.relational.CloudDao;
import com.aleksander.savosh.tasker.dao.relational.parse.AbstractParseDao;

public class ParseCloudExxDaoImpl<Obj> extends AbstractParseDao<Obj> implements CloudDao<Obj> {

    public ParseCloudExxDaoImpl(Class<Obj> objClass) {
        super(objClass, true);
    }

}
