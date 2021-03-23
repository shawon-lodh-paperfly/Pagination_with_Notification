package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.myapplication.PaginationListener.PAGE_START;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private PostRecyclerAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 5;
    private boolean isLoading = false;
    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        swipeRefresh.setOnRefreshListener(this);

        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new PostRecyclerAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(adapter);

        showJustNotification("Page Number",currentPage);
        if(currentPage != 1 ){
            String text = "firstPage";
        }

        doApiCall();


        mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                doApiCall();
                showJustNotification("Page Number",currentPage);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


    }


    private void doApiCall() {
        final ArrayList<PostItem> items = new ArrayList<>();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    itemCount++;
                    PostItem postItem = new PostItem();
                    postItem.setTitle(getString(R.string.text_title) + itemCount);
                    postItem.setDescription(getString(R.string.text_description));
                    items.add(postItem);
                }
                // do this all stuff on Success of APIs response

                if (currentPage != PAGE_START) adapter.removeLoading();
                adapter.addItems(items);
                swipeRefresh.setRefreshing(false);

                // check weather is last page or not
                if (currentPage < totalPage) {
                    adapter.addLoading();
                } else {
                    isLastPage = true;
                }
                isLoading = false;
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {
        itemCount = 0;
        currentPage = PAGE_START;
        isLastPage = false;
        adapter.clear();
        showJustNotification("Page Number",currentPage);
        doApiCall();
    }

    private void showJustNotification(String title,int currentPage) {

        String page_number = "";
        switch (currentPage){
            case 1:
                page_number = "first";
                break;
            case 2 :
                page_number = "second";
                break;
            case 3 :
                page_number = "third";
                break;
            case 4 :
                page_number = "fourth";
                break;
            case 5 :
                page_number = "fifth";
                break;
        }


        int notificationId = new Random().nextInt(100);
        String channelId = "notification_channel_2";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle(title);
        builder.setContentText("You are in "+ page_number + " page");

        //notification with big text style
        //builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s"));
//        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));


        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId, "Notification Channel 1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This notification channel is used to notify user");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Notification notification = builder.build();
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }

}