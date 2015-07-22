package com.aleksander.savosh.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.aleksander.savosh.tasker.adapters.TypeSpinnerAdapter;
import com.aleksander.savosh.tasker.beans.Message;
import com.aleksander.savosh.tasker.beans.Type;
import com.aleksander.savosh.tasker.dao.OrmDatabaseHelper;
import com.aleksander.savosh.tasker.model.LogInData;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends OrmLiteBaseActivity<OrmDatabaseHelper> {

    //some cash
    public static final Hashtable<Integer, Message> MESSAGES = new Hashtable<Integer, Message>();

    MessageAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //load from storage old version
//        final String[] types = TypesDao.select(this);
//        Log.v("LOG_TAG", Arrays.toString(types));
//        List<Message> messages = MessagesDao.select(this);
//        for(Message message : messages){
//            MESSAGES.put(message.getId(), message);
//        }



        //load from storage orm version
//        List<Type> types = getHelper().getTypesRuntimeDao().queryForAll();
//        Log.v(getClass().getName(), Arrays.toString(types.toArray()));

//        List<Message> messages = getHelper().getMessagesRuntimeDao().queryForAll();
//        for(Message message : messages){
//            MESSAGES.put(message.getId(), message);
//        }

//        types.add(0, new Type(getResources().getString(R.string.all)));

//        Spinner spinner = (Spinner) findViewById(R.id.select_btn);
//        spinner.setAdapter(new TypeSpinnerAdapter(this, types));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                adapter.clear();
//
//                String type = ((TextView) view).getText().toString();
//
//                if(type.equalsIgnoreCase(getResources().getString(R.string.all))) {
//                    adapter.addAll(MESSAGES.values());
//                } else {
//                    adapter.addAll(Message.filterByType(MESSAGES.values(), type));
//                }
//                adapter.sort(new Message.PriorityComparator());
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                adapter.clear();
//                adapter.addAll(MESSAGES.values());
//                adapter.sort(new Message.PriorityComparator());
//                adapter.notifyDataSetChanged();
//            }
//        });


        final ListView listview = (ListView) findViewById(R.id.list_view);
        adapter = new MessageAdapter(this, android.R.layout.list_content, new ArrayList<Message>(MESSAGES.values()));
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                final Message item = (Message) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditMessage.class);
                intent.putExtra("selectedMessage", item);
                startActivity(intent);
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.clear();

        Spinner spinner = (Spinner) findViewById(R.id.select_btn);
//        String type = ((Type) spinner.getSelectedItem()).getName();
//        if(type.equalsIgnoreCase(getResources().getString(R.string.all))) {
//            adapter.addAll(MESSAGES.values());
//        } else {
//            adapter.addAll(Message.filterByType(MESSAGES.values(), type));
//        }

        adapter.sort(new Message.PriorityComparator());
        adapter.notifyDataSetChanged();
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


    private class MessageAdapter extends ArrayAdapter<Message> {

        class ViewHolder {
            public Message message;
            public ImageView logo;
            public TextView text;
            public ProgressBar priority;
            public TextView date;

        }

        public MessageAdapter(Context context, int textViewResourceId,
                              List<Message> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public long getItemId(int position) {
            Message item = getItem(position);
            return item.getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
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
                viewHolder.logo = (ImageView) rowView.findViewById(R.id.mess_logo);
                viewHolder.text = (TextView) rowView.findViewById(R.id.mess_value);
                viewHolder.priority = (ProgressBar) rowView.findViewById(R.id.priority_progress_bar);
                viewHolder.date = (TextView) rowView.findViewById(R.id.mess_create_date);

                ImageView deleteMessageIcon = (ImageView) rowView.findViewById(R.id.mess_delete);
                deleteMessageIcon.setClickable(true);
                deleteMessageIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MESSAGES.remove(viewHolder.message.getId());
//                        Log.i(getClass().getName(), " MESSAGES size: " + MESSAGES.size());
                        MessageAdapter.this.remove(viewHolder.message);
//                        Log.i(getClass().getName(), " MessageAdapter size: " + MessageAdapter.this.getCount());
                        MainActivity.this.getHelper().getMessagesRuntimeDao().delete(viewHolder.message);
//                        Log.i(getClass().getName(), " MessagesRuntimeDao size: "
//                                + MainActivity.this.getHelper().getMessagesRuntimeDao().queryForAll().size());

                        MessageAdapter.this.notifyDataSetChanged();
                    }
                });

                rowView.setTag(viewHolder);
            }

            //fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            Message message = getItem(position);
            holder.message = message;
            holder.logo.setImageResource(message.getType().getImageId());
            holder.text.setText(message.getMessage());
            holder.text.setSingleLine(true);
            holder.text.setEllipsize(TextUtils.TruncateAt.END);
            holder.priority.setProgress(message.getPriority());

//            holder.priority.set
            holder.date.setText(new SimpleDateFormat(Message.DATE_PATTERN).format(message.getCreateDate()));

            return rowView;
        }



    }


}
