package com.aleksander.savosh.tasker.model.object;


public class Config extends Base {

    public Config() {
        setObjectId(ID);
    }

    public static final String ACC_ZERO = "Zero";
    public static final String ID = "Config";
    public Boolean rememberMe;
    public String accountId;

}
