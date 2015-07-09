package com.aleksander.savosh.tasker.service;

public interface CloudService<Model> {

    public Model create(Model obj);
    public Model read(String uniqueValue);

}
