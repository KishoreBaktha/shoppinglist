package com.example.kishorebaktha.shoppinglist4;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Newitem extends AppCompatActivity {
    AutoCompleteTextView title;
    String priority;
    TextView budget;
    Button high,medium,low;
    ImageView mic;
    private final int Req_code_speech_output=143;
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
        title.requestFocus();
        mic=(ImageView)findViewById(R.id.mic);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"HI SPEAK NOW....");
                try
                {
                    startActivityForResult(intent,Req_code_speech_output);
                }
                catch (ActivityNotFoundException tim)
                {
                    Toast.makeText(getApplicationContext(),"MIKE NOT RESPONDING",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void buttonhigh(View view) {
        priority="high";
        Toast.makeText(getApplicationContext(),"you chose high priority",Toast.LENGTH_SHORT).show();
        high.setBackgroundColor(Color.rgb(76, 164, 72));
        low.setBackgroundResource(R.drawable.shape);
        medium.setBackgroundResource(R.drawable.shape);
        high.setTextColor(Color.WHITE);
        low.setTextColor(Color.rgb(76, 164, 72));
        medium.setTextColor(Color.rgb(76, 164, 72));
//        low.setBackgroundColor(Color.GREEN);
//        medium.setBackgroundColor(Color.GREEN);
    }

    public void buttonmedium(View view)
    {
        priority="medium";
        Toast.makeText(getApplicationContext(),"you chose medium priority",Toast.LENGTH_SHORT).show();
        medium.setBackgroundColor(Color.rgb(76, 164, 72));
        high.setBackgroundResource(R.drawable.shape);
        low.setBackgroundResource(R.drawable.shape);

        medium.setTextColor(Color.WHITE);
        high.setTextColor(Color.rgb(76, 164, 72));
        low.setTextColor(Color.rgb(76, 164, 72));
//        low.setBackgroundColor(Color.GREEN);
        //        high.setBackgroundColor(Color.GREEN);
    }

    public void buttonlow(View view) {
        priority="low";
        Toast.makeText(getApplicationContext(),"you chose low priority",Toast.LENGTH_SHORT).show();
        low.setBackgroundColor(Color.rgb(76, 164, 72));
        high.setBackgroundResource(R.drawable.shape);
        medium.setBackgroundResource(R.drawable.shape);
        high.setTextColor(Color.rgb(76, 164, 72));
        medium.setTextColor(Color.rgb(76, 164, 72));
        low.setTextColor(Color.WHITE);
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
    }
    private static final String[] ITEMS = new String[] {
            "jacket", "coat", "socks", "shirt", "sweater"
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Req_code_speech_output) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                title.setText(voiceInText.get(0));
            }
        }
    }
}
