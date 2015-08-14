package com.aleksander.savosh.tasker.dao.object.parse;

import com.aleksander.savosh.tasker.model.object.Base;

public class ParseCloudCrudDaoImpl<Model extends Base> extends AbstractParseCrudDaoImpl<Model> {

    public ParseCloudCrudDaoImpl(Class clazz) {
        super(true, clazz);
    }
}
