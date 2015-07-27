package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.Bundle;
import com.aleksander.savosh.tasker.service.ParseUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            ParseObject author = ParseObject.create("Author");
            author.put("name", "Savosh Alex 3");
            author.pin();

            List<ParseObject> list = new ArrayList<ParseObject>();
            for(int i = 0; i < 5; i++) {
                ParseObject object = ParseObject.create("Book");
                object.put("name", "Book 3 " + i);
//                object.put("author", author);
                object.pin();
                list.add(object);
            }
            author.put("books", list);
            author.pin();

            System.out.println(ParseUtil.toString(author));

//            for(ParseObject object : (List<ParseObject>) author.get("books")){
//                System.out.println(ParseUtil.toString(object));
//            }


//            ParseQuery parseQuery = ParseQuery.getQuery("Book");
//            parseQuery.fromLocalDatastore();
//            parseQuery.whereEqualTo("author", author);
//            for(ParseObject book : (List<ParseObject>) parseQuery.find()){
//                System.out.println(ParseUtil.toString(book));
//            }




//            for(ParseObject obj : ParseQuery.getQuery("Test").find()){
//                System.out.println(ParseUtil.toString(obj));
//            }



        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
