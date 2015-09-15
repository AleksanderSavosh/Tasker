package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.aleksander.savosh.tasker.model.object.*;
import com.aleksander.savosh.tasker.service.PropertyService;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;
import com.aleksander.savosh.tasker.task.UpdateAdapterTask;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.*;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE MAIN ACTIVITY === ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ListView listview = (ListView) findViewById(R.id.main_activity_list_view);
        View progressBar = findViewById(R.id.main_activity_progress_bar);

        Adapter adapter = new Adapter(this);
        UpdateAdapterTask.initTask(adapter, listview, progressBar, true);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Notice item = (Notice) parent.getItemAtPosition(position);
                startNoticeActivity(item.getObjectId());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem itemAddNotice = menu.add(R.string.action_add_notice);
        itemAddNotice.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startNoticeActivity(null);
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
                    SingUpLogInLogOutService.LogOutResult result = SingUpLogInLogOutService.logOut();
                    if(!result.isLogOut){
                        Toast.makeText(Application.getContext(), result.message, Toast.LENGTH_LONG).show();
                        return true;
                    }
                    Intent intent = new Intent(Application.getContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
            });
        }

        return true;
    }

    private void startNoticeActivity(String noticeId){
        Intent intent = new Intent(Application.getContext(), NoticeActivity2.class);
        if(noticeId != null){
            intent.putExtra(NoticeActivity2.EXTRA_NOTICE_ID, noticeId);
        }
        startActivity(intent);
        finish();
    }

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
