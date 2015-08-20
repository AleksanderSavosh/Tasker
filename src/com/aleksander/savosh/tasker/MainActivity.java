package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;
import com.aleksander.savosh.tasker.service.PropertyService;

import java.util.*;


public class MainActivity extends Activity {


    private Adapter adapter = new Adapter(Application.getContext());

    private UpdateAdapterTask updateAdapterTask;
    public class UpdateAdapterTask extends AsyncTask<Void, Void, Collection<Notice>> {
        @Override
        protected Collection<Notice> doInBackground(Void... params) {
            try {
                Config config = ((Application) getApplicationContext()).getConfig();

                if(StringUtil.isEmpty(config.accountId)){
                    return ((Application) getApplicationContext()).getLocalNotices().values();
                } else {
                    return ((Application) getApplicationContext())
                            .getAccounts()
                            .get(config.accountId)
                            .getNotices();
                }

            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Collection<Notice> notices) {
            if(notices != null) {
                adapter.clear();
                adapter.addAll(notices);
                adapter.notifyDataSetChanged();
            }
            updateAdapterTask = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final ListView listview = (ListView) findViewById(R.id.main_activity_list_view);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Notice item = (Notice) parent.getItemAtPosition(position);
                Intent intent = new Intent(Application.getContext(), NoticeActivity.class);
                intent.putExtra(NoticeActivity.EXTRA_NOTICE_ID, item.getObjectId());
                startActivity(intent);
                finish();
            }
        });
        if(updateAdapterTask == null){
            updateAdapterTask = new UpdateAdapterTask();
            updateAdapterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem itemAddNotice = menu.add(R.string.action_add_notice);
        itemAddNotice.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Application.getContext(), NoticeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        });

        Config config = ((Application) getApplicationContext()).getConfig();
        if(StringUtil.isEmpty(config.accountId)) {
            MenuItem itemLogIn = menu.add(R.string.action_log_in);
            itemLogIn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(Application.getContext(), LogInActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
            });
        } else {
            MenuItem itemLogOut = menu.add(R.string.action_log_out);
            itemLogOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ((Application) getApplicationContext()).logOut();
                    Intent intent = new Intent(Application.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
            });
        }

        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.menu_main_log_out) {
//            if(logOutTask == null){
//                logOutTask = new LogOutTask();
//                logOutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//            return true;
//        } else if (id == R.id.menu_main_add_notice){
//            Intent intent = new Intent(Application.getContext(), NoticeActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class Adapter extends ArrayAdapter<Notice> {

        class ViewHolder {
            public TextView title;
            public TextView createDate;
        }

        public Adapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            if(rowView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.main_activity_adapter_view, parent, false);

                //configure view holder
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) rowView.findViewById(R.id.main_activity_adapter_view_title);
                viewHolder.createDate = (TextView) rowView.findViewById(R.id.main_activity_adapter_view_date_create);

                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            Map<Integer, List<Property>> properties = PropertyService.convertToMap(getItem(position).getProperties());

            //set title
            if(properties.containsKey(PropertyType.TITLE) && !properties.get(PropertyType.TITLE).isEmpty()) {
                holder.title.setText(properties.get(PropertyType.TITLE).get(0).getText());
            } else if(properties.containsKey(PropertyType.TEXT) && !properties.get(PropertyType.TEXT).isEmpty()) {
                holder.title.setText(properties.get(PropertyType.TEXT).get(0).getText());
            } else {
                holder.title.setText("No text or title...");
            }

            //set create date
            if(properties.containsKey(PropertyType.CREATE_DATE) && !properties.get(PropertyType.CREATE_DATE).isEmpty()) {
                holder.createDate.setText(StringUtil
                        .dateToReadableString(properties.get(PropertyType.CREATE_DATE).get(0).getDate()));
            } else {
                holder.createDate.setText("No create date...");
            }

            return rowView;
        }




    }






}
