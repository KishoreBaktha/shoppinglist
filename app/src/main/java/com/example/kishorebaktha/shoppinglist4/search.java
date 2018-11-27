package com.example.kishorebaktha.shoppinglist4;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.squareup.picasso.Picasso;

public class search extends AppCompatActivity {
    public static ListView data;
    public static customAdapter custom;
    ArrayList<Integer> budget=new ArrayList<Integer>();
            ArrayList<String> name=new ArrayList<String>();
        ArrayList<Integer> priority=new ArrayList<Integer>();
    ArrayList<String> itemname=new ArrayList<String>();
    ArrayList<Integer> itemcost=new ArrayList<Integer>();
    ArrayList<String> itemsize=new ArrayList<String>();
    ArrayList<String> itemdesc=new ArrayList<String>();
    ArrayList<String> itemshop=new ArrayList<String>();
    ArrayList<String> shops=new ArrayList<String>();
    ArrayList<Integer> count=new ArrayList<Integer>();
    ArrayList<Integer> prioritycount=new ArrayList<Integer>();
    ArrayList<String> shopname=new ArrayList<String>();
    ArrayList<Double> rating=new ArrayList<Double>();
    ArrayList<String> timing=new ArrayList<String>();
    ArrayList<Integer> totalcost=new ArrayList<Integer>();
    ArrayList<String>url=new ArrayList<String>();
    int countnumber=0;
    int countposition=0;
    int click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        data = (ListView) findViewById(R.id.listview);
        custom = new customAdapter(this);
        Intent intent=getIntent();
         click=Integer.parseInt(intent.getStringExtra("click"));
//        searchitems(new MyCallback() {
//            @Override
//            public void onCallback(ArrayList<String>name2,ArrayList<Integer>priority2,ArrayList<Integer>budget2) {
//                name=name2;
//                budget=budget2;
//                priority=priority2;
//                display(budget);
//             //   Toast.makeText(getApplicationContext(),String.valueOf(budget.size()),Toast.LENGTH_SHORT).show();
//            }
//        });
          // Toast.makeText(getApplicationContext(),String.valueOf(budget.size()),Toast.LENGTH_SHORT).show();
        searchitems(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String>name2, ArrayList<Integer>priority2, final ArrayList<Integer>budget2) {
                name=name2;
                budget=budget2;
                priority=priority2;
//                for(int i=0;i<budget.size();i++)
//                {
                    searchitems2(new MyCallback2() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onCallback2(ArrayList<String>name2,ArrayList<Integer>cost,ArrayList<String>desc,ArrayList<String>size,ArrayList<String>shop) {
                            itemname=name2;
                            itemcost=cost;
                            itemdesc=desc;
                            itemsize=size;
                            itemshop=shop;
                            shops = new ArrayList<String>(new LinkedHashSet<String>(itemshop));
                            for(int i=0;i<shops.size();i++)
                            {
                                count.add(0);
                                prioritycount.add(0);
                                totalcost.add(0);
                            }
                            for(int i=0;i<shops.size();i++)
                            {
                                for(int j=0;j<itemname.size();j++)
                                {
                                    if(itemshop.get(j).equals(shops.get(i)))
                                    {
                                        for(int m=0;m<name.size();m++)
                                        {
                                            if(name.get(m).equals(itemname.get(j))&&budget.get(m)>=itemcost.get(j))
                                            {
                                                totalcost.set(i,totalcost.get(i)+itemcost.get(j));
                                                prioritycount.set(i,prioritycount.get(i)+priority.get(m));
                                               // Toast.makeText(getApplicationContext(),priority.get(m).toString(),Toast.LENGTH_LONG).show();
                                                count.set(i,count.get(i)+1);
                                            }
                                        }
                                    }
                                }
                            }
                            for(int i=0;i<shops.size();i++)
                            {
                                for(int j=0;j<shops.size()-i-1;j++)
                                {
                                    if(prioritycount.get(j)<prioritycount.get(j+1))
                                    {
                                        int tempcount=prioritycount.get(j);
                                        prioritycount.set(j,prioritycount.get(j+1));
                                        prioritycount.set(j+1,tempcount);
                                        String tempshop=shops.get(j);
                                        shops.set(j,shops.get(j+1));
                                        shops.set(j+1,tempshop);
                                         tempcount=count.get(j);
                                        count.set(j,count.get(j+1));
                                        count.set(j+1,tempcount);
                                        tempcount=totalcost.get(j);
                                        totalcost.set(j,totalcost.get(j+1));
                                        totalcost.set(j+1,tempcount);
                                    }
                                }
                            }

                            searchitems3(new MyCallback3() {
                                @Override
                                public void onCallback3(ArrayList<String> name, ArrayList<Double> rating, ArrayList<String> timing,ArrayList<String> url) {
                                    shopname = name;
                                    rating = rating;
                                    timing = timing;
                                    url=url;
                                    if (countnumber == 0) {
                                        for (int i = 0; i < shops.size(); i++) {

                                            for (int j = 0; j < shopname.size(); j++) {
                                                if (shops.get(i).equals(shopname.get(j))&&count.get(i)>0) {
                                                    custom.list.add(new singleRow(shops.get(i), count.get(i).toString(), rating.get(j).toString(), timing.get(j),totalcost.get(i).toString(),url.get(j)));
                                                }
                                            }
                                        }
                                        data.setAdapter(custom);
                                    }
                                    countnumber++;
                                }
                            });
//                                Intent intent = new Intent(getApplicationContext(), SearchView.class);
//                                intent.putStringArrayListExtra("shops", shops);
//                                intent.putIntegerArrayListExtra("count", count);
//                                startActivity(intent)


                          //  Toast.makeText(getApplicationContext(),count.get(0).toString(),Toast.LENGTH_LONG).show();
                          // Toast.makeText(getApplicationContext(),String.valueOf(shops.size()),Toast.LENGTH_SHORT).show();


                        }
                    });
               // }
             //   Toast.makeText(getApplicationContext(),String.valueOf(itemname.size()),Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getApplicationContext(),String.valueOf(budget.size()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void display(ArrayList<Integer> budget) {
      //  Toast.makeText(getApplicationContext(),String.valueOf(budget.size()),Toast.LENGTH_SHORT).show();
    }

    public void searchitems(final MyCallback mycallback)
    {
        if(click==-1) {

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                name.add(snapshot.child("item").getValue().toString());
                                budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                if (snapshot.child("priority").getValue().toString().equals("high"))
                                    priority.add(3);
                                else if (snapshot.child("priority").getValue().toString().equals("medium"))
                                    priority.add(2);
                                else
                                    priority.add(1);
                            }
                            mycallback.onCallback(name,priority,budget);
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
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                               if(countposition==click)
                               {
                                   name.add(snapshot.child("item").getValue().toString());
                                   budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                   if (snapshot.child("priority").getValue().toString().equals("high"))
                                       priority.add(3);
                                   else if (snapshot.child("priority").getValue().toString().equals("medium"))
                                       priority.add(2);
                                   else
                                       priority.add(1);
                                   break;
                               }
                                countposition++;
                            }
                            mycallback.onCallback(name,priority,budget);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
        // }

    }
    public void searchitems2(final MyCallback2 mycallback2)
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
                            mycallback2.onCallback2(itemname,itemcost,itemdesc,itemsize,itemshop);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    public void searchitems3(final MyCallback3 mycallback3)
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("shops")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //if (snapshot.child("name").getValue().toString().equals(name.get(index).toString())) {
                            shopname.add(snapshot.child("name").getValue().toString());
                            rating.add(Double.parseDouble(snapshot.child("rating").getValue().toString()));
                            timing.add(snapshot.child("timing").getValue().toString());
                            url.add(snapshot.child("url").getValue().toString());
                            //  }

                        }
                        mycallback3.onCallback3(shopname,rating,timing,url);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    }

 interface MyCallback {
    void onCallback(ArrayList<String>name,ArrayList<Integer>priority,ArrayList<Integer>budget);
}
interface MyCallback2 {
    void onCallback2(ArrayList<String>name,ArrayList<Integer>cost,ArrayList<String>desc,ArrayList<String>size,ArrayList<String>shop);
    //void onCallback2(ArrayList<String>name,ArrayList<Integer>cost,ArrayList<String>size,ArrayList<String>desc,ArrayList<String>shop);
}
interface MyCallback3 {
    void onCallback3(ArrayList<String>name,ArrayList<Double>rating,ArrayList<String>timing,ArrayList<String>url);
    //void onCallback2(ArrayList<String>name,ArrayList<Integer>cost,ArrayList<String>size,ArrayList<String>desc,ArrayList<String>shop);
}
class singleRow {
    String shopname;
    String count;
   String rating;
   String timing;
   String totalcost;
   String url;
    public singleRow(String shopname,String count,String rating,String timing,String totalcost,String url) {

        this.shopname = shopname;
        this.count=count;
        this.rating=rating;
        this.timing=timing;
        this.totalcost=totalcost;
        this.url=url;
    }
}
class customAdapter extends BaseAdapter {
    ArrayList<singleRow> list;
    String name;
    Context c;

