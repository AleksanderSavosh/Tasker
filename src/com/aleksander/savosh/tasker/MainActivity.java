package com.aleksander.savosh.tasker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.aleksander.savosh.tasker.model.object.*;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;
import com.aleksander.savosh.tasker.task.UpdateAdapterTask;
import com.aleksander.savosh.tasker.task.holder.UpdateAdapterTaskHolder;
import com.aleksander.savosh.tasker.util.ActivityUtil;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    UpdateAdapterTaskHolder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE MAIN ACTIVITY " + toString() + " === ---");
        ActivityUtil.setTheme(this);
        ActivityUtil.checkIfThemeChanged(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final View progressbar = findViewById(R.id.main_activity_progress_bar);

        final RecyclerViewAdapter mainRecyclerViewAdapter = new RecyclerViewAdapter();

        holder = new UpdateAdapterTaskHolder(){{
            this.progressBar = progressbar;
            this.recyclerViewAdapter = mainRecyclerViewAdapter;
        }};


        getSupportActionBar().setTitle(R.string.main_activity_title);

        //card config
        RecyclerView rv = (RecyclerView)findViewById(R.id.main_activity_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(mainRecyclerViewAdapter);


        Application.getAsyncTaskManager().<UpdateAdapterTaskHolder>updateTask(UpdateAdapterTask.class, holder);

        //this button work correct only api 15+
        if(Build.VERSION.SDK_INT >= 15){
            final View fab = findViewById(R.id.main_activity_create_notice_button);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNoticeActivity(null);
                }
            });
        }

    }

    @Override
    protected void onStart() {
        Log.d(getClass().getName(), "--- === ON START MAIN ACTIVITY " + toString() + " === ---");
        super.onStart();
        Application.getAsyncTaskManager().<UpdateAdapterTaskHolder, Void>startTask(UpdateAdapterTask.class, holder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);


        MenuItem itemLogIn = menu.findItem(R.id.menu_main_log_in);
        MenuItem itemLogOut = menu.findItem(R.id.menu_main_log_out);
        MenuItem itemSettings = menu.findItem(R.id.menu_main_settings);

        //add notice button for api 7 - 14
        if(Build.VERSION.SDK_INT < 15){
            MenuItem itemAddNotice = menu.findItem(R.id.menu_main_add_notice);
            itemAddNotice.setVisible(true);
            itemAddNotice.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startNoticeActivity(null);
                    return true;
                }
            });
        }

        Config config = ((Application) getApplicationContext()).getConfig();
        if(StringUtil.isEmpty(config.accountId)) {
            itemLogIn.setVisible(true);
            itemLogIn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(Application.getContext(), LogInActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        } else {
            itemLogOut.setVisible(true);
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
                    return true;
                }
            });
        }

        itemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void startNoticeActivity(String noticeId){
        Intent intent = new Intent(Application.getContext(), NoticeActivity2.class);
        if(noticeId != null){
            intent.putExtra(NoticeActivity2.EXTRA_NOTICE_ID, noticeId);
        }
        startActivity(intent);
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView titleTextView;
        TextView textView;
        TextView createDateTextView;

        NoticeViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.main_activity_card_view);
            titleTextView = (TextView)itemView.findViewById(R.id.main_activity_card_view_title_text_view);
            textView = (TextView)itemView.findViewById(R.id.main_activity_card_view_text_text_view);
            createDateTextView = (TextView)itemView.findViewById(R.id.main_activity_card_view_create_date_text_view);
        }
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<NoticeViewHolder> {

        List<Notice> notices = new ArrayList<Notice>();

        public RecyclerViewAdapter() {}

        public void addAll(Collection<Notice> collection){
            notices.addAll(new ArrayList<Notice>(collection));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public NoticeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.main_activity_card_view, viewGroup, false);
            NoticeViewHolder nvh = new NoticeViewHolder(v);
            return nvh;
        }

        @Override
        public void onBindViewHolder(NoticeViewHolder nvh, final int i) {

            nvh.titleTextView.setText("No title");
            nvh.textView.setText("No text");
            nvh.createDateTextView.setText("No create date");

            nvh.titleTextView.setVisibility(View.GONE);
            nvh.textView.setVisibility(View.GONE);
            nvh.createDateTextView.setVisibility(View.GONE);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for(Property property : notices.get(i).getProperties()){
                if(property.getType().equals(PropertyType.TITLE)){
                    nvh.titleTextView.setText(property.getText());
                    nvh.titleTextView.setVisibility(View.VISIBLE);
                } else if(property.getType().equals(PropertyType.TEXT)){
                    nvh.textView.setText(property.getText());
                    nvh.textView.setVisibility(View.VISIBLE);
                } else if(property.getType().equals(PropertyType.CREATE_DATE)) {
                    nvh.createDateTextView.setText(sdf.format(property.getDate()));
                    nvh.createDateTextView.setVisibility(View.VISIBLE);
                }
            }

            nvh.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Application.getContext(), NoticeActivity2.class);
                    intent.putExtra(NoticeActivity2.EXTRA_NOTICE_ID, notices.get(i).getObjectId());
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notices.size();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "--- === ON DESTROY MAIN ACTIVITY " + toString() + " === ---");
        super.onDestroy();
    }
}
