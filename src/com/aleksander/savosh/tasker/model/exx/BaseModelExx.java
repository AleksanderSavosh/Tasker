package com.aleksander.savosh.tasker.model.exx;

import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseModelExx extends ParseObject implements Serializable {

    protected BaseModelExx(String name) {
        super(name);
    }


}
