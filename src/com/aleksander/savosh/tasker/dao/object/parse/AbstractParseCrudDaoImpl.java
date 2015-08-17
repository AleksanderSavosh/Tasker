package com.aleksander.savosh.tasker.dao.object.parse;

import android.annotation.SuppressLint;
import android.util.Log;
import com.aleksander.savosh.tasker.StringUtil;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.dao.ReflectionUtil;
import com.aleksander.savosh.tasker.model.object.Base;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
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
            return delete(s);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return false;
    }

    @Override
    public Model createWithRelations(Model model) {
        return null;
    }

    @Override
    public Model readWithRelations(String s) {
        return null;
    }

    @Override
    public Model updateWithRelations(Model model) {
        return null;
    }

    @Override
    public boolean deleteWithRelations(String s) {
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

            ParseObject parseObject = Util.baseModelToParseObject(model, isCloudStorage);

            if(isCloudStorage) {
                parseObject.save();
            } else {
                parseObject.pin();
            }

            return (Model) Util.paresObjectToBaseModel(parseObject, clazz);
        } catch (IllegalAccessException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new CannotCreateException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new OtherException(e.getMessage(), e);
        } catch (DataNotFoundException e) {
            throw new OtherException(e.getMessage(), e);
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

            ParseObject parseObject = Util.getParseObjectById(s, clazz, isCloudStorage);

            return (Model) Util.paresObjectToBaseModel(parseObject, clazz);

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

            ParseObject parseObject = Util.getParseObjectById(model.getObjectId(), clazz, isCloudStorage);

            Util.updateParseObjectByBaseModel(parseObject, model, isCloudStorage);

            if(isCloudStorage) {
                parseObject.save();
            } else {
                parseObject.pin();
            }

            return (Model) Util.paresObjectToBaseModel(parseObject, clazz);

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
    public boolean deleteThrowException(String s) throws DataNotFoundException, OtherException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === DELETE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject parseObject = Util.getParseObjectById(s, clazz, isCloudStorage);


            if(isCloudStorage) {
                parseObject.delete();
            } else {
                parseObject.unpin();
            }

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
    public Model createWithRelationsThrowException(Model model) throws CannotCreateException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            Map<Integer, List<Util.Relations>> map = Util.getMapRelationsRec(model, 1);
            Util.fillMapRelations(map, isCloudStorage);
            Util.createRelations(map);


//            if(isCloudStorage) {
//                parseObject.delete();
//            } else {
//                parseObject.unpin();
//            }
//
//            return true;

        } catch (ParseException e) {
//            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
//                throw new DataNotFoundException(e.getMessage(), e);
//            }
//            throw new OtherException(e.getMessage(), e);
        } catch (OtherException e) {
//            throw new OtherException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
        return null;
    }

    @Override
    public Model readWithRelationsThrowException(String s) throws DataNotFoundException {
        return null;
    }

    @Override
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException {
        return null;
    }

    @Override
    public boolean deleteWithRelationsThrowException(String s) throws DataNotFoundException {
        return false;
    }
}
