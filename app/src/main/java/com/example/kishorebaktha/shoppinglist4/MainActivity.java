package com.example.kishorebaktha.shoppinglist4;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

public class MainActivity extends AppCompatActivity {
    private static int SIGN_IN_CODE = 1;
    RelativeLayout activity_main;
    int year_x, month_x, day_x;
    static final int dialog_id = 0;
    static final int dialog_id2 = 1;
    TextView datetext, timetext;
    int hour_x, minute_x;
    PermissionManager permissionManager;
    private int notificationid=1;
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    Double userLongitude, userLatitude;
    private LocationListener listener;
    Button reminder,searchbut;
    final static int req1 = 1;
    public String a = "0";
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    int count = 0;
    int click;
    int itemcount=0;
    ArrayList<String> voiceInText;
    ArrayAdapter<String> adapter2;
     AutoCompleteTextView item;
    // private FirebaseListAdapter<ListItem> Adapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter2= new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ITEMS);
        BottomNavigationView bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomnavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        Intent intent = new Intent(getApplicationContext(), Newitem.class);
                        intent.putExtra("name", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                        startActivity(intent);
                        break;
                    case R.id.search:
                        click = -1;
                        Intent intent2 = new Intent(getApplicationContext(), search.class);
                        intent2.putExtra("click", String.valueOf(click));
                        search.specificitem = "";
                        startActivity(intent2);
                        break;
                    case R.id.reminder:
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.reminder);
                        dialog.setTitle("REMINDER");
                        Button btndate = (Button) dialog.findViewById(R.id.datebutton);
                        Button btntime = (Button) dialog.findViewById(R.id.timebutton);
                        Button set = (Button) dialog.findViewById(R.id.set);
                        datetext = dialog.findViewById(R.id.datetext);
                        timetext = dialog.findViewById(R.id.timetext);
                        btndate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(dialog_id2);
                            }
                        });
                        btntime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(dialog_id);
                            }
                        });
                        set.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                                intent.putExtra("notificationid", notificationid);
                                intent.putExtra("todo", "PURCHASE YOUR ITEMS IN THE LIST");
                                PendingIntent alarmintent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                int hour = hour_x;
                                int minute = minute_x;
                                java.util.Calendar starttime = java.util.Calendar.getInstance();
                                starttime.set(java.util.Calendar.DAY_OF_MONTH, day_x);
                                starttime.set(java.util.Calendar.MONTH, month_x - 1);
                                starttime.set(java.util.Calendar.YEAR, year_x);
                                starttime.set(java.util.Calendar.HOUR_OF_DAY, hour);
                                starttime.set(java.util.Calendar.MINUTE, minute);
                                starttime.set(java.util.Calendar.SECOND, 0);
                                long alarmstarttime = starttime.getTimeInMillis();
                                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmstarttime, alarmintent);
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    String name = (snapshot.child("item").getValue().toString());
                                                    int budget = (Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                                    int priority;
                                                    if (snapshot.child("priority").getValue().toString().equals("high"))
                                                        priority = 5;
                                                    else if (snapshot.child("priority").getValue().toString().equals("medium"))
                                                        priority = 3;
                                                    else
                                                        priority = 1;
                                                    map.put("item", name);
                                                    map.put("budget", budget);
                                                    map.put("priority", priority);
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("notifications")
                                                            .push().setValue(map);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                Toast.makeText(getApplicationContext(), "Reminder set successfully", Toast.LENGTH_SHORT).show();


                                dialog.dismiss();
                            }
                        });
                        // set width for dialog
                        int width = (int) (MainActivity.this.getResources().getDisplayMetrics().widthPixels * 0.95);
                        // set height for dialog
                        int height = (int) (MainActivity.this.getResources().getDisplayMetrics().heightPixels * 0.7);
                        dialog.getWindow().setLayout(width, height);
                        dialog.show();
                        break;
                }
                return true;
            }
        });

            recyclerView = findViewById(R.id.list);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        check();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLongitude = location.getLongitude();
                userLatitude = location.getLatitude();
                locationManager.removeUpdates(listener);
                //locationManager = null;
                Intent intent = new Intent(getApplicationContext(), search.class);
                intent.putExtra("latitude", userLatitude.toString());
                intent.putExtra("longitude", userLongitude.toString());
                intent.putExtra("click", String.valueOf(click));
                progressDialog.dismiss();
                search.specificitem="";
                startActivity(intent);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void check() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        } else {
            displaydata();
        }
    }

    private void displaydata() {
        itemcount=0;
        permissionManager=new PermissionManager(){};
        permissionManager.checkAndRequestPermissions(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        final Calendar cal;
        cal = Calendar.getInstance();
        year_x = cal.get(java.util.Calendar.YEAR);
        month_x = cal.get(java.util.Calendar.MONTH);
        day_x = cal.get(java.util.Calendar.DAY_OF_MONTH);
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list");

        FirebaseRecyclerOptions<ListItem> options =
                new FirebaseRecyclerOptions.Builder<ListItem>()
                        .setQuery(query, new SnapshotParser<ListItem>() {
                            @NonNull
                            @Override
                            public ListItem parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new ListItem(snapshot.child("item").getValue().toString(),
                                        snapshot.child("budget").getValue().toString(),
                                        snapshot.child("priority").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<ListItem, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, final ListItem model) {
                holder.setItem(model.getItem());
                holder.setBudget(model.getBudget()+"  SEK");
                holder.setPriority(model.getPriority());
                // Toast.makeText(getApplicationContext(),String.valueOf(position),Toast.LENGTH_LONG).show();
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] priority = new String[1];
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.itemupdate);
                        dialog.setTitle("UPDATE");
                         item = (AutoCompleteTextView) dialog.findViewById(R.id.item2);
                        final Button high2 = (Button) dialog.findViewById(R.id.high2);
                        final Button medium2 = (Button) dialog.findViewById(R.id.medium2);
                        final Button low2 = (Button) dialog.findViewById(R.id.low2);
                        final EditText budget2=(EditText)dialog.findViewById(R.id.budget3);
                        final Button update = (Button) dialog.findViewById(R.id.update);
                        budget2.setText(model.getBudget());
                        item.requestFocus();
                        if(model.getPriority().equals("high"))
                        {
                            high2.setBackgroundColor(Color.rgb(76, 164, 72));
                            high2.setTextColor(Color.WHITE);
                            priority[0] ="high";
                        }
                        else if(model.getPriority().equals("medium"))
                        {
                            medium2.setBackgroundColor(Color.rgb(76, 164, 72));
                            medium2.setTextColor(Color.WHITE);
                            priority[0] ="medium";
                        }

                        else if(model.getPriority().equals("low"))
                        {
                            low2.setBackgroundColor(Color.rgb(76, 164, 72));
                            low2.setTextColor(Color.WHITE);
                            priority[0] ="low";
                        }

                        item.setAdapter(adapter2);
                        item.setText(model.getItem());
                        item.setFocusable(true);
                        high2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                priority[0] ="high";
                                Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                                high2.setBackgroundColor(Color.rgb(76, 164, 72));
                                low2.setBackgroundResource(R.drawable.shape);
                                medium2.setBackgroundResource(R.drawable.shape);
                                high2.setTextColor(Color.WHITE);
                                low2.setTextColor(Color.rgb(76, 164, 72));
                                medium2.setTextColor(Color.rgb(76, 164, 72));
                            }
                        });
                        medium2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                priority[0] ="medium";
                                Toast.makeText(getApplicationContext(),"Clicked Medium",Toast.LENGTH_SHORT).show();
                                medium2.setBackgroundColor(Color.rgb(76, 164, 72));
                                high2.setBackgroundResource(R.drawable.shape);
                                low2.setBackgroundResource(R.drawable.shape);

                                medium2.setTextColor(Color.WHITE);
                                low2.setTextColor(Color.rgb(76, 164, 72));
                                high2.setTextColor(Color.rgb(76, 164, 72));

                            }
                        });
                        low2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                priority[0] ="low";
                                Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                                low2.setBackgroundColor(Color.rgb(76, 164, 72));
                                high2.setBackgroundResource(R.drawable.shape);
                                medium2.setBackgroundResource(R.drawable.shape);
                                high2.setTextColor(Color.rgb(76, 164, 72));
                                medium2.setTextColor(Color.rgb(76, 164, 72));
                                low2.setTextColor(Color.WHITE);
                            }
                        });
                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (position == count) {
                                                        snapshot.getRef().removeValue();
                                                        //  Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                                                        recyclerView.setAdapter(null);
                                                        recyclerView.setAdapter(adapter);
                                                        dialog.dismiss();
                                                        break;
                                                    }
                                                    count++;
                                                }
                                            }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                    count = 0;click=0;
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list").push();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("item", item.getText().toString());
                                    map.put("budget", budget2.getText().toString());
                                    map.put("priority",priority[0]);
                                    databaseReference.setValue(map);
                                    Toast.makeText(getApplicationContext(),"UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                                }
                            });
