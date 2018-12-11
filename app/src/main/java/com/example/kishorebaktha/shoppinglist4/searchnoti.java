package com.example.kishorebaktha.shoppinglist4;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class searchnoti extends AppCompatActivity {
    public static ListView data;
    public static customAdapter custom;
    ArrayList<Integer> budget = new ArrayList<Integer>();
    ArrayList<String> name = new ArrayList<String>();
    ArrayList<Integer> priority = new ArrayList<Integer>();
    ArrayList<String> itemname = new ArrayList<String>();
    ArrayList<Integer> itemcost = new ArrayList<Integer>();
    private LocationManager locationManager;
    Double userLongitude, userLatitude;
    private LocationListener listener;
    private final int REQUEST_PERMISSION=1;
    private ProgressDialog progressDialog;
    ArrayList<String> itemsize = new ArrayList<String>();
    ArrayList<String> itemdesc = new ArrayList<String>();
    ArrayList<String> itemshop = new ArrayList<String>();
    ArrayList<String> shops = new ArrayList<String>();
    ArrayList<Integer> count = new ArrayList<Integer>();
    ArrayList<Integer> prioritycount = new ArrayList<Integer>();
    ArrayList<String> shopname = new ArrayList<String>();
    ArrayList<String> number = new ArrayList<String>();
    ArrayList<Double> rating = new ArrayList<Double>();
    public static final double PI = 3.14159265;
    public static final double deg2radians = PI / 180.0;
    ArrayList<Double> latitude = new ArrayList<Double>();
    ArrayList<Double> longitude = new ArrayList<Double>();
    ArrayList<String> timing = new ArrayList<String>();
    ArrayList<Integer> totalcost = new ArrayList<Integer>();
    ArrayList<String> url = new ArrayList<String>();
    ArrayList<String> previtems = new ArrayList<String>();
    int countnumber = 0;
    int countposition = 0;
    int click;
    static String specificitem="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchnoti);
        data = (ListView) findViewById(R.id.listview2);
        custom = new customAdapter(this);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            click= Integer.parseInt(extras.getString("click"));
        }
        else
            click=-1;

        BottomNavigationView bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomnavigation2);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.price:
                        custom.list.clear();
                        //countnumber=0;
                        count = new ArrayList<Integer>();
                        prioritycount = new ArrayList<Integer>();
                        totalcost = new ArrayList<Integer>();
                        name = new ArrayList<String>();
                        budget = new ArrayList<Integer>();
                        priority = new ArrayList<Integer>();
                        itemname = new ArrayList<String>();
                        itemdesc = new ArrayList<String>();
                        itemcost = new ArrayList<Integer>();
                        itemsize = new ArrayList<String>();
                        itemshop = new ArrayList<String>();
                        shops = new ArrayList<String>();
                        shopname = new ArrayList<String>();
                        previtems=new ArrayList<String>();
                        countposition=0;
                        searchitems(new MyCallback() {
                            @Override
                            public void onCallback(ArrayList<String> name2, ArrayList<Integer> priority2, final ArrayList<Integer> budget2) {
                                name = name2;
                                budget = budget2;
                                priority = priority2;
                                searchitems2(new MyCallback2() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onCallback2(ArrayList<String> name2, ArrayList<Integer> cost, ArrayList<String> desc, ArrayList<String> size, ArrayList<String> shop) {
                                        itemname = name2;
                                        itemcost = cost;
                                        itemdesc = desc;
                                        itemsize = size;
                                        itemshop = shop;
                                        shops = new ArrayList<String>(new LinkedHashSet<String>(itemshop));
                                        for (int i = 0; i < shops.size(); i++) {
                                            count.add(0);
                                            prioritycount.add(0);
                                            totalcost.add(0);
                                        }
                                        for (int i = 0; i < shops.size(); i++) {
                                            previtems=new ArrayList<String>();
                                            for (int j = 0; j < itemname.size(); j++) {
                                                if (itemshop.get(j).equals(shops.get(i))) {
                                                    for (int m = 0; m < name.size(); m++) {
                                                        if (name.get(m).equals(itemname.get(j)) && budget.get(m) >= itemcost.get(j)&&!previtems.contains(name.get(m))) {
                                                            previtems.add(name.get(m));
                                                            totalcost.set(i, totalcost.get(i) + itemcost.get(j));
                                                            prioritycount.set(i, prioritycount.get(i) + priority.get(m));
                                                            // Toast.makeText(getApplicationContext(),priority.get(m).toString(),Toast.LENGTH_LONG).show();
                                                            count.set(i, count.get(i) + 1);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        for (int i = 0; i < shops.size(); i++) {
                                            for (int j = 0; j < shops.size() - i - 1; j++) {
                                                if (totalcost.get(j) > totalcost.get(j + 1)) {
                                                    int tempcount = prioritycount.get(j);
                                                    prioritycount.set(j, prioritycount.get(j + 1));
                                                    prioritycount.set(j + 1, tempcount);
                                                    String tempshop = shops.get(j);
                                                    shops.set(j, shops.get(j + 1));
                                                    shops.set(j + 1, tempshop);
                                                    tempcount = count.get(j);
                                                    count.set(j, count.get(j + 1));
                                                    count.set(j + 1, tempcount);
                                                    tempcount = totalcost.get(j);
                                                    totalcost.set(j, totalcost.get(j + 1));
                                                    totalcost.set(j + 1, tempcount);
                                                }
                                            }
                                        }

                                        searchitems3(new MyCallback3() {
                                            @Override
                                            public void onCallback3(ArrayList<String> name, ArrayList<Double> rating, ArrayList<String> timing, ArrayList<String> url, ArrayList<String> number2) {
                                                shopname = name;
                                                rating = rating;
                                                timing = timing;
                                                url = url;
                                                number=number2;
                                                for (int i = 0; i < shops.size(); i++) {

                                                    for (int j = 0; j < shopname.size(); j++) {
                                                        if (shops.get(i).equals(shopname.get(j)) && count.get(i) > 0) {
                                                            custom.list.add(new singleRow(shops.get(i), count.get(i).toString(), rating.get(j).toString(), timing.get(j), totalcost.get(i).toString(), url.get(j),number.get(i)));
                                                        }
                                                    }
                                                }
                                                data.setAdapter(custom);
                                                // countnumber++;
                                            }
                                        });

                                    }
                                });
                            }
                        });

                        break;
                    case R.id.location:
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        {
                        }
                        else
                        {
                            progressDialog = ProgressDialog.show(searchnoti.this,"Fetching user location","Please wait...",false,false);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);

                        }
                        break;
                    case R.id.mapview:
                        final ArrayList<String> shoplist=new ArrayList<String>();
                        final ArrayList<String> latitudelist=new ArrayList<String>();
                        final ArrayList<String> longitudelist=new ArrayList<String>();
                        countposition=0;
                        count = new ArrayList<Integer>();
                        prioritycount = new ArrayList<Integer>();
                        totalcost = new ArrayList<Integer>();
                        name = new ArrayList<String>();
                        budget = new ArrayList<Integer>();
                        priority = new ArrayList<Integer>();
                        itemname = new ArrayList<String>();
                        itemdesc = new ArrayList<String>();
                        itemcost = new ArrayList<Integer>();
                        itemsize = new ArrayList<String>();
                        itemshop = new ArrayList<String>();
                        shops = new ArrayList<String>();
                        number = new ArrayList<String>();
                        previtems=new ArrayList<String>();
                        shopname = new ArrayList<String>();
                        searchitems(new MyCallback() {
                            @Override
                            public void onCallback(ArrayList<String> name2, ArrayList<Integer> priority2, final ArrayList<Integer> budget2) {
                                name = name2;
                                budget = budget2;
                                priority = priority2;
                                searchitems2(new MyCallback2() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onCallback2(ArrayList<String> name2, ArrayList<Integer> cost, ArrayList<String> desc, ArrayList<String> size, final ArrayList<String> shop) {
                                        itemname = name2;
                                        itemcost = cost;
                                        itemdesc = desc;
                                        itemsize = size;
                                        itemshop = shop;
                                        shops = new ArrayList<String>(new LinkedHashSet<String>(itemshop));
                                        for (int i = 0; i < shops.size(); i++) {
                                            count.add(0);
                                            prioritycount.add(0);
                                            totalcost.add(0);
                                        }
                                        for (int i = 0; i < shops.size(); i++) {
                                            previtems=new ArrayList<String>();
                                            for (int j = 0; j < itemname.size(); j++) {
                                                if (itemshop.get(j).equals(shops.get(i))) {
                                                    for (int m = 0; m < name.size(); m++) {
                                                        if (name.get(m).equals(itemname.get(j)) && budget.get(m) >= itemcost.get(j)&&!previtems.contains(name.get(m))) {
                                                            previtems.add(name.get(m));
                                                            totalcost.set(i, totalcost.get(i) + itemcost.get(j));
                                                            prioritycount.set(i, prioritycount.get(i) + priority.get(m));
                                                            // Toast.makeText(getApplicationContext(),priority.get(m).toString(),Toast.LENGTH_LONG).show();
                                                            count.set(i, count.get(i) + 1);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        searchitems10(new MyCallback10() {
                                            @Override
                                            public void onCallback10(ArrayList<String> name, ArrayList<Double> rating, ArrayList<String> timing, ArrayList<String> url, ArrayList<Double> latitude2, ArrayList<Double> longitude2,ArrayList<String> number2) {
                                                shopname = name;
                                                for (int i = 0; i < shops.size(); i++) {

                                                    for (int j = 0; j < latitude.size(); j++) {
                                                        if (shops.get(i).equals(shopname.get(j))&&count.get(i)>0&&j<shopname.size()) {
                                                            shoplist.add(shops.get(i));
                                                            latitudelist.add(String.valueOf(latitude.get(j)));
                                                            longitudelist.add(String.valueOf(longitude.get(j)));

                                                        }
                                                    }
                                                }
                                                //  Toast.makeText(getApplicationContext(),shoplist.get(3),Toast.LENGTH_LONG).show();
                                                if(countnumber==1)
                                                {
                                                    Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                                                    intent.putStringArrayListExtra("shopname",shoplist);
                                                    intent.putStringArrayListExtra("latitude",latitudelist);
                                                    intent.putStringArrayListExtra("longitude",longitudelist);
                                                    getApplicationContext().startActivity(intent);
                                                }


                                            }
                                        });

                                    }
                                });
                            }
                        });
                        break;

                }
                return true;
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLongitude = location.getLongitude();
                userLatitude = location.getLatitude();
                locationManager.removeUpdates(listener);
                progressDialog.dismiss();
                custom.list.clear();
                //countnumber=0;
                countposition=0;
                count = new ArrayList<Integer>();
                prioritycount = new ArrayList<Integer>();
                totalcost = new ArrayList<Integer>();
                name = new ArrayList<String>();
                budget = new ArrayList<Integer>();
                priority = new ArrayList<Integer>();
                itemname = new ArrayList<String>();
                itemdesc = new ArrayList<String>();
                itemcost = new ArrayList<Integer>();
                number = new ArrayList<String>();
                itemsize = new ArrayList<String>();
                itemshop = new ArrayList<String>();
                shops = new ArrayList<String>();
                previtems=new ArrayList<String>();
                shopname = new ArrayList<String>();
                searchitems(new MyCallback() {
                    @Override
                    public void onCallback(ArrayList<String> name2, ArrayList<Integer> priority2, final ArrayList<Integer> budget2) {
                        name = name2;
                        budget = budget2;
                        priority = priority2;
                        searchitems2(new MyCallback2() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onCallback2(ArrayList<String> name2, ArrayList<Integer> cost, ArrayList<String> desc, ArrayList<String> size, ArrayList<String> shop) {
                                itemname = name2;
                                itemcost = cost;
                                itemdesc = desc;
                                itemsize = size;
                                itemshop = shop;
                                shops = new ArrayList<String>(new LinkedHashSet<String>(itemshop));
                                for (int i = 0; i < shops.size(); i++) {
                                    count.add(0);
                                    prioritycount.add(0);
                                    totalcost.add(0);
                                }
                                for (int i = 0; i < shops.size(); i++) {
                                    previtems=new ArrayList<String>();
                                    for (int j = 0; j < itemname.size(); j++) {
                                        if (itemshop.get(j).equals(shops.get(i))) {
                                            for (int m = 0; m < name.size(); m++) {
                                                if (name.get(m).equals(itemname.get(j)) && budget.get(m) >= itemcost.get(j)&&!previtems.contains(name.get(m))) {
                                                    previtems.add(name.get(m));
                                                    totalcost.set(i, totalcost.get(i) + itemcost.get(j));
                                                    prioritycount.set(i, prioritycount.get(i) + priority.get(m));
                                                    // Toast.makeText(getApplicationContext(),priority.get(m).toString(),Toast.LENGTH_LONG).show();
                                                    count.set(i, count.get(i) + 1);
                                                }
                                            }
                                        }
                                    }
                                }
                                searchitems10(new MyCallback10() {
                                    @Override
                                    public void onCallback10(ArrayList<String> name, ArrayList<Double> rating, ArrayList<String> timing, ArrayList<String> url, ArrayList<Double> latitude2, ArrayList<Double> longitude2,ArrayList<String> number2) {
                                        shopname = new ArrayList<String>();
                                        shopname = name;
                                        rating = rating;
                                        timing = timing;
                                        latitude = latitude2;
                                        longitude = longitude2;
                                        ArrayList<Double> distance=new ArrayList<Double>();
                                        url = url;
                                        number=number2;

                                        // userLatitude=44.968046  ;
                                        //userLongitude= -94.420307;

                                        for(int i=0;i<shops.size();i++)
                                        {
                                            for(int j=0;j<latitude.size();j++)
                                            {
                                                if(shops.get(i).equals(shopname.get(j)))
                                                {
                                                    //if(j<shopname.size()
                                                   // Toast.makeText(searchnoti.this,shopname.size(),Toast.LENGTH_LONG).show();
                                                    distance.add(getDistance(userLatitude,userLongitude,latitude.get(j),longitude.get(j)));
                                                }
                                            }

                                        }
                                        for(int i=0;i<shops.size();i++)
                                        {
                                            for(int j=0;j<shops.size()-i-1;j++)
                                            {
                                                if(distance.get(j)>distance.get(j+1))
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
                                                    double tempcount2=distance.get(j);
                                                    distance.set(j,distance.get(j+1));
                                                    distance.set(j+1,tempcount2);
                                                }
                                            }
                                        }

                                        for (int i = 0; i < shops.size(); i++) {

                                            for (int j = 0; j < shopname.size(); j++) {
                                                if (shops.get(i).equals(shopname.get(j))&&count.get(i)>0) {
                                                    custom.list.add(new singleRow(shops.get(i), count.get(i).toString(), rating.get(j).toString(), timing.get(j),totalcost.get(i).toString(),url.get(j),number.get(i)));
                                                }
                                            }
                                        }
                                        data.setAdapter(custom);
                                        // countnumber++;
                                    }
                                });

                            }
                        });
                    }
                });
                //  locationManager = null;

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
        if (countnumber == 0) {
            searchitems(new MyCallback() {
                @Override
                public void onCallback(ArrayList<String> name2, ArrayList<Integer> priority2, final ArrayList<Integer> budget2) {
                    name = name2;
                    budget = budget2;
                    priority = priority2;
//                for(int i=0;i<budget.size();i++)
//                {
                    searchitems2(new MyCallback2() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onCallback2(ArrayList<String> name2, ArrayList<Integer> cost, ArrayList<String> desc, ArrayList<String> size, ArrayList<String> shop) {
                            itemname = name2;
                            itemcost = cost;
                            itemdesc = desc;
                            itemsize = size;
                            itemshop = shop;
                            shops = new ArrayList<String>(new LinkedHashSet<String>(itemshop));
                            for (int i = 0; i < shops.size(); i++) {
                                count.add(0);
                                prioritycount.add(0);
                                totalcost.add(0);
                            }
                            for (int i = 0; i < shops.size(); i++) {
                                previtems=new ArrayList<String>();
                                for (int j = 0; j < itemname.size(); j++) {
                                    if (itemshop.get(j).equals(shops.get(i))) {
                                        for (int m = 0; m < name.size(); m++) {
                                            if (name.get(m).equals(itemname.get(j)) && budget.get(m) >= itemcost.get(j)&&!previtems.contains(name.get(m))) {
                                                previtems.add(name.get(m));
                                                totalcost.set(i, totalcost.get(i) + itemcost.get(j));
                                                prioritycount.set(i, prioritycount.get(i) + priority.get(m));
                                                // Toast.makeText(getApplicationContext(),priority.get(m).toString(),Toast.LENGTH_LONG).show();
                                                count.set(i, count.get(i) + 1);
                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < shops.size(); i++) {
                                for (int j = 0; j < shops.size() - i - 1; j++) {
                                    if (prioritycount.get(j) < prioritycount.get(j + 1)) {
                                        int tempcount = prioritycount.get(j);
                                        prioritycount.set(j, prioritycount.get(j + 1));
                                        prioritycount.set(j + 1, tempcount);
                                        String tempshop = shops.get(j);
                                        shops.set(j, shops.get(j + 1));
                                        shops.set(j + 1, tempshop);
                                        tempcount = count.get(j);
                                        count.set(j, count.get(j + 1));
                                        count.set(j + 1, tempcount);
                                        tempcount = totalcost.get(j);
                                        totalcost.set(j, totalcost.get(j + 1));
                                        totalcost.set(j + 1, tempcount);
                                    }
                                }
                            }

                            searchitems3(new MyCallback3() {
                                @Override
                                public void onCallback3(ArrayList<String> name, ArrayList<Double> rating, ArrayList<String> timing, ArrayList<String> url,ArrayList<String> number) {
                                    shopname = name;
                                    rating = rating;
                                    timing = timing;
                                    url = url;
                                    number=number;
                                    if (countnumber == 0) {
                                        for (int i = 0; i < shops.size(); i++)
                                        {

                                            for (int j = 0; j < shopname.size(); j++) {
                                                if (shops.get(i).equals(shopname.get(j)) && count.get(i) > 0) {
                                                    custom.list.add(new singleRow(shops.get(i), count.get(i).toString(), rating.get(j).toString(), timing.get(j), totalcost.get(i).toString(), url.get(j),number.get(i)));
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
    }

    public void searchitems(final MyCallback mycallback) {
        if (click == -1)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("notifications")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                name.add(snapshot.child("item").getValue().toString());
                                budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                if (snapshot.child("priority").getValue().toString().equals("high"))
                                    priority.add(5);
                                else if (snapshot.child("priority").getValue().toString().equals("medium"))
                                    priority.add(3);
                                else
                                    priority.add(1);
                            }
                            mycallback.onCallback(name, priority, budget);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        } else
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("notifications")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (countposition == click) {
                                    specificitem=snapshot.child("item").getValue().toString();
                                    name.add(snapshot.child("item").getValue().toString());
                                    budget.add(Integer.parseInt(snapshot.child("budget").getValue().toString()));
                                    if (snapshot.child("priority").getValue().toString().equals("high"))
                                        priority.add(5);
                                    else if (snapshot.child("priority").getValue().toString().equals("medium"))
                                        priority.add(3);
                                    else
                                        priority.add(1);
                                    break;
                                }
                                countposition++;
                            }
                            mycallback.onCallback(name, priority, budget);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
        // }

    }

    public void searchitems2(final MyCallback2 mycallback2) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("items")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            //if (snapshot.child("name").getValue().toString().equals(name.get(index).toString())) {
                            itemname.add(snapshot.child("name").getValue().toString());
                            itemcost.add(Integer.parseInt(snapshot.child("cost").getValue().toString()));
                            itemdesc.add(snapshot.child("description").getValue().toString());
                            itemsize.add(snapshot.child("size").getValue().toString());
                            itemshop.add(snapshot.child("shop").getValue().toString());
                            //  }

                        }
                        mycallback2.onCallback2(itemname, itemcost, itemdesc, itemsize, itemshop);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void searchitems3(final MyCallback3 mycallback3) {
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
                            number.add(snapshot.child("phone").getValue().toString());
                            //  }

                        }
                        mycallback3.onCallback3(shopname, rating, timing, url,number);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static void searchitems9(final String name, final MyCallback9 mycallback9) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("shops")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("name").getValue().toString().equals(name)) {
                                Double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                Double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                mycallback9.onCallback9(latitude, longitude);
                                break;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void searchitems10(final MyCallback10 mycallback10) {
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
                            latitude.add(Double.parseDouble(snapshot.child("latitude").getValue().toString()));
                            longitude.add(Double.parseDouble(snapshot.child("longitude").getValue().toString()));
                            number.add(snapshot.child("phone").getValue().toString());
                            //  }

                        }
                        mycallback10.onCallback10(shopname, rating, timing, url, latitude, longitude,number);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

        double lat1 = latitude1 * deg2radians;
        double lat2 = latitude2 * deg2radians;
        double lon1 = longitude1 * deg2radians;
        double lon2 = longitude2 * deg2radians;
        double radd = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
                2.0)
                + Math.cos(lat1)
                * Math.cos(lat2)
                * Math.pow(Math.sin((lon1 - lon2) / 2), 2.0)));
        return radd * 10000;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("notifications").
                    setValue(null);
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}