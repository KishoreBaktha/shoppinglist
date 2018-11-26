package com.example.kishorebaktha.shoppinglist4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class additem extends AppCompatActivity {
    EditText itemname,itemshop,cost,description,size;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items").push();
        itemname=(EditText)findViewById(R.id.nameofitem);
        itemshop=(EditText)findViewById(R.id.nameofshop);
        cost=(EditText)findViewById(R.id.costofitem);
        description=(EditText)findViewById(R.id.description);
        size=(EditText)findViewById(R.id.size);
    }

    public void additem3(View view) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", itemname.getText().toString());
        map.put("shop", itemshop.getText().toString());
        map.put("cost", cost.getText().toString());
        map.put("description", description.getText().toString());
        map.put("size", size.getText().toString());
        databaseReference.setValue(map);
        Toast.makeText(getApplicationContext(),"Inserted",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(),Newitem.class);
        startActivity(intent);
    }
}
