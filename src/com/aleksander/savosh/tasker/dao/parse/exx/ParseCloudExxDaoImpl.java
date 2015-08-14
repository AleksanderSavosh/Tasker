package com.aleksander.savosh.tasker.dao.parse.exx;

import com.aleksander.savosh.tasker.dao.CloudDao;
import com.aleksander.savosh.tasker.dao.parse.AbstractParseDao;

public class ParseCloudExxDaoImpl<Obj> extends AbstractParseDao<Obj> implements CloudDao<Obj> {

    public ParseCloudExxDaoImpl(Class<Obj> objClass) {
        super(objClass, true);
    }

}
