package com.example.kishorebaktha.shoppinglist4;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Newitem extends AppCompatActivity {
    AutoCompleteTextView title;
    String priority;
    TextView budget;
    Button high,medium,low;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newitem);
        title=(AutoCompleteTextView) findViewById(R.id.item);
        budget=(EditText)findViewById(R.id.budget);
        high=(Button)findViewById(R.id.high);
        medium=(Button)findViewById(R.id.medium);
        low=(Button)findViewById(R.id.low);
//        high.setBackgroundColor(Color.GREEN);
//        medium.setBackgroundColor(Color.GREEN);
//        low.setBackgroundColor(Color.GREEN);
      // title.setTextColor(Color.RED);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, ITEMS);
        title.setAdapter(adapter);
    }

    public void buttonhigh(View view) {
        priority="high";
        Toast.makeText(getApplicationContext(),"you chose high priority",Toast.LENGTH_SHORT).show();
        high.setBackgroundColor(Color.GREEN);
        low.setBackgroundResource(R.drawable.shape);
        medium.setBackgroundResource(R.drawable.shape);
//        low.setBackgroundColor(Color.GREEN);
//        medium.setBackgroundColor(Color.GREEN);
    }

    public void buttonmedium(View view)
    {
        priority="medium";
        Toast.makeText(getApplicationContext(),"you chose medium priority",Toast.LENGTH_SHORT).show();
        medium.setBackgroundColor(Color.GREEN);
        high.setBackgroundResource(R.drawable.shape);
        low.setBackgroundResource(R.drawable.shape);
//        high.setBackgroundColor(Color.GREEN);
//        low.setBackgroundColor(Color.GREEN);
    }

    public void buttonlow(View view) {
        priority="low";
        Toast.makeText(getApplicationContext(),"you chose low priority",Toast.LENGTH_SHORT).show();
        low.setBackgroundColor(Color.GREEN);
        high.setBackgroundResource(R.drawable.shape);
        medium.setBackgroundResource(R.drawable.shape);
//        high.setBackgroundColor(Color.GREEN);
//        medium.setBackgroundColor(Color.GREEN);
    }

    public void add(View view) {
        String titletext=title.getText().toString();
        String budgettext=budget.getText().toString();
     //   String remindertext=reminder.getText().toString();
         final Intent intent=getIntent();
        String name=intent.getStringExtra("name");int i;
        for( i=0;i<ITEMS.length;i++)
        {
            if(titletext.equals(ITEMS[i]))
                break;
        }
        if(i==ITEMS.length)
        {
            title.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(),"SORRY,ITEM NOT AVAILABLE",Toast.LENGTH_LONG).show();
            title.requestFocus();
            title.setOnClickListener(new  View.OnClickListener() {
                public void onClick(View view) {
                    title.setTextColor(Color.BLACK);
                    ///do what you want the click to do
                }
            });
          //  if(title.getText().toString().trim().length()==0)
        }
        else
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(name).child("list").push();
            Map<String, Object> map = new HashMap<>();
            map.put("item", titletext);
            map.put("budget", budgettext);
            map.put("priority",priority);
            databaseReference.setValue(map);
            Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
            Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent1);
        }
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();


//        FirebaseDatabase.getInstance().getReference().child(name).push().setValue(new ListItem(titletext,budgettext,priority )  ).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task)
//            {
//                if(task.isSuccessful())
//                {
//                    Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
//                    Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
//                    startActivity(intent1);
//                }
//
//                else
//                    Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_SHORT).show();
//            }
//        });;
    }
    private static final String[] ITEMS = new String[] {
            "jacket", "coat", "socks", "shirt", "sweater"
    };

    public void addshop(View view) {
        final Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        Intent intent2=new Intent(getApplicationContext(),addshop.class);
        intent2.putExtra("name",name);
        startActivity(intent2);
    }

    public void additem2(View view) {
        Intent intent=new Intent(getApplicationContext(),additem.class);
        startActivity(intent);
    }

    public void view(View view) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("items")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Toast.makeText(getApplicationContext(), snapshot.child("name").getValue().toString(), Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }
}
