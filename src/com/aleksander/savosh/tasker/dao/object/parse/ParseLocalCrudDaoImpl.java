package com.aleksander.savosh.tasker.dao.object.parse;

import com.aleksander.savosh.tasker.model.object.Base;

public class ParseLocalCrudDaoImpl<Model extends Base> extends AbstractParseCrudDaoImpl<Model> {

    public ParseLocalCrudDaoImpl(Class clazz) {
        super(false, clazz);
    }
}
