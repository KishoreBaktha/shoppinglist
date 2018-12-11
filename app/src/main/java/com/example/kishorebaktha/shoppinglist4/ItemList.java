package com.example.kishorebaktha.shoppinglist4;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemList extends AppCompatActivity {
    ArrayList<String> itemname = new ArrayList<String>();
    ArrayList<Integer> itemcost = new ArrayList<Integer>();
    ArrayList<String> itemsize = new ArrayList<String>();
    ArrayList<String> itemdesc = new ArrayList<String>();
    ArrayList<String> itemshop = new ArrayList<String>();
    ArrayList<Integer> budget=new ArrayList<Integer>();
    ArrayList<String> name=new ArrayList<String>();
    public static ListView data;
    public static customAdapter2 custom;
    String shopname;
    String specificitem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        data = (ListView) findViewById(R.id.listview2);
        custom = new customAdapter2(this);
        Intent intent=getIntent();
        shopname=intent.getStringExtra("name");
        specificitem=intent.getStringExtra("specificitem");

        searchitems(new MyCallback4() {

            @Override
            public void onCallback4(ArrayList<String> name2, final ArrayList<Integer> budget2) {
                name = name2;
                budget = budget2;
                searchitems2(new MyCallback5() {
                    @Override
                    public void onCallback5(ArrayList<String> name2, ArrayList<Integer> cost, ArrayList<String> desc, ArrayList<String> size, ArrayList<String> shop) {
                        itemname = name2;
                        itemcost = cost;
                        itemdesc = desc;
                        itemsize = size;
                        itemshop = shop;
                       for(int i=0;i<name.size();i++)
                       {
                           for (int j = 0; j < itemname.size(); j++)
                           {
                                 if(name.get(i).equals(itemname.get(j))&&itemshop.get(j).equals(shopname)&&budget.get(i)>=itemcost.get(j))
                                 {
                                     custom.list.add(new singleRow2(itemname.get(j),itemcost.get(j).toString(),itemdesc.get(j),itemsize.get(j)));
                                 }
                           }
                       }
                        data.setAdapter(custom);
                    }
                });
            }
        });

    }
    public void searchitems(final MyCallback4 mycallback)
    {
        if(specificitem.equals(""))
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                name.add(snapshot.child("item").getValue().toString());
                                budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                            }
                            mycallback.onCallback4(name,budget);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
        else
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("list")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(specificitem.equals(snapshot.child("item").getValue().toString()))
                                {
                                    name.add(snapshot.child("item").getValue().toString());
                                    budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                    break;
                                }

                            }
                            mycallback.onCallback4(name,budget);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }


        }

    public void searchitems2(final MyCallback5 mycallback)
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("items")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //if (snapshot.child("name").getValue().toString().equals(name.get(index).toString())) {
                            itemname.add(snapshot.child("name").getValue().toString());
                            itemcost.add(Integer.parseInt(snapshot.child("cost").getValue().toString()));
                            itemdesc.add(snapshot.child("description").getValue().toString());
                            itemsize.add(snapshot.child("size").getValue().toString());
                            itemshop.add(snapshot.child("shop").getValue().toString());
                            //  }

                        }
                        mycallback.onCallback5(itemname,itemcost,itemdesc,itemsize,itemshop);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
interface MyCallback4 {
    void onCallback4(ArrayList<String>name,ArrayList<Integer>budget);
}
interface MyCallback5 {
    void onCallback5(ArrayList<String>name,ArrayList<Integer>cost,ArrayList<String>desc,ArrayList<String>size,ArrayList<String>shop);
}
class singleRow2 {
   String itemname,itemcost,itemdesc,itemsize;
    public singleRow2(String itemname,String itemcost,String itemdesc,String itemsize) {

        this.itemname = itemname;
        this.itemcost=itemcost;
        this.itemdesc=itemdesc;
        this.itemsize=itemsize;
    }
}
class customAdapter2 extends BaseAdapter {
    ArrayList<singleRow2> list;
    String name;
    Context c;

    public customAdapter2(Context context) {
        list = new ArrayList<singleRow2>();
        c = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = layoutInflater.inflate(R.layout.list_row, parent, false);
        final TextView itemname = (TextView) row.findViewById(R.id.itemname2);
        final TextView itemcost = (TextView) row.findViewById(R.id.itemcost2);
        final TextView itemdesc = (TextView) row.findViewById(R.id.itemdesc2);
        final TextView itemsize = (TextView) row.findViewById(R.id.itemsize2);
        final singleRow2 tmp = list.get(position);
        itemname.setText(tmp.itemname);
        itemcost.setText(tmp.itemcost + " SEK");
        //rating.setText(tmp.rating);
        itemdesc.setText(tmp.itemdesc);
        itemsize.setText(" Sizes: "+tmp.itemsize);
        //Toast.makeText(view.getContext(), "UPDATE", Toast.LENGTH_SHORT).show();
        return row;
    }
}