//                        // set width for dialog
                        int width = (int) (MainActivity.this.getResources().getDisplayMetrics().widthPixels * 0.95);
                        // set height for dialog
                        int height = (int) (MainActivity.this.getResources().getDisplayMetrics().heightPixels * 0.7);
                        dialog.getWindow().setLayout(width, height);
                        dialog.show();
                    }
                });

                holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        progressDialog = ProgressDialog.show(view.getContext(),"Sending request","Please wait...",false,false);
                        click = position;
                        Intent intent = new Intent(getApplicationContext(), search.class);
                        intent.putExtra("click", String.valueOf(click));
                        progressDialog.dismiss();
                        search.specificitem="";
                        startActivity(intent);
                        return false;
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                a_builder.setMessage("Do you want to delete the item from list?")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Item deleted!! ", Toast.LENGTH_SHORT).show();
                                //Remove swiped item from list and notify the RecyclerView
                                final int position = viewHolder.getAdapterPosition();
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (position == count) {
                                                        snapshot.getRef().removeValue();
                                                        //  Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                                                        recyclerView.setAdapter(null);
                                                        recyclerView.setAdapter(adapter);
                                                        break;
                                                    }
                                                    count++;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                count = 0;click=0;
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        recyclerView.setAdapter(adapter);

                    }
                });
                AlertDialog ab = a_builder.create();
                ab.setTitle("Alert");
                ab.show();
              //  recyclerView.setAdapter(null);
               // recyclerView.setAdapter(adapter);
               // adapter.startListening();
                }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private static final String[] ITEMS = new String[] {
            "jacket", "coat", "socks", "shirt", "sweater"
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_CODE && resultCode == RESULT_OK) {
            Snackbar.make(activity_main, "Successfully signed in", Snackbar.LENGTH_SHORT).show();
            displaydata();
        } else {
            Snackbar.make(activity_main, "Couldn't sign in...try again later", Snackbar.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signout) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Snackbar.make(activity_main,"Successfully signed out",Snackbar.LENGTH_SHORT).show();
                    check();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    public void additem(View view) {
        Intent intent = new Intent(getApplicationContext(), Newitem.class);
        intent.putExtra("name", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter!=null)
            adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (adapter!=null)
            adapter.stopListening();
    }

    @Override
    public Dialog onCreateDialog(int id) {
        if (id == dialog_id)
            return new TimePickerDialog(this, TimePickerListener, hour_x, minute_x, false);
        else if (id == dialog_id2)
            return new DatePickerDialog(this, DatePickerListener, year_x, month_x, day_x);
        return null;
    }

    protected DatePickerDialog.OnDateSetListener DatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;//DEFAULT STARTS FROM 0
            day_x = dayOfMonth;
            datetext.setText(day_x + "/" + month_x + "/" + year_x);
        }
    };
    protected TimePickerDialog.OnTimeSetListener TimePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            timetext.setText(hour_x + ":" + minute_x);
        }
    };

    public void search(View view) {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
          //  return;
//        }
        click=-1;
        progressDialog = ProgressDialog.show(this,"Sending request","Please wait...",false,false);
        Intent intent = new Intent(getApplicationContext(), search.class);
        intent.putExtra("click", String.valueOf(click));
        progressDialog.dismiss();
        search.specificitem="";
        startActivity(intent);
       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

    }
}
    class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    TextView item;
        TextView budget;
        TextView priority;
        public void setItem(String item) {
            this.item.setText(item);
        }

        public void setBudget(String budget) {
            this.budget.setText(budget);
        }

        public void setPriority(String priority) {
            this.priority.setText(priority);
        }



    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        item=(TextView)itemView.findViewById(R.id.item2);
        budget=(TextView)itemView.findViewById(R.id.budget2);
        priority=(TextView)itemView.findViewById(R.id.priority2);
    }


}