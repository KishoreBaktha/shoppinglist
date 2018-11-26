package com.example.kishorebaktha.shoppinglist4;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int SIGN_IN_CODE=1;
    RelativeLayout activity_main;
    int year_x,month_x,day_x;
    static final int dialog_id=0;
    static final int dialog_id2=1;
    TextView datetext,timetext;
    int hour_x,minute_x;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    int count=0;
   // private FirebaseListAdapter<ListItem> Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.list);
       // activity_main=(RelativeLayout)findViewById(R.id.activity_main);
        check();
    }
    public void check()
    {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_CODE);
        }
        else
        {
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            displaylist();
            final Calendar cal;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                cal = Calendar.getInstance();
                year_x=cal.get(java.util.Calendar.YEAR);
                month_x=cal.get(java.util.Calendar.MONTH);
                day_x=cal.get(java.util.Calendar.DAY_OF_MONTH);
            }
            //displaylist();
            // Snackbar.make(activity_main,"Welcome"+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //load content
        }
    }

    private void displaylist() {
       // Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
//        FirebaseListOptions<ListItem> options = new FirebaseListOptions.Builder<ListItem>()
//                .setQuery(query, ListItem.class)
//                .setLayout(R.layout.list_item)
//                .build();
//      //  Toast.makeText(getApplicationContext(),"hello2",Toast.LENGTH_LONG).show();
//            final ListView message=(ListView)findViewById(R.id.listView);
//         Adapter=new FirebaseListAdapter<ListItem>(options) {
//                @Override
//                protected void populateView(View v, ListItem model, int position)
//                {
//                    //Toast.makeText(getApplicationContext(),"hello3",Toast.LENGTH_LONG).show();
//                   // get references to views of list_item.xml
//                    TextView item,budget,priority;
//                    item=(TextView)v.findViewById(R.id.item2);
//                    budget=(TextView)v.findViewById(R.id.budget2);
//                    priority=(TextView)v.findViewById(R.id.priority2);
//                    item.setText(model.getItem());
//                    budget.setText(model.getBudget());
//                    priority.setText(model.getPriority());
//                }
//            };
//            message.setAdapter(Adapter);
//            message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(getApplicationContext(),"clocked me",Toast.LENGTH_SHORT).show();
//                    remove(position);
//                }
//            });
//        }
//    public void remove(final int pos)
//    {
//        Toast.makeText(getApplicationContext(),String.valueOf(pos),Toast.LENGTH_SHORT).show();
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if(pos==count)
//                            {
//                                snapshot.getRef().removeValue();
//                                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                            count++;
//                        }
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//        count=0;
            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

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
                protected void onBindViewHolder(ViewHolder holder, final int position, ListItem model) {
                    holder.setItem(model.getItem());
                    holder.setBudget(model.getBudget());
                   holder.setPriority(model.getPriority());
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            };
            recyclerView.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Toast.makeText(MainActivity.this, "on Swiped ", Toast.LENGTH_SHORT).show();
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(position==count)
                            {
                                snapshot.getRef().removeValue();
                                Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                break;
                            }
                            count++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        count=0;
                adapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SIGN_IN_CODE&&resultCode==RESULT_OK)
        {
            Snackbar.make(activity_main,"Successfully signed in",Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            Snackbar.make(activity_main,"Couldn't sign in...try again later",Snackbar.LENGTH_SHORT).show();
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
        if(item.getItemId()==R.id.signout)
        {
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
        Intent intent=new Intent(getApplicationContext(),Newitem.class);
        intent.putExtra("name",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        adapter.stopListening();
    }

    public void reminder(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.reminder);
        dialog.setTitle("REMINDER");
        Button btndate = (Button) dialog.findViewById(R.id.datebutton);
        Button btntime = (Button) dialog.findViewById(R.id.timebutton);
        Button set = (Button) dialog.findViewById(R.id.set);
         datetext=dialog.findViewById(R.id.datetext);
         timetext=dialog.findViewById(R.id.timetext);
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
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Reminder set successfully",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        // set width for dialog
        int width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }
    @Override
    public Dialog onCreateDialog(int id)
    {
        if(id==dialog_id)
            return new TimePickerDialog(this,TimePickerListener,hour_x,minute_x,false);
        else if(id==dialog_id2)
            return new DatePickerDialog(this,DatePickerListener,year_x,month_x,day_x);
        return null;
    }
    protected DatePickerDialog.OnDateSetListener DatePickerListener= new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x=year;
            month_x=month+1;//DEFAULT STARTS FROM 0
            day_x=dayOfMonth;
            datetext.setText(day_x+"/"+month_x+"/"+year_x);
        }
    };
    protected TimePickerDialog.OnTimeSetListener TimePickerListener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x=hourOfDay;
            minute_x=minute;
            timetext.setText(hour_x+":"+minute_x);
        }
    };
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