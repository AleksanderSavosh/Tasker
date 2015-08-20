package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.Bundle;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.dao.object.KeyValue;
import com.aleksander.savosh.tasker.dao.object.parse.ParseCloudCrudDaoImpl;
import com.aleksander.savosh.tasker.dao.object.parse.Util;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Phone;
import com.aleksander.savosh.tasker.model.object.Property;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ExxActivity extends Activity {

    private List<Property> createRandomProperties(){
        List<Property> properties = new ArrayList<Property>();
        int count = 2;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            properties.add(new Property(i, "Some text " + i, new Date()));
        }
        return properties;
    }

    private List<Notice> createRandomNotices(){
        List<Notice> notices = new ArrayList<Notice>();
        int count = 2;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            notices.add(new Notice(createRandomProperties()));
        }
        return notices;
    }

    private List<Phone> createRandomPhones(){
        List<Phone> phones = new ArrayList<Phone>();
        int count = 2;// (int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            phones.add(new Phone("Number " + i + i + i + i + i + i));
        }
        return phones;
    }

    private Account createAccount(){
        return new Account("test one", createRandomPhones(), createRandomNotices());
    }


    private void test1() throws CannotCreateException, OtherException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);

        System.out.println("ACCOUNT: " + crudDao.createWithRelationsThrowException(createAccount()));

    }

    private void test2(String id) throws DataNotFoundException, OtherException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        System.out.println(crudDao.readWithRelationsThrowException(id));
    }

    private void test3(String id) throws DataNotFoundException, OtherException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = crudDao.readWithRelationsThrowException(id);
        System.out.println("ACCOUNT: " + account);
        crudDao.deleteWithRelationsThrowException(account.getObjectId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//            testCreateWithRelations();

            testGetParent();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testGetParent() throws OtherException, DataNotFoundException {
        CrudDao<Phone, String> crudDao = new ParseCloudCrudDaoImpl<Phone>(Phone.class);
        Phone phone = crudDao.readThrowException("M6QZ67q2RP");
        System.out.println("PHONE: " + phone);
        Account account = (Account) crudDao.getParentWithRelationsThrowException(Account.class, phone);
        System.out.println("ACCOUNT: " + account);
    }

    public void testCreate() throws OtherException, CannotCreateException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = createAccount();
        crudDao.createThrowException(account);
        System.out.println("ACCOUNT: " + account);
    }

    public void testRead() throws OtherException, DataNotFoundException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = crudDao.readThrowException("Bpiqqs5yLk");
        System.out.println("ACCOUNT: " + account);
    }

    public void testUpdate() throws OtherException, DataNotFoundException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = crudDao.readThrowException("Bpiqqs5yLk");
        System.out.println("ACCOUNT: " + account);
        account.setPassword("New pass");
        crudDao.updateThrowException(account);
        System.out.println("ACCOUNT: " + account);
    }

    public void testCreateWithRelations() throws OtherException, CannotCreateException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = createAccount();
        account.setPassword("WithRelations");
        crudDao.createWithRelationsThrowException(account);
        System.out.println("ACCOUNT: " + account);
    }

    public void testReadWithRelations() throws OtherException, CannotCreateException, DataNotFoundException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        Account account = crudDao.readWithRelationsThrowException("98X0EngczM");
        System.out.println("ACCOUNT: " + account);
    }

    public void testDeleteWithRelations() throws OtherException, CannotCreateException, DataNotFoundException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        System.out.println("DELETE: " + crudDao.deleteWithRelationsThrowException("98X0EngczM"));
        System.out.println("DELETE: " + crudDao.deleteWithRelationsThrowException("p3eS2FqzTF"));
        System.out.println("DELETE: " + crudDao.deleteWithRelationsThrowException("c7vDsW7rnh"));
    }

    public void testUtil() throws NoSuchMethodException, ParseException, InstantiationException, DataNotFoundException, IllegalAccessException, InvocationTargetException, OtherException {
        Account account = createAccount();
        Map<Integer, List<Util.ModelPONode>> map = Util.getModelPoTreeRec(Account.class, 1, account, null, true, true);

        for(Integer key : new TreeSet<Integer>(map.keySet())){
            System.out.println("level: " + key);
            for(Util.ModelPONode node : map.get(key)){
                System.out.println("\tnode: " + node);
                for(Util.ModelPONode child : node.nodes){
                    System.out.println("\t\tchild: " + child);
                }
            }
        }

    }

    public void testUpdateWithRelatives() throws OtherException, DataNotFoundException, CannotCreateException {
        CrudDao<Account, String> crudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        CrudDao<Notice, String> noticeCrudDao = new ParseCloudCrudDaoImpl<Notice>(Notice.class);
        Account account = createAccount();
        crudDao.createWithRelationsThrowException(account);
        System.out.println("ACCOUNT: " + account);

        Notice notice = account.getNotices().get(0);
        System.out.println("NOTICE IN DB: " + notice);
        Iterator it = notice.getProperties().iterator();
        it.next();
        it.remove();
        notice.getProperties().add(new Property(999, "UPDATE", new Date()));
        System.out.println("NOTICE FOR CHANGE: " + notice);
        noticeCrudDao.updateWithRelationsThrowException(notice);
        System.out.println("NOTICE FOR CHANGE: " + notice);

        account = crudDao.readWithRelationsThrowException(account.getObjectId());
        System.out.println("ACCOUNT: " + account);

    }


}