    public customAdapter(Context context) {
        list = new ArrayList<singleRow>();
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
        final View row = layoutInflater.inflate(R.layout.single_row, parent, false);
        final TextView shopname = (TextView) row.findViewById(R.id.shopname2);
        final TextView count = (TextView) row.findViewById(R.id.shopcount2);
        final RatingBar ratingBar=(RatingBar)row.findViewById(R.id.ratingBar);
       // final TextView rating= (TextView) row.findViewById(R.id.rating2);
        final TextView timing = (TextView) row.findViewById(R.id.timing2);
        final TextView totalcost = (TextView) row.findViewById(R.id.cost2);
        final Button viewlist=(Button)row.findViewById(R.id.viewlist);
        final Button location=(Button)row.findViewById(R.id.location);
        final ImageView imageView=(ImageView)row.findViewById(R.id.imageView);
        final singleRow tmp = list.get(position);
        shopname.setText(tmp.shopname);
        count.setText("Count: "+tmp.count);
        ratingBar.setRating(Float.parseFloat(tmp.rating));
        //rating.setText(tmp.rating);
        timing.setText("Timing: "+tmp.timing);
        totalcost.setText("Total Cost:"+tmp.totalcost);
        Picasso.with(row.getContext()).load(tmp.url).into(imageView);
      //  Glide.with(row.getContext()).using(new FirebaseImageLoader()).load(tmp.url).into(imageView);
        //Toast.makeText(view.getContext(), "UPDATE", Toast.LENGTH_SHORT).show();
   viewlist.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           Intent intent=new Intent(row.getContext(),ItemList.class);
           intent.putExtra("name",tmp.shopname);
           row.getContext().startActivity(intent);
       }
   });
        return row;
    }
}