package com.aleksander.savosh.tasker;

import com.parse.Parse;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "cv5X8Il8up7Y4YvrBz6nM6icaf7lBYXfPlwQSmAR", "6fDQLSh7mmIqoZEU5V0BNOrFxHavGEFkVnNDZlrZ");
    }
}
