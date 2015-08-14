package com.aleksander.savosh.tasker.dao.object;

import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.model.object.Base;

public interface CrudDao<Model extends Base, PK> {

    public Model create(Model model);
    public Model read(PK pk);
    public Model update(Model model);
    public boolean delete(PK pk);

    public Model createWithRelations(Model model);
    public Model readWithRelations(PK pk);
    public Model updateWithRelations(Model model);
    public boolean deleteWithRelations(PK pk);

    public Model createThrowException(Model model) throws CannotCreateException;
    public Model readThrowException(PK pk) throws DataNotFoundException;
    public Model updateThrowException(Model model) throws DataNotFoundException;
    public boolean deleteThrowException(PK pk) throws DataNotFoundException;

    public Model createWithRelationsThrowException(Model model) throws CannotCreateException;
    public Model readWithRelationsThrowException(PK pk) throws DataNotFoundException;
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException;
    public boolean deleteWithRelationsThrowException(PK pk) throws DataNotFoundException;

}
