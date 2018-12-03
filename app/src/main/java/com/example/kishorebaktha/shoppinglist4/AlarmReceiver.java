package com.example.kishorebaktha.shoppinglist4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    String CHANNEL_ID = "my_channel_01";
    String content="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(final Context context, Intent intent) {
        final Intent intent2 = new Intent(context, searchnoti.class);
        final int click=-1;
        intent2.putExtra("click", String.valueOf(click));
        search.specificitem="";
        FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("notifications")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {

                            content=content+"Name:"+snapshot.child("item").getValue().toString()+" "+ "Budget:"+snapshot.child("budget").getValue().toString();

                        }

                        PendingIntent pending = PendingIntent.getActivity(context, 0, intent2, 0);
                        intent2.putExtra("click", String.valueOf(click));
                        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Notification noti = new Notification.Builder(context).setChannelId(CHANNEL_ID).setContentTitle("Time to buy items!!").setContentText(content).setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pending).build();
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "Hey", importance);
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.createNotificationChannel(mChannel);
                        // NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        noti.flags |= Notification.FLAG_AUTO_CANCEL;
                        mNotificationManager.notify(0, noti);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }
}