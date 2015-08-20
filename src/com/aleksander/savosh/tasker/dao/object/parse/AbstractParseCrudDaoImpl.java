package com.aleksander.savosh.tasker.dao.object.parse;

import android.util.Log;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.model.object.Base;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class AbstractParseCrudDaoImpl<Model extends Base> implements CrudDao<Model, String> {

    private final boolean isCloudStorage;
    private final Class<Model> clazz;

    public AbstractParseCrudDaoImpl(boolean isCloudStorage, Class<Model> clazz) {
        this.isCloudStorage = isCloudStorage;
        this.clazz = clazz;
    }

    @Override
    public Model create(Model model) {
        try {
            return createThrowException(model);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Model read(String s) {
        try {
            return readThrowException(s);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Model update(Model model) {
        try {
            return updateThrowException(model);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public boolean delete(String s) {
        try {
            return deleteThrowException(s);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return false;
    }

    @Override
    public Model createWithRelations(Model model) {
        try {
            return createWithRelationsThrowException(model);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Model readWithRelations(String s) {
        try {
            return readWithRelationsThrowException(s);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Model updateWithRelations(Model model) {
        try {
            return updateWithRelationsThrowException(model);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public boolean deleteWithRelations(String s) {
        try {
            return deleteWithRelationsThrowException(s);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model createThrowException(Model model) throws CannotCreateException, OtherException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject po = Util.createPO(clazz);
            Util.setModel2PO(model, po, isCloudStorage);
            Util.savePO(po, isCloudStorage);
            Util.setPO2Model(po, model);
            return model;

        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new CannotCreateException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model readThrowException(String s) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === READ " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject po = Util.getPO(clazz, s, isCloudStorage);
            Base base = Util.createModel(clazz);
            Util.setPO2Model(po, base);
            return (Model) base;

        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (OtherException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model updateThrowException(Model model) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === UPDATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject po = Util.getPO(clazz, model.getObjectId(), isCloudStorage);

            Util.setModel2PO(model, po, isCloudStorage);
            Util.savePO(po, isCloudStorage);
            Util.setPO2Model(po, model);

            return model;

        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (OtherException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public boolean deleteThrowException(String s) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === DELETE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject po = Util.getPO(clazz, s, isCloudStorage);
            Util.deletePO(po, isCloudStorage);

            return true;
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new OtherException(e.getMessage(), e);
        } catch (OtherException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }



    @Override
    public Model createWithRelationsThrowException(Model model) throws CannotCreateException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            Map<Integer, List<Util.ModelPONode>> map =
                    Util.getModelPoTreeRec(clazz, 1, model, null, true, isCloudStorage);
            Util.createRelations(map);
            Util.save(map, isCloudStorage);
            Util.setPO2Model(map);

            return model;
        } catch (ParseException e) {
            throw new CannotCreateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (DataNotFoundException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model readWithRelationsThrowException(String s) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === READ WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject po = Util.getPO(clazz, s, isCloudStorage);
            Map<Integer, List<Util.ModelPONode>> map =
                    Util.getModelPoTreeRec(clazz, 1, null, po, true, isCloudStorage);
            Util.createRelations(map);
            Util.setPO2Model(map);

            return (Model) map.get(1).get(0).modelPO.base;
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException();
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === UPDATE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject po = Util.getPO(clazz, model.getObjectId(), isCloudStorage);

            Map<Integer, List<Util.ModelPONode>> mapNeed =
                    Util.getModelPoTreeRec(clazz, 1, model, null, false, isCloudStorage);
            Map<Integer, List<Util.ModelPONode>> mapExists =
                    Util.getModelPoTreeRec(clazz, 1, null, po, false, isCloudStorage);

            //найти разницу между mapExists - mapNeed = diffExistsNeed
            List<Util.ModelPO> diff = Util.diff(mapExists, mapNeed);

            //diffExistsNeed удалить
            Util.delete(diff, isCloudStorage);

            //mapNeed сохранить
            Util.createRelations(mapNeed);
            Util.save(mapNeed, isCloudStorage);
            Util.setPO2Model(mapNeed);

            return model;
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public boolean deleteWithRelationsThrowException(String s) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === DELETE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject po = Util.getPO(clazz, s, isCloudStorage);
            Map<Integer, List<Util.ModelPONode>> map =
                    Util.getModelPoTreeRec(clazz, 1, null, po, true, isCloudStorage);
            Util.createRelations(map);
            Util.delete(map, isCloudStorage);

            return true;
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException();
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Model> readAllWithRelationsThrowException() throws OtherException, DataNotFoundException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === READ ALL WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);

            List<Base> bases = new ArrayList<Base>();
            List<ParseObject> pos = Util.getPOs(clazz, isCloudStorage);

            for(ParseObject po : pos) {
                Map<Integer, List<Util.ModelPONode>> map =
                        Util.getModelPoTreeRec(clazz, 1, null, po, true, isCloudStorage);
                Util.createRelations(map);
                Util.setPO2Model(map);
                bases.add(map.get(1).get(0).modelPO.base);
            }

            return (List<Model>) bases;
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException();
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public List<Model> readAllWithRelations() {
        try {
            return readAllWithRelationsThrowException();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Model> readAllThrowException() throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === READ ALL " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);

            List<Base> bases = new ArrayList<Base>();
            List<ParseObject> pos = Util.getPOs(clazz, isCloudStorage);
            for(ParseObject po : pos) {
                Base base = Util.createModel(clazz);
                Util.setPO2Model(po, base);
                bases.add(base);
            }

            return (List<Model>) bases;

        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (OtherException e) {
            throw new OtherException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public List<Model> readAll() {
        try {
            return readAllThrowException();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }
}
