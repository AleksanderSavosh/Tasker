package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.Bundle;
import com.aleksander.savosh.tasker.dao.parse.exx.ParseCloudExxService;
import com.aleksander.savosh.tasker.model.exx.AccountExx;
import com.aleksander.savosh.tasker.model.exx.PhoneExx;
import com.parse.ParseException;

public class ExxActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            AccountExx accountExx = new AccountExx();
            accountExx.setPassword("My password");
            accountExx = ParseCloudExxService.createAccountExx(accountExx);

            PhoneExx phoneExx = new PhoneExx();
            phoneExx.setNumber("777");
            phoneExx.setAccountExx(accountExx);

            phoneExx = ParseCloudExxService.createPhoneExx(phoneExx);

            System.out.println(accountExx);
            System.out.println(phoneExx);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
