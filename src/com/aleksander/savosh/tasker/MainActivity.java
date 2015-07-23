package com.aleksander.savosh.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.aleksander.savosh.tasker.dao.OrmDatabaseHelper;
import com.aleksander.savosh.tasker.model.LogInData;
import com.aleksander.savosh.tasker.model.Notice;
import com.aleksander.savosh.tasker.model.Property;
import com.aleksander.savosh.tasker.model.PropertyType;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.util.List;
import java.util.Map;


public class MainActivity extends OrmLiteBaseActivity<OrmDatabaseHelper> {

    private Adapter adapter = new Adapter(Application.getContext());

    private UpdateAdapterTask updateAdapterTask;
    public class UpdateAdapterTask extends AsyncTask<Void, Void, List<Notice>> {
        @Override
        protected List<Notice> doInBackground(Void... params) {
            //get notices from local repository;
            return null;
        }
        @Override
        protected void onPostExecute(List<Notice> notices) {
            adapter.clear();
            adapter.addAll(notices);
            adapter.notifyDataSetChanged();
            updateAdapterTask = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
//                final Message item = (Message) parent.getItemAtPosition(position);
//                Intent intent = new Intent(MainActivity.this, EditMessage.class);
//                intent.putExtra("selectedMessage", item);
//                startActivity(intent);
            }

        });
        if(updateAdapterTask == null){
            updateAdapterTask = new UpdateAdapterTask();
            updateAdapterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_main_log_out) {
            if(logOutTask == null){
                logOutTask = new LogOutTask();
                logOutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private LogOutTask logOutTask;
    private class LogOutTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Application.getLogInDataLocalDao().delete(LogInData.builder().build());
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(Application.getContext(), LogInActivity.class);
            MainActivity.this.startActivity(intent);
            MainActivity.this.finish();
            logOutTask = null;
        }
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
                rowView = inflater.inflate(R.layout.item_view, parent, false);

                //configure view holder
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) rowView.findViewById(R.id.main_adapter_view_title);
                viewHolder.createDate = (TextView) rowView.findViewById(R.id.main_adapter_view_date_create);

                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            Map<PropertyType, List<Property>> properties = getItem(position).getProperties();

            //set title
            if(properties.containsKey(PropertyType.TITLE)) {
                holder.title.setText(properties.get(PropertyType.TITLE).get(0).getText());
            } else if(properties.containsKey(PropertyType.TEXT)) {
                holder.title.setText(properties.get(PropertyType.TEXT).get(0).getText());
            } else {
                holder.title.setText("No text or title...");
            }

            //set create date
            if(properties.containsKey(PropertyType.CREATE_DATE)) {
                holder.createDate.setText(StringUtil
                        .dateToReadableString(properties.get(PropertyType.CREATE_DATE).get(0).getDate()));
            } else {
                holder.createDate.setText("No create date...");
            }

            return rowView;
        }
    }






}
