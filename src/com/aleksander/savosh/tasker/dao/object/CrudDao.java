package com.aleksander.savosh.tasker.dao.object;

import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.model.object.Base;

import java.util.List;

public interface CrudDao<Model extends Base, PK> {

    public Model create(Model model);
    public Model read(PK pk);
    public Model update(Model model);
    public boolean delete(PK pk);

    public Model createWithRelations(Model model);
    public Model readWithRelations(PK pk);
    public Model updateWithRelations(Model model);
    public boolean deleteWithRelations(PK pk);

    public Model createThrowException(Model model) throws CannotCreateException, OtherException;
    public Model readThrowException(PK pk) throws DataNotFoundException, OtherException;
    public Model updateThrowException(Model model) throws DataNotFoundException, OtherException;
    public boolean deleteThrowException(PK pk) throws DataNotFoundException, OtherException;

    public Model createWithRelationsThrowException(Model model) throws CannotCreateException, OtherException;
    public Model readWithRelationsThrowException(PK pk) throws DataNotFoundException, OtherException;
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException, OtherException;
    public boolean deleteWithRelationsThrowException(PK pk) throws DataNotFoundException, OtherException;


    //additions
    public List<Model> readAll();
    public List<Model> readAllThrowException() throws DataNotFoundException, OtherException;
    public List<Model> readAllWithRelations();
    public List<Model> readAllWithRelationsThrowException() throws OtherException, DataNotFoundException;


}
