package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.Bundle;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.dao.object.parse.ParseCloudCrudDaoImpl;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Phone;
import com.aleksander.savosh.tasker.model.object.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExxActivity extends Activity {

    private List<Property> createRandomProperties(){
        List<Property> properties = new ArrayList<Property>();
        int count = 5;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            properties.add(new Property(i, "Some text " + i, new Date()));
        }
        return properties;
    }

    private List<Notice> createRandomNotices(){
        List<Notice> notices = new ArrayList<Notice>();
        int count = 5;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            notices.add(new Notice(createRandomProperties()));
        }
        return notices;
    }

    private List<Phone> createRandomPhones(){
        List<Phone> phones = new ArrayList<Phone>();
        int count = 5;// (int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            phones.add(new Phone("Number " + i + i + i + i + i + i));
        }
        return phones;
    }

    private Account createAccount(){
        return new Account("test one", createRandomPhones(), createRandomNotices());
    }


    private void test1() throws CannotCreateException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);

        Account one = new Account("test one", createRandomPhones(), createRandomNotices());
//        Account two = new Account("test two", createRandomPhones(), createRandomNotices());
//        Account three = new Account("test three", createRandomPhones(), createRandomNotices());

        crudDao.createWithRelationsThrowException(one);
//        crudDao.createWithRelationsThrowException(two);
//        crudDao.createWithRelationsThrowException(three);


    }

    private void test2(String id) throws DataNotFoundException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        System.out.println(crudDao.readWithRelationsThrowException(id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//            test1();

            test2("Xn2oAEi3vP");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
